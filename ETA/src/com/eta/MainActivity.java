package com.eta;

import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private final static String TAG = MainActivity.class.getSimpleName();
	public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	
	//server project ID 
    String GCM_SERVER_PROJECT_ID = "177762432832";
    
	private GoogleCloudMessaging gcm;
	private String gcmClientRegId;
	
	 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
     // Check device for Play Services APK.
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            gcm = GoogleCloudMessaging.getInstance(this);
            gcmClientRegId = getRegistrationId(this);
            
            if (gcmClientRegId.isEmpty()) {
                registerInBackground(this);
            }
        
        }
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        //need to check Play Services APK  here too.
        checkPlayServices();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View view){
		Intent intent = null;
		switch(view.getId()){

		case R.id.button1:
			 intent = new Intent(this, ContactListActivity.class);
			break;


		case R.id.button2:
			intent = new Intent(this, SendETAActivity.class);
			break;

		case R.id.button3:
			intent = new Intent(this, ViewETAActivity.class);
			break;
		
		case R.id.button4:
			intent = new Intent(this, RegistrationActivity.class);
			break;
		case R.id.button5:
			String clientRegistrationid = getRegistrationId(this);
		    Toast.makeText(this, clientRegistrationid, Toast.LENGTH_SHORT).show();
			Log.d(TAG, getRegistrationId(this));
			break;
		default:
			Toast.makeText(this, "There is no such button", Toast.LENGTH_SHORT).show();
			return;
		}
		if (intent != null) {
			startActivity(intent);
		}
		
	}
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
        
    private boolean checkPlayServices(){
    	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, 
                									  this,
                									  PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported."); 
                // if Google play service is not found and error is not 
                // recoverable, then show message to user and close
                // the application.
                Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Google Play Service missing");
                alert.setMessage("This device is not supported");
                alert.setNegativeButton("OK", null);
                alert.show();
                finish();
            }
            return false;
        }
        return true;
    }
    
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    
    private String getRegistrationId(Context context) {
    	final SharedPreferences prefs = getGCMPreferences(context);
    	String registrationId = prefs.getString(PROPERTY_GCM_REG_ID, "");
    	if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
    	// Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                					Context.MODE_PRIVATE);
    }
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final Context context) {
    	//AsyncTask is used for registering to GCM service.
        new AsyncTask<Void, Void, String>() {
        	
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    // It takes the server's id and registers using this id
                    gcmClientRegId = gcm.register(GCM_SERVER_PROJECT_ID);
                    msg = "Device registered, registration ID=" + gcmClientRegId;
                    
                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, gcmClientRegId);

                    //Defering the sending of registration ID to backend server
                    //Once user completes account registration then application will 
                    //send the GCM-client-registration-ID with other information.
    
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	//Show Toast message once GCM registration is done
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }
    /**
    * Sends the registration ID to your server over HTTP
    */
   private void sendRegistrationIdToBackendServer(String gcmRegistrationId) {
       //TODO: This method will send the GCM registration ID to backend server
   }
   
   /**
    * Stores the registration ID and app versionCode in the application's
    * {@code SharedPreferences}.
    *
    * @param context application's context.
    * @param regId registration ID
    */
   private void storeRegistrationId(Context context, String regId) {
       final SharedPreferences prefs = getGCMPreferences(context);
       int appVersion = getAppVersion(context);
       Log.i(TAG, "Saving regId on app version " + appVersion);
       SharedPreferences.Editor editor = prefs.edit();
       editor.putString(PROPERTY_GCM_REG_ID, regId);
       editor.putInt(PROPERTY_APP_VERSION, appVersion);
       editor.commit();
   }
  }

