package com.eta.util;

import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * This class provide helper utility methods.
 *
 */
public class Utility {

	public static String getDevicePhoneNumber(Context context) {
		TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}

	public static String makeLocationUrl(double sourcelat, double sourcelog, double destlat, double destlog ){
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http")
			   .authority("maps.googleapis.com")
			   .appendPath("maps")
			   .appendPath("api")
			   .appendPath("directions")
			   .appendPath("json")
			   .encodedQuery("origin="+Double.toString(sourcelat) + "," + Double.toString(sourcelog)+"&destination="+ Double.toString(destlat) + "," + Double.toString(destlog))
			   .appendQueryParameter("sensor", "false")
			   .appendQueryParameter("mode", "driving");

		return builder.build().toString();
	}
}
