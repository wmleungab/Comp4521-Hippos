package com.hkust.comp4521.hippos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.gcm.InventoryInfoChangedEvent;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.views.InventoryListAdapter;
import com.hkust.comp4521.hippos.views.ViewPagerAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class InventoryListActivity extends AppCompatActivity {

    // Mode Flag
    public static int MODE_NORMAL = 0;
    public static int MODE_SELECT_INVENTORY = 1;
    private int currentMode = MODE_NORMAL;

    // Views
    private RecyclerView recList;
    private ViewPager mViewPager;
    private RelativeLayout mActionBar;
    private ImageButton btnAddInventory;

    // Data
    private InventoryListAdapter adapter;
    List<View> viewList;
    List<InventoryListAdapter> adapterList = new ArrayList<InventoryListAdapter>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);

        // change onclicklistener behaviour for different mode
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Boolean modeChange = extras.getBoolean("selection_mode");
            if(modeChange) {
                currentMode = MODE_SELECT_INVENTORY;
            }
        }

        // Change action bar theme
        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mActionBar));

        // Add inventory button
        btnAddInventory = (ImageButton) findViewById(R.id.ib_inventory_list_add_item);
        btnAddInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryListActivity.this, EditInventoryActivity.class);
                startActivity(intent);
            }
        });

        // Initialize Inventory List
        setupList();
    }

    private void setupList() {
        // Setup pages of inventories
        LayoutInflater mInflater = getLayoutInflater().from(this);
        viewList = new ArrayList<View>();

        for(int i=0; i < Commons.getCategoryCount(); i++) {
            View v = mInflater.inflate(R.layout.view_inventory_list, null);
            viewList.add(v);
        }

        // Initialize ViewPager
        mViewPager = (ViewPager) findViewById(R.id.vp_inventory_list);
        mViewPager.setAdapter(new ViewPagerAdapter(viewList, Commons.getCategoryTabs()));
        mViewPager.setCurrentItem(0);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_inventory_list);
        tabs.setViewPager(mViewPager);

        // Bind RecyclerView and setup appropriate adapter for each of the page
        for(int i=0; i < Commons.getCategoryCount(); i++) {
            // Setup RecyclerView
            View view = viewList.get(i);
            recList = (RecyclerView) view.findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);

            // Setup list adapter
            final int cId = Commons.getCategory(i).getID();
            adapter = new InventoryListAdapter(this, cId, i);
            adapter.setOnClickListener(new InventoryListAdapter.OnInventoryClickListener() {
                @Override
                public void onClick(View v, int catId, int invIndex) {
                    // get position of the card
                    Inventory item = Commons.getInventoryFromIndex(catId, invIndex);
                    if(currentMode == MODE_NORMAL) {
                        Intent intent = new Intent(InventoryListActivity.this, InventoryDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Inventory.INVENTORY_INV_ID, item.getId());
                        if(item.getImage() != null && !item.getImage().equals(""))
                            InventoryDetailsActivity.itemIndex = invIndex;
                        else
                            InventoryDetailsActivity.itemIndex = -1;
                        intent.putExtras(bundle);
                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                InventoryListActivity.this,
                                new Pair<View, String>(v.findViewById(R.id.iv_inventory), InventoryDetailsActivity.VIEW_NAME_HEADER_IMAGE),
                                new Pair<View, String>(v.findViewById(R.id.tv_inventory_item_name), InventoryDetailsActivity.VIEW_NAME_HEADER_TITLE)
                        );
                        // Now we can start the Activity, providing the activity options as a bundle
                        ActivityCompat.startActivity(InventoryListActivity.this, intent, activityOptions.toBundle());
                    } else if(currentMode == MODE_SELECT_INVENTORY) {
                        Intent i=new Intent();
                        Bundle b=new Bundle();
                        b.putInt(Inventory.INVENTORY_INV_ID, item.getId());
                        i.putExtras(b);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                }
            });
            recList.setAdapter(adapter);
            adapterList.add(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update view holder contents when back from another activity
        // Remember to update from according adapter
        InventoryListAdapter adapter = adapterList.get(mViewPager.getCurrentItem());
        Inventory inv = null;
        if(InventoryDetailsActivity.itemIndex != -1)
            inv = adapter.getInventoryList().get(InventoryDetailsActivity.itemIndex);
        if(adapter != null && inv != null && inv.getStatus() == Inventory.INVENTORY_DIRTY) {
            adapter.notifyItemChanged(InventoryDetailsActivity.itemIndex);
            inv.setStatus(Inventory.INVENTORY_NORMAL);
        }
        // Register for bus
        Commons.getBusInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Always unregister when an object no longer should be on the bus.
        Commons.getBusInstance().unregister(this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, android.R.anim.fade_out);

        // Clear item index
        InventoryDetailsActivity.itemIndex = -1;
    }

    @Subscribe
    public void onInventoryInfoChanged(InventoryInfoChangedEvent event) {
        Toast.makeText(this, "Bus test!", Toast.LENGTH_SHORT).show();

        // Update inventory view from bus message
        if(adapter != null && event.getInventory() != null) {
            Inventory inv = event.getInventory();
            InventoryListAdapter adapter = adapterList.get(inv.getCatIndex());
            adapter.notifyItemChanged(inv.getInvIndex());
            inv.setStatus(Inventory.INVENTORY_NORMAL);
        }
    }

}
