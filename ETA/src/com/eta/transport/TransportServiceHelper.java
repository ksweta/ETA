package com.eta.transport;

import retrofit.RestAdapter;

import com.eta.util.RetrofitLog;

/**
 * This is a singleton class which returns TransportService reference. 
 *
 */
public class TransportServiceHelper {

   private static TransportService service;

   private TransportServiceHelper() {
      //Making sure other class can not create instance of this class.
   }

   /**
    * This method returns the TransportService reference, which
    * can be used to send HTTP request to server.
    * @return
    */

   public static TransportService getTransportService(){


      if(service == null) {
         //If service is not yet instantiated then instantiate it.
         RestAdapter.Builder adapterBuilder = new RestAdapter.Builder();
         adapterBuilder.setConverter(TransportService.DATA_CONVERTER) //Convert object to json and vice-versa
         .setEndpoint(TransportService.SERVICE_ENDPOINT) //server root url
         //TODO remove these lines once debugging is complete.
         .setLogLevel(RestAdapter.LogLevel.FULL) //Log level of Retrofit.
         .setLog(new RetrofitLog());  //Retrofit logger class for logging.

         service = adapterBuilder.build().create(TransportService.class);
      }

      return service;
   }
}
