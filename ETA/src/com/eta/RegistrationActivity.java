package com.eta;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.eta.transport.RegistrationRequest;
import com.eta.transport.TransportService;
import com.eta.transport.TransportServiceFactory;
import com.eta.transport.User;
import com.eta.util.ApplicationSharedPreferences;
import com.eta.util.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	private final String TAG = RegistrationActivity.class.getSimpleName();
	private EditText etName;
	private EditText etEmail;
	private EditText etPhone;
	private EditText etPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		etName = (EditText)findViewById(R.id.et_registration_name);
		etEmail =  (EditText)findViewById(R.id.et_registration_email);
		etPhone = (EditText)findViewById(R.id.et_registration_phone);
		etPassword = (EditText)findViewById(R.id.et_registration_password);
		String phoneNumber = Utility.getDevicePhoneNumber(this);
		if (phoneNumber != null && !phoneNumber.isEmpty()) {
			etPhone.setText(phoneNumber);
		}
	}

	public void registerUser(View view){

		String name = etName.getText().toString();
		String email = etEmail.getText().toString();
		String phone = etPhone.getText().toString();
		String password = etPassword.getText().toString();
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
		if (password.isEmpty()) {
			msg += "password\n";
			isError = true;
		}
		if (isError) {
			Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Registration");
			alert.setMessage(msg);
			alert.setNegativeButton("OK", null);
			alert.show();
			return;
		}

		String gcmRegistrationId = ApplicationSharedPreferences.getGCMClientRegistrationId(this);
		//if GCM registration id is empty then don't proceed.
		//There is something terribly wrong.
		if (gcmRegistrationId.isEmpty()) {
			Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("GCM Registration missing");
			alert.setMessage("GCM Registration ID not found, Something gone terribly wrong, closing application.");
			alert.setNegativeButton("OK", null);
			alert.show();
			finish();
		}
		User user = new User (name,
				email,
				phone,
				password,
				gcmRegistrationId);
		TransportService service = TransportServiceFactory.getTransportService();
		service.registerUser(new RegistrationRequest(user),
				TransportService.HEADER_CONTENT_TYPE,
				TransportService.HEADER_ACCEPT,
				new Callback<Void>() {

			@Override
			public void failure(RetrofitError error) {
				
				Response response = error.getResponse();
				//TODO Need to put proper error handling.
				Toast.makeText(getApplicationContext(), 
							   response.getReason(), 
							   Toast.LENGTH_SHORT).show();
				Log.e(TAG, error.getStackTrace().toString());
			}

			@Override
			public void success(Void voidreturn, Response response) {
				Log.i(TAG, " STATUS : " + String.valueOf(response.getStatus()));
				Context context = getApplicationContext();
				Toast.makeText(context,
						       "Successfully registered", 
						       Toast.LENGTH_SHORT).show();
				
				//Time to launch ContactListActivity
				Intent intent = new Intent(context, ContactListActivity.class);
				startActivity(intent);
			}

		});
	}
}
