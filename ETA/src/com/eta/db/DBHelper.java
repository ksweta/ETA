package com.eta.db;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eta.data.ContactDetails;

/**
 * This a helper class which method to access SQLite database access.
 * 
 *
 */
public class DBHelper extends SQLiteOpenHelper {
   private static final String TAG = DBHelper.class.getSimpleName();
   public static final String TABLE_NAME_CONTACTS = "contacts";
   public static final String COLUMN_ID = "_id";
   public static final String COLUMN_NAME = "name";
   public static final String COLUMN_PHONE = "phone"; 
   public static final String COLUMN_REGISTERED = "registered";
   public static final String COLUMN_SYNC_DATE = "syncDate";
   private static final String DATABASE_NAME = "contact_details.db";
   private static final int DATABASE_VERSION = 1;

   /**
    * SQLite stores the date as string format, so need SimpleDateFormat
    * to convert string to date object.
    */
   private SimpleDateFormat simpleDateFormat;

   private static final String DATABASE_CREATE_CONTACT_TABLE = "CREATE TABLE "+ TABLE_NAME_CONTACTS + 
         "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
         + COLUMN_NAME + " TEXT NOT NULL, "
         + COLUMN_PHONE + " TEXT NOT NULL, "
         + COLUMN_REGISTERED + " BOOL NOT NULL,"
         + COLUMN_SYNC_DATE + " TEXT NOT NULL);";


   public DBHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
      simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTACTS);
      onCreate(db);
   }

   public long insertContact(ContactDetails contact) {

      long result = getWritableDatabase().insert(DBHelper.TABLE_NAME_CONTACTS, 
                                                 null, 
                                                 getValues(contact));
      Log.d(TAG, "InsertData->result : " + result);
      return result ;
   }

   public List<ContactDetails> readAllContacts() {
      //Get all columns from Contacts  table. 
      String[] allColumns = new String[] {DBHelper.COLUMN_ID, 
                                          DBHelper.COLUMN_NAME,
                                          DBHelper.COLUMN_PHONE,
                                          DBHelper.COLUMN_REGISTERED,
                                          DBHelper.COLUMN_SYNC_DATE};

      Cursor cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS,
                                                  allColumns,
                                                  null,
                                                  null,
                                                  null,
                                                  null,
                                                  null);
      List<ContactDetails> contactList = new LinkedList<ContactDetails>();
      if(cursor != null) {
         //cursor.moveToFirst();

         // Get the index of the various columns. This helps to get 
         // the column value from the cursor object.
         int idColIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
         int nameColIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
         int phoneColIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);
         int registeredColIndex = cursor.getColumnIndex(DBHelper.COLUMN_REGISTERED);
         int syncDateIndex = cursor.getColumnIndex(DBHelper.COLUMN_SYNC_DATE);


         while(cursor.moveToNext()) {
            //simpleDateFormat.parse() throws exception, necessary to have try-catch block here.
            try {

               ContactDetails contact = new ContactDetails(cursor.getLong(idColIndex),
                                                           cursor.getString(nameColIndex),
                                                           cursor.getString(phoneColIndex),
                                                           cursor.getInt(registeredColIndex) == 1,
                                                           simpleDateFormat.parse(cursor.getString(syncDateIndex)));

               contactList.add(contact);
            } catch(Exception e) {
               Log.e(TAG, e.getMessage(), e);
            }
         }

         //Close the DB connection once work is done.
         cursor.close();
      }
      return contactList;
   }

   public int updateContact(ContactDetails contact) {
      return getWritableDatabase().update(DBHelper.TABLE_NAME_CONTACTS, 
            getValues(contact),
            //Where clause
            DBHelper.COLUMN_ID + "=?",
            new String[] {contact.getId().toString()});
   }

   public int deleteContact(ContactDetails contact){
      return getWritableDatabase().delete(DBHelper.TABLE_NAME_CONTACTS, 
            DBHelper.COLUMN_ID + "=?", 
            new String[]{contact.getId().toString()});
   }

   /**
    * This method check if the given phone number is already present in the contact list.
    * @param phone Contact's phone number.
    * @return returns true if the phone number is already present in database.
    */
   public boolean isContactPresent(String phone) {
      String[] columns = new String[] {DBHelper.COLUMN_ID};
      String[] selectionArgs = new String[]{phone};
      Cursor cursor = null;
      try {

         cursor = getReadableDatabase().query(DBHelper.TABLE_NAME_CONTACTS, 
               columns, 
               DBHelper.COLUMN_PHONE + "=?", 
               selectionArgs, 
               null, 
               null, 
               null);

         if(cursor == null) {
            Log.e(TAG, "isContactPresent(): Serious problem, cursor is null");
         }
         if (cursor.getCount() > 0) {

            return true;
         } else {
            return false;
         }
      } finally {
         if(cursor != null) {
            cursor.close();
         }
      }
   }
   /**
    * This is a helper method to convert ContactDetails object to ContentValues
    * This contentValues object is used in various CRUD methods.
    * @param contact
    * @return
    */
   private ContentValues getValues(ContactDetails contact){
      ContentValues values = new ContentValues();
      values.put(DBHelper.COLUMN_NAME, contact.getName());
      values.put(DBHelper.COLUMN_PHONE, contact.getPhone());
      values.put(DBHelper.COLUMN_REGISTERED, contact.getRegistered());
      values.put(DBHelper.COLUMN_SYNC_DATE, simpleDateFormat.format(contact.getSyncDate()));
      return values;
   }
}
