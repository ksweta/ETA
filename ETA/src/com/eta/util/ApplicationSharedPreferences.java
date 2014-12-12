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
	 * This is a helper method which sets boolean shared preference to given value.
	 * @param context Context of the application.
	 * @param preferenceName Property name.
	 * @param bool Desired boolean value.
	 */
	private static void setBooleanSharedPreference(Context context, String preferenceName, boolean bool) {
		final SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(preferenceName, bool);
		editor.commit();
	}

	/**
	 * This is a helper method which reads the given preference name and returns the value. It returns false
	 * as default value if it doesn't find the preference value.
	 * @param context Context of the application.
	 * @param preferenceName Shared preference name.
	 * @return Returns the store preference value if it finds it otherwise it returns false.
	 */
	private static boolean getBooleanSharedPreference(Context context, String preferenceName){
		return getSharedPreferences(context).getBoolean(preferenceName, false);
	}
	
	/**
	 * This method sets signed-in flag 
	 * @param context
	 */
	public static void setSignedInFlag(Context context) {
		setBooleanSharedPreference(context, ApplicationConstants.PROPERTY_IS_SIGNED_IN, true);
	}
	
	/**
	 * This method resets signed-in flag
	 *  
	 * @param context
	 */
	public static void resetSignedInFlag(Context context) {
		setBooleanSharedPreference(context, ApplicationConstants.PROPERTY_IS_SIGNED_IN, false);
	}

	/**
	 * This method checks the status of signed-in flag stored in shared preferences.
	 * This is used in MainActivity to determine the signed state of the application
	 * @param context
	 * @return
	 */
	public static boolean getSignedInFlag(Context context) {
		return getBooleanSharedPreference(context, ApplicationConstants.PROPERTY_IS_SIGNED_IN);
	}
	
	/**
	 * This method set signin show password shared preference flag as true.
	 * @param context
	 */
	public static void setSigninShowPasswordFlag(Context context) {
		setBooleanSharedPreference(context, ApplicationConstants.PROPERTY_SIGNIN_SHOW_PASSWORD, true);
	}
	
	/**
	 * This method set signin show password shared preference flag as false.
	 * @param context
	 */
	public static void resetSigninShowPasswordFlag(Context context) {
		setBooleanSharedPreference(context, ApplicationConstants.PROPERTY_SIGNIN_SHOW_PASSWORD, false);
	}
	
	/**
	 * This method returns signin show password shared preference flag. It returns false if it doesn't find 
	 * the save value.
	 * @param context
	 * @return
	 */
	public static boolean getSigninShowPasswordFlag(Context context) {
		return getBooleanSharedPreference(context, ApplicationConstants.PROPERTY_SIGNIN_SHOW_PASSWORD);
	}

	/**
	 * This method set signup show password shared preference flag as true.
	 * @param context
	 */
	public static void setSignupShowPasswordFlag(Context context) {
		setBooleanSharedPreference(context, ApplicationConstants.PROPERTY_SIGNUP_SHOW_PASSWORD, true);
	}
	/**
	 * This method set signup show password shared preference flag as false.
	 * @param context
	 */
	public static void resetSignupShowPasswordFlag(Context context) {
		setBooleanSharedPreference(context, ApplicationConstants.PROPERTY_SIGNUP_SHOW_PASSWORD, false);
	}
	/**
	 * This method returns signup show password shared preference flag. It returns false if it doesn't find 
	 * the save value.
	 * @param context
	 * @return
	 */
	public static boolean getSignupShowPasswordFlag(Context context) {
		return getBooleanSharedPreference(context, ApplicationConstants.PROPERTY_SIGNUP_SHOW_PASSWORD);
	}
}
