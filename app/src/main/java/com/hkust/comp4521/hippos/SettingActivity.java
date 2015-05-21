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

import com.hkust.comp4521.hippos.database.DatabaseHelper;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.utils.ImageUtils;


public class SettingActivity extends PreferenceActivity {

    // Views
    private RelativeLayout mActionBar;

    // Intent
    static Intent intent;
    static Context mContext;

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
    }

    /*SharedPreferences preferences = getActivity().getSharedPreferences(
            SettingActivity.PrefsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE);
            */
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
                    PreferenceService.saveStringValue(getResources().getString(R.string.user_email_prefs), "");
                    PreferenceService.saveStringValue(getResources().getString(R.string.user_password_prefs), "");

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
                    DatabaseHelper.delete();
                    startActivity(intent);
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
            }
        }

    }


}
