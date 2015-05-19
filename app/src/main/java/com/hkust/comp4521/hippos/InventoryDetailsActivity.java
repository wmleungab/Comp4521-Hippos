package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.services.NFCService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;


public class InventoryDetailsActivity extends ActionBarActivity {

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    // Views
    private ImageView mHeaderImageView;
    private TextView mHeaderTitle, tvItemDesc, tvItemPrice, tvItemStock;
    private ImageButton btnBack, btnEdit;
    private LinearLayout btnNFCAssign;

    // Activity-related
    private Activity mActivity;
    private Context mContext;
    MaterialDialog mNFCDialog;

    // Data structure
    private Inventory mItem;

    // Services
    private NFCService mNFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);

        mActivity = this;
        mContext = this;

        mNFC = new NFCService(mActivity, mContext);

        // get information from previous activity
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            int invId = bundle.getInt(Inventory.INVENTORY_INV_ID);
            mItem = Commons.getInventory(invId);
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
        /*
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
        }*/
        // setup textviews
        mHeaderTitle = (TextView) findViewById(R.id.tv_inventory_item_name);
        mHeaderTitle.setText(mItem.getName());
        tvItemDesc = (TextView) findViewById(R.id.tv_inventory_item_desc);
        tvItemDesc.setText(mItem.getTimeStamp());
        tvItemPrice = (TextView) findViewById(R.id.tv_inventory_item_price);
        tvItemPrice.setText(mItem.getFormattedPrice());
        tvItemStock = (TextView) findViewById(R.id.tv_inventory_item_stock);
        tvItemStock.setText("Stock: " + mItem.getStock());
        // actionBar buttons
        btnBack = (ImageButton) findViewById(R.id.ib_actionBar_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        btnEdit = (ImageButton) findViewById(R.id.ib_actionBar_pencil);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EditInventoryActivity.class);
                Bundle b = new Bundle();
                b.putInt(Inventory.INVENTORY_INV_ID, mItem.getId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        // NFC assign button and its dialog
        btnNFCAssign = (LinearLayout) findViewById(R.id.ll_inventory_details_nfc_assign);
        btnNFCAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate view for dialog
                LayoutInflater mInflater = getLayoutInflater().from(mContext);
                View vv = mInflater.inflate(R.layout.dialog_nfc_assign, null);
                mNFCDialog =  new MaterialDialog.Builder(InventoryDetailsActivity.this)
                                .customView(vv, false)
                                .negativeText("Cancel")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        mNFC.WriteModeOff();
                                    }
                                })
                                .show();
                mNFC.WriteModeOn();
                // start ripple animation
                final RippleBackground rippleBackground=(RippleBackground) vv.findViewById(R.id.content);
                rippleBackground.startRippleAnimation();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mNFC.WriteModeOff();
        TintedStatusBar.changeStatusBarColor(this, this.getResources().getColor(R.color.green_primary));
    }


    // NFC intent received actions
    @Override
    protected void onNewIntent(Intent intent) {
        if(mNFC.discoverTag(intent) != null) {
            String jsonStr = "";
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(Inventory.INVENTORY_INV_ID, mItem.getId());
                jsonStr = jsonObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mNFC.writeTag(jsonStr, new NFCService.NFCWriteTagListener() {
                @Override
                public void onTagWrite(String writeStr) {
                    Toast.makeText(mContext, "NFC write successfully", Toast.LENGTH_SHORT).show();
                    //mNFC.WriteModeOff();
                    mNFCDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mNFC.WriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
