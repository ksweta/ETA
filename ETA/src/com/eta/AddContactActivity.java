package com.eta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.eta.db.DBHelper;
import com.eta.util.ApplicationConstants;
import com.eta.util.Utility;

public class AddContactActivity extends Activity {

	private EditText etContactName;
	private EditText etContactPhone;
	private DBHelper db;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_add_contact);
	        etContactName = (EditText)findViewById(R.id.et_contact_name);
	        etContactPhone = (EditText)findViewById(R.id.et_contact_phone);
	        db = new DBHelper(this);
	 }
	 
	 public void onClick(View view){
		 String msg = "Please fix the following problem(s):\n";
		 String name = etContactName.getText().toString();
		 String phone = etContactPhone.getText().toString();
		 boolean isError = false;
		 if(name == null || name.isEmpty()) {
			 msg += "Name field is empty\n";
			 isError = true;
		 }
		 if (phone == null || phone.isEmpty()) {
			 msg += "Phone field is empty\n";
			 isError = true;
		 }
		 
		 
		
		if(db.isContactPresent(phone)) {
			//Make sure the phone number is not already present
			//in the contact list.
			msg += "Duplicate phone number\n";
			isError = true;
		}
		
		if (phone.length() != ApplicationConstants.PHONE_NUMBER_LENGTH) {
			msg += "Phone number should be 10 digits long";
			isError = true;
		}
		if (isError) {
			Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Add Contact");
			alert.setMessage(msg);
			alert.setNegativeButton("OK", null);
			alert.show();
			return;
		}
		//If everything looks good then return the name and phone in the intent to 
		//ContactListActivity. Contact list will insert the contact details in SQLite.
		
		Intent intent = new Intent();
		intent.putExtra(ContactListActivity.CONTACT_NAME, name);
		intent.putExtra(ContactListActivity.CONTACT_PHONE, phone);
		setResult(RESULT_OK, intent);
		finish();
	 }
}
