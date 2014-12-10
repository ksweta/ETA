package com.eta.task;

import java.util.List;

import android.os.AsyncTask;

import com.eta.ContactListActivity;
import com.eta.data.ContactDetails;
import com.eta.db.DBHelper;
/**
 * Accessing SQLite DB can be slow if there are too many contacts. Slow query 
 * can block the UI and android can kill the Activity. This AsyncTask provide a 
 * way to asynchronously fetch all contacts from SQLite.
 *
 */
public class ListContactTask extends AsyncTask<Void, Void, List<ContactDetails>> {
	private ContactListActivity contactActivity;	
	public ListContactTask(ContactListActivity activity) {
		contactActivity = activity;
	}
	
	@Override
	protected List<ContactDetails> doInBackground(Void... params) {
		List <ContactDetails> contactList =  new DBHelper(contactActivity).readAllContacts();
		return contactList;
	}
	
	 protected void onPostExecute(List<ContactDetails> result) {  
	    	//TODO call some method in ContactListActivity to update the contact.
	 }
}
