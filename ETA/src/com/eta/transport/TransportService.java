package com.eta.transport;

import com.google.gson.Gson;

import retrofit.Callback;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

public interface TransportService {
	//This will help to convert java object to json and vice-versa
	Converter DATA_CONVERTER = new GsonConverter(new Gson());
	
	//TODO Need to put Heroku url.
	//String SERVICE_ENDPOINT = "https://immense-brushlands-1440.herokuapp.com";
	String SERVICE_ENDPOINT = "http://10.0.0.10:3000";
	//Server expects json request.
	String HEADER_CONTENT_TYPE_JSON = "application/json";
	//Client expects json response.
	String HEADER_ACCEPT_JSON = "application/json";
	//Status codes
	int RESPONSE_STATUS_OK = 200;
	
	//Used for Sign up
	@POST("/users.json")
	void signUp(@Body 
			    RegistrationRequest request,
				@Header("Content-Type") 
	            String contentType,
				@Header("Accept") 
	            String accept,
				Callback<Void> callback);
	
	@POST("/users/clientRegistrationId.json")
	void updateRegistrationId(@Body 
			                  UpdateClientRegistrationIdRequest request,
			 				  @Header("Content-Type") 
							  String contentType,
			 				  @Header("Accept") 
							  String accept,
							  Callback<Void> callback);
	@POST("/users/login.json")
	void signIn(@Body 
			    LoginRequest request,
    			@Header("Content-Type") 
	            String contentType,
			    @Header("Accept") 
	            String accept,
				Callback<Void> callback);
	
	@GET("/users/isReceipientRegistered")
	void isReceipientRegistered(@Query("phoneNumber") 
	                            String phoneNumber, 
								@Header("Accept") 
	                            String accept,
								Callback<Void> callback);
	
	@POST("/notification/sendETA.json")
	void sendETA(@Body ETANotificationRequest request, 
			     @Header("Content-Type") String contentType,
		         @Header("Accept") String accept,
			     Callback<ETANotificationResponse> callback);
}
