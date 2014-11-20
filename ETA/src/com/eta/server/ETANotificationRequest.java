package com.eta.server;

public class ETANotificationRequest {

	//Receiver phone number.
	protected String phoneNumber;
	
	//Customized message.
	protected String message;
	
	//Source location
	protected Double srcLongitude;
	protected Double srcLaltitude;
	
	//Destination location
	protected Double dstLongitude;
	protected Double dstLatitude;
	
	public ETANotificationRequest() {
		super();
		//For retrofit
	}
	
	public ETANotificationRequest(String phoneNumber, 
							   String message,
							   Double srcLongitude, 
							   Double srcLaltitude, 
							   Double dstLongitude,
							   Double dstLatitude) {
		super();
		this.phoneNumber = phoneNumber;
		this.message = message;
		this.srcLongitude = srcLongitude;
		this.srcLaltitude = srcLaltitude;
		this.dstLongitude = dstLongitude;
		this.dstLatitude = dstLatitude;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	@Override
	public String toString() {
		return "SendETANotification [phoneNumber=" + phoneNumber + ", message="
				+ message + ", srcLongitude=" + srcLongitude
				+ ", srcLaltitude=" + srcLaltitude + ", dstLongitude="
				+ dstLongitude + ", dstLatitude=" + dstLatitude + "]";
	}
}
