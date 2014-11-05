package com.eta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegistrationActivity extends Activity {
	
	private EditText etName;
	private EditText etEmail;
	private EditText etPhone;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.register);
	        
	        etName = (EditText)findViewById(R.id.et_registration_name);
	        etEmail =  (EditText)findViewById(R.id.et_registration_email);
	        etPhone = (EditText)findViewById(R.id.et_registration_phone);
	 
	 }
	 
	 public void registerUser(View view){
		 
		  String name = etName.getText().toString();
		  String email = etEmail.getText().toString();
		  String phone = etPhone.getText().toString();
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
		  if (isError) {
			  Builder alert = new AlertDialog.Builder(this);
              alert.setTitle("Registration");
              alert.setMessage(msg);
              alert.setNegativeButton("OK", null);
              alert.show();
              return;
		  }
		  
		 // TODO it would send info to server to register user
		 
		 
		 
	 }
	 
}
