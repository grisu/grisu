package grisu.model;

import grisu.control.ServiceInterface;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class GrisuRegistryManager {

	static final Logger myLogger = Logger.getLogger(GrisuRegistryManager.class
			.getName());

	private static Map<ServiceInterface, GrisuRegistry> cachedRegistries = new HashMap<ServiceInterface, GrisuRegistry>();

	public static GrisuRegistry getDefault(
			final ServiceInterface serviceInterface) {

		if (serviceInterface == null) {
			throw new RuntimeException(
					"ServiceInterface not initialized yet. Can't get default registry...");
		}

		synchronized (serviceInterface) {

			if (cachedRegistries.get(serviceInterface) == null) {
				GrisuRegistry defaultRegistry;
				try {

					final String classname = serviceInterface.getClass()
							.getName();

					if (!classname.contains("Local")) {
						// trying to manage mds information locally, because
						// it's
						// much faster...
						final Class clientSideRegistryClass = Class
								.forName("grisu.frontend.info.clientsidemds.ClientSideGrisuRegistry");
						final Constructor clientSideRegistryConstructor = clientSideRegistryClass
								.getConstructor(ServiceInterface.class);
						defaultRegistry = (GrisuRegistry) (clientSideRegistryConstructor
								.newInstance(serviceInterface));
						myLogger.info("Using client side mds library.");
					} else {
						defaultRegistry = new GrisuRegistryImpl(
								serviceInterface);
					}
				} catch (final Exception e) {
					myLogger.info("Couldn't use client side mds library: "
							+ e.getLocalizedMessage());
					myLogger.info("Using grisu service interface to calculate mds information...");
					defaultRegistry = new GrisuRegistryImpl(serviceInterface);
				}
				cachedRegistries.put(serviceInterface, defaultRegistry);
			}

		}

		return cachedRegistries.get(serviceInterface);
	}

	public synchronized static void setDefault(
			final ServiceInterface serviceInterface, GrisuRegistry registry) {
		cachedRegistries.put(serviceInterface, registry);
	}

}
