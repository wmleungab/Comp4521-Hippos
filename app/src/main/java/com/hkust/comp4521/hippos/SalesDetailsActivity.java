package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;


public class SalesDetailsActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private View mFlexibleSpaceView;
    private View mToolbarView;
    private TextView mTitleView;
    private int mFlexibleSpaceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFlexibleSpaceView = findViewById(R.id.flexible_space);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getTitle());
        setTitle(null);
        mToolbarView = findViewById(R.id.toolbar);

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
        if (scrollY < 0) {
            adjustedScrollY = 0;
        } else if (mFlexibleSpaceHeight < scrollY) {
            adjustedScrollY = mFlexibleSpaceHeight;
        }
        float maxScale = (float) (mFlexibleSpaceHeight - mToolbarView.getHeight()) / mToolbarView.getHeight();
        //float scale = maxScale * ((float) mFlexibleSpaceHeight - adjustedScrollY) / mFlexibleSpaceHeight;
        float scale = 0f;

        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, 1 + scale);
        ViewHelper.setScaleY(mTitleView, 1 + scale);
        /*
        int maxTitleTranslationY = mToolbarView.getHeight() + mFlexibleSpaceHeight - (int) (mTitleView.getHeight() * (1 + scale));
        int titleTranslationY = (int) (maxTitleTranslationY * ((float) mFlexibleSpaceHeight - adjustedScrollY) / mFlexibleSpaceHeight);*/

        int maxTitleTranslationY = mToolbarView.getHeight() + mFlexibleSpaceHeight - mTitleView.getHeight();
        float heightScale = (float) (mFlexibleSpaceHeight - adjustedScrollY) / mFlexibleSpaceHeight;
        /*Log.wtf("SalesDetails", "mFlexibleSpaceHeight: " + mFlexibleSpaceHeight);
        Log.wtf("SalesDetails", "mToolbarView.getHeight(): " + mToolbarView.getHeight());
        Log.wtf("SalesDetails", "adjustedScrollY: " + adjustedScrollY);*/
        if(adjustedScrollY > mFlexibleSpaceHeight - mToolbarView.getHeight()) {
            heightCompensation = 0;
            heightScale = 0;
        }
        int titleTranslationY = (int) (maxTitleTranslationY * heightScale - heightCompensation);
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
    }
}