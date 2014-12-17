package com.eta;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.eta.util.ApplicationConstants;
import com.eta.util.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class ViewETAActivity extends FragmentActivity implements 
OnMapReadyCallback,
LocationListener
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

   private GoogleMap map;

   //This reference will get updated many places whenever
   //application detects that the location has changed.
   private Location currentLocation;
   private LocationManager locationManager;
   private String locationProvider;

   //GCM message related.
   private String senderName;
   private String senderPhone;
   private Integer eta;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.view_eta);

      // Accessing map fragment to configure it
      SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);

      //If GPS is disabled then show an alert to User.
      if(!Utility.isGpsEnabled(this)){
         Utility.getGpsDisableAlert(this).show();
      }
      //Get the location manager.
      locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
      locationProvider = locationManager.getBestProvider(new Criteria(), true);
   }

   public void drawRoute(EtaDetails etaDetails) {

      PolylineOptions polylineOptions = new PolylineOptions();
      polylineOptions.addAll(etaDetails.route).width(7.0f).color(Color.BLUE);
      map.addPolyline(polylineOptions);

      LatLngBounds.Builder builder = new LatLngBounds.Builder();

      //Set the zoom to a level which can include the entire route.
      for (LatLng latlng : etaDetails.route){
         builder.include(latlng);
      }

      map.animateCamera( CameraUpdateFactory
            .newLatLngBounds(builder.build(), 100));

      MarkerOptions markerOptions = new MarkerOptions();
      markerOptions.title(etaDetails.senderName + "'s ETA ")
      .snippet(etaDetails.senderName + " will be " +  etaDetails.eta + " late")
      .position(etaDetails.route.get(0))
      .icon((BitmapDescriptorFactory.fromResource(R.drawable.eta_location)));

      Marker m = map.addMarker(markerOptions);
      //This will display the information window without even clicking it.
      m.showInfoWindow();
   }

   private void getRouteDetails(){
      Intent intent = getIntent();
      Bundle bundle = intent.getExtras();
      String senderPhone = bundle.getString(ApplicationConstants.GCM_MSG_SENDER_PHONE_NUMBER, "");
      
      if (!senderPhone.isEmpty()) {
         String senderName = bundle.getString(ApplicationConstants.GCM_MSG_SENDER_NAME, "");
         Double srcLatitude = Double.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_SRC_LATITUDE));
         Double srcLongitude = Double.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_SRC_LONGITUDE));
         Double dstLatitude = Double.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_DST_LATITUDE, "0.0D"));
         Double dstLongitude = Double.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_DST_LONGITUDE, "0.0D"));
         Integer eta = Integer.valueOf(bundle.getString(ApplicationConstants.GCM_MSG_ETA, "0"));

         
         Toast.makeText(this, 
               "From GCM " + srcLatitude.toString() + ", " + srcLongitude.toString(), 
               Toast.LENGTH_SHORT).show();

         if (!isLocationAvailable()) {
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
         }
         String url = Utility.makeLocationUrl(srcLatitude, 
                                               srcLongitude, 
                                               currentLocation.getLatitude(), 
                                               currentLocation.getLongitude());

         String latLong = Utility.getLatLng(this, currentLocation);

         Log.d(TAG, "lat long from Location manager : " + latLong);

         final EtaDetails etaDetails = new EtaDetails();
         etaDetails.senderName = senderName;
         etaDetails.senderPhone = senderPhone;
         etaDetails.url = url;
         if(!eta.equals(0)) {
            //Eta is not provided 
            etaDetails.eta = Utility.convertSecondsToText(eta);
         }
         //Created AsyncTask to get the route information from Google map server.

         new AsyncTask<EtaDetails, Void, EtaDetails>() {

            @Override
            protected EtaDetails doInBackground(EtaDetails... params) {

               HttpResponse response = null;
               String responseText = null;
               EtaDetails etaDetails = params[0];
               
               try {
                  Log.d(TAG, "URL : "+etaDetails.url);
                  HttpClient client = new DefaultHttpClient();
                  HttpGet request = new HttpGet();
                  request.setURI(new URI(etaDetails.url));
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

                     if(etaDetails.eta.isEmpty()) {
                        etaDetails.eta = legObject.getJSONObject(TAG_DURATION).getString(TAG_TEXT);
                     }
                     Log.d(TAG, "Duration : " + etaDetails.eta);

                     JSONArray steps = legObject.getJSONArray(TAG_STEPS);

                     JSONObject end_loc = null;

                     for (int i = 0; i < steps.length(); i++) {
                        JSONObject start_loc = steps.getJSONObject(i).getJSONObject(TAG_START_LOCATION);
                        end_loc = steps.getJSONObject(i).getJSONObject(TAG_END_LOCATION);

                        etaDetails.route.add(new LatLng(start_loc.getDouble(TAG_LAT),
                              start_loc.getDouble(TAG_LNG)));
                     }

                     if(end_loc != null) {
                        etaDetails.route.add(new LatLng(end_loc.getDouble(TAG_LAT), 
                              end_loc.getDouble(TAG_LNG)));
                     }
                  }

               } catch (JSONException e) {
                  Log.d(TAG, e.getMessage(), e);
               }

               return etaDetails;
            }

            @Override
            protected void onPostExecute(EtaDetails etaDetails){
               Toast.makeText(getApplicationContext(), "Total : " + etaDetails.route.size(), Toast.LENGTH_SHORT).show();
               drawRoute(etaDetails);
            }
         }.execute(etaDetails);
      }
   }

   /* Request updates at startup */
   @Override
   protected void onResume() {
      super.onResume();
      locationManager.requestLocationUpdates(locationProvider, 
            2000, //Minimum time between update in milliseconds
            5, //Minimum distance in meters
            this);
   }

   /* Remove the locationlistener updates when Activity is paused */
   @Override
   protected void onPause() {
      super.onPause();
      locationManager.removeUpdates(this);
   }

   @Override
   public void onMapReady(GoogleMap map) {
      this.map = map;
      //When map is ready then display the route.
      getRouteDetails();
   }

   @Override
   public void onLocationChanged(Location location) {
      currentLocation = location;
   }

   @Override
   public void onStatusChanged(String provider, int status, Bundle extras) {
      
   }

   @Override
   public void onProviderEnabled(String provider) {
      locationProvider = provider;
   }

   @Override
   public void onProviderDisabled(String provider) {
      //TODO 
   }

   private boolean isLocationAvailable(){
      return currentLocation != null;
   }

   private class EtaDetails{
      public List<LatLng> route;
      public String senderName;
      public String senderPhone;
      public String eta;
      public String url;
      
      public EtaDetails() {
         route = new LinkedList<LatLng>();
         eta = new String();
      }

      @Override
      public String toString() {
         return "EtaDetails [route=" + route + ", senderName=" + senderName
               + ", senderPhone=" + senderPhone + ", eta=" + eta + ", url="
               + url + "]";
      }
   }
}
