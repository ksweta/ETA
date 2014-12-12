package com.eta.transport;

public class ReceipientRegisteredRequest extends Response {
	protected String phoneNumber;
	public ReceipientRegisteredRequest() {
		//Gson uses this constructor.
	}
	
	public ReceipientRegisteredRequest(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "ReceipientRegisteredRequest [phoneNumber=" + phoneNumber + "]";
	}
}
