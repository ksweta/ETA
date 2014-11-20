package com.eta.server;

import java.util.Date;

public class UserResponse extends User {

	protected Long id;
	protected Date created_at;
	protected Date updated_at;
	
	public UserResponse(String firstName, 
							String lastName, 
							String email,
							String phoneNumber, 
							String password, 
							String clientRegistrationId,
							Long id, 
							Date created_at, 
							Date updated_at) {
		super(firstName, 
			  lastName, 
			  email, 
			  phoneNumber, 
			  password,
			  clientRegistrationId);
		this.id = id;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}
	 public UserResponse() {
		 //For GSON and retrofit
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	@Override
	public String toString() {
		return "UserResponse [id=" + id + ", created_at=" + created_at
				+ ", updated_at=" + updated_at + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email
				+ ", phoneNumber=" + phoneNumber + ", password=" + password
				+ ", clientRegistrationId=" + clientRegistrationId + "]";
	} 
}