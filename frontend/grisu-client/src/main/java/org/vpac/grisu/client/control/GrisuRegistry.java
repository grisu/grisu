package org.vpac.grisu.client.control;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.fs.model.InformationObject;

public class GrisuRegistry {
	
	// singleton stuff
	final private static GrisuRegistry REGISTRY = new GrisuRegistry();
	
	public static GrisuRegistry getDefault() {
		return REGISTRY;
	}
	

	private Map<String, InformationObject> cachedInformationObjects = new HashMap<String, InformationObject>();
	
	public GrisuRegistry() {
		
	}

}
