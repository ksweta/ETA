package com.eta;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.eta.task.GcmRegistrationTask;
import com.eta.transport.SignInRequest;
import com.eta.transport.TransportService;
import com.eta.transport.TransportServiceHelper;
import com.eta.util.ApplicationSharedPreferences;
import com.eta.util.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SignInActivity extends Activity {
   private static final String TAG = SignInActivity.class.getSimpleName();
   private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
   private EditText etPhone;
   private EditText etPassword;
   private CheckBox cbShowPassword;
   private ProgressDialog progressDialog;

   //Gcm related
   private GoogleCloudMessaging gcm;
   private String gcmClientRegId;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_signin);
      etPhone = (EditText)findViewById(R.id.et_signin_phone);
      etPassword = (EditText)findViewById(R.id.et_signin_password);
      cbShowPassword = (CheckBox)findViewById(R.id.cb_signin_show_password);

      //Setup Progress dialog for later use
      progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(true);
      progressDialog.setIndeterminate(true);
      
      //Auto-populate the phone field.
      String phoneNumber = Utility.getDevicePhoneNumber(this);
      if (phoneNumber != null && !phoneNumber.isEmpty()) {
         etPhone.setText(Utility.purgePhoneNumber(phoneNumber));
      }

      //Bring the focus to password field
      etPassword.setFocusableInTouchMode(true);
      etPassword.requestFocus();

      //Check shared preference if show password is true then 
      //set the show password check box. Otherwise 
      if(ApplicationSharedPreferences.getSigninShowPasswordFlag(this)) {
         cbShowPassword.setChecked(true);
         Utility.disablePasswordTransformation(etPassword);

      } else {
         cbShowPassword.setChecked(false);
         Utility.enablePasswordTransformation(etPassword);
      }

      // Check device for Play Services APK.
      if (checkPlayServices()) {
         // If this check succeeds, proceed with normal processing.
         // Otherwise, prompt user to get valid Play Services APK.
         gcm = GoogleCloudMessaging.getInstance(this);
         gcmClientRegId = ApplicationSharedPreferences.getGCMClientRegistrationId(this);
         if (gcmClientRegId.isEmpty()) {
            //If client is not registered to GCM then register it using 
            //GcmRegistrationTask.
            new GcmRegistrationTask(this, gcm).execute(null,null);
         }
      }
   }

   @Override
   protected void onResume() {
      super.onResume();
      //need to check Play Services APK here too.
      checkPlayServices();
   }

   public void onClick(View view){
      switch(view.getId()){
      case R.id.bt_signin:
         if(Utility.isNetworkOnline(this)) {
            //If network is enable then only allow signin,
            //otherwise network enable show dialog.
            signin();
         } else {
            Utility.getNetworkOnlineAlert(this).show();
         }
         break;
      case R.id.tv_signup:
         signupRedirect();
         break;
      case R.id.cb_signin_show_password:
         showPassword();
         break;
      default:
         Log.e(TAG, "There is no such button");
         break;
      }
   }

   /**
    * This method manages the signin and starts ContactListActivity
    */
   private void signin(){
      String phone = etPhone.getText().toString();
      String password = etPassword.getText().toString();
      boolean isError = false;
      String message = "Please proivde values for following field(s):\n";

      if(phone.isEmpty()) {
         message +="Phone\n";
         isError = true;
      }

      if(password.isEmpty()) {
         message +="Password\n";
         isError = true;
      }

      if(isError) {
         Utility.showErrorMessageWithOKButton(this, 
               "Signin", 
               message);
         return;
      }

      //Get the transport service to make post request to server.
      TransportService service = TransportServiceHelper.getTransportService();
      SignInRequest loginRequest = new SignInRequest(phone, password);
      SignInCallback callback = new SignInCallback(this);
    
     
      progressDialog.show();
      
      //If sign-in is successful then redirecting to ContactListActivity in Callback class.
      service.signIn(loginRequest, 
            TransportService.HEADER_CONTENT_TYPE_JSON, 
            TransportService.HEADER_ACCEPT_JSON, 
            callback);
   }

   /**
    * This method starts Signup activity
    */
   private void signupRedirect() {
      Intent intent = new Intent(this, SignupActivity.class);
      startActivity(intent);
   }

   /**
    * This is a helper method which disables password inputType and make it normal.
    */
   private void showPassword() {
      if (cbShowPassword.isChecked()) {
         //Disable the transformation method to show password.
         Utility.disablePasswordTransformation(etPassword);
         //Save this preference.
         ApplicationSharedPreferences.setSigninShowPasswordFlag(this);
         Log.d(TAG, "Disabled password transformation");
      } else {
         //Enable the transformation method to hide password.
         Utility.enablePasswordTransformation(etPassword);
         //Save the preference
         ApplicationSharedPreferences.resetSigninShowPasswordFlag(this);
         Log.d(TAG, "Enabled password transformation");
      }
   }

   /**
    * Check the device to make sure it has the Google Play Services APK. If
    * it doesn't, display a dialog that allows users to download the APK from
    * the Google Play Store or enable it in the device's system settings.
    */

   private boolean checkPlayServices(){
      int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
      if (resultCode != ConnectionResult.SUCCESS) {
         if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, 
                  this,
                  PLAY_SERVICES_RESOLUTION_REQUEST).show();
         } else {
            Log.i(TAG, "This device is not supported."); 
            // if Google play service is not found and error is not 
            // recoverable, then show message to user and close
            // the application.
            Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Google Play Service missing");
            alert.setMessage("This device is not supported");
            alert.setNegativeButton("OK", null);
            alert.show();
            finish();
         }
         return false;
      }
      return true;
   }

   /**
    * This class implements Callback interface of Retrofit. It will be used while making 
    * signIn() method call. 
    *
    */
   private class SignInCallback implements Callback<Void> {
      private Context context;
      public SignInCallback(Context c) {
         this.context = c;
      }

      @Override
      public void failure(RetrofitError error) {
         Response response = error.getResponse();

         if(response != null && response.getStatus() == TransportService.RESPONSE_FORBIDDEN) {
            Utility.showErrorMessageWithOKButton(context, 
                  "Sign-in error", 
                  "Phone or password is wrong");
         } else {
            Utility.showErrorMessageWithOKButton(context, 
                  "Sign-in server error",
                  error.getMessage());
         }
         Log.e(TAG, error.getMessage(), error.getCause());
         if(progressDialog.isShowing()) {
            progressDialog.dismiss();
         }
      }

      @Override
      public void success(Void voidReturn, Response response) {
         if(response.getStatus() == TransportService.RESPONSE_STATUS_OK) {
            //If login is successful, do following
            // 1. Save this information in shared-preferences.
            ApplicationSharedPreferences.setSignedInFlag(context);
            if(progressDialog.isShowing()) {
               progressDialog.dismiss();
            }
            // 2. Redirect to ContactList activity.
            context.startActivity(new Intent(context, ContactListActivity.class));
         }
      }
   }
}
