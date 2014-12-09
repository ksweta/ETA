package com.eta.db;

import com.eta.data.ContactDetails;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This a helper class which method to access SQLite database access.
 * 
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = DBHelper.class.getSimpleName();
	public static final String TABLE_CONTACTS = "contacts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_PHONE = "phone"; 
	public static final String COLUMN_REGISTERED = "registered";
	
	private static final String DATABASE_NAME = "contact_details.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE_CONTACT_TABLE = "CREATE TABLE "+ TABLE_CONTACTS + 
			  "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			  + COLUMN_NAME + " TEXT NOT NULL, "
			  + COLUMN_PHONE + " TEXT NOT NULL, "
			  + COLUMN_REGISTERED + " BOOL NOT NULL );";
	
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//Create Contact table
		db.execSQL(DATABASE_CREATE_CONTACT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//TODO Dropping the table and recreating is not a good idea.
		// need to write a better logic to handle DB upgrade.
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		onCreate(db);
	}
	
	public boolean insertContact(ContactDetails contact) {
		
		long result = getWritableDatabase().insert(DBHelper.TABLE_CONTACTS, 
				                                   null, 
				                                   getValues(contact));
		Log.d(TAG, "InsertData->result : " + result);
		return result > 0 ;
	}
	
	public Cursor readAllContacts() {
		//Get almost all column from Contacts  table. 
		String[] allColumns = new String[] {DBHelper.COLUMN_ID, 
				                            DBHelper.COLUMN_NAME,
				                            DBHelper.COLUMN_PHONE,
				                            DBHelper.COLUMN_REGISTERED};
		
		Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_CONTACTS,
								  			        allColumns,
								  			        null,
								  			        null,
								  			        null,
								  			        null,
								  			        null);
		
		return cursor;
	}
	
	public int updateContact(ContactDetails contact) {
		return getWritableDatabase().update(DBHelper.TABLE_CONTACTS, 
							   				getValues(contact),
							   				//Where clause
							   				DBHelper.COLUMN_ID + "=?",
							   				new String[] {contact.getId().toString()});
	}
	
	public void deleteContact(ContactDetails contact){
		getWritableDatabase().delete(DBHelper.TABLE_CONTACTS, 
					    			 DBHelper.COLUMN_ID + "=?", 
					    			 new String[]{contact.getId().toString()});
	}
	
	
	/**
	 * This is a helper method to convert ContactDetails object to ContentValues
	 * This contentValues object will be used in various CRUD methods.
	 * @param contact
	 * @return
	 */
	private ContentValues getValues(ContactDetails contact){
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_NAME, contact.getName());
		values.put(DBHelper.COLUMN_PHONE, contact.getPhone());
		values.put(DBHelper.COLUMN_REGISTERED, contact.getRegistered());
		return values;
	}
}
