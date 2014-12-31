package com.eta.transport;

import com.google.gson.Gson;

import retrofit.Callback;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

public interface TransportService {
   //This will help to convert java object to json and vice-versa
   Converter DATA_CONVERTER = new GsonConverter(new Gson());

   //TODO Need to put Heroku url.
   String SERVICE_ENDPOINT = "https://immense-brushlands-1440.herokuapp.com";
   //String SERVICE_ENDPOINT = "http://10.0.0.7:3000";
   
   //Server expects json request.
   String HEADER_CONTENT_TYPE_JSON = "application/json";
   //Client expects json response.
   String HEADER_ACCEPT_JSON = "application/json";

   //Status codes
   int RESPONSE_STATUS_OK = 200;
   int RESPONSE_UNAUTHORIZED = 401;
   int RESPONSE_FORBIDDEN = 403;
   int RESPONSE_NOT_FOUND = 404;
   int RESPONSE_CONFLICT = 409;
   //Used for Sign up
   @POST("/users.json")
   void signUp(@Body 
               SignupRequest request,
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
               SignInRequest request,
               @Header("Content-Type") 
               String contentType,
               @Header("Accept") 
               String accept,
               Callback<Void> callback);
   /**
    * This method check if the given phone number is registered with ETA server or not.
    * It returns 200 status code if phone number is registered. Otherwise returns 404 
    * status code.
    * @param phoneNumber Phone number of recipient.
    * @param accept
    * @param callback
    */
   @POST("/users/receipientRegistered.json")
   void isReceipientRegistered(@Body
                               ReceipientRegisteredRequest request, 
                               @Header("Content-Type") 
                               String contentType,
                               @Header("Accept")
                               String accept,
                               Callback<Void> callback);
   /**
    * This method send the ETA notification request to server and server sends it to 
    * device through Google-GCM server.
    * @param request
    * @param contentType
    * @param accept
    * @param callback
    */
   @POST("/notification/sendETA.json")
   retrofit.client.Response sendETA(@Body 
                                    ETANotificationRequest request, 
                                    @Header("Content-Type") 
                                    String contentType,
                                    @Header("Accept") 
                                    String accept);
}
