package com.eta;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
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
import com.eta.transport.ETANotificationRequest;
import com.eta.transport.ReceipientRegisteredRequest;
import com.eta.transport.TransportService;
import com.eta.transport.TransportServiceHelper;
import com.eta.util.ApplicationSharedPreferences;
import com.eta.util.Utility;

public class ContactListActivity extends Activity  implements 
LocationListener {
   //Constants
   private static final String TAG = ContactListActivity.class.getSimpleName();
   private static final int CONTACT_PICKER_RESULT = 1503;
   private static final int ADD_CONTACT_RESULT = 1504;
   public static final String CONTACT_NAME = "CONTACT_NAME";
   public static final String CONTACT_PHONE = "CONTACT_PHONE";
   
   private ListView lvContacts;
   private List<ContactDetails> contactList;
   private DBHelper db;
   private ContactListAdapter contactListAdapter;
   //This reference will get updated many places whenever
   //application detects that the location has changed.
   private Location currentLocation;
   private LocationManager locationManager;
   private String locationProvider;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contact_list);
      lvContacts = (ListView)findViewById(R.id.lv_contact_list);

      db = new DBHelper(this);
      contactList = db.readAllContacts();
      contactListAdapter = new ContactListAdapter(this, R.layout.contact_list_item);
      lvContacts.setAdapter(contactListAdapter);
       
      //If GPS is disabled then show an alert to User.
      if(!Utility.isGpsEnabled(this)){
         Utility.getGpsDisableAlert(this).show();
      }
      
      //Get the location manager.
      locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
      locationProvider = locationManager.getBestProvider(new Criteria(), true);
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

   @Override
   public void onActivityResult(int requestResult, int resultCode, Intent intent){

      if (resultCode == Activity.RESULT_OK){

         switch(requestResult) {
         case CONTACT_PICKER_RESULT:
            importContact(intent);
            break;

         case ADD_CONTACT_RESULT:
            syncContact(intent.getExtras().getString(CONTACT_NAME),
                  intent.getExtras().getString(CONTACT_PHONE));	
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
    * This method gets the Contact URI in intent and uses it to fetch the 
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
         String name = "";
         if (nameColIndex > -1){
            name = cursor.getString(nameColIndex);
         }
         //Some cleanup job
         cursor.close();

         //Make sure the phone number is not already in the contact list.
         if(db.isContactPresent(phone)) {
            Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Import Contact");
            alert.setMessage(phone+" already present in contact list");
            alert.setNegativeButton("OK", null);
            alert.show();
            return;
         }

         syncContact(name, Utility.purgePhoneNumber(phone));

      }
   }
   /**
    * Helper method to sync the registration status of the contact and save in DB.
    * @param contact
    */
   private void syncContact(String name, String phone){

      ContactDetails contact = new ContactDetails(name,
                                                  phone,
                                                  false,
                                                  new Date());
      
      TransportService service = TransportServiceHelper.getTransportService();
      
      service.isReceipientRegistered(new ReceipientRegisteredRequest(phone),
                                     TransportService.HEADER_CONTENT_TYPE_JSON,
                                     TransportService.HEADER_ACCEPT_JSON,
                                     new ContactSyncCallback(this, 
                                                             db, 
                                                             contactList, 
                                                             contactListAdapter, 
                                                             contact));
   }


   class ContactListAdapter extends BaseAdapter {
      Context context;
      LayoutInflater inflater;
      // contact list item layout
      int layout;

      public ContactListAdapter(Context c, int l) {
         context = c;
         inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         layout = l;
      }
      
      @Override
      public int getCount() {
         return contactList.size();
      }
      
      @Override
      public String getItem(int position) {
         return contactList.get(position).getName();
      }
      
      @Override
      public long getItemId(int position) {
         return position;
      }

      @Override
      public View getView(final int position, View convertView, ViewGroup parent) {
         if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
         }

         TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
         tvName.setText(contactList.get(position).getName());

         TextView tvPhone = (TextView) convertView.findViewById(R.id.tv_phone);
         tvPhone.setText(contactList.get(position).getPhone());

         ImageView ivStatus = (ImageView) convertView.findViewById(R.id.iv_status);
         ivStatus.setImageResource(contactList.get(position).isRegistered() ? R.drawable.registered : R.drawable.not_registered);

         ImageView btn = (ImageView) convertView.findViewById(R.id.iv_send);

         btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
               String location = "";
               Log.d(TAG, "Phone : " + contactList.get(position).getPhone());
               if(isLocationAvailable()) {
                  if (contactList.get(position).isRegistered()) {
                     location = Utility.getLatLng(context, locationManager.getLastKnownLocation(locationProvider));
                     Log.d(TAG,  "Location : " + location);
                     sendETANotification(locationManager.getLastKnownLocation(locationProvider), 
                                          contactList.get(position).getPhone());
                  } else {
                     Toast.makeText(context,
                                    "Couldn't send ETA because receiver's phone is not registered", 
                                    Toast.LENGTH_SHORT).show();
                  }
               } else {
                  Toast.makeText(context, 
                                 "Couldn't send ETA because location is not available", 
                                 Toast.LENGTH_SHORT).show();
               }
                
            }
         });

         return convertView;
      }
      /**
       * This method sends ETA notification request to ETA-Server.
       * @param location
       */
      private void sendETANotification(Location location, String receiverPhone) {
         TransportService service = TransportServiceHelper.getTransportService();
         String senderPhone = Utility.purgePhoneNumber(Utility.getDevicePhoneNumber(context));
         //Get User name from shared preferences.
         String senderName = ApplicationSharedPreferences.getUserName(context);
         ETANotificationRequest request = new ETANotificationRequest(receiverPhone, 
                                                                     senderPhone,
                                                                     senderName,
                                                                     //src Latitude
                                                                     location.getLatitude(),
                                                                     //src Longitude
                                                                     location.getLongitude(),
                                                                     //dst Latitude
                                                                     0.0D, 
                                                                     //dst Longitude
                                                                     0.0D, 
                                                                     //ETA
                                                                     0);
         service.sendETA(request,
                         TransportService.HEADER_CONTENT_TYPE_JSON,
                         TransportService.HEADER_ACCEPT_JSON, 
                         new SendETACallback(context, receiverPhone));
      }
   }


 


   private class ContactSyncCallback implements Callback<Void> {
      private final Context context;
      private final DBHelper db;
      private final List<ContactDetails> contactList;
      private final ContactListAdapter contactListAdapter;
      private final ContactDetails contact;

      public ContactSyncCallback(Context c, 
            DBHelper db, 
            List<ContactDetails> contactList, 
            ContactListAdapter contactListAdapter,
            ContactDetails contact) {
         this.context = c;
         this.db = db;
         this.contactList = contactList;
         this.contactListAdapter = contactListAdapter;
         this.contact = contact;
      }

      @Override
      public void failure(RetrofitError error) {
         contact.setRegistered(false);
         insertContact();
      }

      @Override
      public void success(Void voidresult, Response response) {

         if(response.getStatus() == TransportService.RESPONSE_STATUS_OK) {
            contact.setRegistered(true);
            insertContact();
         }

         Log.d(TAG, "Status Code : " + response.getStatus() + ", body : " + response.getBody());
      }

      private void insertContact() {
         //Put the current time in the syncDate field.
         contact.setSyncDate(new Date());
         if (db.insertContact(contact)) {
            //If contact is added successfully then add it in the 
            //contactList and notify the adapter.

            contactList.add(contact);
            contactListAdapter.notifyDataSetChanged();

            //Notify user.
            Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Contact added successfully");
         } else {
            Toast.makeText(context, "Couldn't add contact", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Couldn't add contact");
         }
      }
   }
   
   
   private class SendETACallback implements Callback<Void> {
      private Context context;
      private String receiverPhone;
      public SendETACallback(Context context, String phone) {
         this.context = context;
         this.receiverPhone = phone;
      }
      @Override
      public void failure(RetrofitError error) {
         Toast.makeText(context, 
                        "Server error : " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
         Log.e(TAG, error.getMessage(), error.getCause());
      }

      @Override
      public void success(Void voidResult, Response response) {
         
         if(response.getStatus() == TransportService.RESPONSE_STATUS_OK){
            Toast.makeText(context, "ETA sent successfully to " + receiverPhone, Toast.LENGTH_SHORT).show();
         }
            
        Log.d(TAG, "Status Code : " + response.getStatus() + ", body : " + response.getBody());
      }
   }


   private boolean isLocationAvailable(){
      return currentLocation != null;
   }
   
   @Override
   public void onLocationChanged(Location location) {
      currentLocation = location;
   }

   @Override
   public void onStatusChanged(String provider, int status, Bundle extras) {
     Log.d(TAG, "Provider " + provider + " status changed to " + status);
   }

   @Override
   public void onProviderEnabled(String provider) {
      Log.d(TAG, "Provider " + provider + " is enabled");
   }

   @Override
   public void onProviderDisabled(String provider) {
     Log.d(TAG, "Provider " + provider + " is disabled");
   }
   
   /* Request updates at startup */
   @Override
   protected void onResume() {
     super.onResume();
     locationManager.requestLocationUpdates(locationProvider, 
                                            1000, //Minimum time between update in milliseconds
                                            5, //Minimum distance in meters
                                            this);
   }

   /* Remove the locationlistener updates when Activity is paused */
   @Override
   protected void onPause() {
     super.onPause();
     locationManager.removeUpdates(this);
   }
}
