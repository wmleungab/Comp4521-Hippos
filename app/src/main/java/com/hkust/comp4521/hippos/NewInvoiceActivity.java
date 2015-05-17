package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.Invoice;
import com.hkust.comp4521.hippos.datastructures.InvoiceInventory;
import com.hkust.comp4521.hippos.services.NFCService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.views.InvoiceInventoryListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class NewInvoiceActivity extends AppCompatActivity {

    // Activity-related
    private Activity mActivity;
    private Context mContext;
    private InvoiceInventoryListAdapter adapter;

    // Views
    private RelativeLayout mActionBar;
    private ImageButton btnAddFromInventoryList, btnCompleteSales;
    MaterialDialog mSalesConfirmDialog;

    // Services
    private NFCService mNFC;

    // Data
    private Invoice mInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice);

        mActivity = this;
        mContext = this;
        mNFC = new NFCService(mActivity, mContext);

        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        btnAddFromInventoryList = (ImageButton) findViewById(R.id.ib_new_invoice_add_from_inv_list);
        btnCompleteSales = (ImageButton) findViewById(R.id.ib_new_invoice_complete);
        TintedStatusBar.changeStatusBarColor(mActivity, TintedStatusBar.getColorFromTag(mActionBar));

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        adapter = new InvoiceInventoryListAdapter(mContext, Commons.MODE_NEW_INVOICE);
        recList.setAdapter(adapter);
        recList.setItemAnimator(new DefaultItemAnimator());

        btnAddFromInventoryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, InventoryListActivity.class);
                i.putExtra("selection_mode", true);
                startActivityForResult(i, InventoryListActivity.MODE_SELECT_INVENTORY);
            }
        });
        btnCompleteSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate view for dialog
                LayoutInflater mInflater = getLayoutInflater().from(mContext);
                View vv = mInflater.inflate(R.layout.dialog_sales_confirm, null);
                mSalesConfirmDialog =  new MaterialDialog.Builder(mActivity)
                                        .customView(vv, false)
                                        .positiveText("Confirm")
                                        .negativeText("Cancel")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                            }
                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                            }
                                        })
                                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == InventoryListActivity.MODE_SELECT_INVENTORY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                Inventory inv = Commons.getInventory(b.getInt("category_id"), b.getInt("inventory_id"));
                adapter.addItem(new InvoiceInventory(inv, 1));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_invoice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // NFC intent received actions
    @Override
    protected void onNewIntent(Intent intent) {
        if(mNFC.discoverTag(intent) != null) {
            mNFC.readTag(new NFCService.NFCReadTagListener() {
                @Override
                public void onTagRead(String readStr) {
                    //Toast.makeText(mContext, "str: " + readStr , Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject nfcJSON = new JSONObject(readStr);
                        //Container retrievedContainer = containerHM.get(nfcJSON.getInt("container_id"));
                        Toast.makeText(mContext, readStr, Toast.LENGTH_SHORT).show();
                        Inventory inv = Commons.getInventory(nfcJSON.getInt("category_id"), nfcJSON.getInt("inventory_id"));
                        adapter.addItem(new InvoiceInventory(inv, 1));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(mContext, "NFC tag is invalid", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onError() {
                    Toast.makeText(mContext, "NFC tag is invalid", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mNFC.WriteModeOff();
        overridePendingTransition(R.anim.none, android.R.anim.fade_out);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNFC.WriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        mNFC.WriteModeOn();
    }
}
