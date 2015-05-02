package com.hkust.comp4521.hippos;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.views.InventoryListAdapter;
import com.hkust.comp4521.hippos.views.InventoryListViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class SalesHistoryActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private RelativeLayout mActionBar;
    List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        // Change action bar theme
        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mActionBar));

        // Setup pages of inventories
        LayoutInflater mInflater = getLayoutInflater().from(this);
        viewList = new ArrayList<View>();

        for(int i=0; i < Commons.getCategoryCount(); i++) {
            View v = mInflater.inflate(R.layout.view_inventory_list, null);
            viewList.add(v);
        }

        // Initialize ViewPager
        mViewPager = (ViewPager) findViewById(R.id.vp_sales_history);
        mViewPager.setAdapter(new InventoryListViewPagerAdapter(viewList));
        mViewPager.setCurrentItem(0);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_sales_history);
        tabs.setViewPager(mViewPager);

        // Bind RecyclerView and setup appropriate adapter for each of the page
        for(int i=0; i < Commons.getCategoryCount(); i++) {
            // Setup RecyclerView
            View view = viewList.get(i);
            RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);

            // Setup list adapter
            final int cId = i;
            InventoryListAdapter adapter = new InventoryListAdapter(this, cId);
            adapter.setOnClickListener(new InventoryListAdapter.OnInventoryClickListener() {
                @Override
                public void onClick(View v, int catId, int invId) {
                    // get position of the card
                    Inventory item = Commons.getInventory(catId, invId);
                    Intent intent = new Intent(SalesHistoryActivity.this, InventoryDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Inventory.INVENTORY_CAT_ID, catId);
                    bundle.putInt(Inventory.INVENTORY_INV_ID, invId);
                    intent.putExtras(bundle);
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            SalesHistoryActivity.this,
                            new Pair<View, String>(v.findViewById(R.id.iv_inventory), InventoryDetailsActivity.VIEW_NAME_HEADER_IMAGE),
                            new Pair<View, String>(v.findViewById(R.id.tv_inventory_item_name), InventoryDetailsActivity.VIEW_NAME_HEADER_TITLE)
                    );
                    // Now we can start the Activity, providing the activity options as a bundle
                    ActivityCompat.startActivity(SalesHistoryActivity.this, intent, activityOptions.toBundle());

                }
            });
            recList.setAdapter(adapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sales_history, menu);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, android.R.anim.fade_out);
    }
}
