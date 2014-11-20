package com.eta.server;

/**
 * This class is used to send the registration information to server.
 *
 */
public class User {
	
	protected String firstName;
	protected String lastName;
	protected String email;
	protected String phoneNumber;
	protected String password;
	//GCM client registration ID
	protected String clientRegistrationId;
	
	
	public User(String firstName, 
			    String lastName, 
			    String email,
			    String phoneNumber, 
			    String password, 
			    String clientRegistrationId) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.clientRegistrationId = clientRegistrationId;
	}
	
	public User(){
		//This is for GSON/Retrofit
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
		return "User [firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", phoneNumber=" + phoneNumber
				+ ", password=" + password + ", clientRegistrationId="
				+ clientRegistrationId + "]";
	}
}
