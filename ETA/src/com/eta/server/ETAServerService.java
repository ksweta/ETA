package com.eta.server;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ETAServerService {
	String SERVICE_ENDPOINT = "http://localhost:3000";
	
	@POST("/user.json")
	void registerUser(@Body RegistrationRequest request, Callback<UserResponse> callback);
	
	@POST("/user/clientRegistrationId.json")
	void updateRegistrationId(@Body UpdateClientRegistrationIdRequest request, 
							  Callback<UserResponse> callback);
	
	@POST("notification/sendETA")
	void sendETA(@Body ETANotificationRequest request, Callback callback);
}
