package com.eta.task;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.eta.util.ApplicationConstants;
import com.eta.util.ApplicationSharedPreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmRegistrationTask extends AsyncTask<Void, Void, Void> {
   private static final String TAG = GcmRegistrationTask.class.getSimpleName();
   private Context context;
   private GoogleCloudMessaging gcm;
   public GcmRegistrationTask(Context context, GoogleCloudMessaging gcm) {
      this.context = context;
      this.gcm = gcm;
   }
   @Override
   protected Void doInBackground(Void... params) {
      try {
         if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(context);
         }
         // It takes the server's id and registers using this id
         String gcmClientRegId = gcm.register(ApplicationConstants.GCM_SERVER_PROJECT_ID);
         Log.d(TAG, "Gcm client registration id : " + gcmClientRegId);

         // Persist the regID - no need to register again.
         ApplicationSharedPreferences.storeGCMClientRegistrationId(context, gcmClientRegId);

      } catch (IOException ex) {
         Log.e(TAG, ex.getMessage(), ex);
      }
      return null;
   }
}
