package com.hkust.comp4521.hippos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;


public class InventoryDetailsActivity extends ActionBarActivity {

    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "detail:_id";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    private ImageView mHeaderImageView;
    private TextView mHeaderTitle;
    private ImageButton btnBack, btnCamera;
    private LinearLayout btnNFCAssign;

    private Inventory mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);

        // get information from previous activity
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            int catId = bundle.getInt(Inventory.INVENTORY_CAT_ID);
            int invId = bundle.getInt(Inventory.INVENTORY_INV_ID);
            mItem = Commons.getInventory(catId, invId);
        }

        initViews();
        // set view transitions
        ViewCompat.setTransitionName(mHeaderImageView, VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(mHeaderTitle, VIEW_NAME_HEADER_TITLE);

        // Avoid "blinking" effect when involving scene transition
        if (Build.VERSION.SDK_INT >= 21) {
            Transition fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
        }
    }

    private void initViews() {
        // Header image and tint status bar by extracting Palette
        mHeaderImageView = (ImageView) findViewById(R.id.iv_inventory);
        try {
            InputStream bmStream = getAssets().open(mItem.getFileName() + ".jpg");
            Bitmap bm = BitmapFactory.decodeStream(bmStream);
            Drawable drawable = Drawable.createFromStream(bmStream, null);
            mHeaderImageView.setImageDrawable(drawable);
            int newColor = Palette.generate(bm).getDarkVibrantColor(Color.parseColor("#696969"));
            // Change action bar theme
            TintedStatusBar.changeStatusBarColor(this, newColor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Item name
        mHeaderTitle = (TextView) findViewById(R.id.tv_inventory_item_name);
        mHeaderTitle.setText(mItem.getName());
        // actionBar buttons
        btnBack = (ImageButton) findViewById(R.id.ib_actionBar_back);
        btnCamera = (ImageButton) findViewById(R.id.ib_actionBar_pencil);
        // NFC assign button and its dialog
        btnNFCAssign = (LinearLayout) findViewById(R.id.ll_inventory_details_nfc_assign);
        btnNFCAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate view for dialog
                LayoutInflater mInflater = getLayoutInflater().from(InventoryDetailsActivity.this);
                View vv = mInflater.inflate(R.layout.dialog_nfc_assign, null);
                final MaterialDialog mMaterialDialog = new MaterialDialog(InventoryDetailsActivity.this).setContentView(vv);
                mMaterialDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
                mMaterialDialog.show();
                // start ripple animation
                final RippleBackground rippleBackground=(RippleBackground) vv.findViewById(R.id.content);
                rippleBackground.startRippleAnimation();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory_details, menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TintedStatusBar.changeStatusBarColor(this, this.getResources().getColor(R.color.green_primary));
    }
}