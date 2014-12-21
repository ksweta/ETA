package com.eta;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.eta.util.ApplicationConstants;
import com.eta.util.ApplicationSharedPreferences;
import com.eta.util.Utility;

public class ContactListActivity extends Activity  implements 
LocationListener,
OnItemClickListener
{
   //Constants
   private static final String TAG = ContactListActivity.class.getSimpleName();
   private static final int CONTACT_PICKER_RESULT = 1503;
   private static final int ADD_CONTACT_RESULT = 1504;
   public static final String CONTACT_NAME = "CONTACT_NAME";
   public static final String CONTACT_PHONE = "CONTACT_PHONE";
   
   private ListView lvContacts;
   private View emptyView; 
   private ProgressDialog progressDialog;
   
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
      emptyView  = findViewById(R.id.tv_no_contact);
      lvContacts = (ListView)findViewById(R.id.lv_contact_list);
      //Setup Progress dialog for later use
      progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(true);
      progressDialog.setIndeterminate(true);
      
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
      Log.d(TAG, "Location provider : " + locationProvider);
      //Toast.makeText(this, "LocationProvider : " + locationProvider, Toast.LENGTH_SHORT).show();
      
      lvContacts.setOnItemClickListener(this);
      
      //Enable overflow menu for Android < 4.4.
      getOverflowMenu();
   }
   /**
    * This is a generic button onClick listener for 
    * all button present on current activity.
    * @param view
    */
   public void onClick(View view){
      
      switch(view.getId()) {
      case R.id.bt_contact_list_import_contact:
         importContact();
         Log.d(TAG, "Import contact button is pressed in ContactListActivity");
         break;

      case R.id.bt_contact_list_add_contact:
         addContact();
         Log.d(TAG, "Add contact button is pressed in ContactListActivity");
         break;

      default:
         Log.e(TAG, "onClick() control should come in default case");
      }
   }
   /**
    * Helper method to start System contact list activity.
    */
   private void importContact(){
      //Importing contact using Contact picker.
      Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
      intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
      startActivityForResult(intent, CONTACT_PICKER_RESULT);
   }
   /**
    * Helper method to start Add Contact activity.
    */
   private void addContact() {
      Intent intent = new Intent(this, AddContactActivity.class);
      startActivityForResult(intent, ADD_CONTACT_RESULT);
   }
   /**
    * Helper method to start invite sms activity.
    * @param phone Phone number of the person where invite sms will be sent.
    */
   private void inviteContact(String phone) {
      Intent intent = new Intent(this, InviteSmsActivity.class);
      intent.putExtra(ApplicationConstants.INVITE_SMS_PHONE, 
                      phone);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
      startActivity(intent);
   }
   /**
    * This is a helper method to logout/redirect to signin activity.
    */
   private void logOut() {
      Intent intent = new Intent(this, SignInActivity.class);
      startActivity(intent);
      finish();
   }
   /**
    * This is hack to enable overflow menu for API level lower than Android 4.4
    */
   private void getOverflowMenu() {
      if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
      try {
         ViewConfiguration config = ViewConfiguration.get(this);
         java.lang.reflect.Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
         if(menuKeyField != null) {
             menuKeyField.setAccessible(true);
             menuKeyField.setBoolean(config, false);
         }
     } catch (Exception e) {
         e.printStackTrace();
     }
   }
   @Override
   public void onActivityResult(int requestResult, int resultCode, Intent intent){

      if (resultCode == Activity.RESULT_OK){

         switch(requestResult) {
         case CONTACT_PICKER_RESULT:
            importContactResult(intent);
            break;

         case ADD_CONTACT_RESULT:
            saveNewContact(intent.getExtras().getString(CONTACT_NAME),
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
   private void importContactResult(Intent intent){
      
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
         String name = cursor.getString(nameColIndex);
         //Some cleanup job
         cursor.close();

         if (phone.length() >= ApplicationConstants.PHONE_NUMBER_LENGTH) {
            phone = Utility.purgePhoneNumber(phone);
         }
         //Make sure the phone number is not already in the contact list.
         if(db.isContactPresent(phone)) {
            Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Import Contact");
            alert.setMessage(phone+" already present in contact list");
            alert.setNegativeButton("OK", null);
            alert.show();
            return;
         }
         saveNewContact(name, phone);
      }
   }
   /**
    * This is a helper method to save the contact in conatact list.
    * 
    * @param name  Contact name.
    * @param phone Contact phone number.
    */
   private void saveNewContact(String name, String phone) {
      ContactDetails contact = new ContactDetails(name,
                                                  phone,
                                                  false,
                                                  new Date());

      long conId = db.insertContact(contact);
      if (conId > 0) {
         //If contact is added successfully then add it in the 
         //contactList and notify the adapter.
         contact.setId(conId);
         contactList.add(contact);
         contactListAdapter.notifyDataSetChanged();

         //Notify user.
         Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
         Log.i(TAG, "Contact added successfully");
         //Time to sync contact with ETA-Server.
         syncContact(contact);
      } else {
         Toast.makeText(this, "Couldn't add contact", Toast.LENGTH_SHORT).show();
         Log.e(TAG, "Couldn't add contact");
      }
   }
  
    /**
    * Helper method to sync the registration status of the contact and save in DB.
    * @param contacgt Contact details.
    */
   private void syncContact(ContactDetails contact){
      TransportService service = TransportServiceHelper.getTransportService();
      
      service.isReceipientRegistered(new ReceipientRegisteredRequest(contact.getPhone()),
                                     TransportService.HEADER_CONTENT_TYPE_JSON,
                                     TransportService.HEADER_ACCEPT_JSON,
                                     new ContactSyncCallback(this, 
                                                             db, 
                                                             contactList,
                                                             contactListAdapter,
                                                             contact));
   }

   private boolean isLocationAvailable(){
      return currentLocation != null;
   }
   
   @Override
   public void onLocationChanged(Location location) {
      currentLocation = location;
      Log.d(TAG, "onLocationChanged : " + location.toString());
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
                                            100, //Minimum time between update in milliseconds
                                            1, //Minimum distance in meters
                                            this);
   }

   @Override 
   public void onBackPressed() {
      AlertDialog alert = new AlertDialog.Builder(this).create();
      alert.setTitle("Logout");
      alert.setIcon(R.drawable.logout);
      alert.setMessage("Do you really want to logout?");
      alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new OnClickListener(){
     
         @Override
         public void onClick(DialogInterface dialog, int which) {
            finish();
         }
      });
 
      alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
         
         @Override
         public void onClick(DialogInterface dialog, int which) {
            //NOP
         }
      });
      
      alert.show();
   }
   /* Remove the locationlistener updates when Activity is paused */
   @Override
   protected void onPause() {
     super.onPause();
     locationManager.removeUpdates(this);
   }
   
   @Override
   public void onContentChanged() {
      super.onContentChanged();
      //For empty contact list.
      lvContacts = (ListView)findViewById(R.id.lv_contact_list);
      emptyView  = findViewById(R.id.tv_no_contact);
      lvContacts.setEmptyView(emptyView);
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      //Inflate the menu items for use in the action bar
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.contact_activity_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }
   
   @Override 
   public boolean onOptionsItemSelected(MenuItem item) {
      //Handle presses on the action bar items
      switch(item.getItemId()) {
      case R.id.action_add_contact:
         Log.d(TAG, "Add contact action is pressed");
         addContact();
         return true;
         
      case R.id.action_import_contact:
         Log.d(TAG, "Import contact action is pressed");
         importContact();
         return true;
         
      case R.id.action_invite:
         Log.d(TAG, "Invite sms action is pressed");
         inviteContact("");
         return true;
            
      case R.id.action_logout:
         Log.d(TAG, "Logout action is pressed");
         logOut();
         return true;
            
       default:
            return super.onOptionsItemSelected(item);
      }
   }
   
   @Override
   public void onItemClick(AdapterView<?> parent, 
                           View view,
                           int position, 
                           long id) {

      final ContactDetails cd = contactList.get(position);
      
      
      AlertDialog alert = new AlertDialog.Builder(this).create();
      
      alert.setTitle("Delete contact");
      alert.setMessage("Do you really want to delete " + cd.getName() + "(" +cd.getPhone() +")?");
      alert.setIcon(R.drawable.delete);
      alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new OnClickListener() {
         
         @Override
         public void onClick(DialogInterface dialog, int which) {
            //If ok is clicked, delete the contact.
            if(db.deleteContact(cd) != 0) {
               contactList.remove(cd);
               contactListAdapter.notifyDataSetChanged();
               Toast.makeText(getApplicationContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
            } else {
               Toast.makeText(getApplicationContext(), "Couldn't delete contact", Toast.LENGTH_SHORT).show();
            }
         }
      });
      
      alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
         
         @Override
         public void onClick(DialogInterface dialog, int which) {
          //Do nothing.
         }
      });
      alert.show();
   }
   /////////////////////// Inner classes /////////////////////////////

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
         updateContact();
         if(progressDialog.isShowing()) {
            progressDialog.dismiss();
         }
         Response response = error.getResponse();
         if(response != null && response.getStatus() == TransportService.RESPONSE_NOT_FOUND) {
            Toast.makeText(context,
                           String.format("Contact %s(%s) is not yet registered.", contact.getName(), contact.getPhone()),
                           Toast.LENGTH_SHORT).show();
         } else {
            Toast.makeText(context, 
                           "Error while syncing contact\n" + error.getMessage(), 
                           Toast.LENGTH_SHORT).show();
         }
      }

      @Override
      public void success(Void voidresult, Response response) {

         if(response.getStatus() == TransportService.RESPONSE_STATUS_OK) {
            contact.setRegistered(true);
            updateContact();
            Toast.makeText(context,
                  String.format("Contact %s(%s) is synced", contact.getName(), contact.getPhone()),
                  Toast.LENGTH_SHORT).show();
         }
         Log.d(TAG, "Status Code : " + response.getStatus() + ", body : " + response.getBody());
         if(progressDialog.isShowing()) {
            progressDialog.dismiss();
         }
      }
      
     private void updateContact() {
        //Put the sync date first.
        contact.setSyncDate(new Date());
        if(db.updateContact(contact) > 0) {
           //Notifiy Adapter about change.
           contactListAdapter.notifyDataSetChanged();
           
        } else {
           Toast.makeText(context, 
                          "Couldn't update Contact " + contact.getPhone(), 
                          Toast.LENGTH_SHORT).show();
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
         if(progressDialog.isShowing()) {
            progressDialog.dismiss();
         }
      }

      @Override
      public void success(Void voidResult, Response response) {
         
         if(response.getStatus() == TransportService.RESPONSE_STATUS_OK){
            Toast.makeText(context, "ETA sent successfully to " + receiverPhone, Toast.LENGTH_SHORT).show();
         }
            
        Log.d(TAG, "Status Code : " + response.getStatus() + ", body : " + response.getBody());
        if(progressDialog.isShowing()) {
           progressDialog.dismiss();
        }
      }
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
               
               if(!isLocationAvailable()) {
                  currentLocation = locationManager.getLastKnownLocation(locationProvider);
               }
               Log.d(TAG, "Location : " + currentLocation);
              
                  if (contactList.get(position).isRegistered()) {
                     if(isLocationAvailable()) {
                     Log.d(TAG,  "Location : " + Utility.getLatLng(context, 
                           locationManager.getLastKnownLocation(locationProvider)));
                     //Send notification.
                     sendETANotification(locationManager.getLastKnownLocation(locationProvider), 
                                         contactList.get(position).getPhone());
                     } else {
                        Toast.makeText(context, 
                                       "Couldn't send ETA because location is not available", 
                                       Toast.LENGTH_SHORT).show();
                     }
                  } else {
                     updateUnregisteredContact(contactList.get(position));
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
         //Show progress bar, and dismiss it in Retrofit callback methods.
         progressDialog.show();
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
      private void updateUnregisteredContact(final ContactDetails contact){
         
         AlertDialog alert = new AlertDialog.Builder(context).create();
         
         alert.setTitle("Sync Contact");
         alert.setMessage("Sync/Invite contact " + contact.getName() + "(" +contact.getPhone() +")");
         alert.setButton(AlertDialog.BUTTON_POSITIVE,
                         "Sync", new OnClickListener() {
                           
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                              //Show progress bar, and dismiss it in Retrofit callback methods.
                              progressDialog.show();
                              TransportService service = TransportServiceHelper.getTransportService();
                              ReceipientRegisteredRequest request = new ReceipientRegisteredRequest(contact.getPhone());
                              service.isReceipientRegistered(request,
                                                             TransportService.HEADER_CONTENT_TYPE_JSON, 
                                                             TransportService.HEADER_ACCEPT_JSON, 
                                                             new ContactSyncCallback(context,
                                                                                     db,
                                                                                     contactList,
                                                                                     contactListAdapter,
                                                                                     contact));
                           }
                        });
         alert.setButton(AlertDialog.BUTTON_NEUTRAL, 
                         "Invite", 
                         new OnClickListener() {
                           
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                              Intent intent = new Intent(context, InviteSmsActivity.class);
                              intent.putExtra(ApplicationConstants.INVITE_SMS_PHONE, 
                                              contact.getPhone());
                              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                              startActivity(intent);
                           }
                        });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
         
         @Override
         public void onClick(DialogInterface dialog, int which) {
            //No Operation(NOP) 
         }
      }); 
        
      alert.show();
      }
   }
}