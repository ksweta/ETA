package com.eta;

import com.eta.transport.ETANotificationRequest;
import com.eta.transport.TransportService;
import com.eta.transport.ETAServiceManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SendETAActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_eta);

	}
	
	/*
	 * This is onClick listener for send eta button.
	 */
	public void sendEta(View view){
		Toast.makeText(this, "sending ETA notification", Toast.LENGTH_SHORT).show();
		TransportService service = ETAServiceManager.getETAServerService();
		ETANotificationRequest request = new ETANotificationRequest("5551112000",
																	"Hi there",
																	0.0,
																	0.0,
																	0.0,
																	0.0);
		//TODO: Send eta to server.
	}
}

