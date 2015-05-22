package com.hkust.comp4521.hippos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.Invoice;
import com.hkust.comp4521.hippos.datastructures.InvoiceInventory;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.utils.ImageRetriever;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;


public class SalesDetailsActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private Context mContext;

    private View mFlexibleSpaceView;
    private View mToolbarView;
    private TextView mTitleView, mSubTitleView;
    private int mFlexibleSpaceHeight;
    private ImageButton btnBack, btnShare, btnQR;

    private Invoice currentInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_details);

        mContext = this;

        // Init views
        mFlexibleSpaceView = findViewById(R.id.flexible_space);
        mTitleView = (TextView) findViewById(R.id.tv_sales_details_title);
        mSubTitleView = (TextView) findViewById(R.id.tv_sales_details_subtitle);
        btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnQR = (ImageButton) findViewById(R.id.ib_sales_details_qr);
        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnShare = (ImageButton) findViewById(R.id.ib_sales_details_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String companyName =  PreferenceService.getStringValue(mContext.getString(R.string.company_name_prefs));

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, currentInvoice.getEmail());
                intent.putExtra(Intent.EXTRA_SUBJECT,  mContext.getString(R.string.email_subject_text)+companyName);
                intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.email_text_msg)+ RestClient.SERVER_URL+RestClient.SERVER_RECEIPT+currentInvoice.getId());

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        // get information from previous activity (for retrieving sales info)
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            int invId = bundle.getInt(Inventory.INVENTORY_INV_ID);
            currentInvoice = Commons.getRemoteInvoice(invId);

            // Setup views
            mTitleView.setText(getResources().getString(R.string.invoice_id_text) + currentInvoice.getId());
            mSubTitleView.setText(getResources().getString(R.string.sales_details_invoice_date) + currentInvoice.getDateTime()+ "\n"
                                 + getResources().getString(R.string.sales_details_invoice_handled_by) + Commons.getUser().name);
        }

        //mTitleView.setText("Invoice #001");
        //mSubTitleView.setText("Date: May 26th, 2015\nHandled by: Wyman Leung");
        setTitle(null);

        // Change status bar color
        mToolbarView = findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mToolbarView));

        final ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height);
        int flexibleSpaceAndToolbarHeight = mFlexibleSpaceHeight + mTitleView.getHeight();

        findViewById(R.id.body).setPadding(0, flexibleSpaceAndToolbarHeight, 0, 0);
        mFlexibleSpaceView.getLayoutParams().height = flexibleSpaceAndToolbarHeight;

        ScrollUtils.addOnGlobalLayoutListener(mTitleView, new Runnable() {
            @Override
            public void run() {
                updateFlexibleSpaceText(scrollView.getCurrentScrollY());
            }
        });

        LinearLayout llInvoiceItems = (LinearLayout) findViewById(R.id.ll_sales_details_invoice_items);
        List<InvoiceInventory> invList = currentInvoice.getInvoiceInventories();
        for(int i=0; i < invList.size(); i++) {
            InvoiceInventory ci = invList.get(i);
            Inventory inv = ci.getInventory();

            // set views accordingly
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_sales_details, null, false);
            TextView tv = (TextView) itemView.findViewById(R.id.tv_inventory_item_name);
            tv.setText(inv.getName());
            tv = (TextView) itemView.findViewById(R.id.tv_card_inventory_item_price);
            tv.setText(inv.getFormattedPrice());
            tv = (TextView) itemView.findViewById(R.id.tv_card_inventory_item_stock);
            tv.setText("x" + ci.getQuantity());
            ImageView heroImage = (ImageView) itemView.findViewById(R.id.iv_inventory);
            new ImageRetriever(heroImage, inv.getImage(), getResources().getDrawable(R.mipmap.placeholder)).execute();
            Log.i("SalesDetails", inv.getName() + " inflated");
            llInvoiceItems.addView(itemView);
        }
        // Adding padding item
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_sales_details, null, false);
        itemView.setVisibility(View.INVISIBLE);
        llInvoiceItems.addView(itemView);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        updateFlexibleSpaceText(scrollY);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void updateFlexibleSpaceText(final int scrollY) {
        ViewHelper.setTranslationY(mFlexibleSpaceView, -scrollY);
        int adjustedScrollY = scrollY;
        int heightCompensation = mTitleView.getHeight();
        int flexibleSpacetoToolBarRange = mFlexibleSpaceHeight - mToolbarView.getHeight();
        if (scrollY < 0) {
            adjustedScrollY = 0;
        } else if (mFlexibleSpaceHeight < scrollY) {
            adjustedScrollY = mFlexibleSpaceHeight;
        }

        float maxScale = 1.2f;
        // Scale of Title: from 1.2f to 0.0f, 0.0f reached when flexible space is collapsed
        float scale = Math.max(maxScale * ((float) flexibleSpacetoToolBarRange - adjustedScrollY) / flexibleSpacetoToolBarRange, 0.0f);

        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, 1 + scale);
        ViewHelper.setScaleY(mTitleView, 1 + scale);
        ViewHelper.setPivotX(mSubTitleView, 0);
        ViewHelper.setPivotY(mSubTitleView, 0);
        ViewHelper.setScaleX(mSubTitleView, 1);
        ViewHelper.setScaleY(mSubTitleView, 1);

        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding_top);
        int maxTitleTranslationY = mToolbarView.getHeight() + mTitleView.getHeight() - 2*padding;
        float heightScale = scale / maxScale;

        int titleTranslationY = (int) (maxTitleTranslationY * heightScale);
        ViewHelper.setTranslationY(mSubTitleView, titleTranslationY - 2*padding);
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
        ViewHelper.setAlpha(mSubTitleView, heightScale);
    }

}