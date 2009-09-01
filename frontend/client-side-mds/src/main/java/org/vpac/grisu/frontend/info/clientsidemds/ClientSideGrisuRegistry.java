package org.vpac.grisu.frontend.info.clientsidemds;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.info.CachedMdsInformationManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.UserEnvironmentManagerImpl;
import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.model.info.ResourceInformation;
import org.vpac.grisu.model.info.UserApplicationInformation;
import org.vpac.grisu.settings.Environment;
import org.vpac.historyRepeater.DummyHistoryManager;
import org.vpac.historyRepeater.HistoryManager;
import org.vpac.historyRepeater.SimpleHistoryManager;

import au.org.arcs.jcommons.interfaces.InformationManager;

public class ClientSideGrisuRegistry implements GrisuRegistry {
	
	static final Logger myLogger = Logger.getLogger(ClientSideGrisuRegistry.class.getName());
	
	private final ServiceInterface serviceInterface;

	private final InformationManager infoManager;

	private HistoryManager historyManager = null;
	private Map<String, ApplicationInformation> cachedApplicationInformationObjects = new HashMap<String, ApplicationInformation>();
	private Map<String, UserApplicationInformation> cachedUserInformationObjects = new HashMap<String, UserApplicationInformation>();
	private UserEnvironmentManager cachedUserInformation;
	private ResourceInformation cachedResourceInformation;
	private FileManager cachedFileHelper;
	
	public ClientSideGrisuRegistry(ServiceInterface serviceInterface) throws Exception {
		this.serviceInterface = serviceInterface;
		this.infoManager = new CachedMdsInformationManager(Environment.getGrisuDirectory().getPath());
		
//		try {
//			this.infoManager.getAllSubmissionLocations();
//		} catch (Exception e) {
//			throw new Exception("Couldn't query mds from here. Please use another GrisuRegistry...");
//		}
		
	}
	
	public ApplicationInformation getApplicationInformation(
			String applicationName) {

		if (cachedApplicationInformationObjects.get(applicationName) == null) {
			ApplicationInformation temp = new ClientSideApplicationInformation(this, applicationName, infoManager);
			cachedApplicationInformationObjects.put(applicationName, temp);
		}
		return cachedApplicationInformationObjects.get(applicationName);
		
	}

	public FileManager getFileManager() {
		if (cachedFileHelper == null) {
			cachedFileHelper = new FileManager(serviceInterface);
		}
		return cachedFileHelper;
	}

	public HistoryManager getHistoryManager() {
		
		if (historyManager == null) {
			File historyFile = new File(Environment.getGrisuDirectory().getPath(),
					GRISU_HISTORY_FILENAME);
			if (!historyFile.exists()) {
				try {
					historyFile.createNewFile();

				} catch (IOException e) {
					myLogger.debug(e);
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

	public ResourceInformation getResourceInformation() {
		
		if (cachedResourceInformation == null) {
			cachedResourceInformation = new ClientSideResourceInformation(this, infoManager);
		}
		return cachedResourceInformation;
	}

	public UserApplicationInformation getUserApplicationInformation(
			String applicationName) {
		
		if (cachedUserInformationObjects.get(applicationName) == null) {
			UserApplicationInformation temp = new ClientSideUserApplicationInformation(this, applicationName, infoManager);
			cachedUserInformationObjects.put(applicationName, temp);
		}
		return cachedUserInformationObjects.get(applicationName);
	}

	public UserEnvironmentManager getUserEnvironmentManager() {
		if (cachedUserInformation == null) {
			this.cachedUserInformation = new UserEnvironmentManagerImpl(
					serviceInterface);
		}
		return this.cachedUserInformation;
	}

	public void setUserEnvironmentManager(UserEnvironmentManager ui) {
		this.cachedUserInformation = ui;
	}

}
