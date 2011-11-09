package grisu.model;

import grisu.control.ServiceInterface;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.ResourceInformation;
import grisu.model.info.UserApplicationInformation;
import grith.jgrith.Credential;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrisuRegistryManager {

	static final Logger myLogger = LoggerFactory
			.getLogger(GrisuRegistryManager.class.getName());

	private static Map<ServiceInterface, GrisuRegistry> cachedRegistries = new HashMap<ServiceInterface, GrisuRegistry>();

	public static Object get(ServiceInterface si, String key) {
		return getDefault(si).get(key);
	}

	public static ApplicationInformation getApplicationInformation(
			ServiceInterface si, String app) {
		return getDefault(si).getApplicationInformation(app);
	}

	public static Credential getCredential(ServiceInterface si) {
		return getDefault(si).getCredential();
	}

	public static GrisuRegistry getDefault(
			final ServiceInterface serviceInterface) {

		if (serviceInterface == null) {
			throw new RuntimeException(
					"ServiceInterface not initialized yet. Can't get default registry...");
		}

		synchronized (serviceInterface) {

			if (cachedRegistries.get(serviceInterface) == null) {

				throw new RuntimeException("ServiceInterface not registered...");
				// GrisuRegistry defaultRegistry;
				// try {
				//
				// final String classname = serviceInterface.getClass()
				// .getName();
				//
				// if (!classname.contains("Local")) {
				// // trying to manage mds information locally, because
				// // it's
				// // much faster...
				// final Class clientSideRegistryClass = Class
				// .forName("grisu.frontend.info.clientsidemds.ClientSideGrisuRegistry");
				// final Constructor clientSideRegistryConstructor =
				// clientSideRegistryClass
				// .getConstructor(ServiceInterface.class);
				// defaultRegistry = (GrisuRegistry)
				// (clientSideRegistryConstructor
				// .newInstance(serviceInterface));
				// myLogger.info("Using client side mds library.");
				// } else {
				// defaultRegistry = new GrisuRegistryImpl(
				// serviceInterface);
				// }
				// } catch (final Exception e) {
				// myLogger.info("Couldn't use client side mds library: "
				// + e.getLocalizedMessage());
				// myLogger.info("Using grisu service interface to calculate mds information...");
				// defaultRegistry = new GrisuRegistryImpl(serviceInterface);
				// // }
				// cachedRegistries.put(serviceInterface, defaultRegistry);
			}

		}

		return cachedRegistries.get(serviceInterface);
	}

	public static FileManager getFileManager(ServiceInterface si) {
		return getDefault(si).getFileManager();
	}

	public static ResourceInformation getResourceInformation(ServiceInterface si) {
		return getDefault(si).getResourceInformation();
	}

	public static UserApplicationInformation getUserApplicationInformation(
			ServiceInterface si, String app) {
		return getDefault(si).getUserApplicationInformation(app);
	}

	public static UserEnvironmentManager getUserEnvironmentManager(ServiceInterface si) {
		return getDefault(si).getUserEnvironmentManager();
	}

	public static void registerServiceInterface(ServiceInterface si,
			Credential c) {

		cachedRegistries.put(si, new GrisuRegistryImpl(si, c));

	}

	// public synchronized static void setDefault(
	// final ServiceInterface serviceInterface, GrisuRegistry registry) {
	// cachedRegistries.put(serviceInterface, registry);
	// }

}
