package com.hkust.comp4521.hippos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hkust.comp4521.hippos.database.CategoryDB;
import com.hkust.comp4521.hippos.database.DatabaseHelper;
import com.hkust.comp4521.hippos.database.InventoryDB;
import com.hkust.comp4521.hippos.database.InvoiceDB;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.utils.ImageUtils;


public class SettingActivity extends PreferenceActivity {

    // Views
    private RelativeLayout mActionBar;

    // Intent
    static Intent intent;
    static Context mContext;

    // Bus
    private boolean busRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = this;

        // Change action bar theme
        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mActionBar));

        // Intent for restarting application
        intent = new Intent(getApplicationContext(), PreLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, android.R.anim.fade_out);

        // Always unregister when an object no longer should be on the bus.
        if(busRegistered == true) {
            Commons.getBusInstance().unregister(this);
            busRegistered = false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // Register for bus
        if(busRegistered == false) {
            Commons.getBusInstance().register(this);
            busRegistered = true;
        }
    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Define the settings file to use by this settings fragment
            getPreferenceManager().setSharedPreferencesName(PreferenceService.PREFERENCE_NAME);
            getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            //Set the default values
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            String s;
            String nth = "**Not set**";
            s = sp.getString(getResources().getString(R.string.company_name_prefs), "");
            if (s != null && !s.equals(""))
                findPreference(getResources().getString(R.string.company_name_prefs)).setSummary(s);
            else
                findPreference(getResources().getString(R.string.company_name_prefs)).setSummary(nth);

            s = sp.getString(getResources().getString(R.string.company_address_prefs), "");
            if (s != null && !s.equals(""))
                findPreference(getResources().getString(R.string.company_address_prefs)).setSummary(s);
            else
                findPreference(getResources().getString(R.string.company_address_prefs)).setSummary(nth);

            s = sp.getString(getResources().getString(R.string.company_email_prefs), "");
            if (s != null && !s.equals(""))
                findPreference(getResources().getString(R.string.company_email_prefs)).setSummary(s);
            else
                findPreference(getResources().getString(R.string.company_email_prefs)).setSummary(nth);

            s = sp.getString(getResources().getString(R.string.company_phone_prefs), "");
            if (s != null && !s.equals(""))
                findPreference(getResources().getString(R.string.company_phone_prefs)).setSummary(s);
            else
                findPreference(getResources().getString(R.string.company_phone_prefs)).setSummary(nth);

            // Set general settings listener
            Preference button = (Preference)findPreference(getString(R.string.logout_prefs));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    PreferenceService.removeValue(getResources().getString(R.string.user_email_prefs));
                    PreferenceService.removeValue(getResources().getString(R.string.user_password_prefs));
                    RestClient.resetServerAPI();
                    RestClient.resetClient(mContext);

                    startActivity(intent);
                    return true;
                }
            });
            button = (Preference)findPreference(getString(R.string.clear_cache_prefs));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ImageUtils.clearImageCache();
                    return true;
                }
            });

            button = (Preference)findPreference(getString(R.string.clear_db_prefs));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DatabaseHelper.getDatabase().delete(InventoryDB.TABLE_NAME, "1", null);
                    DatabaseHelper.getDatabase().delete(InvoiceDB.TABLE_NAME, "1", null);
                    DatabaseHelper.getDatabase().delete(CategoryDB.TABLE_NAME, "1", null);
                    Commons.resetInventoryList();
                    Commons.forceUpdateInventoryList(new Commons.onInitializedListener() {
                        @Override
                        public void onInitialized() {
                            startActivity(intent);
                        }
                    });
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            Preference pref = findPreference(s);
            Log.i("Perference Fragment", "onChanged");
            if (pref instanceof EditTextPreference) {
                EditTextPreference editTextPreferencePref = (EditTextPreference) pref;
                pref.setSummary(editTextPreferencePref.getText());

                String name = PreferenceService.getStringValue(mContext.getString(R.string.company_name_prefs));
                String email = PreferenceService.getStringValue(mContext.getString(R.string.company_email_prefs));
                String phone = PreferenceService.getStringValue(mContext.getString(R.string.company_phone_prefs));
                String address = PreferenceService.getStringValue(mContext.getString(R.string.company_address_prefs));
                RestClient.getInstance(mContext).updateCompanyDetail(name, email, phone, address, new RestListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Toast.makeText(mContext, mContext.getString(R.string.company_info_updated_msg),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int status) {

                    }
                });
            }
        }

    }


}
