package com.hkust.comp4521.hippos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.InventoryRevenue;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.utils.StatisticsUtils;
import com.hkust.comp4521.hippos.views.InventoryRevenueListAdapter;
import com.hkust.comp4521.hippos.views.InvoiceListAdapter;
import com.hkust.comp4521.hippos.views.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


public class SalesHistoryActivity extends AppCompatActivity {

    // Application
    private Context mContext;

    // Views
    private ViewPager mViewPager;
    private RelativeLayout mActionBar;
    private SwipeRefreshLayout mRefreshLayout;
    private LineChartView chartTop;
    private ColumnChartView chartBottom;
    private RecyclerView localRecList, remoteRecList, revenueRecList;

    // Chart data
    private LineChartData lineData;
    private ColumnChartData columnData;

    // Data
    List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        mContext = this;

        // Change action bar theme
        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mActionBar));

        // Setup pages of inventories
        LayoutInflater mInflater = getLayoutInflater().from(this);
        viewList = new ArrayList<View>();
        View v = mInflater.inflate(R.layout.view_invoice, null);
        viewList.add(v);
        v = mInflater.inflate(R.layout.view_invoice, null);
        viewList.add(v);
        v = mInflater.inflate(R.layout.view_statistics, null);
        viewList.add(v);
        v = mInflater.inflate(R.layout.view_revenue, null);
        viewList.add(v);

        // Initialize ViewPager
        mViewPager = (ViewPager) findViewById(R.id.vp_sales_history);
        mViewPager.setAdapter(new ViewPagerAdapter(viewList, Commons.getSalesHistoryTabs(this)));
        mViewPager.setCurrentItem(0);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_sales_history);
        tabs.setViewPager(mViewPager);

        // setup data for each page
        setupLocalInvoicePage();
        setupRemoteInvoicePage();
        setupRevenuePage();

    }

    private void setupLocalInvoicePage() {
        View view = viewList.get(0);

        // initialize RecyclerView
        localRecList = (RecyclerView) view.findViewById(R.id.cardList);
        localRecList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        localRecList.setLayoutManager(llm);

        // initialize pull-to-refresh listener
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        // retrieve invoice from server
        mRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshLocalInvoiceItems();
            }
        });
        mRefreshLayout.setRefreshing(true);
        refreshLocalInvoiceItems();
    }

    private void refreshLocalInvoiceItems() {
        Commons.initializeLocalInvoiceList();
        // setup list adapter
        InvoiceListAdapter adapter = new InvoiceListAdapter(SalesHistoryActivity.this, Commons.getLocalInvoiceList());
        adapter.setOnClickListener(new InvoiceListAdapter.OnInvoiceClickListener() {
            @Override
            public void onClick(View v, int invIndex) {
                /*Intent i = new Intent(SalesHistoryActivity.this, SalesDetailsActivity.class);
                                Bundle b = new Bundle();
                                b.putInt(Inventory.INVENTORY_INV_ID, invIndex);
                                i.putExtras(b);
                                startActivity(i);*/
            }
        });
        localRecList.setAdapter(adapter);
    }

    private void setupRevenuePage() {
        View view = viewList.get(3);

        // initialize RecyclerView
        revenueRecList = (RecyclerView) view.findViewById(R.id.cardList);
        revenueRecList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        revenueRecList.setLayoutManager(llm);

        RestClient.getInstance().getRevenueList(new RestListener<List<InventoryRevenue>>() {
            @Override
            public void onSuccess(List<InventoryRevenue> inventoryRevenues) {
                // setup list adapter
                InventoryRevenueListAdapter adapter = new InventoryRevenueListAdapter(SalesHistoryActivity.this, inventoryRevenues);
                revenueRecList.setAdapter(adapter);
            }

            @Override
            public void onFailure(int status) {

            }
        });
    }

    private void setupStatisticsPage() {
        View view = viewList.get(2);

        // Generate and set data for line chart
        chartTop = (LineChartView) view.findViewById(R.id.chart_top);
        generateInitialWeekData();
        chartBottom = (ColumnChartView) view.findViewById(R.id.chart_bottom);
        generateMonthData();
    }

    private void setupRemoteInvoicePage() {
        View view = viewList.get(1);

        // initialize RecyclerView
        remoteRecList = (RecyclerView) view.findViewById(R.id.cardList);
        remoteRecList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        remoteRecList.setLayoutManager(llm);

        // initialize pull-to-refresh listener
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        // retrieve invoice from server
        mRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshRemoteInvoiceItems();
            }
        });
        mRefreshLayout.setRefreshing(true);
        refreshRemoteInvoiceItems();

    }

    private void refreshRemoteInvoiceItems() {
        Commons.initializeInvoiceList(new Commons.onInitializedListener() {
            @Override
            public void onInitialized() {
                // setup list adapter
                InvoiceListAdapter adapter = new InvoiceListAdapter(SalesHistoryActivity.this, Commons.getRemoteInvoiceList());
                adapter.setOnClickListener(new InvoiceListAdapter.OnInvoiceClickListener() {
                    @Override
                    public void onClick(View v, int invIndex) {
                        Intent i = new Intent(SalesHistoryActivity.this, SalesDetailsActivity.class);
                        Bundle b = new Bundle();
                        b.putInt(Inventory.INVENTORY_INV_ID, invIndex);
                        i.putExtras(b);
                        startActivity(i);
                    }
                });
                remoteRecList.setAdapter(adapter);
                // Stop refresh animation
                mRefreshLayout.setRefreshing(false);
                setupStatisticsPage();
            }
        });
    }

    private void generateMonthData() {
        // Generate data from StatisticsUtils
        int[] monthlyDP = StatisticsUtils.getMonthlyDataPoints();
        int numColumns = StatisticsUtils.MONTHS.length;

        // Setup x-axis of the chart
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {
            // add the only bar "subcolumn" value to the column and set properties
            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue((float) monthlyDP[i], ChartUtils.pickColor()));
            axisValues.add(new AxisValue(i).setLabel(StatisticsUtils.MONTHS[i]));
            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        // setup bar chart properties
        columnData = new ColumnChartData(columns);
        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));
        chartBottom.setColumnChartData(columnData);

        // Set value touch listener that will trigger changes for chartTop.
        chartBottom.setOnValueTouchListener(new ValueTouchListener());

        // Set selection mode to keep selected month column highlighted.
        // also disable zooming
        Viewport v = new Viewport(-1, StatisticsUtils.getMaximumMonthValue(), numColumns, 0);
        chartBottom.setMaximumViewport(v);
        chartBottom.setCurrentViewport(v);
        chartBottom.setValueSelectionEnabled(true);
        chartBottom.setZoomEnabled(false);
    }

    /**
     * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
     * will select value on column chart.
     */
    private void generateInitialWeekData() {
        int numValues = StatisticsUtils.DAYS.length;

        // Setup for x-axis of the chart
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, 0));
            axisValues.add(new AxisValue(i).setLabel(StatisticsUtils.DAYS[i]));
        }

        // initialize the line
        Line line = new Line(values);
        line.setColor(ChartUtils.DEFAULT_COLOR).setCubic(false);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        // setup line chart properties
        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));
        chartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        // also disable zooming
        Viewport v = new Viewport(0, StatisticsUtils.getMaximumWeekValue(), numValues-1, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);
        chartTop.setZoomEnabled(false);
    }

    private void generateLineData(int color, int fromMonth) {
        // Generate data from StatisticsUtils
        int[] weeklyDP = StatisticsUtils.getWeeklyDataPoints(fromMonth);
        // Cancel last animation if not finished.
        chartTop.cancelDataAnimation();

        // Modify data targets from the only line
        Line line = lineData.getLines().get(0);
        line.setColor(color);
        PointValue value = null;
        for(int i=0; i<line.getValues().size(); i++) {
            value = line.getValues().get(i);
            value.setTarget(value.getX(), (float) weeklyDP[i]);
        }

        // Start new data animation with 300ms duration;
        Viewport v = new Viewport(0, StatisticsUtils.getMaximumMonthValue(), 6, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);
        chartTop.startDataAnimation(300);
    }

    private void resetLineData() {
        // Cancel last animation if not finished.
        chartTop.cancelDataAnimation();

        // Zero out data targets from the only line
        Line line = lineData.getLines().get(0);
        line.setColor(ChartUtils.DEFAULT_COLOR);
        for (PointValue value : line.getValues()) {
            value.setTarget(value.getX(), 0);
        }

        // Start new data animation with 300ms duration;
        chartTop.startDataAnimation(300);
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            generateLineData(value.getColor(), columnIndex);
        }

        @Override
        public void onValueDeselected() {
            resetLineData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, android.R.anim.fade_out);
    }

}
