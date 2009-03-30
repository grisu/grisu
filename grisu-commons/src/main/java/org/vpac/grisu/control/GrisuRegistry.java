package org.vpac.grisu.control;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.js.model.ApplicationInformationObject;
import org.vpac.grisu.js.model.InformationObject;

public class GrisuRegistry {
	
	// singleton stuff
	private static GrisuRegistry REGISTRY;
	
	public static GrisuRegistry getDefault() {
		if ( singletonServiceInterface == null ) {
			throw new RuntimeException("ServiceInterface not initialized yet. Can't get default registry...");
		}
		
		if ( REGISTRY == null) {
			REGISTRY = new GrisuRegistry(singletonServiceInterface);
		}
		return REGISTRY;
	}
	private static ServiceInterface singletonServiceInterface;
	
	private final ServiceInterface serviceInterface;
	
	/**
	 * This needs to be called before calling {@link #getDefault()} for the first time...
	 * @param serviceInterface
	 */
	public static void setServiceInterface(ServiceInterface serviceInterfaceTemp) {
		singletonServiceInterface = serviceInterfaceTemp;
	}
	
	private Map<String, InformationObject> cachedInformationObjects = new HashMap<String, InformationObject>();
	
	public GrisuRegistry(ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	
	public InformationObject getInformationObject(String applicationName) {
		
		if ( cachedInformationObjects.get(applicationName) == null ) {
			InformationObject temp = new ApplicationInformationObject(serviceInterface, applicationName);
			cachedInformationObjects.put(applicationName, temp);
		}
		return cachedInformationObjects.get(applicationName);
	}

}
