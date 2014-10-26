package com.eta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

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
			intent = new Intent(this, SendETAActivity.class);
			break;

		case R.id.button3:
			intent = new Intent(this, ViewETAActivity.class);
			break;
		
		default:
			Toast.makeText(this, "There is no such button", Toast.LENGTH_SHORT).show();
			return;
		}
		startActivity(intent);
		
	}
}
