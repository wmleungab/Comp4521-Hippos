package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hkust.comp4521.hippos.services.NFCService;
import com.skyfishjy.library.RippleBackground;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class WiFiTransferActivity extends ActionBarActivity {
    // enum for NFC Transfer stages
    private static int WIFI_SEARCHER = 0;
    private static int WIFI_EMITTER = 1;
    private static int WIFI_TRANSFERRING_SEARCHER = 2;
    private static int WIFI_TRANSFERRING_EMITTER = 3;
    private static int WIFI_COMPLETED = 4;
    private int NFCTransferStage = WIFI_SEARCHER;

    // Context
    private Activity mActivity;

    // View-related
    private SmoothProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctransfer);
        mActivity = this;

        initViews();
    }

    private void initViews() {
        progressBar = (SmoothProgressBar) findViewById(R.id.pb_nfc_transferring);
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        final ImageView imageView=(ImageView)findViewById(R.id.iv_nfc_self);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NFCTransferStage == WIFI_EMITTER) {
                    NFCTransferStage = WIFI_SEARCHER;
                    imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_hippos_grey));
                    scaleView(rippleBackground, 1, 0);
                    //rippleBackground.stopRippleAnimation();
                } else {
                    NFCTransferStage = WIFI_EMITTER;
                    imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_hippos_white));
                    scaleView(rippleBackground, 0, 1);
                    rippleBackground.stopRippleAnimation();
                    rippleBackground.startRippleAnimation();
                }
            }
        });
        findViewById(R.id.btn_nfc_nextstage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NFCTransferStage == WIFI_SEARCHER || NFCTransferStage == WIFI_EMITTER) {
                    transitionToTransferStage();
                } else if(NFCTransferStage == WIFI_TRANSFERRING_SEARCHER || NFCTransferStage == WIFI_TRANSFERRING_EMITTER) {
                    endTransfer();
                }
            }
        });
    }

    private void transitionToTransferStage() {
        // Animate for hippos, then show progress bar
        float xShift = -getDeviceWidth()/2.0f * 0.65f;
        if(NFCTransferStage == WIFI_SEARCHER) {
            xShift *= -1;
            NFCTransferStage = WIFI_TRANSFERRING_SEARCHER;
        }
        // but if self-emitting, kill off the ripple animation first
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        final ImageView imageView=(ImageView)findViewById(R.id.iv_nfc_self);
        if (NFCTransferStage == WIFI_EMITTER) {
            NFCTransferStage = WIFI_TRANSFERRING_EMITTER;
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_hippos_grey));
            scaleView(rippleBackground, 1, 0);
        }
        final ImageView imageView2=(ImageView)findViewById(R.id.iv_nfc_opponent);
        TranslateAnimation animation = new TranslateAnimation(0.0f, xShift, 0.0f, 0.0f);
        animation.setDuration(300);  // animation duration
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startTransfer();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animation);  // start animation
        TranslateAnimation animation2 = new TranslateAnimation(0.0f, -xShift, 0.0f, 0.0f);
        animation2.setDuration(300);  // animation duration
        animation2.setFillAfter(true);
        imageView2.startAnimation(animation2);  // start animation
    }

    private void startTransfer() {
        // Completely kill off ripple background
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.stopRippleAnimation();
        // Change progress bar params first
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        params.width = (int) (getDeviceWidth() * 0.4f);
        progressBar.setLayoutParams(params);
        progressBar.setVisibility(View.VISIBLE);
        // Animation for progress bar
        AlphaAnimation animation = new AlphaAnimation(0, 1);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(300);  // animation duration
        animation.setFillAfter(true);
        progressBar.startAnimation(animation);  // start animation
    }

    private void endTransfer() {
        // Animation for progress bar
        AlphaAnimation a1 = new AlphaAnimation(1, 0);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        Animation a2 = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AnimationSet as = new AnimationSet(true);
        as.setDuration(500);
        as.addAnimation(a1);
        as.addAnimation(a2);
        as.setFillAfter(true);
        progressBar.startAnimation(as);  // start animation
    }

    public int getDeviceWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(300);
        anim.setInterpolator(new DecelerateInterpolator());
        v.startAnimation(anim);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nfctransfer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        Log.i("NFC", "onResume()");
    }

    protected void onNewIntent(Intent intent) {
        // Get the Intent action
        super.onNewIntent(intent);
        String action = getIntent().getAction();
        Toast.makeText(this, action, Toast.LENGTH_LONG);

    }
}
