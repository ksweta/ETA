package com.eta.transport;

public class LoginRequest extends Request {
	private String phoneNumber;
	private String password;
	
	public LoginRequest(){
		//For Gson library.
	}
	
	public LoginRequest(String phoneNumber, String password) {
		super();
		this.phoneNumber = phoneNumber;
		this.password = password;
	}

	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginRequest [phoneNumber=" + phoneNumber + ", password="
				+ password + "]";
	}
	
	
}
