package com.eta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.eta.util.ApplicationConstants;
import com.eta.util.ApplicationSharedPreferences;
import com.eta.util.Utility;

public class InviteSmsActivity extends Activity {
   private static final String TAG = InviteSmsActivity.class.getSimpleName();
   private EditText etPhone;
   private EditText etMessage;
   private SmsManager smsManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_sms_invite);
      etPhone = (EditText)findViewById(R.id.et_invite_phone);
      etMessage = (EditText)findViewById(R.id.et_invite_msg);
      smsManager = SmsManager.getDefault();
   }

   
   public void onClick(View view) {
      switch(view.getId()) {
      case R.id.bt_invite:
         sendInvite();
         break;

      case R.id.bt_invite_cancel:
         //Just close this activity.
         finish();
         break;

      default:
         Log.d(TAG, "There is no such button id : "+view.getId());
      }
   }
   
   private void sendInvite() {
      String phone = etPhone.getText().toString();
      String message = etMessage.getText().toString();
      String errorMsg = "Please proivde right value(s) for following field(s):\n";
      boolean isError = false;
      if(phone.isEmpty() || phone.length() != ApplicationConstants.PHONE_NUMBER_LENGTH) {
         errorMsg += "Invalid phone\n";
         isError = true;
         etPhone.setFocusableInTouchMode(true);
         etPhone.requestFocus();
      }
      if(message.isEmpty()) {
         errorMsg += "Empty message\n";
         isError = true;
      }
      if (isError) {
         Utility.showErrorMessageWithOKButton(this, "Invite SMS", errorMsg);
         return;
      }
      smsManager.sendTextMessage(phone, null, message, null, null);
      //Once message is sent, close this activity.
      finish();
   }
   
   @Override
   protected void onNewIntent(Intent intent) {
      setIntent(intent);
   }
   
   @Override
   protected void onResume() {
      super.onResume();
      Bundle extra = getIntent().getExtras();
      
      String phone = extra.getString(ApplicationConstants.INVITE_SMS_PHONE, "");
      String senderPhone = Utility.purgePhoneNumber(Utility.getDevicePhoneNumber(this));
      String userName = ApplicationSharedPreferences.getUserName(this);
      
      if(phone.isEmpty()) {
         //request focus
         etPhone.setFocusableInTouchMode(true);
         etPhone.requestFocus();
      } else {
         etPhone.setText(phone);
      }
      
      String message = String.format("<strong>%s(%s)</strong> invites you to use Android <strong>ETA application</strong>. "
                                     + "This application keeps you updated about sender's <strong>ETA</strong>.", 
                                     userName,
                                     senderPhone);
      etMessage.setText(Html.fromHtml(message));
   }
}
