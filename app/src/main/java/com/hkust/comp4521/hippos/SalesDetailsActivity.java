package com.hkust.comp4521.hippos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.nineoldandroids.view.ViewHelper;


public class SalesDetailsActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private View mFlexibleSpaceView;
    private View mToolbarView;
    private TextView mTitleView, mSubTitleView;
    private int mFlexibleSpaceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFlexibleSpaceView = findViewById(R.id.flexible_space);
        mTitleView = (TextView) findViewById(R.id.tv_sales_details_title);
        mSubTitleView = (TextView) findViewById(R.id.tv_sales_details_subtitle);
        mTitleView.setText("Invoice #001");
        mSubTitleView.setText("Date: May 26th, 2015\nHandled by: Wyman Leung");
        setTitle(null);
        mToolbarView = findViewById(R.id.toolbar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mToolbarView));

        final ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height);
        int flexibleSpaceAndToolbarHeight = mFlexibleSpaceHeight + getSupportActionBar().getHeight();

        findViewById(R.id.body).setPadding(0, flexibleSpaceAndToolbarHeight, 0, 0);
        mFlexibleSpaceView.getLayoutParams().height = flexibleSpaceAndToolbarHeight;

        ScrollUtils.addOnGlobalLayoutListener(mTitleView, new Runnable() {
            @Override
            public void run() {
                updateFlexibleSpaceText(scrollView.getCurrentScrollY());
            }
        });
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