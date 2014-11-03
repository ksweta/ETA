package com.eta;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.app.Activity;
import android.os.Bundle;

public class ViewETAActivity extends Activity {
	private GoogleMap map;
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
	}


}
