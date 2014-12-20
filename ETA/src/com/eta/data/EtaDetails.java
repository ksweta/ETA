package com.eta.data;

import java.util.LinkedList;
import java.util.List;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

public class EtaDetails {
   private List<LatLng> route;
   private String senderName;
   private String senderPhone;
   private String eta;
   private String url;
   private Address srcAddress;
   
   public EtaDetails() {
      route = new LinkedList<LatLng>();
      eta = new String();
   }
   
   public List<LatLng> getRoute() {
      return route;
   }

   public void setRoute(List<LatLng> route) {
      this.route = route;
   }

   public String getSenderName() {
      return senderName;
   }

   public void setSenderName(String senderName) {
      this.senderName = senderName;
   }


   public String getSenderPhone() {
      return senderPhone;
   }

   public void setSenderPhone(String senderPhone) {
      this.senderPhone = senderPhone;
   }

   public String getEta() {
      return eta;
   }

   public void setEta(String eta) {
      this.eta = eta;
   }


   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public Address getSrcAddress() {
      return srcAddress;
   }


   public void setSrcAddress(Address srcAddress) {
      this.srcAddress = srcAddress;
   }

   public boolean isSrcAddressAvailable(){
      return srcAddress != null;
   }
   
   @Override
   public String toString() {
      return "EtaDetails [route=" + route + ", senderName=" + senderName
            + ", senderPhone=" + senderPhone + ", eta=" + eta + ", url="
            + url + ", srcAddress=" + srcAddress + "]";
   }
}
