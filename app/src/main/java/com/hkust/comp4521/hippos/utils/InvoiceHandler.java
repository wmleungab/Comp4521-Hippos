package com.hkust.comp4521.hippos.utils;

/**
 * Created by TC on 5/19/2015.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.hkust.comp4521.hippos.database.DatabaseHelper;
import com.hkust.comp4521.hippos.database.InvoiceDB;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Invoice;
import com.hkust.comp4521.hippos.events.InvoiceSynchronizedEvent;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;

import java.util.List;

public class InvoiceHandler extends IntentService {

    private Context mContext;
    private Handler mHandler;
    public InvoiceHandler() {
        super("InvoiceHandler");
    }

    private int inventoryId;
    private int statusCode = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mContext = this.getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        // Attempt to synchronize local invoice to server
        Commons.recoverLoginInformation(mContext, new Commons.onInitializedListener() {
            @Override
            public void onInitialized() {
                // Upload invoice record by record, if record is uploaded, remove it from local DB
                DatabaseHelper.initDatabase(mContext);
                final InvoiceDB invoiceHelper = InvoiceDB.getInstance();
                List<Invoice> invoiceList = invoiceHelper.getAll();
                Toast.makeText(mContext, "Start syncing ...", Toast.LENGTH_SHORT).show();
                for(final Invoice inv : invoiceList) {
                    // Mark local record as updating to prevent repeated upload
                    if(inv.getStatus() != Invoice.INVOICE_UPLOADING) {
                        Log.i("InvoiceHandler", "Uploading invoice: " + inv.getId());
                        inv.setStatus(Invoice.INVOICE_UPLOADING);
                        invoiceHelper.update(inv);
                        RestClient.getInstance(mContext).createInvoice(inv, new RestListener<Invoice>() {
                            @Override
                            public void onSuccess(Invoice invoice) {
                                Toast.makeText(mContext, "Uploaded invoice as #" + invoice.getId(), Toast.LENGTH_SHORT);
                                invoiceHelper.delete(inv.getId());

                                Commons.getBusInstance().register(mContext);
                                Commons.getBusInstance().post(new InvoiceSynchronizedEvent());
                                Commons.getBusInstance().unregister(mContext);
                            }

                            @Override
                            public void onFailure(int status) {
                                inv.setStatus(Invoice.INVOICE_LOCAL);
                                invoiceHelper.update(inv);
                            }
                        });
                    }
                }
            }
        });

        NetworkChangeReceiver.completeWakefulIntent(intent);

    }

}
