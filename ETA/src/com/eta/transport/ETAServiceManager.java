package com.eta.transport;

import retrofit.RestAdapter;

public class ETAServiceManager {
	private static TransportService etaServerService;
	private ETAServiceManager() {
		//Making sure noone else can create instance of
		// this class.
	}
	
	public static TransportService getETAServerService() {
		if(etaServerService == null) {
			etaServerService = new RestAdapter.Builder()
											  .setEndpoint(TransportService.SERVICE_ENDPOINT)
											  .build()
											  .create(TransportService.class);
		}
		
		return etaServerService;
	}
}
