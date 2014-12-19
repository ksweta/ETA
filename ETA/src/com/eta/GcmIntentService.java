package com.eta;


import java.util.List;
import java.util.Locale;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eta.util.ApplicationConstants;
import com.eta.util.Utility;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
   private static final String TAG = GcmIntentService.class.getSimpleName();
   
   private NotificationManager mNotificationManager;

   public GcmIntentService() {
      super("GcmIntentService");
   }


   @Override
   protected void onHandleIntent(Intent intent) {
      
      Bundle bundle = intent.getExtras();
      Log.d(TAG, "extras : " + bundle.toString());
      GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
      // The getMessageType() intent parameter must be the intent you received
      // in your BroadcastReceiver.
      String messageType = gcm.getMessageType(intent);

      if (!bundle.isEmpty()) {  // has effect of unparcelling Bundle
         /*
          * Filter messages based on message type. Since it is likely that GCM will be
          * extended in the future with new message types, just ignore any message types you're
          * not interested in, or that you don't recognize.
          */
         if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            Log.i(TAG, "Send error: " + bundle.toString());
         } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            Log.i(TAG, "Deleted messages on server: " + bundle.toString());
            // If it's a regular GCM message, do some work.
         } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

            // Post notification of received message.
            sendNotification(bundle);
         }
      }
      // Release the wake lock provided by the WakefulBroadcastReceiver.
      GcmBroadcastReceiver.completeWakefulIntent(intent);
   }

   // Put the message into a notification and post it.
   //Here I am assuming that the Google Play Services sends all the information
   //in bundle

   private void sendNotification(Bundle bundle) {
      String senderName = bundle.getString(ApplicationConstants.GCM_MSG_SENDER_NAME);
      String senderPhone = bundle.getString(ApplicationConstants.GCM_MSG_SENDER_PHONE_NUMBER);
      Double srcLatitude = Double.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_SRC_LATITUDE));
      Double srcLongitude = Double.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_SRC_LONGITUDE));
      Double dstLatitude = Double.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_DST_LATITUDE, "0.0D"));
      Double dstLongitude = Double.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_DST_LONGITUDE, "0.0D"));
      Integer eta = Integer.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_ETA));
      String bigText = senderName + " sent you an ETA ";
      
      Address srcAddress = getSenderAddress(srcLatitude, srcLongitude);
      String srcAddressString = "";
      
      if(srcAddress != null) {
         //There could be scenario where address is null.
         srcAddressString = Utility.formatAddress(srcAddress);
         
         //if Address is present then put it in intent bundle. It will be used by viewETAActivity.
         bundle.putParcelable(ApplicationConstants.GCM_MSG_SRC_ADDRESS, 
                              srcAddress);
      }
      
      StringBuilder contentText = new StringBuilder();
      
      if(!srcAddressString.isEmpty()) {
         contentText.append(String.format("%s is at %s and may be running late.",
                                          senderName,
                                          srcAddressString));
      } else {
         contentText.append(String.format("%s is running late.", 
               senderName));
      }
      Log.d(TAG, " Content Text : " + contentText);
      mNotificationManager = (NotificationManager)
            this.getSystemService(Context.NOTIFICATION_SERVICE);

      // This intent will have almost the same parameters that are passed in
      // GCM broadcast intent. These parameters will be used by View Activity 
      // to show the sender on the map.
      Intent viewETAIntent = new Intent(this, ViewETAActivity.class);
      viewETAIntent.putExtras(bundle);
     
      int iUniqueId = 0;
      //This logic will help to group the notification sent from same user(phone).
      if (senderPhone.length() > 5) {
         iUniqueId = Integer.valueOf(senderPhone.substring(senderPhone.length() - 5, senderPhone.length()));
      } else {
           iUniqueId = (int) (System.currentTimeMillis()  & 0xFFFFFF);
      }
      Log.d(TAG, "iUniqueId : " + iUniqueId);
      PendingIntent contentIntent = PendingIntent.getActivity(this, 
                                                              iUniqueId,
                                                              viewETAIntent,
                                                              PendingIntent.FLAG_UPDATE_CURRENT);


      //Prepare the notification
      NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                                                                  .setSmallIcon(R.drawable.eta_notification)
                                                                  .setContentTitle("ETA Notification")
                                                                  .setStyle(new NotificationCompat.BigTextStyle()
                                                                  .bigText(bigText))
                                                                  .setContentText(contentText)
                                                                  .setAutoCancel(true)
                                                                  .setOngoing(false)
                                                                  .setGroup(ApplicationConstants.GCM_NOTIFICATION_GROUP_KEY);

      //Set the content intent. This intent will launch the ViewETAActicity
      mBuilder.setContentIntent(contentIntent);
      // to add sound to the notification
      Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      mBuilder.setSound(alarmSound);

      //Time to show the notification.
      mNotificationManager.notify(iUniqueId, mBuilder.build());
   }
   
   private Address getSenderAddress(Double latitude, Double longitude) {
      Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
      List<Address> addresses = null;
      try {
         addresses = geocoder.getFromLocation(latitude, longitude, 1);
      } catch(Exception e) {
         Log.e(TAG, e.getMessage(), e);
      }
      if(addresses != null && addresses.size() > 0) {
         return addresses.get(0);
      } else {
         //If no address found then return null.
         return null;
      }
   }
}
