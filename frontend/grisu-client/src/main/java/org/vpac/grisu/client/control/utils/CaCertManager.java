package org.vpac.grisu.client.control.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class just tries to map serviceInterface urls with cacerts. I can't be arsed to 
 * implement an extra config file thingy just for this so I'll hardcode it in here...
 * @author Markus Binsteiner
 *
 */
public class CaCertManager {
	
	private Map<String, String> urls = new HashMap<String, String>();
	
	public CaCertManager() {
		
		urls.put("https://ngportal.vpac.org/grisu-ws/services/grisu", "cacert.pem");
		urls.put("https://ngportaldev.vpac.org/grisu-ws/services/grisu", "cacert.pem");
		urls.put("http://localhost:8080/grisu-ws/services/grisu", "cacert.pem");
		urls.put("https://www.sapac.edu.au/grisu-ws/services/grisu", "ersaca.pem");
		
	}

	/**
	 * Returns the name of the cacert file for the given serviceInterface url
	 * @param serviceInterfaceUrl the url
	 * @return the cacert filename or null if url not found
	 */
	public String getCaCertNameForServiceInterfaceUrl(String serviceInterfaceUrl) {
		return urls.get(serviceInterfaceUrl);
	}
	
}
