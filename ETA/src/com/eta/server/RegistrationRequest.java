package com.eta.server;

public class RegistrationRequest {
	
	protected User user;

	public RegistrationRequest(User user) {
		super();
		this.user = user;
	}
	
	public RegistrationRequest() {
		super();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "RegistrationRequest [user=" + user + "]";
	}
}
