package com.eta;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RequestInterceptor.RequestFacade;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.eta.transport.RegistrationRequest;
import com.eta.transport.TransportService;
import com.eta.transport.TransportServiceFactory;
import com.eta.transport.User;
import com.eta.util.ApplicationConstants;
import com.eta.util.ApplicationSharedPreferences;
import com.eta.util.RetrofitLog;
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
			gcmClientRegId = ApplicationSharedPreferences.getGCMClientRegistrationId(this);
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
			intent = new Intent(this, SignupActivity.class);
			break;
		case R.id.button5:
			String clientRegistrationid = ApplicationSharedPreferences.getGCMClientRegistrationId(this);
			Toast.makeText(this, clientRegistrationid, Toast.LENGTH_SHORT).show();
			Log.d(TAG, clientRegistrationid);
			break;
		case R.id.button6:
			serverController();
			break;
		case R.id.button7:
			startActivity(new Intent(this, SignInActivity.class));
			break;
			
		default:
			Toast.makeText(this, "There is no such button", Toast.LENGTH_SHORT).show();
			return;
		}
		if (intent != null) {
			startActivity(intent);
		}

	}
	private void serverController() {

		TransportService service = TransportServiceFactory.getTransportService();
		service.isReceipientRegistered("5551112000", 
				                       TransportService.HEADER_ACCEPT_JSON,
				                       new RegisteredReceipietCallback(this));

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
					ApplicationSharedPreferences.storeGCMClientRegistrationId(context, gcmClientRegId);

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
	
	class RegisteredReceipietCallback implements Callback<Void> {

		private Context context;
		public RegisteredReceipietCallback(Context c) {
			context = c;
		}
		@Override
		public void failure(RetrofitError arg0) {
			
		}

		@Override
		public void success(Void arg0, Response response) {
			if (response.getStatus() == TransportService.RESPONSE_STATUS_OK) {
				Toast.makeText(context, "Phone is registered", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}

