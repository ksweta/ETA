package com.eta;

import android.content.Context;
import android.content.SharedPreferences;

import com.eta.util.ApplicationSharedPreferences;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * This is a helper class to persist state of google map. 
 *
 */
public class MapStateManager {
   private static final String LONGITUDE = "LONGITUDE";
   private static final String LATITUDE = "LATITUDE";
   private static final String ZOOM = "ZOOM";
   private static final String BEARING = "BEARING";
   private static final String TILT = "TILT";
   private static final String MAP_TYPE = "MAP_TYPE";
   public static final float DEFAULT_ZOOM = 13.0f;
   public static final float DEFAULT_TILT = 30.0f;
   public static final float DEFAULT_BEARING = 0.0f;
   
   private SharedPreferences mapStatePreferences;
   
   public MapStateManager(Context context) {
      mapStatePreferences = ApplicationSharedPreferences.getSharedPreferences(context);
   }
   
   public void saveMapState(GoogleMap map) {
      SharedPreferences.Editor editor = mapStatePreferences.edit();
      CameraPosition position = map.getCameraPosition();
      editor.putFloat(LATITUDE, (float)position.target.latitude);
      editor.putFloat(LONGITUDE, (float)position.target.longitude);
      editor.putFloat(ZOOM, position.zoom);
      editor.putFloat(BEARING, position.bearing);
      editor.putFloat(TILT, position.tilt);
      editor.putInt(MAP_TYPE, map.getMapType());
      editor.commit();
   }
   
   /**
    * This method returns save CameraPosition. If there is not saved 
    * camera position then it returns default camera position.
    * @return Saved CameraPosition.
    */
   public CameraPosition getSavedCameraPosition() {
      double latitude = mapStatePreferences.getFloat(LATITUDE, 0);
      
      if(latitude  == 0){
         //Coudln't find stored camera position return default camera position.
         return getDefaultCameraPosition();
      }
      double longitude = mapStatePreferences.getFloat(LONGITUDE, 0);
      LatLng latLng = new LatLng(latitude, longitude);
      float zoom = mapStatePreferences.getFloat(ZOOM, 0);
      float tilt = mapStatePreferences.getFloat(TILT, 0);
      float bearing = mapStatePreferences.getFloat(BEARING, 0);
      
      return new CameraPosition(latLng, zoom, tilt, bearing);
   }
   /**
    * This method returns the saved map type. If it doesn't 
    * find any save map type then it returns NORMAL map type.
    * @return
    */
   public int getMapType(){
      return mapStatePreferences.getInt(MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
   }
   
   /**
    * Default camera position.
    * @return
    */
   public CameraPosition getDefaultCameraPosition(){
      //LatLng of SFSU
      LatLng latLng = new LatLng(37.7208458D, -122.4845107);
      return new CameraPosition(latLng, DEFAULT_ZOOM, DEFAULT_TILT, DEFAULT_BEARING);
      
   }
}