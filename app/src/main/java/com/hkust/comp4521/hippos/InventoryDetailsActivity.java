package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.events.InventoryInfoChangedEvent;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.NFCService;
import com.hkust.comp4521.hippos.services.ThreadService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.utils.ImageRetriever;
import com.skyfishjy.library.RippleBackground;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;


public class InventoryDetailsActivity extends ActionBarActivity {

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    // Views
    private RelativeLayout mHeaderLayout;
    private ImageView mHeaderImageView;
    private TextView mHeaderTitle, tvItemDesc, tvItemPrice, tvItemStock;
    private ImageButton btnBack, btnEdit;
    private LinearLayout btnNFCAssign, btnDisableInventory;

    // Activity-related
    private Activity mActivity;
    private Context mContext;
    MaterialDialog mNFCDialog;
    public static Drawable heroImageDrawable;
    public static int itemIndex;        // for recyclerview update

    // Data structure
    private Inventory mItem;

    // Services
    private NFCService mNFC;

    // Bus
    private boolean busRegistered = false;

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
        ViewCompat.setTransitionName(mHeaderLayout, VIEW_NAME_HEADER_IMAGE);
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
        if(heroImageDrawable != null)
            setupHeroImageFromDrawable();
        mHeaderLayout = (RelativeLayout) findViewById(R.id.rl_inventory_header);
        // setup textviews
        mHeaderTitle = (TextView) findViewById(R.id.tv_inventory_item_name);
        mHeaderTitle.setText(mItem.getName());
        tvItemDesc = (TextView) findViewById(R.id.tv_inventory_item_desc);
        tvItemDesc.setText(mItem.getTimeStamp());
        tvItemPrice = (TextView) findViewById(R.id.tv_inventory_item_price);
        tvItemPrice.setText(mItem.getFormattedPrice());
        tvItemStock = (TextView) findViewById(R.id.tv_inventory_item_stock);
        tvItemStock.setText(mContext.getString(R.string.stock) + mItem.getStock());
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
        // animate actionBar buttons
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        btnBack.setAnimation(alphaAnimation);
        btnEdit.setAnimation(alphaAnimation);
        alphaAnimation.setStartOffset(300);
        alphaAnimation.start();
        // NFC assign button and its dialog
        btnNFCAssign = (LinearLayout) findViewById(R.id.ll_inventory_details_nfc_assign);
        btnNFCAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate view for dialog
                LayoutInflater mInflater = getLayoutInflater().from(mContext);
                View vv = mInflater.inflate(R.layout.dialog_nfc_assign, null);
                mNFCDialog = new MaterialDialog.Builder(InventoryDetailsActivity.this)
                        .customView(vv, false)
                        .negativeText(mContext.getString(R.string.dialog_cancel))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                mNFC.WriteModeOff();
                            }
                        })
                        .show();
                mNFC.WriteModeOn();
                // start ripple animation
                final RippleBackground rippleBackground = (RippleBackground) vv.findViewById(R.id.content);
                rippleBackground.startRippleAnimation();
            }
        });
        // disable inventory button and its dialog
        btnDisableInventory = (LinearLayout) findViewById(R.id.ll_inventory_details_disable_inventory);
        btnDisableInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate view for dialog
                mNFCDialog =  new MaterialDialog.Builder(InventoryDetailsActivity.this)
                        .content(mContext.getString(R.string.dialog_disable_inventory_content))
                        .positiveText(mContext.getString(R.string.dialog_okay))
                        .negativeText(mContext.getString(R.string.dialog_cancel))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                RestClient.getInstance(mContext).updateInventory(mItem.getId(), mItem.getName(), mItem.getPrice(), mItem.getStock(), Inventory.INVENTORY_DISABLED, mItem.getCategory(), new RestListener<Inventory>() {
                                    @Override
                                    public void onSuccess(Inventory inventory) {
                                        Toast.makeText(mContext, "Inventory " + inventory.getName() + " disabled!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int status) {

                                    }
                                });
                            }
                            @Override
                            public void onNegative(MaterialDialog dialog) {

                            }
                        })
                        .show();
            }
        });
        // disable inventory action (if needed)
        if(mItem.getStatus() == Inventory.INVENTORY_DISABLED)
            disableInventory();
    }

    private void setupHeroImageFromDrawable() {
        // for image view
        mHeaderImageView.setImageDrawable(heroImageDrawable);
        // for tinted status bar
        Bitmap bitmap = ((BitmapDrawable)heroImageDrawable).getBitmap();
        int color = Palette.generate(bitmap).getDarkVibrantColor(Color.parseColor("#696969"));
        TintedStatusBar.changeStatusBarColor(this, color);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TintedStatusBar.changeStatusBarColor(this, this.getResources().getColor(R.color.green_primary));
        // Always unregister when an object no longer should be on the bus.
        if(busRegistered == true) {
            Commons.getBusInstance().unregister(this);
            busRegistered = false;
        }
        // animate actionBar buttons
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(150);
        btnBack.setAnimation(alphaAnimation);
        btnEdit.setAnimation(alphaAnimation);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.start();
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
                    Toast.makeText(mContext, mContext.getString(R.string.nfc_success_msg), Toast.LENGTH_SHORT).show();
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
        // Register for bus
        if(busRegistered == false) {
            Commons.getBusInstance().register(this);
            busRegistered = true;
        }
        // Re-fetch image in case image changed
        if(mItem.getStatus() == Inventory.INVENTORY_DIRTY) {
            new ImageRetriever(mHeaderImageView, mItem.getImage(), getResources().getDrawable(R.mipmap.placeholder), mActivity).execute();
        }
    }

    @Subscribe
    public void onInventoryInfoChanged(InventoryInfoChangedEvent event) {
        // Update inventory view from bus message
        final Inventory updatedItem = event.getInventory();
        if(updatedItem != null && mItem.getId() == updatedItem.getId()) {
            mHeaderTitle.setText(updatedItem.getName());
            tvItemDesc.setText(updatedItem.getTimeStamp());
            tvItemPrice.setText(updatedItem.getFormattedPrice());
            tvItemStock.setText(mContext.getString(R.string.stock) + updatedItem.getStock());
            if(updatedItem.getStatus() == Inventory.INVENTORY_DISABLED)
                disableInventory();

            ThreadService.delayedStart(mActivity, new Runnable() {
                @Override
                public void run() {
                    new ImageRetriever(mHeaderImageView, updatedItem.getImage(), getResources().getDrawable(R.mipmap.placeholder), mActivity).execute();
                }
            }, 300);
        }
    }

    public void disableInventory() {
        // Change views when inventory is disabled
        mHeaderTitle.setText(mItem.getName() + getString(R.string.inventory_details_disabled));
        btnNFCAssign.setVisibility(View.GONE);
        btnDisableInventory.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
    }
}
