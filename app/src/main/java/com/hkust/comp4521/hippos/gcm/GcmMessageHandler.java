package com.hkust.comp4521.hippos.gcm;

/**
 * Created by TC on 5/19/2015.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hkust.comp4521.hippos.database.DatabaseHelper;
import com.hkust.comp4521.hippos.database.InventoryDB;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.utils.ImageUtils;

public class GcmMessageHandler extends IntentService {

    private Context mContext;
    private Handler mHandler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    private int inventoryId;
    private int statusCode = -1;

    // Status codes
    public static final int INVEN_IMAGE_CHANGED = 1;
    public static final int INVEN_TEXT_CHANGED = 2;
    public static final int BOTH_INVEN_IMAGE_TEXT_CHANGED = 3;
    public static final int COMPANY_CHANGED = 4;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mContext = this.getApplicationContext();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if(extras.getString("id") != null) {
            inventoryId = Integer.parseInt(extras.getString("id"));
            statusCode = Integer.parseInt(extras.getString("statusCode"));
            showToast();
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast(){
        mHandler.post(new Runnable() {
            public void run() {
                // initialize database for updating
                DatabaseHelper.initDatabase(mContext);

                switch (statusCode) {
                    case INVEN_IMAGE_CHANGED:
                        notifyImageChanged();
                        break;
                    case INVEN_TEXT_CHANGED:
                        notifyTextChanged();
                        break;
                }

            }
        });
    }

    private void notifyTextChanged() {
        RestClient.getInstance().getInventory(inventoryId, new RestListener<Inventory>(){
            @Override
            public void onSuccess(Inventory inventory) {
                // Update database record
                InventoryDB inventoryHelper = InventoryDB.getInstance();
                inventoryHelper.update(inventory);

                // Update inventory list if exists
                boolean reinitialize = false;
                Inventory toUpdateInv = Commons.getInventory(inventory.getId());
                if(toUpdateInv != null) {
                    // Handle for categorical changes
                    if(toUpdateInv.getCategory() != inventory.getCategory()) {
                        reinitialize = true;
                    }
                    toUpdateInv.update(inventory);
                }

                Toast.makeText(mContext, "Inventory updated!", Toast.LENGTH_SHORT).show();

                // Send message to activity using Bus event
                if(reinitialize) {
                    Commons.initializeInventoryList(new Commons.onInitializedListener() {
                        @Override
                        public void onInitialized() {
                            Commons.getBusInstance().register(mContext);
                            Commons.getBusInstance().post(new InventoryInfoChangedEvent());
                            Commons.getBusInstance().unregister(mContext);
                        }
                    });
                } else {
                    Commons.getBusInstance().register(mContext);
                    Commons.getBusInstance().post(new InventoryInfoChangedEvent(toUpdateInv));
                    Commons.getBusInstance().unregister(mContext);
                }
            }
            @Override
            public void onFailure(int status) {

            }
        });
    }

    private void notifyImageChanged() {
        RestClient.getInstance().getInventory(inventoryId, new RestListener<Inventory>(){
            @Override
            public void onSuccess(Inventory inventory) {
                // Update database record
                InventoryDB inventoryHelper = InventoryDB.getInstance();
                inventoryHelper.update(inventory);

                // Update inventory list if exists
                Inventory toUpdateInv = Commons.getInventory(inventory.getId());
                if(toUpdateInv != null) {
                    toUpdateInv.update(inventory);
                    toUpdateInv.setStatus(Inventory.INVENTORY_DIRTY);
                    ImageUtils.deleteFile(toUpdateInv);
                }

                Toast.makeText(mContext, "Inventory image updated!", Toast.LENGTH_SHORT).show();

                // Send message to activity using Bus event
                Commons.getBusInstance().register(mContext);
                Commons.getBusInstance().post(new InventoryInfoChangedEvent(toUpdateInv));
                Commons.getBusInstance().unregister(mContext);
            }
            @Override
            public void onFailure(int status) {

            }
        });
    }
}
