package com.hkust.comp4521.hippos;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.events.ConnectivitiyChangedEvent;
import com.hkust.comp4521.hippos.services.ThreadService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.squareup.otto.Subscribe;

import at.markushi.ui.RevealColorView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    // Views
    private RevealColorView revealColorView;
    private View selectedView;
    private TextView offlineModeText;

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
        backgroundColor = Color.TRANSPARENT;
        TintedStatusBar.changeStatusBarColor(this, Color.parseColor("#FFFFFF"));
        revealColorView = (RevealColorView) findViewById(R.id.revealview_main_bg);

        findViewById(R.id.btn_main_newinvoice).setOnClickListener(this);
        findViewById(R.id.btn_main_invlist).setOnClickListener(this);
        findViewById(R.id.btn_main_saleshistory).setOnClickListener(this);
        findViewById(R.id.btn_main_settings).setOnClickListener(this);
        //findViewById(R.id.qr_test_btn).setOnClickListener(this);
        offlineModeText = (TextView) findViewById(R.id.tv_main_offline_mode);
        if(Commons.ONLINE_MODE) {
            offlineModeText.setText("");
        } else {
            offlineModeText.setText(getString(R.string.offline_mode_msg));
        }
    }

    @Override
    public void onClick(final View v) {
        final int color = TintedStatusBar.getColorFromTag(v);
        final Point p = getLocationInView(revealColorView, v);

        if (selectedView == v) {
            revealColorView.hide(p.x, p.y, backgroundColor, 0, 300, null);
            selectedView = null;
            TintedStatusBar.changeStatusBarColor(this, backgroundColor);
        } else {
            revealColorView.reveal(p.x, p.y, color, v.getHeight() / 2, 340, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ThreadService.delayedStart(MainActivity.this, new Runnable() {
                        @Override
                        public void run() {
                            TintedStatusBar.changeStatusBarColor(MainActivity.this, color);
                        }
                    }, 100);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // launch actual activity
                    Intent i = null;
                    switch (v.getId()) {
                        case R.id.btn_main_newinvoice:
                            i = new Intent(mContext, NewInvoiceActivity.class);
                            break;
                        case R.id.btn_main_invlist:
                            i = new Intent(mContext, InventoryListActivity.class);
                            break;
                        case R.id.btn_main_saleshistory:
                            i = new Intent(mContext, SalesHistoryActivity.class);
                            break;
                        case R.id.btn_main_settings:
                            i = new Intent(mContext, SettingActivity.class);
                            break;
//                        case R.id.qr_test_btn:
//                            i = new Intent(mContext, SettingActivity.class);
//                            break;
                        default:
                            break;
                    }
                    if (i != null) {
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
            subActivityLaunched = true;
        }
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
                        TintedStatusBar.changeStatusBarColor(MainActivity.this, Color.parseColor("#FFFFFF"));
                        subActivityLaunched = false;
                    }
                }
            }, 600);
        }

        // Register Bus event
        Commons.getBusInstance().register(this);

        // Check for online mode
        if(Commons.ONLINE_MODE) {
            offlineModeText.setText("");
        } else {
            offlineModeText.setText(getString(R.string.offline_mode_msg));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister Bus event
        Commons.getBusInstance().unregister(this);
    }


    @Override
    public void onBackPressed() {
        if (selectedView != null) {
            final Point p = getLocationInView(revealColorView, selectedView);
            revealColorView.hide(p.x, p.y, backgroundColor, 0, 300, null);
            selectedView = null;
            TintedStatusBar.changeStatusBarColor(this, backgroundColor);
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onConnectivityChanged(ConnectivitiyChangedEvent event) {
        // Update online status
        Commons.ONLINE_MODE = event.onlineMode;
        if(Commons.ONLINE_MODE) {
            offlineModeText.setText("");
        } else {
            offlineModeText.setText(getString(R.string.offline_mode_msg));
        }
    }
}
