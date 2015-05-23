package com.hkust.comp4521.hippos.utils;

/**
 * Created by TC on 5/19/2015.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.events.InvoiceSynchronizedEvent;

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


        Commons.getBusInstance().register(mContext);
        Commons.getBusInstance().post(new InvoiceSynchronizedEvent());
        Commons.getBusInstance().unregister(mContext);

        NetworkChangeReceiver.completeWakefulIntent(intent);

    }

}
