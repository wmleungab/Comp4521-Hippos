package com.hkust.comp4521.hippos;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.hkust.comp4521.hippos.services.ThreadService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;

import at.markushi.ui.RevealColorView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    // Views
    private RevealColorView revealColorView;
    private View selectedView;

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

        setupGCMButtons();
    }

    private void setupGCMButtons() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RestClient.getInstance().registerGCM(PreferenceService.getStringValue(PreferenceService.KEY_GCM_REGISTRATION_ID)) == true) {
                    Toast.makeText(mContext, "GCM registered!", Toast.LENGTH_SHORT).show();
                };
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestClient.getInstance().sendGCM(1, true, false);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestClient.getInstance().sendGCM(2, false, true);
            }
        });
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
}
