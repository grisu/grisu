package org.vpac.grisu.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.model.info.ApplicationInformationImpl;
import org.vpac.grisu.model.info.ResourceInformation;
import org.vpac.grisu.model.info.ResourceInformationImpl;
import org.vpac.grisu.model.info.UserApplicationInformation;
import org.vpac.grisu.model.info.UserApplicationInformationImpl;
import org.vpac.grisu.settings.Environment;
import org.vpac.historyRepeater.DummyHistoryManager;
import org.vpac.historyRepeater.HistoryManager;
import org.vpac.historyRepeater.SimpleHistoryManager;

/**
 * The GrisuRegistry provides access to all kinds of information via an
 * easy-to-use api.
 * 
 * You can access the following information objects via the registry:
 * 
 * {@link UserEnvironmentManager}: to find out what resources the current user
 * can access <br/> {@link UserApplicationInformation}: information about the
 * applications/versions of applications this user has got access to<br/>
 * {@link ApplicationInformation}: information about the applications that are
 * provided grid-wide<br/> {@link ResourceInformation}: general information about the
 * available resources like queues, submissionlocations, stagingfilesystems<br/>
 * {@link HistoryManager}: can be used to store / retrieve data that the user
 * used in past jobs<br/> {@link FileManager}: to do file transfer and such<br/>
 * 
 * @author Markus Binsteiner
 * 
 */
public class GrisuRegistryImpl implements GrisuRegistry {

	static final Logger myLogger = Logger.getLogger(GrisuRegistryImpl.class
			.getName());

	// caching the registries for different serviceinterfaces. for a desktop
	// application this most
	// likely only ever contains only one registry object. But for
	// webapplications it can hold more
	// than that. Advantage is that several users can share for example the
	// ResourceInformation object
	// but can have (or must have) seperate UserApplicationInformation objects

	private final ServiceInterface serviceInterface;

	private HistoryManager historyManager = null;
	private final Map<String, ApplicationInformation> cachedApplicationInformationObjects = new HashMap<String, ApplicationInformation>();
	private final Map<String, UserApplicationInformation> cachedUserInformationObjects = new HashMap<String, UserApplicationInformation>();
	private UserEnvironmentManager cachedUserInformation;
	private ResourceInformation cachedResourceInformation;
	private FileManager cachedFileHelper;

	public GrisuRegistryImpl(final ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.vpac.grisu.model.GrisuRegistryInterface#getApplicationInformation
	 * (java.lang.String)
	 */
	public final ApplicationInformation getApplicationInformation(
			final String applicationName) {

		if (cachedApplicationInformationObjects.get(applicationName) == null) {
			ApplicationInformation temp = new ApplicationInformationImpl(
					serviceInterface, applicationName);
			cachedApplicationInformationObjects.put(applicationName, temp);
		}
		return cachedApplicationInformationObjects.get(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.model.GrisuRegistryInterface#getFileManager()
	 */
	public final FileManager getFileManager() {
		if (cachedFileHelper == null) {
			cachedFileHelper = new FileManager(serviceInterface);
		}
		return cachedFileHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.model.GrisuRegistryInterface#getHistoryManager()
	 */
	public final HistoryManager getHistoryManager() {
		if (historyManager == null) {
			File historyFile = new File(Environment.getGrisuClientDirectory()
					.getPath(), GRISU_HISTORY_FILENAME);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.model.GrisuRegistryInterface#getResourceInformation()
	 */
	public final ResourceInformation getResourceInformation() {
		if (cachedResourceInformation == null) {
			cachedResourceInformation = new ResourceInformationImpl(
					serviceInterface);
		}
		return cachedResourceInformation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.vpac.grisu.model.GrisuRegistryInterface#getUserApplicationInformation
	 * (java.lang.String)
	 */
	public final UserApplicationInformation getUserApplicationInformation(
			final String applicationName) {

		if (cachedUserInformationObjects.get(applicationName) == null) {
			UserApplicationInformation temp = new UserApplicationInformationImpl(
					serviceInterface, getUserEnvironmentManager(),
					applicationName);
			cachedUserInformationObjects.put(applicationName, temp);
		}
		return cachedUserInformationObjects.get(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.vpac.grisu.model.GrisuRegistryInterface#getUserEnvironmentManager()
	 */
	public final UserEnvironmentManager getUserEnvironmentManager() {

		if (cachedUserInformation == null) {
			this.cachedUserInformation = new UserEnvironmentManagerImpl(
					serviceInterface);
		}
		return cachedUserInformation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.vpac.grisu.model.GrisuRegistryInterface#setUserEnvironmentManager
	 * (org.vpac.grisu.model.UserEnvironmentManager)
	 */
	public final void setUserEnvironmentManager(final UserEnvironmentManager ui) {
		this.cachedUserInformation = ui;
	}

}
