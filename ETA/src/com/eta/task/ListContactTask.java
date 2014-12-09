package com.eta.task;

import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;

import com.eta.ContactListActivity;
import com.eta.data.ContactDetails;
import com.eta.db.DBHelper;

public class ListContactTask extends AsyncTask<Void, Void, List<ContactDetails>> {
	private ContactListActivity contactActivity;
	private FragmentTransaction fragmentTransaction;
	
	public ListContactTask(ContactListActivity activity, FragmentTransaction ft) {
		contactActivity = activity;
		fragmentTransaction = ft;
	}
	
	@Override
	protected List<ContactDetails> doInBackground(Void... params) {
		DBHelper db = new DBHelper(contactActivity);
		Cursor cursor = db.readAllContacts();
		List <ContactDetails> contactList = new LinkedList<ContactDetails>();
		
		if (cursor != null) {
			//Get the index of the various columns. This helps to get 
			// the column value from the cursor object.
			int idColIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
			int nameColIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
			int phoneColIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
			int registeredColIndex = cursor.getColumnIndex(DBHelper.COLUMN_REGISTERED);
			
			cursor.moveToFirst();
			
			while(cursor.moveToNext()) {
				ContactDetails contact = new ContactDetails(cursor.getLong(idColIndex),
															cursor.getString(nameColIndex),
															cursor.getString(phoneColIndex),
															cursor.getInt(registeredColIndex) == 1);
				contactList.add(contact);
			}
			cursor.close();
		}
		//Close the Database connection.
		db.close();
		return contactList;
	}
	
	 protected void onPostExecute(List<ContactDetails> result) {  
	    	contactActivity.addContactListFragment(fragmentTransaction, result);
	 }
}
