package grisu.frontend.info.clientsidemds;

import grisu.backend.info.InformationManagerManager;
import grisu.control.ServiceInterface;
import grisu.control.TemplateManager;
import grisu.model.FileManager;
import grisu.model.GrisuRegistry;
import grisu.model.UserEnvironmentManager;
import grisu.model.UserEnvironmentManagerImpl;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.ResourceInformation;
import grisu.model.info.UserApplicationInformation;
import grisu.settings.Environment;
import grisu.settings.ServerPropertiesManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.vpac.historyRepeater.DummyHistoryManager;
import org.vpac.historyRepeater.HistoryManager;
import org.vpac.historyRepeater.SimpleHistoryManager;

import au.org.arcs.jcommons.interfaces.InformationManager;
import au.org.arcs.jcommons.interfaces.MatchMaker;

public class ClientSideGrisuRegistry implements GrisuRegistry {

	static final Logger myLogger = Logger
			.getLogger(ClientSideGrisuRegistry.class.getName());

	public static void preloadInfoManager() {

		// staticInfoManager = CachedMdsInformationManager
		// .getDefaultCachedMdsInformationManager(Environment
		// .getGrisuClientDirectory().getPath());
		//
		// new Thread() {
		// @Override
		// public void run() {
		// System.out.println("Loading all sublocs...");
		// staticInfoManager.getAllSubmissionLocations();
		// System.out.println("Loading all sublocs finished.");
		// }
		// }.start();
		//
		// new Thread() {
		// @Override
		// public void run() {
		// System.out.println("Loading all resources...");
		// staticInfoManager.getAllGridResources();
		// System.out.println("Loading all resources finished.");
		// }
		// }.start();
		//
		// new Thread() {
		// @Override
		// public void run() {
		// System.out.println("Loading all hosts...");
		// staticInfoManager.getAllHosts();
		// System.out.println("Loading all hosts finished.");
		// }
		// }.start();

	}

	private final ServiceInterface serviceInterface;
	private final InformationManager infoManager;

	private final MatchMaker matchMaker;
	private HistoryManager historyManager = null;
	private final Map<String, ApplicationInformation> cachedApplicationInformationObjects = new HashMap<String, ApplicationInformation>();
	private final Map<String, UserApplicationInformation> cachedUserInformationObjects = new HashMap<String, UserApplicationInformation>();
	private UserEnvironmentManager cachedUserInformation;
	private ResourceInformation cachedResourceInformation;
	private FileManager cachedFileHelper;

	private final Map<String, Object> values = new HashMap<String, Object>();

	private TemplateManager templateManager;

	private ClientSideGrisuRegistry(ServiceInterface serviceInterface)
			throws Exception {
		this.serviceInterface = serviceInterface;

		this.infoManager = InformationManagerManager
				.getInformationManager(ServerPropertiesManager
						.getInformationManagerConf());
		this.matchMaker = InformationManagerManager
				.getMatchMaker(ServerPropertiesManager.getMatchMakerConf());
	}

	public Object get(String key) {
		return values.get(key);
	}

	public ApplicationInformation getApplicationInformation(
			String applicationName) {

		if (cachedApplicationInformationObjects.get(applicationName) == null) {

			final ApplicationInformation temp = new ClientSideApplicationInformation(
					this, applicationName, infoManager, matchMaker);
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
			final File historyFile = new File(Environment
					.getGrisuClientDirectory().getPath(),
					GRISU_HISTORY_FILENAME);
			if (!historyFile.exists()) {
				try {
					historyFile.createNewFile();

				} catch (final IOException e) {
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
			cachedResourceInformation = new ClientSideResourceInformation(this,
					infoManager);
		}
		return cachedResourceInformation;
	}

	public TemplateManager getTemplateManager() {

		if (templateManager == null) {
			templateManager = new TemplateManager(serviceInterface);
		}
		return templateManager;
	}

	public UserApplicationInformation getUserApplicationInformation(
			String applicationName) {

		if (cachedUserInformationObjects.get(applicationName) == null) {
			final UserApplicationInformation temp = new ClientSideUserApplicationInformation(
					this, applicationName, infoManager, matchMaker);
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

	public void set(String key, Object object) {
		values.put(key, object);
	}

	public void setUserEnvironmentManager(UserEnvironmentManager ui) {
		this.cachedUserInformation = ui;
	}

}
