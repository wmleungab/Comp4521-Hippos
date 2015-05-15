package com.hkust.comp4521.hippos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.InvoiceInventory;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.views.InvoiceInventoryListAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewInvoiceActivity extends AppCompatActivity {

    private RelativeLayout mActionBar;
    private ImageButton btnAddFromInventoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice);

        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        btnAddFromInventoryList = (ImageButton) findViewById(R.id.ib_new_invoice_add_from_inv_list);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mActionBar));

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        final InvoiceInventoryListAdapter adapter = new InvoiceInventoryListAdapter(this, Commons.MODE_NEW_INVOICE);
        recList.setAdapter(adapter);
        recList.setItemAnimator(new DefaultItemAnimator());

        btnAddFromInventoryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Inventory inv = Commons.getRandomInventory();
                adapter.addItem(new InvoiceInventory(inv, 1));
            }
        });
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

    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, android.R.anim.fade_out);
    }
}
