package com.hkust.comp4521.hippos;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.RelativeLayout;

import com.hkust.comp4521.hippos.services.TintedStatusBar;


public class SettingActivity extends PreferenceActivity {

    // Views
    private RelativeLayout mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Change action bar theme
        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mActionBar));
    }

    /*SharedPreferences preferences = getActivity().getSharedPreferences(
            SettingActivity.PrefsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE);
            */
    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private final static String TAG = PrefsFragment.class.getName();
        public final static String SETTINGS_SHARED_PREFERENCES_FILE_NAME = TAG + ".settings";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Define the settings file to use by this settings fragment
            getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);
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

            s = sp.getString(getResources().getString(R.string.user_email_prefs), "");
            if (s != null && !s.equals(""))
                findPreference(getResources().getString(R.string.user_email_prefs)).setSummary(s);
            else
                findPreference(getResources().getString(R.string.user_email_prefs)).setSummary(nth);

            s = sp.getString(getResources().getString(R.string.user_password_prefs), "");
            if (s != null && !s.equals(""))
                findPreference(getResources().getString(R.string.user_password_prefs)).setSummary("******");
            else
                findPreference(getResources().getString(R.string.user_password_prefs)).setSummary(nth);

            s = sp.getString(getResources().getString(R.string.server_location_prefs), "");
            if (s != null && !s.equals(""))
                findPreference(getResources().getString(R.string.server_location_prefs)).setSummary(s);
            else
                findPreference(getResources().getString(R.string.server_location_prefs)).setSummary(nth);


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
