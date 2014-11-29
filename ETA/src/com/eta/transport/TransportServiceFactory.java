package com.eta.transport;

import retrofit.RestAdapter;

import com.eta.util.RetrofitLog;

/**
 * This is a singleton factory class which returns TransportService object. 
 *
 */
public class TransportServiceFactory {
	
	private static TransportService service;
	
	private TransportServiceFactory() {
		//Making sure other class can not create instance of this class.
	}
	
	/**
	 * This method returns the TransportService object, which
	 * can be used to send HTTP request to server.
	 * @return
	 */
	
	public static TransportService getTransportService(){
		
		
		if(service == null) {
			//If service is not yet instantiated then instantiate it.
			RestAdapter.Builder adapterBuilder = new RestAdapter.Builder();
	    	adapterBuilder.setConverter(TransportService.DATA_CONVERTER)
	    				  .setEndpoint(TransportService.SERVICE_ENDPOINT)
	    				  .setLogLevel(RestAdapter.LogLevel.FULL)
	    				  .setLog(new RetrofitLog());
	    	
	       service = adapterBuilder.build().create(TransportService.class);
		}
		
		return service;
	}
}
