package com.eta.util;

import java.util.regex.Pattern;

import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * This class provide helper utility methods.
 *
 */
public class Utility {

	public static final int PHONE_NUMBER_LENGTH = 10;
	
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
			   .appendQueryParameter("mode", "driving");

		return builder.build().toString();
	}
	/**
	 * This method purge the extra character(s)  present in the phone string
	 * e.g +1(510)761-1364 => 5107611364
	 * @return
	 */
	public static String purgePhoneNumber(String rowPhoneNumber){
		//Replace white space first
		String phone = rowPhoneNumber.replaceAll("\\s", "");
		String charToDel = "()-+";
		String pattern = "[" + Pattern.quote(charToDel) + "]";
		phone = phone.replaceAll(pattern, "");
		//Remove international code if there is any.
		phone = phone.substring(phone.length() - Utility.PHONE_NUMBER_LENGTH, phone.length());
		return phone;
	}
}
