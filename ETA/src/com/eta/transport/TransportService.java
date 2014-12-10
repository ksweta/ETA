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
	Converter DATA_CONVERTER = new GsonConverter(new Gson());
	//TODO Need to put Heroku url.
	String SERVICE_ENDPOINT = "http://10.0.0.10:3000";
	String HEADER_CONTENT_TYPE = "application/json";
	String HEADER_ACCEPT = "application/json";
	
	@POST("/users.json")
	void registerUser(@Body RegistrationRequest request,
					  @Header("Content-Type") String contentType,
					  @Header("Accept") String accept,
					  Callback<Void> callback);
	
	@POST("/user/clientRegistrationId.json")
	void updateRegistrationId(@Body UpdateClientRegistrationIdRequest request,
			 				  @Header("Content-Type") String contentType,
			 				  @Header("Accept") String accept,
							  Callback<RegistrationResponse> callback);
	@POST("/user/login.json")
	void loging(@Body LoginRequest request,
    			@Header("Content-Type") String contentType,
			    @Header("Accept") String accept,
				Callback<LogingResponse> callback);
	
	@GET("/user/isReceipientRegistered")
	void isReceipientRegistered(@Query("phoneNumber") String phoneNumber, 
								@Header("Accept") String accept,
								Callback<ReceipientRegisteredResponse> callback);
	
	@POST("/notification/sendETA.json")
	void sendETA(@Body ETANotificationRequest request, 
			     @Header("Content-Type") String contentType,
		         @Header("Accept") String accept,
			     Callback<ETANotificationResponse> callback);
}
