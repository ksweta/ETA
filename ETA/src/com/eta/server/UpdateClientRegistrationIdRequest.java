package com.eta.server;

public class UpdateClientRegistrationIdRequest {
	protected String phoneNumber;
	protected String clientRegistrationId;
	
	public UpdateClientRegistrationIdRequest() {
		super();
	}
	
	public UpdateClientRegistrationIdRequest(String phoneNumber,
											 String clientRegistrationId) {
		super();
		this.phoneNumber = phoneNumber;
		this.clientRegistrationId = clientRegistrationId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getClientRegistrationId() {
		return clientRegistrationId;
	}

	public void setClientRegistrationId(String clientRegistrationId) {
		this.clientRegistrationId = clientRegistrationId;
	}

	@Override
	public String toString() {
		return "UpdateClientRegistrationIdRequest [phoneNumber=" + phoneNumber
				+ ", clientRegistrationId=" + clientRegistrationId + "]";
	}
}
