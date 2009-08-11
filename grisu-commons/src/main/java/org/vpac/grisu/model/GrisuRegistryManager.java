package org.vpac.grisu.model;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.control.ServiceInterface;

public class GrisuRegistryManager {
	
	private static Map<ServiceInterface, GrisuRegistry> cachedRegistries = new HashMap<ServiceInterface, GrisuRegistry>();

	public static GrisuRegistry getDefault(final ServiceInterface serviceInterface) {

		if (serviceInterface == null) {
			throw new RuntimeException(
					"ServiceInterface not initialized yet. Can't get default registry...");
		}

		if (cachedRegistries.get(serviceInterface) == null) {
			GrisuRegistryImpl temp = new GrisuRegistryImpl(serviceInterface);
			cachedRegistries.put(serviceInterface, temp);
		}

		return cachedRegistries.get(serviceInterface);
	}
	
	public static void setDefault(final ServiceInterface serviceInterface, GrisuRegistry registry) {
		cachedRegistries.put(serviceInterface, registry);
	}

}
