package com.eta.transport;

import com.google.gson.annotations.SerializedName;

public class ETANotificationRequest extends Request {

   //Receiver phone number.
   @SerializedName("GCM_MSG_RECEIVER_PHONE_NUMBER")
   protected String receiverPhone;

   //Sender phone number.
   @SerializedName("GCM_MSG_SENDER_PHONE_NUMBER")
   protected String senderPhone;
   
   //Sender name.
   @SerializedName("GCM_MSG_SENDER_NAME")
   protected String senderName;
   
   //Source location
   @SerializedName("GCM_MSG_SRC_LATITUDE")
   protected Double srcLaltitude;
   @SerializedName("GCM_MSG_SRC_LONGITUDE")
   protected Double srcLongitude;
   

   //Destination location
   @SerializedName("GCM_MSG_DST_LATITUDE")
   protected Double dstLatitude;
   @SerializedName("GCM_MSG_DST_LONGITUDE")
   protected Double dstLongitude;
  

   @SerializedName("GCM_MSG_ETA")
   protected Integer eta;
   
   public ETANotificationRequest() {
      //Gson needs this const
   }
   
   public ETANotificationRequest(String receiverPhone, String senderPhone,
         String senderName, Double srcLaltitude, Double srcLongitude,
         Double dstLatitude, Double dstLongitude, Integer eta) {
      super();
      this.receiverPhone = receiverPhone;
      this.senderPhone = senderPhone;
      this.senderName = senderName;
      this.srcLaltitude = srcLaltitude;
      this.srcLongitude = srcLongitude;
      this.dstLatitude = dstLatitude;
      this.dstLongitude = dstLongitude;
      this.eta = eta;
   }



   public String getReceiverPhone() {
      return receiverPhone;
   }

   public void setReceiverPhone(String receiverPhone) {
      this.receiverPhone = receiverPhone;
   }

   public String getSenderPhone() {
      return senderPhone;
   }

   public void setSenderPhone(String senderPhone) {
      this.senderPhone = senderPhone;
   }

   public String getSenderName() {
      return senderName;
   }

   public void setSenderName(String senderName) {
      this.senderName = senderName;
   }

   public Double getSrcLongitude() {
      return srcLongitude;
   }

   public void setSrcLongitude(Double srcLongitude) {
      this.srcLongitude = srcLongitude;
   }

   public Double getSrcLaltitude() {
      return srcLaltitude;
   }

   public void setSrcLaltitude(Double srcLaltitude) {
      this.srcLaltitude = srcLaltitude;
   }

   public Double getDstLongitude() {
      return dstLongitude;
   }

   public void setDstLongitude(Double dstLongitude) {
      this.dstLongitude = dstLongitude;
   }

   public Double getDstLatitude() {
      return dstLatitude;
   }

   public void setDstLatitude(Double dstLatitude) {
      this.dstLatitude = dstLatitude;
   }

   public Integer getEta() {
      return eta;
   }

   public void setEta(Integer eta) {
      this.eta = eta;
   }

   @Override
   public String toString() {
      return "ETANotificationRequest [receiverPhone=" + receiverPhone
            + ", senderPhone=" + senderPhone + ", senderName=" + senderName
            + ", srcLongitude=" + srcLongitude + ", srcLaltitude="
            + srcLaltitude + ", dstLongitude=" + dstLongitude
            + ", dstLatitude=" + dstLatitude + ", eta=" + eta + "]";
   }
}
