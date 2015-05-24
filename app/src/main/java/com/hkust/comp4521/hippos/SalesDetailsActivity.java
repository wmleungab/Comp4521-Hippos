package com.hkust.comp4521.hippos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.Invoice;
import com.hkust.comp4521.hippos.datastructures.InvoiceInventory;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.utils.ImageRetriever;
import com.hkust.comp4521.hippos.utils.QRCodeEncoder;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;


public class SalesDetailsActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private Context mContext;

    private View mFlexibleSpaceView;
    private View mToolbarView;
    private TextView mTitleView, mSubTitleView, mItemTotal, mAdjustment, mGrandTotal, mPaid, mChanges;
    private int mFlexibleSpaceHeight;
    private ImageButton btnBack, btnShare, btnQR;

    private static Invoice currentInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_details);

        mContext = this;

        // get information from previous activity (for retrieving sales info)
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            int invId = bundle.getInt(Inventory.INVENTORY_INV_ID);
            currentInvoice = Commons.getRemoteInvoice(invId);
        }

        // Init views
        mFlexibleSpaceView = findViewById(R.id.flexible_space);
        mTitleView = (TextView) findViewById(R.id.tv_sales_details_title);
        mSubTitleView = (TextView) findViewById(R.id.tv_sales_details_subtitle);
        mItemTotal = (TextView) findViewById(R.id.tv_sales_details_item_total);
        mAdjustment = (TextView) findViewById(R.id.tv_sales_details_adjustment);
        mGrandTotal = (TextView) findViewById(R.id.tv_sales_details_grand_total);
        mPaid = (TextView) findViewById(R.id.tv_sales_details_paid);
        mChanges = (TextView) findViewById(R.id.tv_sales_details_changes);
        mItemTotal.setText(currentInvoice.getFormattedTotalPrice());
        mAdjustment.setText(currentInvoice.getFormattedAdjustment());
        mGrandTotal.setText(currentInvoice.getFormattedFinalPrice());
        mPaid.setText(currentInvoice.getFormattedPaid());
        mChanges.setText(currentInvoice.getFormattedChange());
        btnQR = (ImageButton) findViewById(R.id.ib_sales_details_qr);
        btnShare = (ImageButton) findViewById(R.id.ib_sales_details_share);
        if(currentInvoice.getStatus() == Invoice.INVOICE_REMOTE) {
            btnQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_qrcode_layout, null);
                    int id = currentInvoice.getId();
                    String link = RestClient.SERVER_URL + RestClient.SERVER_RECEIPT + id;
                    //Encode with a QR Code image
                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(link,
                            null,
                            Contents.Type.TEXT,
                            BarcodeFormat.QR_CODE.toString(),
                            getResources().getDimensionPixelSize(R.dimen.qr_code_dimension));
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                        ImageView myImage = (ImageView) dialogView.findViewById(R.id.qr_dialog_image);
                        myImage.setImageBitmap(bitmap);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.QR_Dialog_title)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setView(dialogView)
                            .show();
                }
            });
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String companyName =  PreferenceService.getStringValue(mContext.getString(R.string.company_name_prefs));

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, currentInvoice.getEmail());
                    intent.putExtra(Intent.EXTRA_SUBJECT,  mContext.getString(R.string.email_subject_text)+companyName);
                    intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.email_text_msg)+ RestClient.SERVER_URL+RestClient.SERVER_RECEIPT+currentInvoice.getId());

                    startActivity(Intent.createChooser(intent, getString(R.string.sales_details_shared_via)));
                }
            });
        } else {
            btnQR.setVisibility(View.GONE);
            btnShare.setVisibility(View.GONE);
        }

        btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Setup views
        mTitleView.setText(getResources().getString(R.string.invoice_id_text) + currentInvoice.getId());
        mSubTitleView.setText(getResources().getString(R.string.sales_details_invoice_date) + currentInvoice.getDateTime()+ "\n"
                + getResources().getString(R.string.sales_details_invoice_handled_by) + Commons.getUser().name);
        setTitle(null);

        // Change status bar color
        mToolbarView = findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mToolbarView));


        // Setup for dynamic header
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

        // Setup invoice item list to LinearLayout
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

        // Adding padding items
        if(invList.size() < 6) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_sales_details, null, false);
            itemView.setVisibility(View.INVISIBLE);
            llInvoiceItems.addView(itemView);
            itemView = LayoutInflater.from(this).inflate(R.layout.item_sales_details, null, false);
            itemView.setVisibility(View.INVISIBLE);
            llInvoiceItems.addView(itemView);
        }
    }

    // For initializing from invoice object
    public static void setCurrentInvoice(Invoice currentInvoice) {
        SalesDetailsActivity.currentInvoice = currentInvoice;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        updateFlexibleSpaceText(scrollY);
    }

    // Update dynamic header dimension
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

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}