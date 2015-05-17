package com.hkust.comp4521.hippos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hkust.comp4521.hippos.rest.RestClient;


public class DebugActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        findViewById(R.id.btn_debug_main).setOnClickListener(this);
        findViewById(R.id.btn_debug_newinvoice).setOnClickListener(this);
        findViewById(R.id.btn_debug_salesconfirm).setOnClickListener(this);
        findViewById(R.id.btn_debug_saleshistory).setOnClickListener(this);
        findViewById(R.id.btn_debug_invlist).setOnClickListener(this);
        findViewById(R.id.btn_debug_setting).setOnClickListener(this);
        findViewById(R.id.btn_debug_nfctransfer).setOnClickListener(this);
        findViewById(R.id.btn_debug_invoicetest).setOnClickListener(this);
        findViewById(R.id.btn_debug_categorytest).setOnClickListener(this);
        RestClient.getInstance();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug, menu);
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

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.btn_debug_main:
                i = new Intent(this, MainActivity.class);
                break;
            case R.id.btn_debug_newinvoice:
                i = new Intent(this, NewInvoiceActivity.class);
                break;
            case R.id.btn_debug_salesconfirm:
                i = new Intent(this, SalesDetailsActivity.class);
                break;
            case R.id.btn_debug_saleshistory:
                i = new Intent(this, SalesHistoryActivity.class);
                break;
            case R.id.btn_debug_invlist:
                i = new Intent(this, InventoryListActivity.class);
                break;
            case R.id.btn_debug_nfctransfer:
                i = new Intent(this, InventoryTestActivity.class);
                break;
            case R.id.btn_debug_setting:
                i = new Intent(this, SettingActivity.class);
                break;
            case R.id.btn_debug_invoicetest:
                i = new Intent(this, InvoiceTestActivity.class);
                break;
            case R.id.btn_debug_categorytest:
                i = new Intent(this, CategoryTestActivity.class);
                break;
        }
        if (i != null)
            startActivity(i);
    }
}
