package com.hkust.comp4521.hippos.gcm;

/**
 * Created by TC on 5/19/2015.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hkust.comp4521.hippos.R;
import com.hkust.comp4521.hippos.database.DatabaseHelper;
import com.hkust.comp4521.hippos.database.InventoryDB;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.events.InventoryInfoChangedEvent;
import com.hkust.comp4521.hippos.rest.Response_Company;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.hkust.comp4521.hippos.utils.ImageUtils;

public class GcmMessageHandler extends IntentService {

    private Context mContext;
    private Handler mHandler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    private int inventoryId;
    private int statusCode = -1;

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
            reactMessage();
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void reactMessage(){
        mHandler.post(new Runnable() {
            public void run() {
                // initialize database for updating
                DatabaseHelper.initDatabase(mContext);
                // initialize login information
                Commons.recoverLoginInformation(mContext, new Commons.onInitializedListener() {
                    @Override
                    public void onInitialized() {
                        // perform action on different status code
                        switch (statusCode) {
                            case RestClient.ONLY_INVEN_IMAGE_CHANGED:
                                notifyImageChanged();
                                break;
                            case RestClient.ONLY_INVEN_TEXT_CHANGED:
                                notifyTextChanged();
                                break;
                            case RestClient.BOTH_INVEN_IMAGE_TEXT_CHANGED:
                                notifyNewInventory();
                                break;
                            case RestClient.COMPANY_CHANGED:
                                notifyCompanyChanged();
                                break;
                        }
                    }
                });

            }
        });
    }

    private void notifyCompanyChanged() {
        RestClient.getInstance(mContext).getCompanyDetail(new RestListener<Response_Company>() {
            @Override
            public void onSuccess(Response_Company response_company) {
                String nameKey = mContext.getString(R.string.company_name_prefs);
                PreferenceService.saveStringValue(nameKey, response_company.name);
                String emailKey = mContext.getString(R.string.company_email_prefs);
                PreferenceService.saveStringValue(emailKey, response_company.email);
                String phoneKey = mContext.getString(R.string.company_phone_prefs);
                PreferenceService.saveStringValue(phoneKey, response_company.phone);
                String addKey = mContext.getString(R.string.company_address_prefs);
                PreferenceService.saveStringValue(addKey, response_company.address);
            }

            @Override
            public void onFailure(int status) {

            }
        });
    }

    private void notifyNewInventory() {
        RestClient.getInstance(mContext).getInventory(inventoryId, new RestListener<Inventory>(){
            @Override
            public void onSuccess(Inventory inventory) {
                // Create database record
                InventoryDB inventoryHelper = InventoryDB.getInstance();
                inventoryHelper.insert(inventory);

                // Send message to activity using Bus event
                Commons.initializeInventoryList(new Commons.onInitializedListener() {
                    @Override
                    public void onInitialized() {
                        Commons.getBusInstance().register(mContext);
                        Commons.getBusInstance().post(new InventoryInfoChangedEvent());
                        Commons.getBusInstance().unregister(mContext);
                    }
                });
            }
            @Override
            public void onFailure(int status) {

            }
        });
    }

    private void notifyTextChanged() {
        RestClient.getInstance(mContext).getInventory(inventoryId, new RestListener<Inventory>(){
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
        RestClient.getInstance(mContext).getInventory(inventoryId, new RestListener<Inventory>(){
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
