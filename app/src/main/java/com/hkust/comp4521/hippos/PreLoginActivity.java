package com.hkust.comp4521.hippos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

        // for determining login information and presence of local DB data
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
                            Intent i = new Intent(PreLoginActivity.this, MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, R.anim.none);
                            finish();
                        }
                    });
                }

                @Override
                public void onFailure(int status) {
                    Intent i = new Intent(PreLoginActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, R.anim.none);
                    finish();
                }
            });
        } else {
            // not enough info for login, jump to loginActivity
            Intent i = new Intent(PreLoginActivity.this, LoginActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, R.anim.none);
            finish();
        }
    }
}
