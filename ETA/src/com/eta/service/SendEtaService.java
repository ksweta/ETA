package com.eta.service;

import java.util.Date;
import java.util.List;

import retrofit.client.Response;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.eta.R;
import com.eta.location.LocationCriteriaFactory;
import com.eta.transport.ETANotificationRequest;
import com.eta.transport.TransportService;
import com.eta.transport.TransportServiceHelper;
import com.eta.util.ApplicationConstants;
/**
 * This 
 *
 */
public class SendEtaService extends IntentService implements
LocationListener {
   private static final String TAG = SendEtaService.class.getSimpleName();
   //Max time for listing for Location updates
   private static final int MAX_LOCATION_UPDATE_TIME = 1000 * 60 * 10; //In milliseconds
   private static final int MAX_LOCATION_UPDATE_COUNT = 5;
   private static final float DESIRED_ACCURACY = 60.0f; // In meter
   private static final long WAIT_TIME_FOR_LOCATION = 2000; // In milliseconds.
   private static final int MAX_TRY_FOR_LOCATION = 5;
   
   public static final String RECEIVER_PHONE_NUMBER = "RECEIVER_PHONE_NUMBER";
   public static final String SENDER_PHONE_NUMBER = "SENDER_PHONE_NUMBER";
   public static final String SENDER_NAME = "SENDER_NAME";
   
   //This reference will get updated many places whenever
   //application detects that the location has changed.
   private Location currentLocation;
   private LocationManager locationManager;
   private String currentLocationProvider;
   
   //This is a flag which indicates that the first location fix is 
   //available and application can switch the location provider to 
   //Fine coarse location provider.
   private boolean firstLocationFixAvailable;
   private static int locationUpdateCounter;
   
   public SendEtaService() {
      super(SendEtaService.class.getSimpleName());
   }
   
   @Override
   public void onCreate(){
      super.onCreate();
      setFirstLocationFixAvailable(false);
      locationUpdateCounter = 0;
      //Location related.
      locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
      currentLocationProvider = locationManager.getBestProvider(LocationCriteriaFactory.createCoarseCriteria(), 
                                                                true);
      locationManager.requestLocationUpdates(currentLocationProvider, 
                                             0, //Minimum time between update in milliseconds
                                             0, //Minimum distance in meters
                                             this);
      //Get initial location fix
      getInitialLastKnownLocationFix();
   }

   @Override
   public void onDestroy() {
      //Some cleanup task.
      locationManager.removeUpdates(this);
      Log.d(TAG, "onDestroy() => Removing location listener before servervice get destroyed");
      super.onDestroy();
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      //Remove retry notification first if it exists
      if (intent.hasExtra(ApplicationConstants.NOTIFICATION_ID)) {
         cancelNotification(intent.getExtras().getInt(ApplicationConstants.NOTIFICATION_ID, -1));
      }
      //If location is not available then wait for some time.
      int numberOfTries = 0;
      while(!isFirstLocationFixAvailable()){
         
         //Sleep for some time.
         try{Thread.sleep(WAIT_TIME_FOR_LOCATION);} 
         catch(Exception e){ Log.e(TAG, e.getMessage(), e);}
         
         //Increment try counter.
         numberOfTries++;
         if(numberOfTries > MAX_TRY_FOR_LOCATION && !isLocationAvailable()) {
            // Notify user about the failure and suggest 
            // him some action that he can take.
            //Show failed ETA request notification to user.
            showFailedEtaRequestNotification(intent);
            return;
         }
      }
      //Current location must be available at this point, now send ETA.
      if(!intent.hasExtra(RECEIVER_PHONE_NUMBER)) {
         //Some error check.
         ShowToastInIntentService("Reciever phone number is not present in ETA request");
         Log.d(TAG, "Reciever phone number is not present in ETA request");
      }
      
      sendEtaNotification(intent);
   }
   /**
    * This is a helper method to cancel the notification.
    * @param notificationId 
    */
   private void cancelNotification(int notificationId) {
      if(notificationId > 0) {
         NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
         notificationManager.cancel(notificationId);
      }
   }

   @Override
   public void onLocationChanged(Location location) {
      if(!isFirstLocationFixAvailable()) {
         setFirstLocationFixAvailable(true);
       //If first fix is available then switch to fine coarse location provider
         String newLocationProvider  = locationManager.getBestProvider(LocationCriteriaFactory.createFineCriteria(), 
                                                                       true);
         if(currentLocationProvider != null 
               && !currentLocationProvider.equals(newLocationProvider)) {
            //If new location provider is found then change the current location provider.
            currentLocationProvider = newLocationProvider;
            //Remove the old location listener 
            locationManager.removeUpdates(this);
            
            //Set location listener for new location provider.
            locationManager.requestLocationUpdates(currentLocationProvider, 
                                                   0, //Minimum time between update in milliseconds
                                                   0, //Minimum distance in meters
                                                   this);
         }
      } else {
         // If it is not first fix then keep incrementing
         // locationUpdate Counter;
         locationUpdateCounter++;
      }
      
      if (isBetterLocation(location, currentLocation)) {
         currentLocation = location;
      }
      if(isDesiredLocationAccuracy(currentLocation)){
         // If location accuracy satisfies the accuracy requirement then
         // remove the location listener.
         locationManager.removeUpdates(this);
         Log.i(TAG, "Desired location accuracy is met. Location update listener is removed.");
      }
      Log.d(TAG, "onLocationChanged() => location: " + location.toString() +
            ", locationUpdateCounter : " + locationUpdateCounter + 
            ", currentLocationProvider : " + currentLocationProvider + " time : " + new Date());
   }

   @Override
   public void onStatusChanged(String provider, int status, Bundle extras) {
     String strStatus;
      switch(status) {
      case LocationProvider.OUT_OF_SERVICE:
         strStatus = "OUT_OF_SERVICE";
         break;
      case LocationProvider.TEMPORARILY_UNAVAILABLE:
         strStatus = "TEMPORARILY_UNAVAILABLE";
         break;
      case LocationProvider.AVAILABLE:
         strStatus = "AVAILABLE";
         break;
      default:
         strStatus = "UNKNOWN";
      }
      Log.i(TAG, "Location provider " + provider + " status changed to " + strStatus);
   }

   @Override
   public void onProviderEnabled(String provider) {
      Log.i(TAG, "Location provider " + provider + " is enabled");
   }

   @Override
   public void onProviderDisabled(String provider) {
      Log.d(TAG, "Provider " + provider + " is disabled");
      if(currentLocationProvider.equals(provider)) {
         //If current location provider is disabled then time to get a new one.
         currentLocationProvider = locationManager.getBestProvider(LocationCriteriaFactory.createFineCriteria(), true);
         Log.d(TAG, "new location provider : " + currentLocationProvider);
      }
   }
   
   /**
    * Helper method to check whether both the providers are same or not.
    * @param provider1
    * @param provider2
    * @return
    */
   private boolean isSameProvider(String provider1, String provider2) {
      if (provider1 == null) {
        return provider2 == null;
      }
      return provider1.equals(provider2);
  }
   
   public boolean isFirstLocationFixAvailable(){
      return firstLocationFixAvailable;
   }
   
   public void setFirstLocationFixAvailable(boolean fixAvailable){
      firstLocationFixAvailable = fixAvailable;
   }

   /**
    * A helper method to determine the better locate between two locate results.
    * @param location
    * @param currentBestLocation
    * @return 
    */
   private boolean isBetterLocation(Location location, Location currentBestLocation) {
      
      if(currentBestLocation == null) {
         //A new location is always better than no location
         return true;
      }
      
      //Check whether the new location fix is newer or older.
      long timeDelta = location.getTime() - currentBestLocation.getTime();
      boolean isSignificantlyNewer = timeDelta > MAX_LOCATION_UPDATE_TIME;
      boolean isSignificantlyOlder = timeDelta < -MAX_LOCATION_UPDATE_TIME;
      boolean isNewer = timeDelta > 0;
      
      // If it's been more than two minutes since the current location, use the new location
      // because the user has likely moved
      if (isSignificantlyNewer) {
          return true;
      // If the new location is more than two minutes older, it must be worse
      } else if (isSignificantlyOlder) {
          return false;
      }

      int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
      boolean isLessAccurate = accuracyDelta > 0;
      boolean isMoreAccurate = accuracyDelta < 0;
      boolean isSignificantlyLessAccurate = accuracyDelta > 200;

      // Check if the old and new location are from the same provider
      boolean isFromSameProvider = isSameProvider(location.getProvider(),
                                                  currentBestLocation.getProvider());

      // Determine location quality using a combination of timeliness and accuracy
      if (isMoreAccurate) {
          return true;
      } else if (isNewer && !isLessAccurate) {
          return true;
      } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
          return true;
      }
      return false;
   }

   /**
    * This methods gets location from all location provider and returns the best location
    * among them. 
    */
   private void getInitialLastKnownLocationFix() {
      List<String> matchingProviders = locationManager.getAllProviders();
      for (String provider: matchingProviders) {
         Location location = locationManager.getLastKnownLocation(provider);
         if (location != null ) {
            Log.d(TAG, "getInitialLocationFix() => location : " + location);

            if(isBetterLocation(location, currentLocation)) {
               currentLocation = location;
            }
         }
      }
      if(currentLocation != null) {
         Log.d(TAG, "getInitialLocationFix() => currentLocation : " + currentLocation);
      } else {
         Log.w(TAG, "getInitialLocationFix() => couldn't get the location");
      }
   }
   
   /**
    * This method check if given location satisfies the accuracy 
    * requirement.
    * @param location
    * @return true if given location satisfies the requirement otherwise 
    *         false.
    */
   private boolean isDesiredLocationAccuracy(Location location) {
      if(location == null) {
         //If location is null, then it is as good as bad accuracy.
         return false;
      }
      if (location.getAccuracy() <= DESIRED_ACCURACY 
            || locationUpdateCounter > MAX_LOCATION_UPDATE_COUNT) {
         // If obtained location accuracy is <= desired accuracy or 
         // locationUpdate counter is > max locate update, then 
         // return true. Otherwise return false.
         return true;
      } else {
         return false;
      }
   }
   
   private boolean isLocationAvailable(){
      return currentLocation != null;
   }
   /**
    * This method sends ETA notification request to ETA-Server.
    * @param intent Intent which contains Receiver phone, sender phone, and sender name.
    */
   private void sendEtaNotification(Intent intent) {
      Bundle extra = intent.getExtras();
      String rxPhoneNumber = extra.getString(RECEIVER_PHONE_NUMBER);
      String txPhoneNumber = extra.getString(SENDER_PHONE_NUMBER);
      String txName = extra.getString(SENDER_NAME);
      TransportService service = TransportServiceHelper.getTransportService();
      ETANotificationRequest request = new ETANotificationRequest(rxPhoneNumber, 
                                                                  txPhoneNumber,
                                                                  txName,
                                                                  //src Latitude
                                                                   currentLocation.getLatitude(),
                                                                  //src Longitude
                                                                  currentLocation.getLongitude(),
                                                                  //dst Latitude
                                                                  0.0D, 
                                                                  //dst Longitude
                                                                  0.0D, 
                                                                  //ETA
                                                                  0);
     Response response = service.sendETA(request,
                                         TransportService.HEADER_CONTENT_TYPE_JSON,
                                         TransportService.HEADER_ACCEPT_JSON);
     
     if(response.getStatus() == TransportService.RESPONSE_STATUS_OK) {
        ShowToastInIntentService("ETA notification successfully sent to " + rxPhoneNumber);
     } else {
        ShowToastInIntentService("couldn't send ETA");
        Log.e(TAG, "coudln't send ETA, status code: " + response.getStatus() 
                   + ", reason: " + response.getReason());
     }
   }
   
   /**
    * This is a helper method to show notification for failed ETA request.
    * @param intent
    */
   private void showFailedEtaRequestNotification(Intent intent) {
      String rxPhoneNumber = intent.getExtras().getString(RECEIVER_PHONE_NUMBER);
      String title = "ETA Request failed";
      String bigText = "Couldn't send ETA request for " + rxPhoneNumber;
      String contentText = "Please make sure location services are enabled. "
                           + "If it is already enabled then retry";
      int iUniqueId = 0;
     
      iUniqueId = (int) (System.currentTimeMillis()  & 0xFFFFFF);
      Log.d(TAG, "iUniqueId 1: " + iUniqueId);
      PendingIntent pInentSetting = PendingIntent.getActivity(getApplicationContext(), 
                                                                iUniqueId, 
                                                                new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 
                                                                PendingIntent.FLAG_UPDATE_CURRENT);
      Intent retryIntent  = new Intent(getApplicationContext(), SendEtaService.class);
      Bundle extra = intent.getExtras();
      //This is required for canceling the notification.
      extra.putInt(ApplicationConstants.NOTIFICATION_ID, iUniqueId);
      retryIntent.putExtras(extra);
      
      PendingIntent pIntentRetry = PendingIntent.getService(getApplicationContext(), 
                                                             iUniqueId, 
                                                             retryIntent,
                                                             PendingIntent.FLAG_UPDATE_CURRENT);
      Log.d(TAG, "Intent info: " + retryIntent.getClass() + ", package: " + retryIntent.getPackage());
      
      //Prepare the notification
      NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                                                  .setSmallIcon(R.drawable.not_registered)
                                                                  .setContentTitle(title)
                                                                  .setStyle(new NotificationCompat.BigTextStyle()
                                                                  .bigText(bigText))
                                                                  .setContentText(contentText)
                                                                  .setAutoCancel(true)
                                                                  .setOngoing(false);
      //Set the Actions
      builder.addAction(R.drawable.gps, "Enable", pInentSetting);
      builder.addAction(R.drawable.retry, "Retry", pIntentRetry);
      //Set the notification sound
      Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      builder.setSound(alarmSound);
      
      Notification notification = builder.build();
      
     NotificationManager notificationManager = (NotificationManager)
            getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(iUniqueId, notification);
   }
   
   private void ShowToastInIntentService(final String sText) {
      final Context context = this;
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {  @Override public void run()
         {  
            Toast.makeText(context, sText, Toast.LENGTH_SHORT).show();
         }
      });
   }
}
