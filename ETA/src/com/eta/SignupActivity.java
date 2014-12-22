package com.eta;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.eta.transport.SignupRequest;
import com.eta.transport.TransportService;
import com.eta.transport.TransportServiceHelper;
import com.eta.transport.User;
import com.eta.util.ApplicationConstants;
import com.eta.util.ApplicationSharedPreferences;
import com.eta.util.Utility;

public class SignupActivity extends Activity {
   private final String TAG = SignupActivity.class.getSimpleName();
   private EditText etName;
   private EditText etEmail;
   private EditText etPhone;
   private EditText etPassword;
   private EditText etConfirmPassword;
   private CheckBox cbShowPassword;
   private ProgressDialog progressDialog;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_signup);

      etName = (EditText)findViewById(R.id.et_signup_name);
      etEmail =  (EditText)findViewById(R.id.et_signup_email);
      etPhone = (EditText)findViewById(R.id.et_signup_phone);
      etPassword = (EditText)findViewById(R.id.et_signup_password);
      etConfirmPassword = (EditText)findViewById(R.id.et_signup_confirm_password);
      cbShowPassword = (CheckBox)findViewById(R.id.cb_signup_show_password);

      //Setup Progress dialog for later use
      progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(true);
      progressDialog.setIndeterminate(true);
      
      String phoneNumber = Utility.getDevicePhoneNumber(this);
      if (phoneNumber != null && !phoneNumber.isEmpty()) {
         etPhone.setText(Utility.purgePhoneNumber(phoneNumber));
      }

      //Check shared preference if show password is true then 
      //set the show password check box. Otherwise 
      if(ApplicationSharedPreferences.getSignupShowPasswordFlag(this)) {
         cbShowPassword.setChecked(true);
         Utility.disablePasswordTransformation(etPassword);
         Utility.disablePasswordTransformation(etConfirmPassword);
      } else {
         cbShowPassword.setChecked(false);
         Utility.enablePasswordTransformation(etPassword);
         Utility.enablePasswordTransformation(etConfirmPassword);
      }
      
     
   }

   @Override
   protected void onResume(){
      super.onResume();
      //Reset the password field.
      etPassword.setText("");
   }
   
   @Override
   protected void onPause(){
      etPassword.setText("");
      super.onPause();
   }
   
   public void onClick(View view) {
      switch(view.getId()) {
      case R.id.bt_signup:
         if(Utility.isNetworkOnline(this)) {
            //If network is enable then let the user complete signup,
            //otherwise show him the network enable alert dialog.
            signUpUser();
         } else {
            Utility.getNetworkOnlineAlert(this).show();
         }
         break;

      case R.id.bt_signup_cancel:
         //finish activity.
         finish();
         break;

      case R.id.cb_signup_show_password:
         showPassword();
         break;

      default:
         Log.e(TAG, "There is no such button");
         break;
      }
   }
   //onClick method for Signup button
   public void signUpUser(){

      String name = etName.getText().toString();
      String email = etEmail.getText().toString();
      String phone = etPhone.getText().toString();
      String password = etPassword.getText().toString();
      String confirmPassword = etConfirmPassword.getText().toString();
      String msg = "Please proivde values for following field(s):\n";
      boolean isError = false;
      if(name.isEmpty()){
         msg += "name\n";
         isError = true;
      }

      if(email.isEmpty()) {
         msg += "email\n";
         isError = true;
      }
      
      
         
      if(phone.isEmpty()) {
         msg += "phone\n";
         isError = true;
      }
     if( phone.length() != ApplicationConstants.PHONE_NUMBER_LENGTH) {
        msg += "Invalid phone\n";
        isError = true;
     }
     
      if (password.isEmpty()) {
         msg += "password\n";
         isError = true;
      }
      if (confirmPassword.isEmpty()) {
         msg += "confirm password\n";
         isError = true;
      }

      if (isError) {
         Utility.showErrorMessageWithOKButton(this, "Signup", msg);
         return;
      }

    //Check the validity of email.
      if (!Utility.isValidEmail(email)){
         Utility.showErrorMessageWithOKButton(this, 
                                              "Signup", 
                                              "Please provide valid email address."); 
      }
      
      //Check if password doesn't match.
      if(!password.equals(confirmPassword)) {
         Utility.showErrorMessageWithOKButton(this, 
               "Password", 
               "Passwords are not matching");
         //Reset password fields
         etPassword.setText("");
         etConfirmPassword.setText("");
         //Bring the focus.
         etPassword.setFocusableInTouchMode(true);
         etPassword.requestFocus();
         return;
      }
      
      String gcmRegistrationId = ApplicationSharedPreferences.getGCMClientRegistrationId(this);
      //if GCM registration id is empty then don't proceed.
      //There is something terribly wrong.
      if (gcmRegistrationId.isEmpty()) {
         Utility.showErrorMessageWithOKButton(this, 
               "GCM Registration missing",
               "GCM Registration ID not found, Something gone terribly wrong, closing application.");
         finish();
      }

      //Show progress dialog  now, we can dismiss it in Retrofit callback.
      progressDialog.show();
      
      //Get the transport service to make post request to server.
      TransportService service = TransportServiceHelper.getTransportService();

      User user = new User (name,
                           email,
                           phone,
                           password,
                           gcmRegistrationId);

      service.signUp(new SignupRequest(user),
            TransportService.HEADER_CONTENT_TYPE_JSON,
            TransportService.HEADER_ACCEPT_JSON,
            new Callback<Void>() {

         @Override
         public void failure(RetrofitError error) {

            Response response = error.getResponse();
            Utility.showErrorMessageWithOKButton(getApplicationContext(), 
                  "Signup error", 
                  "Server returned error during Singup");
            if(response != null) {
               Log.i(TAG, response.getReason());
               Log.e(TAG, error.getStackTrace().toString());
            }
            //Dismiss the dialog waiting dialog.
            if (progressDialog.isShowing()) {
               progressDialog.dismiss();
            }
         }

         @Override
         public void success(Void voidreturn, Response response) {
            Log.i(TAG, " STATUS : " + String.valueOf(response.getStatus()));
            Context context = getApplicationContext();
            Toast.makeText(context,
                  "Successfully Signup", 
                  Toast.LENGTH_SHORT).show();
            //Treat successful signup as success sign in and save this information in 
            //shared preferences.
            ApplicationSharedPreferences.setSignedInFlag(context);
            //Dismiss the dialog waiting dialog.
            if (progressDialog.isShowing()) {
               progressDialog.dismiss();
            }
            //Save user name in shared preference.
            ApplicationSharedPreferences.setUserName(context, etName.getText().toString());
            
            //Time to launch ContactListActivity
            startActivity(new Intent(context, ContactListActivity.class));
            //Time to finish this activity.
            finish();
         }
      });
   }

   private void showPassword() {
      if (cbShowPassword.isChecked()) {
         //Disable the transformation method to show password.
         Utility.disablePasswordTransformation(etPassword);
         Utility.disablePasswordTransformation(etConfirmPassword);

         //Save this preference.
         ApplicationSharedPreferences.setSignupShowPasswordFlag(this);
         Log.d(TAG, "Password transformation disabled");
      } else {
         //Enable the transformation method to hide password.
         Utility.enablePasswordTransformation(etPassword);
         Utility.enablePasswordTransformation(etConfirmPassword);

         //Save the preference
         ApplicationSharedPreferences.resetSignupShowPasswordFlag(this);
         Log.d(TAG, "Password transformation enabled");
      }
   }
}
