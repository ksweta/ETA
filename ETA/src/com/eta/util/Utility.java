package com.eta.util;

import java.util.regex.Pattern;

import com.eta.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.location.Location;
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
			   .appendQueryParameter("mode", "driving");

		return builder.build().toString();
	}
	/**
	 * This method purge the extra character(s)  present in the phone string
	 * e.g +1(510) 761-1364 => 5107611364
	 * @return
	 */
	public static String purgePhoneNumber(String rowPhoneNumber){
		//Removing all witespaces first
		String phone = rowPhoneNumber.replaceAll("\\s", "");
		String charToDel = "()-+";
		String pattern = "[" + Pattern.quote(charToDel) + "]";
		phone = phone.replaceAll(pattern, "");
		//Remove international code if there is any, make sure it is only 10 digit long
		phone = phone.substring(phone.length() - ApplicationConstants.PHONE_NUMBER_LENGTH, phone.length());
		return phone;
	}
	
	public static String getLatLng(Context context, Location currentLocation) {
        // If the location is valid
        if (currentLocation != null) {

            // Return the latitude and longitude as strings
            return context.getString(
                    R.string.latitude_longitude,
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude());
        } else {
            // Otherwise, return the empty string
            return new String();
        }
    }
	/**
	 * This helper method shows error message in a dialog with OK button
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void showErrorMessageWithOKButton(Context context, String title, String message) {
		Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setNegativeButton("OK", null);
		alert.show();
	}
}
