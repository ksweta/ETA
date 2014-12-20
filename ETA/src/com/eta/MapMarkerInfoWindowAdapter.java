package com.eta;
import android.content.Context;
import android.location.Address;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eta.util.Utility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapMarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

   private Context context;
   
   public MapMarkerInfoWindowAdapter(Context context) {
      this.context = context;
   }
   
   @Override
   public View getInfoContents(Marker marker) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
      View view = inflater.inflate(R.layout.map_marker, null);
      TextView tvSenderEtaDetail = (TextView)view.findViewById(R.id.tv_map_marker_sender_eta);
      TextView tvAddress = (TextView)view.findViewById(R.id.tv_map_marker_address);
      tvSenderEtaDetail.setText(Html.fromHtml(String.format("<strong>%s's ETA : %s</strong> ", 
                                                            marker.getTitle(),
                                                            marker.getSnippet())));
      Address address = Utility.getSenderAddress(context, marker.getPosition().latitude, marker.getPosition().longitude);
      tvAddress.setText(Utility.formatAddress(address));
      return view;
   }

   @Override
   public View getInfoWindow(Marker marker) {
      //We don't want to create window each time. 
      //Since we will be using the same window. 
      //This method shoudln't be implemented and 
      //should return null.
      return null;
   }

}
