package com.eta.util;

import retrofit.RestAdapter;
import android.util.Log;

/**
 * This is a helper class to log Retrofit debug line. It uses Android Log 
 * method to log messages. 
 *
 */
public class RetrofitLog implements RestAdapter.Log {
   final String TAG = RetrofitLog.class.getSimpleName();
   @Override
   public void log(String msg) {
      Log.d(TAG, msg);
   }
}
