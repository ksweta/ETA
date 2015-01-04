package com.eta.util;

public interface ApplicationConstants {
   String SHARED_PREFERENCE_FILE = "eta.shared.preferences";
   String PROPERTY_GCM_REG_ID = "gcm.registration.id";
   String PROPERTY_APP_VERSION = "app.version";
   String PROPERTY_IS_SIGNED_IN = "signed.in";
   String PROPERTY_USER_NAME = "user.name";
   String PROPERTY_USER_PHONE = "user.phone";
   String PROPERTY_SIGNIN_SHOW_PASSWORD = "signin.show.password";
   String PROPERTY_SIGNUP_SHOW_PASSWORD = "signup.show.password";
   //ETA-Server project ID 
   String GCM_SERVER_PROJECT_ID = "177762432832";
   int PHONE_NUMBER_LENGTH = 10;
   int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
   String GCM_MSG_SENDER_PHONE_NUMBER = "GCM_MSG_SENDER_PHONE_NUMBER";
   String GCM_MSG_SENDER_NAME = "GCM_MSG_SENDER_NAME";
   String GCM_MSG_SRC_LONGITUDE = "GCM_MSG_SRC_LONGITUDE";
   String GCM_MSG_SRC_ADDRESS = "GCM_MSG_SRC_ADDRESS";
   String GCM_MSG_SRC_LATITUDE = "GCM_MSG_SRC_LATITUDE";
   String GCM_MSG_DST_LONGITUDE = "GCM_MSG_DST_LONGITUDE";
   String GCM_MSG_DST_LATITUDE = "GCM_MSG_DST_LATITUDE";
   String GCM_MSG_ETA = "GCM_MSG_ETA";
   
   //Notification related
   String NOTIFICATION_ID = "NOTIFICATION_ID";
   
   //ETA notification
   String GCM_NOTIFICATION_GROUP_KEY = "GCM_NOTIFICATION_GROUP_KEY";
   
   //Invite related constants.
   String INVITE_SMS_PHONE = "INVITE_SMS_PHONE";
}
