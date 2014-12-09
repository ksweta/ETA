package com.eta;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eta.data.ContactDetails;
import com.eta.db.DBHelper;

public class ContactListActivity extends Activity {
	private static final String TAG = ContactListActivity.class.getSimpleName();
	
	private static final int CONTACT_PICKER_RESULT = 1503;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
	}

	/**
	 * This method will be used the ListContactTask to add the ContactListFragment in 
	 * this activity.
	 * @param fragmentTransaction
	 * @param result
	 */
	public void addContactListFragment(FragmentTransaction fragmentTransaction,
			List<ContactDetails> result) {
		//TODO Need to add code which will add the contactListFragment to current activity
	}
	
	public void onClick(View view){
		Intent intent;
		switch(view.getId()) {
		case R.id.bt_contact_list_import_contact:
			//Importing contact using Contact picker.
			intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			startActivityForResult(intent, CONTACT_PICKER_RESULT);
			Log.d(TAG, "Import Contact is pressed in ContactListActivity");
			break;
			
		case R.id.bt_contact_list_add_contact:
			intent = new Intent(this, AddContactActivity.class);
			startActivity(intent);
			Log.d(TAG, "Add contact button is pressed in ContactListActivity");
			break;
			
		default:
			Log.e(TAG, "onClick() control should come in default case");
		}
	}
	
	public void onActivityResult(int requestResult, int resultCode, Intent intent){
		       
		if (resultCode == RESULT_OK){
			switch(requestResult) {
				case CONTACT_PICKER_RESULT:
					addContact(intent);
					break;
				default:
					Log.w(TAG, "Different requestResult : "+requestResult);
			}
		} else {
			//User didn't select the contact correctly
			Log.w(TAG, "Contact picker activity result NOT OK");
		}
	}
	/**
	 * This method get the Contact URI in intent and uses it to fetch the 
	 * contact details from Contact content provider. It insert the contacts
	 * details in the its own application db.
	 * @param intent
	 */
	private void addContact(Intent intent){
		Uri result = intent.getData();
		Log.d(TAG, "Contact URI : " + result.toString());
		String[] allColumns = new String[] {Data.DISPLAY_NAME, Phone.NUMBER};
		Cursor cursor = getContentResolver().query(result,
										   		   allColumns,
										           null,
										           null,
										           null);
		if (cursor != null) {
			int nameColIndex = cursor.getColumnIndex(Data.DISPLAY_NAME);
			int phoneColIndex = cursor.getColumnIndex(Phone.NUMBER);
			cursor.moveToFirst();
			String name = cursor.getString(nameColIndex);
			String phone = cursor.getString(phoneColIndex);
			//TODO need to verify if  the phone number is registered.
			ContactDetails contact = new ContactDetails(name,
														phone,
														//TODO replace this with ETA-server return value
														true);
			insertContact(contact);
		}
	}
	/**
	 * Helper method to insert ContactDetails object in db. 
	 * @param contact
	 */
	private void insertContact(ContactDetails contact){
		DBHelper db = new DBHelper(this);
		if (db.insertContact(contact)) {
			Toast.makeText(this, "Added contact successfully", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "Added the contact successfully");
		} else {
			Toast.makeText(this, "Coudn't add the contact", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Coudn't add the contact");
		}
	}
}
