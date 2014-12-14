package com.eta.util;

import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;

import com.eta.R;

/**
 * This class provide helper utility methods.
 *
 */
public class Utility {
   //Constants
   private static final String TAG = Utility.class.getSimpleName();
   
   /**
    * This method fetches phone number of the current device.
    * @param context
    * @return
    */
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
   /**
    * This method disables password transformation method and shows password. 
    * @param passwordField
    */
   public static void disablePasswordTransformation(EditText etPassword) {
      etPassword.setTransformationMethod(null);
   }

   /**
    * This method enable password transformation method and hides password.
    * @param etPassword
    */
   public static void enablePasswordTransformation(EditText etPassword) {
      etPassword.setTransformationMethod(new PasswordTransformationMethod());
   }
   
   /**
    * This method checks the Internet connection. It returns true if network is 
    * connected otherwise false.
    * @param context
    * @return It returns true if network is connected otherwise false.
    */
   public boolean isNetworkOnline(Context context) {
      boolean status = false;
      try { 
         ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
         //Try 
         NetworkInfo netInfo = cm.getNetworkInfo(0);
         if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
             status= true;
         }else {
             netInfo = cm.getNetworkInfo(1);
             if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                 status= true;
         }
      } catch (Exception e) {
         Log.e(TAG, e.getMessage(), e);
         status = false;
      }
      return status;
   }
}
