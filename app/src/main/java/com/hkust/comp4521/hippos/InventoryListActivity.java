package com.hkust.comp4521.hippos;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.events.InventoryInfoChangedEvent;
import com.hkust.comp4521.hippos.services.ThreadService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.views.InventoryListAdapter;
import com.hkust.comp4521.hippos.views.ViewPagerAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class InventoryListActivity extends AppCompatActivity {

    // Activity
    private Context mContext;

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

    // Bus
    private boolean busRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);

        mContext = this;

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
        Commons.recoverLoginInformation(mContext, new Commons.onInitializedListener() {
            @Override
            public void onInitialized() {
                Commons.initializeInventoryList(new Commons.onInitializedListener() {
                    @Override
                    public void onInitialized() {
                        setupList();
                    }
                });
            }
        });
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
                        // Launch inventory detail activity
                        Intent intent = new Intent(InventoryListActivity.this, InventoryDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Inventory.INVENTORY_INV_ID, item.getId());
                        intent.putExtras(bundle);
                        // Add scene transition for Lolipop devices
                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                InventoryListActivity.this,
                                new Pair<View, String>(v.findViewById(R.id.iv_inventory), InventoryDetailsActivity.VIEW_NAME_HEADER_IMAGE),
                                new Pair<View, String>(v.findViewById(R.id.tv_inventory_item_name), InventoryDetailsActivity.VIEW_NAME_HEADER_TITLE)
                        );
                        if(Build.VERSION.SDK_INT >= 21) {
                            TimeInterpolator interpolator = AnimationUtils.loadInterpolator(mContext, android.R.interpolator.fast_out_slow_in);
                            Window window = getWindow();
                            window.getSharedElementEnterTransition().setInterpolator(interpolator);
                            window.getSharedElementExitTransition().setInterpolator(interpolator);
                            window.getSharedElementReenterTransition().setInterpolator(interpolator);
                            window.getSharedElementReturnTransition().setInterpolator(interpolator);
                        }
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
        // Register for bus
        if(busRegistered == false) {
            Commons.getBusInstance().register(this);
            busRegistered = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, android.R.anim.fade_out);

        // Clear item index
        InventoryDetailsActivity.itemIndex = -1;

        // Always unregister when an object no longer should be on the bus.
        if(busRegistered == true) {
            Commons.getBusInstance().unregister(this);
            busRegistered = false;
        }
    }

    @Subscribe
    public void onInventoryInfoChanged(final InventoryInfoChangedEvent event) {
        // Update inventory view from bus message
        if(event.refreshAll == true) {
            Log.i("onInventoryInfoChanged", "refreshAll");
            // notify all adapters that info changed
            for(InventoryListAdapter adapter : adapterList) {
                adapter.setInventoryList(Commons.getInventoryList(adapter.getCategoryId()));
                adapter.notifyDataSetChanged();
            }
        }
        Log.i("onInventoryInfoChanged", "onInventoryInfoChanged()");
        if(adapter != null && event.getInventory() != null) {
            final Inventory inv = event.getInventory();
            final InventoryListAdapter adapter = adapterList.get(inv.getCatIndex());
            ThreadService.delayedStart(InventoryListActivity.this, new Runnable() {
                @Override
                public void run() {
                    Log.i("onInventoryInfoChanged", "Reloading image " + inv.getName());
                    adapter.notifyItemChanged(inv.getInvIndex());
                }
            }, 300);
        }
    }

}
