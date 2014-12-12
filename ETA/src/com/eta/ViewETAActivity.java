package com.eta;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eta.util.ApplicationConstants;
import com.eta.util.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ViewETAActivity extends Activity implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener
{
   // Global constants
   private final static String TAG = ViewETAActivity.class.getSimpleName();
   private final static String TAG_STATUS = "status";
   private final static String TAG_ROUTES = "routes";
   private final static String TAG_LEGS = "legs";
   private final static String TAG_DISTANCE = "distance";
   private final static String TAG_DURATION = "duration";
   private final static String TAG_STEPS = "steps";
   private final static String TAG_VALUE = "value";
   private final static String TAG_TEXT = "text";
   private final static String TAG_START_LOCATION = "start_location";
   private final static String TAG_END_LOCATION = "end_location";
   private final static String TAG_LAT = "lat";
   private final static String TAG_LNG = "lng";

   /*
    * Define a request code to send to Google Play services
    * This code is returned in Activity.onActivityResult
    */


   private GoogleMap map;
   // Stores the current instantiation of the location client in this object
   private LocationClient locationClient;
   private boolean locationClientConnected;
   private List<LatLng> route;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.view_eta);

      // Accessing map fragment to configure it
      map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
            .getMap();

      if (map!=null){
         map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
         map.setMyLocationEnabled(true);// shows current location
      }

      locationClient =  new LocationClient(this, this, this);
      locationClientConnected = false;
   }

   @Override
   public void onConnectionFailed(ConnectionResult connectionResult) {
      locationClientConnected = false;
      /*
       * Google Play services can resolve some errors it detects.
       * If the error has a resolution, try sending an Intent to
       * start a Google Play services activity that can resolve
       * error.
       */
      if (connectionResult.hasResolution()) {
         try {

            // Start an Activity that tries to resolve the error
            connectionResult.startResolutionForResult(this,
                  ApplicationConstants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */

         } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, e.getMessage(), e);
         }
      } else {

         // If no resolution is available, display a dialog to the user with the error.
         Toast.makeText(this, "Location connection request failed", Toast.LENGTH_SHORT).show();
      }
   }
   @Override
   public void onConnected(Bundle connectionHint) {
      locationClientConnected = true;
      Toast.makeText(this, "Location client is connected", Toast.LENGTH_SHORT).show();
      Location currentLocation = locationClient.getLastLocation();
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),
            currentLocation.getLongitude()),
            20));
   }
   @Override
   public void onDisconnected() {
      locationClientConnected = false;
      Toast.makeText(this, "Location client is disconnected", Toast.LENGTH_SHORT).show();
   }
   @Override
   public void onStart() {
      //Start the location client when start is called.
      super.onStart();
      locationClient.connect();
   }

   @Override
   public void onStop(){
      //Stop the location client when stop is called.
      locationClient.disconnect();
      super.onStop();
   }


   public void getLocation(View view){
      if(locationClientConnected) {
         Location currentLocation = locationClient.getLastLocation();
         String latLong = Utility.getLatLng(this, currentLocation);
         Toast.makeText(this, latLong, Toast.LENGTH_SHORT).show();
         //Berkeley University
         //Double lat = 37.8723172D;
         //Double lon = -122.267056D;
         //SFSU University
         Double lat = 37.7231022D;
         Double lon = -122.4760709D;
         
         String url = Utility.makeLocationUrl(lat, 
               lon, 
               currentLocation.getLatitude(), 
               currentLocation.getLongitude());
         getRouteDetails(url);

      }
   }
   public void drawRoute() {

      PolylineOptions polylineOptions = new PolylineOptions();
      polylineOptions.addAll(route).width(5.0f).color(Color.RED);
      map.addPolyline(polylineOptions);

      LatLngBounds.Builder builder = new LatLngBounds.Builder();

      //Set the zoom to a level which can include the entire route.
      for (LatLng latlng : route){
         builder.include(latlng);
      }

      map.animateCamera( CameraUpdateFactory
            .newLatLngBounds(builder.build(), 100));

      MarkerOptions markerOptions = new MarkerOptions();
      markerOptions.title("Sweta ETA: 12 mins")
      .snippet("Sweta will be 12 mins late")
      .position(route.get(0))
      .icon((BitmapDescriptorFactory.fromResource(R.drawable.eta_location)));

      Marker m = map.addMarker(markerOptions);
      //This will display the information window without even clicking it.
      m.showInfoWindow();
   }

   private void getRouteDetails(String url){

      //Created AsyncTask to get the route information from Google map server.

      new AsyncTask<String, Void, List<LatLng>>() {

         @Override
         protected List<LatLng> doInBackground(String... params) {

            HttpResponse response = null;
            String responseText = null;
            List<LatLng>  locationList= new ArrayList<LatLng>();
            try {
               Log.d(TAG, params[0]);
               HttpClient client = new DefaultHttpClient();
               HttpGet request = new HttpGet();
               request.setURI(new URI(params[0]));
               response = client.execute(request);


               responseText = EntityUtils.toString(response.getEntity());
            } catch (Exception e){
               Log.e(TAG, e.getMessage(), e);
            }


            Log.d(TAG, "Response text : " + responseText);

            try {
               JSONObject json = new JSONObject(responseText);
               String  status = json.getString(TAG_STATUS);
               if (!"OK".equals(status)) {
                  Log.d(TAG, "Status is not OK, stat: "+status);
               }
               JSONArray legs = json.getJSONArray(TAG_ROUTES).getJSONObject(0).getJSONArray(TAG_LEGS);

               if (legs.length() > 0) {

                  JSONObject legObject = legs.getJSONObject(0);

                  int duration = legObject.getJSONObject(TAG_DURATION).getInt(TAG_VALUE);

                  Log.d(TAG, "Duration : "+duration);

                  JSONArray steps = legObject.getJSONArray(TAG_STEPS);

                  JSONObject end_loc = null;

                  for (int i = 0; i < steps.length(); i++) {
                     JSONObject start_loc = steps.getJSONObject(i).getJSONObject(TAG_START_LOCATION);
                     end_loc = steps.getJSONObject(i).getJSONObject(TAG_END_LOCATION);

                     locationList.add(new LatLng(start_loc.getDouble(TAG_LAT),
                           start_loc.getDouble(TAG_LNG)));
                  }

                  if(end_loc != null) {
                     locationList.add(new LatLng(end_loc.getDouble(TAG_LAT), 
                           end_loc.getDouble(TAG_LNG)));
                  }
               }

            } catch (JSONException e) {
               Log.d(TAG, e.getMessage(), e);
            }

            return locationList;
         }

         @Override
         protected void onPostExecute(List<LatLng> locationList){
            route = locationList;
            Toast.makeText(getApplicationContext(), "Total : " + route.size(), Toast.LENGTH_SHORT).show();
            drawRoute();
         }
      }.execute(url);
   }


}
