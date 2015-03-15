package com.hkust.comp4521.hippos;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.GridLayout;

import com.hkust.comp4521.hippos.services.ThreadService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;

import at.markushi.ui.RevealColorView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    private RevealColorView revealColorView;
    private View selectedView;
    private GridLayout buttonGridLayout;
    private int backgroundColor;

    private Context mContext;
    private boolean subActivityLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initViews();
    }

    private void initViews() {
        backgroundColor = Color.parseColor("#212121");
        revealColorView = (RevealColorView) findViewById(R.id.revealview_main_bg);
        buttonGridLayout = (GridLayout) findViewById(R.id.gl_main_buttons);

        findViewById(R.id.btn_main_newinvoice).setOnClickListener(this);
        findViewById(R.id.btn_main_invlist).setOnClickListener(this);
        findViewById(R.id.btn_main_saleshistory).setOnClickListener(this);
        findViewById(R.id.btn_main_settings).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        final int color = TintedStatusBar.getColorFromTag(v);
        final Point p = getLocationInView(revealColorView, v);

        if (selectedView == v) {
            revealColorView.hide(p.x, p.y, backgroundColor, 0, 300, null);
            selectedView = null;
            applyFadeAnimation(buttonGridLayout, 1);
            TintedStatusBar.changeStatusBarColor(this, 0);
        } else {
            revealColorView.reveal(p.x, p.y, color, v.getHeight() / 2, 340, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // launch actual activity
                    Intent i = null;
                    switch(v.getId()) {
                        case R.id.btn_main_newinvoice:
                            i = new Intent(mContext, NewInvoiceActivity.class);
                            break;
                        default:
                            break;
                    }
                    if(i != null) {
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, R.anim.none);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            selectedView = v;
            applyFadeAnimation(buttonGridLayout, 0);
            TintedStatusBar.changeStatusBarColor(this, color);
            subActivityLaunched = true;
        }
    }

    public void applyFadeAnimation(View view, float fade) {
        AlphaAnimation alpha = new AlphaAnimation(1 - fade, fade);
        alpha.setDuration(300);
        alpha.setFillAfter(true);
        view.startAnimation(alpha);
    }

    private Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    protected void onResume() {
        super.onResume();

        // Withdraw animation
        if (subActivityLaunched) {
            ThreadService.delayedStart(this, new Runnable() {
                @Override
                public void run() {
                    if(selectedView != null) {
                        final Point p = getLocationInView(revealColorView, selectedView);
                        revealColorView.hide(p.x, p.y, backgroundColor, 0, 300, null);
                        selectedView = null;
                        applyFadeAnimation(buttonGridLayout, 1);
                        TintedStatusBar.changeStatusBarColor(MainActivity.this, 0);
                        subActivityLaunched = false;
                    }
                }
            }, 600);
        }
    }


    @Override
    public void onBackPressed() {
        if (selectedView != null) {
            final Point p = getLocationInView(revealColorView, selectedView);
            revealColorView.hide(p.x, p.y, backgroundColor, 0, 300, null);
            selectedView = null;
            applyFadeAnimation(buttonGridLayout, 1);
            TintedStatusBar.changeStatusBarColor(this, 0);
        } else {
            super.onBackPressed();
        }
    }
}
