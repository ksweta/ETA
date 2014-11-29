package com.eta.transport;

/**
 * This class is used to send the registration information to server.
 *
 */
public class User {
	
	protected String name;
	protected String email;
	protected String phoneNumber;
	protected String password;
	//GCM client registration ID
	protected String clientRegistrationId;
	
	
	public User(String name, 
			    String email,
			    String phoneNumber, 
			    String password, 
			    String clientRegistrationId) {
		
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.clientRegistrationId = clientRegistrationId;
	}
	
	public User(){
		//This is for Retrofit
	}

	public String getName() {
		return name;
	}

	public void setFirstName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getClientRegistrationId() {
		return clientRegistrationId;
	}

	public void setClientRegistrationId(String clientRegistrationId) {
		this.clientRegistrationId = clientRegistrationId;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", email=" + email + ", phoneNumber="
				+ phoneNumber + ", password=" + password
				+ ", clientRegistrationId=" + clientRegistrationId + "]";
	}
}
