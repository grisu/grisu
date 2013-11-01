package grisu.model;

import grisu.control.ServiceInterface;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.ResourceInformation;
import grisu.model.info.UserApplicationInformation;
import grith.jgrith.cred.Cred;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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

	public static Cred getCredential(ServiceInterface si) {
		return getDefault(si).getCredential();
	}

	public static ServiceInterface getDefaultServiceInterface() {
		if ( cachedRegistries == null || cachedRegistries.size() == 0 ) {
			return null;
		} else {
			return cachedRegistries.keySet().iterator().next();
		}
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
			Cred c) {

		cachedRegistries.put(si, new GrisuRegistryImpl(si, c));

	}


}
