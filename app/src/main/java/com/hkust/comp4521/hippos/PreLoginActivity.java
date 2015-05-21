package com.hkust.comp4521.hippos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hkust.comp4521.hippos.database.CategoryDB;
import com.hkust.comp4521.hippos.database.DatabaseHelper;
import com.hkust.comp4521.hippos.database.InventoryDB;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.User;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.hkust.comp4521.hippos.utils.ImageUtils;


public class PreLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ensure app folders exist
        ImageUtils.checkAppFolderStructure();

        // look for local DB data, initialize if not exist
        DatabaseHelper.initDatabase(this);

        // for determining login information
        PreferenceService.initPreference(this);
        String email = PreferenceService.getStringValue(this, PreferenceService.KEY_LOGIN_USERNAME);
        String pw = PreferenceService.getStringValue(this, PreferenceService.KEY_LOGIN_PASSWORD);
        String server = PreferenceService.getStringValue(this, PreferenceService.KEY_SERVER_LOCATION);

        // if all info is here, try to login with that
        if(email != null && pw != null && server != null) {
            RestClient.getInstance().login(email, pw, new RestListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Commons.setUser(user);
                    // Initialize Inventory List
                    Commons.initializeInventoryList(new Commons.onInitializedListener() {
                        @Override
                        public void onInitialized() {
                            RestClient.getInstance().registerGCM(PreferenceService.getStringValue(PreferenceService.KEY_GCM_REGISTRATION_ID), new RestListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Toast.makeText(PreLoginActivity.this, "GCM service registered!", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(int status) {

                                }
                            });
                            launchActivity(MainActivity.class);
                        }
                    });
                }

                @Override
                public void onFailure(int status) {
                    Log.i("PreLogin", InventoryDB.getInstance().getCount() + " && " + CategoryDB.getInstance().getCount());
                    if(status == RestListener.NETWORK_UNREACHABLE) {
                        // Server unreachable, see if local DB exists
                        Log.i("PreLogin2", InventoryDB.getInstance().getCount() + " && " + CategoryDB.getInstance().getCount());
                        if(InventoryDB.getInstance().getCount() > 0 && CategoryDB.getInstance().getCount() > 0) {
                            // if yes, use the app as usual in offline mode
                            Commons.ONLINE_MODE = false;
                            Commons.initializeInventoryList(new Commons.onInitializedListener() {
                                @Override
                                public void onInitialized() {
                                    launchActivity(MainActivity.class);
                                }
                            });
                        } else {
                            // if no, jump back to LoginActivity
                            launchActivity(LoginActivity.class);
                        }
                    } else {
                        // Other failures, jump back to LoginActivity
                        launchActivity(LoginActivity.class);
                    }
                }
            });
        } else {
            // not enough info for login, jump to loginActivity
            launchActivity(LoginActivity.class);
        }
    }

    private void launchActivity(Class<?> activityClass) {
        Intent i = new Intent(this, activityClass);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, R.anim.none);
        finish();
    }
}
