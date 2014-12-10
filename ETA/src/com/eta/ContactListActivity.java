package com.eta;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eta.data.ContactDetails;
import com.eta.db.DBHelper;
import com.eta.util.Utility;

public class ContactListActivity extends Activity {
	private static final String TAG = ContactListActivity.class.getSimpleName();
	private static final int CONTACT_PICKER_RESULT = 1503;
	private static final int ADD_CONTACT_RESULT = 1504;
	public static final String CONTACT_NAME = "CONTACT_NAME";
	public static final String CONTACT_PHONE = "CONTACT_PHONE";
	private ListView lvContacts;
	private List<ContactDetails> contactList;
	private DBHelper db;
	private ContactListAdapter contactListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		lvContacts = (ListView)findViewById(R.id.lv_contact_list);
		db = new DBHelper(this);
		contactList = db.readAllContacts();
		contactListAdapter = new ContactListAdapter(this, R.layout.contact_list_item);
		lvContacts.setAdapter(contactListAdapter);
	}
	
	public void onClick(View view){
		Intent intent;
		switch(view.getId()) {
		case R.id.bt_contact_list_import_contact:
			//Importing contact using Contact picker.
			intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
			startActivityForResult(intent, CONTACT_PICKER_RESULT);
			Log.d(TAG, "Import Contact is pressed in ContactListActivity");
			break;
			
		case R.id.bt_contact_list_add_contact:
			intent = new Intent(this, AddContactActivity.class);
			startActivityForResult(intent, ADD_CONTACT_RESULT);
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
					importContact(intent);
					break;
				case ADD_CONTACT_RESULT:
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
	private void importContact(Intent intent){
		Uri uri = intent.getData();
		Log.d(TAG, "Contact URI : " + uri.toString());
		
		Cursor cursor = getContentResolver().query(uri,
										   		   null,
										           null,
										           null,
										           null);
		if (cursor != null) {
			
			int phoneColIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			int nameColIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			cursor.moveToFirst();
			String phone = cursor.getString(phoneColIndex);
			String name = null;
			if (nameColIndex > 0){
				name = cursor.getString(nameColIndex);
			}
			//Some cleanup job
			cursor.close();
			
			ContactDetails contact = new ContactDetails(name,
														//Purge the phone number
														Utility.purgePhoneNumber(phone),
														//TODO replace this with ETA-server return value
														false,
														new Date());
			insertContact(contact);
			
		}
	}
	/**
	 * This method extract name and phone number present in the intent returned by AddContact
	 * activity. 
	 * @param intent
	 */
	private void addContact(Intent intent){
		Bundle extra = intent.getExtras();
		String name = extra.getString(CONTACT_NAME);
		String phone = extra.getString(CONTACT_PHONE);
		ContactDetails contact = new ContactDetails(name,
													phone,
													//TODO replace this with ETA-Server return value
													false,
													new Date());
		
		insertContact(contact);		
	}
	/**
	 * Helper method to insert ContactDetails object in db. 
	 * @param contact
	 */
	private void insertContact(ContactDetails contact){
		if (db.insertContact(contact)) {
			//If contact is added successfully then add it in the 
			//contactList and notify the adapter.
			contactList.add(contact);
			contactListAdapter.notifyDataSetChanged();
			
			//Notify user.
			Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "Contact added successfully");
		} else {
			Toast.makeText(this, "Couldn't add contact", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Couldn't add contact");
		}
	}
	
	class ContactListAdapter extends BaseAdapter {
		Context context;
		LayoutInflater Inflater;
		int layout;

		public ContactListAdapter(Context c, int alayout) {
			context = c;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = alayout;
		}

		public int getCount() {
			return contactList.size();
		}

		public String getItem(int position) {
			return contactList.get(position).getName();
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);
			}

			TextView tvName = (TextView) convertView
					.findViewById(R.id.tv_name);
			tvName.setText(contactList.get(position).getName());

			TextView tvPhone = (TextView) convertView
					.findViewById(R.id.tv_phone);
			tvPhone.setText(contactList.get(position).getPhone());

			ImageView ivStatus = (ImageView) convertView
					.findViewById(R.id.iv_status);
			ivStatus.setImageResource(contactList.get(position).isRegistered() ? R.drawable.green
					: R.drawable.red);

			ImageView btn = (ImageView) convertView.findViewById(R.id.iv_send);
			btn.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Toast.makeText(context,
							contactList.get(pos).getPhone(),
							Toast.LENGTH_SHORT).show();
				}
			});

			return convertView;
		}
	}
	
}
