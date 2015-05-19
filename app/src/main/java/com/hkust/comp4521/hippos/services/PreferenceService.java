package com.hkust.comp4521.hippos.services;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceService {
	// Shared Preferences
	private static SharedPreferences preferences;
	public static final String PREFERENCE_NAME = "EC2_Settings";
	public static final String KEY_APP_STATE = "app_state";			// 0 - initial; 1 - in select activity
	public static final int APP_INITIAL = 0;
	public static final int APP_IN_SELECT_ACTIVITY = 1;
	
	public static final String KEY_AUTO_LOGIN = "auto_login";
	public static final String KEY_VEHICLE_ID = "vehicle_id";
	public static final String KEY_SHIPMENT_ID = "shipment_id";
	public static final String KEY_LAST_LAT = "last_lat";
	public static final String KEY_LAST_LNG = "last_lng";
	
	// Shared Preferences methods
	public static void initPreference(Context context) {
		preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}
	
	public static String getStringValue(Context context, String key) {
		if(preferences == null && context != null) 
			initPreference(context);
		return preferences.getString(key, "");
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
