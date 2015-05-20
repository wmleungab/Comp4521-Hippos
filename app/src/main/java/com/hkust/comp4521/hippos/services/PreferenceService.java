package com.hkust.comp4521.hippos.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.hkust.comp4521.hippos.datastructures.Commons;

public class PreferenceService {
	// Shared Preferences
	private static SharedPreferences preferences;
	public static final String PREFERENCE_NAME = Commons.appName + ".settings";

	public static final String KEY_LOGIN_USERNAME = "user_email";
	public static final String KEY_LOGIN_PASSWORD = "user_password";
	public static final String KEY_SERVER_LOCATION = "server_location";
	public static final String KEY_GCM_REGISTRATION_ID = "gcm_registration_id";
	
	// Shared Preferences methods
	public static void initPreference(Context context) {
		preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public static String getStringValue(Context context, String key) {
		if(preferences == null && context != null)
			initPreference(context);
		return preferences.getString(key, "");
	}

	public static String getStringValue(String key) {
		if(preferences == null)
			return null;
		return preferences.getString(key, "");
	}

	public static boolean getBooleanValue(String key) {
		if(preferences == null)
			return false;
		return preferences.getBoolean(key, false);
	}

	public static boolean getBooleanValue(Context context, String key) {
		if(preferences == null && context != null)
			initPreference(context);
		return preferences.getBoolean(key, false);
	}

	public static int getIntValue(Context context, String key) {
		if(preferences == null && context != null) 
			initPreference(context);
		return preferences.getInt(key, 4);
	}
	
	public static long getLongValue(Context context, String key) {
		if(preferences == null && context != null) 
			initPreference(context);
		return preferences.getLong(key, 0);
	}

	public static void saveStringValue(Context context, String key,String info) {
		if(preferences == null && context != null)
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, info);
		editor.commit();
	}

	public static void saveStringValue(String key,String info) {
		if(preferences == null)
			return;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, info);
		editor.commit();
	}

	public static void saveBooleanValue(String key,Boolean info) {
		if(preferences == null)
			return;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, info);
		editor.commit();
	}

	public static void saveBooleanValue(Context context, String key,Boolean info) {
		if(preferences == null && context != null)
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, info);
		editor.commit();
	}
	
	public static void saveIntValue(Context context, String key, int info) {
		if(preferences == null && context != null) 
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, info);
		editor.commit();
	}
	
	public static void saveLongValue(Context context, String key, long value) {
		if(preferences == null && context != null) 
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

}
