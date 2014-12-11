package com.eta;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.eta.transport.LoginRequest;
import com.eta.transport.TransportService;
import com.eta.transport.TransportServiceFactory;
import com.eta.util.ApplicationSharedPreferences;
import com.eta.util.Utility;

public class SignInActivity extends Activity {
	private static final String TAG = SignInActivity.class.getSimpleName();
	private EditText etPhone;
	private EditText etPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		etPhone = (EditText)findViewById(R.id.et_signin_phone);
		etPassword = (EditText)findViewById(R.id.et_signin_password);
		etPhone.setText(Utility.purgePhoneNumber(Utility.getDevicePhoneNumber(this)));
		
		//Bring the focus to password field
		etPassword.setFocusableInTouchMode(true);
		etPassword.requestFocus();
	}
	
	public void onClick(View view){
		switch(view.getId()){
		case R.id.bt_signin:
			signin();
			break;
		case R.id.tv_signup:
			signupRedirect();
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
		TransportService service = TransportServiceFactory.getTransportService();
		LoginRequest loginRequest = new LoginRequest(phone, password);
		SignInCallback callback = new SignInCallback(this);
		
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
	 * This class implements Callback interface of Retrofit. It will be used while making 
	 * signIn() method call. 
	 *
	 */
	private class SignInCallback implements Callback<Void> {
		Context context;
		public SignInCallback(Context c) {
			this.context = c;
		}
		@Override
		public void failure(RetrofitError error) {
			Response response = error.getResponse();
			
			if(response.getStatus() == TransportService.RESPONSE_FORBIDDEN) {
				Utility.showErrorMessageWithOKButton(context, 
													"Sign-in error", 
													"Phone or password is wrong");
			} else {
				Utility.showErrorMessageWithOKButton(context, 
													 "Sign-in server error",
													 error.getMessage());
			}
			Log.e(TAG, error.getMessage(), error.getCause());
		}

		@Override
		public void success(Void voidReturn, Response response) {
			if(response.getStatus() == TransportService.RESPONSE_STATUS_OK) {
				//If login is successful, do following
				// 1. Save this information in shared-preferences.
				ApplicationSharedPreferences.setSignedInFlag(context);
				
				// 2. Redirect to ContactList activity.
				context.startActivity(new Intent(context, ContactListActivity.class));
			}
		}
	}
}
