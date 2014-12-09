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
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eta.util.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class ViewETAActivity extends Activity implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	// Global constants
	private final static String TAG = ViewETAActivity.class.getSimpleName();
	private final static String TAG_STATUS = "status";
	private final static String TAG_ROUTES = "routes";
	private final static String TAG_LEGS = "legs";
	private final static String TAG_DISTANCE = "distance";
	private final static String TAG_DURATION = "duration";
	private final static String TAG_STEPS = "steps";
	private final static String TAG_VALUE = "value";
	private final static String TAG_START_LOCATION = "start_location";
	private final static String TAG_END_LOCATION = "end_location";
	private final static String TAG_LAT = "lat";
	private final static String TAG_LNG = "lng";
	
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private GoogleMap map;
	// Stores the current instantiation of the location client in this object
    private LocationClient locationClient;
    private boolean locationClientConnected;
    private Long totalTimeInSeconds;
    private List<Location> route;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_eta);
  
		// Accessing map fragment to configure it
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		if (map!=null){
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			map.setMyLocationEnabled(true);// shows current location
		}
		
		locationClient =  new LocationClient(this, this, this);
		locationClientConnected = false;
	}
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
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
                        								  CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
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
	}
	@Override
	public void onDisconnected() {
		locationClientConnected = false;
		Toast.makeText(this, "Location client is disconnected", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onStop(){
		//Stop the location client when stop is called.
		locationClient.disconnect();
		super.onStop();
	}
	
	public void onStart() {
		//Start the location client when start is called.
		super.onStart();
		locationClient.connect();
	}
	
	public void getLocation(View view){
		if(locationClientConnected) {
			Location currentLocation = locationClient.getLastLocation();
			String latLong = getLatLng(this, currentLocation);
			Toast.makeText(this, latLong, Toast.LENGTH_SHORT).show();
			Double lat = 37.8723172D;
			Double lon = -122.267056D;
			String url = Utility.makeLocationUrl(lat, 
					                             lon, 
					                             currentLocation.getLatitude(), 
												 currentLocation.getLongitude());
			getRouteDetails(url);
			
		}
	}
	
	private void getRouteDetails(String url){
	
		new AsyncTask<String, Void, List<Location>>() {

			@Override
			protected List<Location> doInBackground(String... params) {

				HttpResponse response = null;
				String responseText = null;
				List<Location>  locationList= new ArrayList<Location>();
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
						int duration = legObject.getJSONObject("duration").getInt("value");
						Log.d(TAG, "Duration : "+duration);
						JSONArray steps = legObject.getJSONArray(TAG_STEPS);
						JSONObject end_loc = null;
						for (int i = 0; i < steps.length(); i++) {
							JSONObject start_loc = steps.getJSONObject(i).getJSONObject(TAG_START_LOCATION);
							Location loc = new Location("");
							loc.setLatitude(start_loc.getDouble(TAG_LAT));
							loc.setLongitude(start_loc.getDouble(TAG_LNG));
							end_loc = steps.getJSONObject(i).getJSONObject(TAG_END_LOCATION);
							locationList.add(loc);
						}
						
						if(end_loc != null) {
							Location loc = new Location("");
							loc.setLatitude(end_loc.getDouble(TAG_LAT));
							loc.setLongitude(end_loc.getDouble(TAG_LNG));
							locationList.add(loc);
						}
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return locationList;
			}
			
			@Override
			protected void onPostExecute(List<Location> routeList){
				route = routeList;
				Toast.makeText(getApplicationContext(), "Total : " + route.size(), Toast.LENGTH_SHORT).show();
			}
		}.execute(url);
	}
	
	private String getLatLng(Context context, Location currentLocation) {
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
	
}
