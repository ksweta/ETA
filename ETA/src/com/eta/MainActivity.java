package com.eta;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eta.transport.TransportService;
import com.eta.transport.TransportServiceHelper;
import com.eta.util.ApplicationSharedPreferences;


public class MainActivity extends Activity {
   private final static String TAG = MainActivity.class.getSimpleName();
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

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
      //TransportService service = TransportServiceFactory.getTransportService();
   }
}

