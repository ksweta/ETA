package com.eta.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * This class provide helper method for application Shared
 * preferences.
 *
 */
public class ApplicationSharedPreferences {
	private static final String TAG = ApplicationSharedPreferences.class.getSimpleName();

	public static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(ApplicationConstants.SHARED_PREFERENCE_FILE,
				Context.MODE_PRIVATE);
	}
	/**
	 * This method stores the GCM client registration id in shared preferences.
	 * @param context
	 * @param registrationId
	 */
	public static void storeGCMClientRegistrationId(Context context, String registrationId) {
		final SharedPreferences prefs = getSharedPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(ApplicationConstants.PROPERTY_GCM_REG_ID, registrationId);
		editor.putInt(ApplicationConstants.PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}
	/**
	 * This method returns the GCM client registration id stored in shared 
	 * preference. If it doesn't find then it return an empty string.
	 * @param context
	 * @return
	 */
	public static String getGCMClientRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(context);
		String registrationId = prefs.getString(ApplicationConstants.PROPERTY_GCM_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(ApplicationConstants.PROPERTY_APP_VERSION, 
											 Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}
	/**
	 * This method returns application version number.
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	
	/**
	 * This method sets signed-in flag 
	 * @param context
	 */
	public static void setSignedInFlag(Context context) {
		final SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(ApplicationConstants.PROPERTY_IS_SIGNED_IN, true);
		editor.commit();
	}
	
	/**
	 * This method resets signed-in flag
	 *  
	 * @param context
	 */
	public static void resetSignedInFlag(Context context) {
		final SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(ApplicationConstants.PROPERTY_IS_SIGNED_IN, false);
		editor.commit();
	}

	/**
	 * This method checks the status of signed-in flag stored in shared preferences.
	 * This is used in MainActivity to determine the signed state of the application
	 * @param context
	 * @return
	 */
	public static boolean getSignedInFlag(Context context) {
		final SharedPreferences prefs = getSharedPreferences(context);
		return prefs.getBoolean(ApplicationConstants.PROPERTY_IS_SIGNED_IN, false);
	}
}
