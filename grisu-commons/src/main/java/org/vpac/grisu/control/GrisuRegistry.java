package org.vpac.grisu.control;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.model.ApplicationInformation;
import org.vpac.grisu.model.ApplicationInformationImpl;
import org.vpac.grisu.model.EnvironmentSnapshotValues;
import org.vpac.grisu.model.ResourceInformation;
import org.vpac.grisu.model.ResourceInformationImpl;
import org.vpac.grisu.model.UserApplicationInformation;
import org.vpac.grisu.model.UserApplicationInformationImpl;
import org.vpac.grisu.model.UserInformation;
import org.vpac.grisu.model.UserInformationImpl;
import org.vpac.historyRepeater.DummyHistoryManager;
import org.vpac.historyRepeater.HistoryManager;
import org.vpac.historyRepeater.SimpleHistoryManager;

public class GrisuRegistry {
	
	// singleton stuff
	private static GrisuRegistry REGISTRY;
	
	public static GrisuRegistry getDefault() {
		if ( singletonServiceInterface == null ) {
			throw new RuntimeException("ServiceInterface not initialized yet. Can't get default registry...");
		}
		
		if ( REGISTRY == null) {
			REGISTRY = new GrisuRegistry(singletonServiceInterface, singletonEsv);
		}
		return REGISTRY;
	}
	private static ServiceInterface singletonServiceInterface;
	private static EnvironmentSnapshotValues singletonEsv;
	
	private final ServiceInterface serviceInterface;
	
	/**
	 * This needs to be called before calling {@link #getDefault()} for the first time...
	 * @param serviceInterface
	 */
	public static void setServiceInterface(ServiceInterface serviceInterfaceTemp) {
		singletonServiceInterface = serviceInterfaceTemp;
	}
	
	public static void setEnvironmentSnapshotValues(EnvironmentSnapshotValues esv) {
		singletonEsv = esv;
	}
	
	private EnvironmentSnapshotValues esv = null;
	
	
	// here starts the real class...
	
	public static final String GRISU_HISTORY_FILENAME = "grisu.history";
	
	private HistoryManager historyManager = null;
	private Map<String, ApplicationInformation> cachedApplicationInformationObjects = new HashMap<String, ApplicationInformation>();
	private Map<String, UserApplicationInformation> cachedUserInformationObjects = new HashMap<String, UserApplicationInformation>();
	private UserInformation cachedUserInformation;
	private ResourceInformation cachedResourceInformation;
	
	public GrisuRegistry(ServiceInterface serviceInterface, EnvironmentSnapshotValues esv) {
		this.serviceInterface = serviceInterface;
		this.esv = esv;
	}
	
	public UserApplicationInformation getUserApplicationInformation(String applicationName) {
		
		if ( cachedUserInformationObjects.get(applicationName) == null ) {
			UserApplicationInformation temp = new UserApplicationInformationImpl(serviceInterface, getUserInformation(), applicationName);
			cachedUserInformationObjects.put(applicationName, temp);
		}
		return cachedUserInformationObjects.get(applicationName);
	}
	
	public ApplicationInformation getApplicationInformationObject(String applicationName) {
		
		if ( cachedApplicationInformationObjects.get(applicationName) == null ) {
			ApplicationInformation temp = new ApplicationInformationImpl(serviceInterface, applicationName);
			cachedApplicationInformationObjects.put(applicationName, temp);
		}
		return cachedApplicationInformationObjects.get(applicationName);
	}
	
	public UserInformation getUserInformation() {
		
		if ( cachedUserInformation == null ) {
			this.cachedUserInformation = new UserInformationImpl(serviceInterface);
		}
		return cachedUserInformation;
	}

	public EnvironmentSnapshotValues getEnvironmentSnapshotValues() {
		return esv;
	}
	
	public ResourceInformation getResourceInformation() {
		if ( cachedResourceInformation == null ) {
			cachedResourceInformation = new ResourceInformationImpl(serviceInterface);
		}
		return cachedResourceInformation;
	}
	
	public HistoryManager getHistoryManager() {
		if ( historyManager == null ) {
			File historyFile = new File(Environment.GRISU_DIRECTORY,
					GRISU_HISTORY_FILENAME);
			if (!historyFile.exists()) {
				try {
					historyFile.createNewFile();

				} catch (IOException e) {
					// well
				}
			}
			if (!historyFile.exists()) {
				historyManager = new DummyHistoryManager();
			} else {
				historyManager = new SimpleHistoryManager(historyFile);
			}
		}
		return historyManager;
	}

}
