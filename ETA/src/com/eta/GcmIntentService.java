package com.eta;


import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService {

	public static final int NOTIFICATION_ID         = 1;
	private static final String GCM_MSG_SENDER_PHONE_NUMBER = "GCM_MSG_SENDER_PHONE_NUMBER";
	private static final String GCM_MSG_SENDER_NAME  	    = "GCM_MSG_SENDER_NAME";
	private static final String GCM_MSG_LONGITUDE    	    = "GCM_MSG_LONGITUDE";
	private static final String GCM_MSG_LATITUDE            = "GCM_MSG_LATITUDE";
	private static final String GCM_MSG_ETA                 = "GCM_MSG_ETA";
	
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(TAG, "Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
               
                // Post notification of received message.
                sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    
    private void sendNotification(Bundle bundle) {
    	String senderName = bundle.getString(GCM_MSG_SENDER_NAME);
    	String senderPhone = bundle.getString(GCM_MSG_SENDER_PHONE_NUMBER);
    	Double longitude = bundle.getDouble(GCM_MSG_LONGITUDE);
    	Double latitude = bundle.getDouble(GCM_MSG_LATITUDE);
    	//TODO ETA in seconds, I can improve the formatting later.
    	Integer eta = bundle.getInt(GCM_MSG_ETA);
    	String bigText = String.format("%s's ETA %d sec.", senderName, eta);
    	//TODO improve the message, make it more meaningful.
    	String contentTextFormat = "%s's is running late. he is currently at Long %d, Lat %d."
    								+ " He will arrive in %d seconds";
    	String contentText = String.format(contentTextFormat, 
    									   senderName, 
    									   longitude,
    									   latitude,
    									   eta);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // This intent will have almost the same parameters that are passed in
        // GCM broadcast intent. These parameters will be used by View Activity 
        // to show the sender on the map.
        Intent viewETAIntent = new Intent(this, ViewETAActivity.class);
        viewETAIntent.putExtras(bundle);
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 
        														0,
        														viewETAIntent,
        														0);
        
        
        //Prepare the notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.eta_notification)
        .setContentTitle("ETA Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(bigText))
        .setContentText(contentText);
        
        //Set the content intent. This intent will launch the ViewETAActicity
        mBuilder.setContentIntent(contentIntent);
        
        //Time to show the notification.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }																										
}
