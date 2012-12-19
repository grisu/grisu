package grisu.settings;

import grisu.control.events.ClientPropertiesEvent;
import grisu.jcommons.constants.Enums.LoginType;
import grith.gsindl.SLCS;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the $HOME/.grisu/grisu.config file.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class ClientPropertiesManager {

	public static final String[] DEFAULT_HELPDESK_CLASSES = new String[] {
			"org.vpac.helpDesk.model.anonymousRT.AnonymousRTHelpDesk",
			"org.vpac.helpDesk.model.trac.TracHelpDesk" };
	public static final String HELPDESK_CONFIG = "support.properties";

	public static final int CONCURRENT_THREADS_DEFAULT = 5;
	public static final int DEFAULT_JOBSUBMIT_STATUS_RECHECK_INTERVAL_IN_SECONDS = 5;

	private static final int DEFAULT_FILE_UPLOAD_THREADS = 1;
	private static final int DEFAULT_FILE_UPLOAD_RETRIES = 5;

	private static int concurrent_upload_thread_dynamic = -1;

	private static final int DEFAULT_FILE_DOWNLOAD_THREADS = 1;

	public static final Long DEFAULT_DOWNLOAD_FILESIZE_TRESHOLD = new Long(
			1024 * 1024 * 2);
	private static final long DEFAULT_CACHE_FILESIZE_TRESHOLD = new Long(
			1024 * 1024 * 20);
	private static final long DEFAULT_CACHE_FOLDERSIZE_TRESHOLD = new Long(
			1024 * 1024 * 60);

	public static final LoginType DEFAULT_LOGIN_TYPE = LoginType.SHIBBOLETH;

	public static final long DEFAULT_GRICLI_LS_COMPLETION_TIMEOUT = 500;

	// keys
	public static final String JOBSTATUS_RECHECK_INTERVAL_KEY = "statusRecheck";

	public static final int DEFAULT_TIMEOUT = 0;

	public static final String DEFAULT_SERVICE_INTERFACE = "BeSTGRID";

	private static PropertiesConfiguration config = null;

	static final Logger myLogger = LoggerFactory
			.getLogger(ClientPropertiesManager.class);
	private static final int DEFAULT_ACTION_STATUS_RECHECK_INTERVAL_IN_SECONDS = 5;

	public static final String AUTO_LOGIN_KEY = "autoLogin";
	public static final String ADMIN_KEY = "admin";

	/**
	 * Call this if the user wants a new (server-side) template to his personal
	 * templates.
	 * 
	 * @param templateName
	 *            the name of the template
	 */
	public static void addServerTemplate(final String templateName) {

		boolean alreadyThere = false;
		for (final String url : config.getStringArray("serverTemplates")) {
			if (url.equals(templateName)) {
				alreadyThere = true;
				break;
			}

		}

		if (!alreadyThere) {
			config.addProperty("serverTemplates", templateName);
			try {
				config.save();
			} catch (final ConfigurationException e) {
				ClientPropertiesManager.myLogger
						.debug("Could not write config file: " + e.getMessage());
			}
		}
	}

	/**
	 * Use this if you want to add the url to the list of previously used.
	 * 
	 * @param serviceInterfaceUrl
	 *            the url of the ServiceInterface
	 */
	public static void addServiceInterfaceUrl(final String serviceInterfaceUrl) {

		boolean alreadyThere = false;
		for (final String url : config.getStringArray("serviceInterfaceUrl")) {
			if (url.equals(serviceInterfaceUrl)) {
				alreadyThere = true;
				break;
			}

		}

		if (!alreadyThere) {
			config.addProperty("serviceInterfaceUrl", serviceInterfaceUrl);
			try {
				config.save();
			} catch (final ConfigurationException e) {
				ClientPropertiesManager.myLogger
						.debug("Could not write config file: " + e.getMessage());
			}
		}
	}

	public static boolean getAutoLogin() {

		boolean autoLogin = false;
		try {
			autoLogin = Boolean.parseBoolean(getClientConfiguration()
					.getString(AUTO_LOGIN_KEY));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return false;
		}

		return autoLogin;

	}

	public static String getImpersonationDN() {

		String impersonate = null;

		impersonate = System.getenv("GRISU_IMPERSONATE");
		if (StringUtils.isBlank(impersonate)) {

			impersonate = System.getProperty("grisu.impersonate");

			if (StringUtils.isBlank(impersonate)) {
				try {
					impersonate = (String) (getClientConfiguration()
							.getProperty("impersonate"));
				} catch (final ConfigurationException e) {
					myLogger.debug("Problem with config file: "
							+ e.getMessage());
				}
			}
		}
		
		return impersonate;
	}

	public static boolean isAdmin() {

		boolean admin = false;
		try {
			admin = Boolean.parseBoolean(getClientConfiguration().getString(
					ADMIN_KEY));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return false;
		}

		return admin;

	}

	/**
	 * Retrieves the configuration parameters from the properties file.
	 * 
	 * @return the configuration
	 * @throws ConfigurationException
	 *             if the file could not be read/parsed
	 */
	public static PropertiesConfiguration getClientConfiguration()
			throws ConfigurationException {
		if (config == null) {
			final File grisuDir = Environment.getGrisuClientDirectory();
			config = new PropertiesConfiguration(new File(grisuDir,
					"grisu.config"));
		}
		return config;
	}

	public static int getConcurrentDownloadThreads() {
		// TODO Auto-generated method stub
		int threads = -1;
		try {
			threads = Integer.parseInt(getClientConfiguration().getString(
					"concurrentDownloadThreads"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_FILE_DOWNLOAD_THREADS;
		}
		if (threads == -1) {
			return DEFAULT_FILE_DOWNLOAD_THREADS;
		}

		return threads;
	}

	public static int getConcurrentThreadsDefault() {
		int threads = -1;
		try {
			threads = Integer.parseInt(getClientConfiguration().getString(
					"concurrentThreads"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return CONCURRENT_THREADS_DEFAULT;
		}
		if (threads == -1) {
			return CONCURRENT_THREADS_DEFAULT;
		}

		return threads;
	}

	public static int getConcurrentUploadThreads() {

		int threads = concurrent_upload_thread_dynamic;

		if (threads > 0) {
			return threads;
		}

		try {
			threads = Integer.parseInt(getClientConfiguration().getString(
					"concurrentUploadThreads"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_FILE_UPLOAD_THREADS;
		}
		if (threads == -1) {
			return DEFAULT_FILE_UPLOAD_THREADS;
		}

		return threads;
	}

	/**
	 * Gets the connection timeout for the connection to a backend.
	 * 
	 * @return the timeout
	 */
	public static long getConnectionTimeoutInMS() {
		long timeout = -1;
		try {
			timeout = Long.parseLong(getClientConfiguration().getString(
					"connectionTimeout"));

		} catch (final Exception e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_TIMEOUT;
		}
		if (timeout == -1) {
			return DEFAULT_TIMEOUT;
		}

		return timeout;
	}

	public static int getDefaultActionStatusRecheckInterval() {

		int intervalInSeconds = -1;
		try {
			intervalInSeconds = Integer.parseInt(getClientConfiguration()
					.getString("actionStatusRecheckInterval"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_ACTION_STATUS_RECHECK_INTERVAL_IN_SECONDS;
		}
		if (intervalInSeconds == -1) {
			return DEFAULT_ACTION_STATUS_RECHECK_INTERVAL_IN_SECONDS;
		}

		return intervalInSeconds;

	}

	/**
	 * Returns the full path to the executable that is used as a default to
	 * handle files with the specified extension.
	 * 
	 * @param extension
	 *            the file extension
	 * @return the full path to the executable
	 */
	public static String getDefaultExternalApplication(final String extension) {
		String path = null;
		try {
			path = getClientConfiguration().getString(extension);
		} catch (final ConfigurationException ce) {
			myLogger.debug("Problem with config file: " + ce.getMessage());
		}
		if ((path == null) || "".equals(path) || !new File(path).exists()) {
			return null;
		}
		return path;
	}

	public static String[] getDefaultHelpDesks() {

		return DEFAULT_HELPDESK_CLASSES;

	}

	/**
	 * Returns the default (most likely: last used) serviceinterfaceurl for this
	 * user.
	 * 
	 * @return the serviceInterfaceurl
	 */
	public static String getDefaultServiceInterfaceUrl() {
		String defaultUrl = null;
		try {

			defaultUrl = System.getProperty("grisu.defaultServiceInterface");
			if (StringUtils.isBlank(defaultUrl)) {
				defaultUrl = getClientConfiguration().getString(
						"defaultServiceInterfaceUrl");
			}
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

		if (StringUtils.isBlank(defaultUrl) || "ARCS".equals(defaultUrl)) {
			defaultUrl = DEFAULT_SERVICE_INTERFACE;
		}
		return defaultUrl;
	}

	public static long getDownloadFileSizeTresholdInBytes() {

		long treshold = -1;
		try {
			treshold = Long.parseLong(getClientConfiguration().getString(
					"downloadFileSizeTreshold"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_DOWNLOAD_FILESIZE_TRESHOLD;
		}
		if (treshold <= 0L) {
			return DEFAULT_DOWNLOAD_FILESIZE_TRESHOLD;
		}

		return treshold;

	}

	/**
	 * This one determines how big files can be to be kept in the local cache.
	 * 
	 * If a file is bigger and gets downloaded, it will be downloaded to the
	 * cache but then moved to the final destination. Otherwise it'll be
	 * copied...
	 * 
	 * @return the threshold in bytes
	 */
	public static long getFileSizeThresholdForCache() {

		long treshold = -1;
		try {
			treshold = Long.parseLong(getClientConfiguration().getString(
					"cacheFileSizeTreshold"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_CACHE_FILESIZE_TRESHOLD;
		}
		if (treshold <= 0L) {
			return DEFAULT_CACHE_FILESIZE_TRESHOLD;
		}

		return treshold;

	}

	public static int getFileUploadRetries() {

		int retries = -1;
		try {
			retries = Integer.parseInt(getClientConfiguration().getString(
					"fileUploadRetries"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_FILE_UPLOAD_RETRIES;
		}
		if (retries == -1) {
			return DEFAULT_FILE_UPLOAD_RETRIES;
		}

		return retries;
	}

	/**
	 * This one determines how big files can be to be kept in the local cache.
	 * 
	 * If a file is bigger and gets downloaded, it will be downloaded to the
	 * cache but then moved to the final destination. Otherwise it'll be
	 * copied...
	 * 
	 * @return the threshold in bytes
	 */
	public static long getFolderSizeThresholdForCache() {

		long treshold = -1;
		try {
			treshold = Long.parseLong(getClientConfiguration().getString(
					"cacheFolderSizeTreshold"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_CACHE_FOLDERSIZE_TRESHOLD;
		}
		if (treshold <= 0L) {
			return DEFAULT_CACHE_FOLDERSIZE_TRESHOLD;
		}

		return treshold;

	}

	public static long getGricliCompletionSleepTimeInMS() {

		long timeout = -1;
		try {
			timeout = Long.parseLong(getClientConfiguration().getString(
					"gricliLsCompletionTimeout"));

		} catch (final Exception e) {
			return DEFAULT_GRICLI_LS_COMPLETION_TIMEOUT;
		}
		if (timeout == -1) {
			return DEFAULT_GRICLI_LS_COMPLETION_TIMEOUT;
		}

		return timeout;
	}

	public static String getHelpDeskConfig() {
		return HELPDESK_CONFIG;
	}

	public static long getJobSubmitStatusRecheckIntervall() {

		int interval = 0;
		try {
			interval = getClientConfiguration().getInt(
					JOBSTATUS_RECHECK_INTERVAL_KEY);
		} catch (final Exception e) {
		}

		if (interval <= 0) {
			interval = DEFAULT_JOBSUBMIT_STATUS_RECHECK_INTERVAL_IN_SECONDS;
		}

		return interval;

	}

	public static LoginType getLastLoginType() {

		String lastLoginType = null;
		try {
			lastLoginType = getClientConfiguration().getString("lastLoginType");
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

		if (StringUtils.isBlank(lastLoginType)) {
			return DEFAULT_LOGIN_TYPE;
		}
		final LoginType result = LoginType.fromString(lastLoginType);

		// if ( result == null ) {
		// return DEFAULT_LOGIN_TYPE;
		// }

		return result;
	}

	/**
	 * Loads the last selected tab: certificate or myproxy.
	 * 
	 * @return the index of the tab
	 */
	public static int getLastSelectedTab() {
		int tab = -1;
		try {
			tab = Integer.parseInt(getClientConfiguration().getString(
					"selectedTab"));

		} catch (final Exception e) {
			// myLogger.debug("Problem with config file: " + e.getMessage());
			return 0;
		}
		if (tab == -1) {
			return 0;
		}

		return tab;
	}

	/**
	 * Returns the last used fqan for this user.
	 * 
	 * @return the fqan
	 */
	public static String getLastUsedFqan() {
		String fqan = null;
		fqan = System.getProperty("grisu.defaultFqan");

		if (StringUtils.isBlank(fqan)) {
			try {
				fqan = (String) (getClientConfiguration()
						.getProperty("lastUsedFqan"));
			} catch (final ConfigurationException e) {
				myLogger.debug("Problem with config file: " + e.getMessage());
			}
		}
		return fqan;
	}

	public static String getLastUsedLeftUrl() {

		String defaultUrl = null;
		try {
			defaultUrl = getClientConfiguration().getString("lastUsedLeftUrl");
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

		if ((defaultUrl == null) || "".equals(defaultUrl)) {
			defaultUrl = new File(System.getProperty("user.home")).toURI()
					.toString();
		}
		return defaultUrl;

	}

	public static String getLastUsedRightUrl() {

		String defaultUrl = null;
		try {
			defaultUrl = getClientConfiguration().getString("lastUsedRightUrl");
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

		if ((defaultUrl == null) || "".equals(defaultUrl)) {
			defaultUrl = new File(System.getProperty("user.home")).toURI()
					.toString();
		}
		return defaultUrl;

	}

	public static String getProperty(String key) {

		String value = null;
		try {
			value = getClientConfiguration().getString(key);
		} catch (final ConfigurationException e) {
			throw new RuntimeException(e);
		}
		return value;

	}

	/**
	 * Returns the name of the last used shib idp.
	 * 
	 * @return the idp name.
	 */
	public static String getSavedShibbolethIdp() {
		String idp = null;
		try {
			idp = (String) (getClientConfiguration()
					.getProperty("shibbolethIdp"));
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
		return idp;
	}

	/**
	 * Returns the last used shib username.
	 * 
	 * @return the username
	 */
	public static String getSavedShibbolethUsername() {
		String username = null;
		try {
			username = (String) (getClientConfiguration()
					.getProperty("shibbolethUsername"));
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
		return username;
	}

	/**
	 * Returns all the (server-side) templates this users added to his personal
	 * templates.
	 * 
	 * @return the templates
	 */
	public static String[] getServerTemplates() {

		String[] templates = null;

		try {
			templates = getClientConfiguration().getStringArray(
					"serverTemplates");
		} catch (final ConfigurationException ce) {
			// myLogger.debug("Problem with config file: " + ce.getMessage());
			return new String[] {};
		}

		if (templates.length == 0) {
			myLogger.debug("No server templates found.");
			addServerTemplate("generic");
			setProperty("lastCreatePanel", "Generic");
			templates = new String[] { "generic" };
		}
		return templates;
	}

	/**
	 * Returns all serviceInterface urls that the user connected to successfully
	 * in the past.
	 * 
	 * @return the urls of the ServiceInterfaces
	 */
	public static String[] getServiceInterfaceUrls() {

		String[] urls = null;

		try {
			urls = getClientConfiguration().getStringArray(
					"serviceInterfaceUrl");
		} catch (final ConfigurationException e1) {
			// myLogger.debug("Problem with config file: " + e1.getMessage());
			return new String[] { DEFAULT_SERVICE_INTERFACE };
		}

		if (urls.length == 0) {
			config.setProperty("serviceInterfaceUrl", DEFAULT_SERVICE_INTERFACE);
			try {
				config.save();
			} catch (final ConfigurationException e) {
				ClientPropertiesManager.myLogger
						.debug("Could not write config file: " + e.getMessage());
			}
			return new String[] { DEFAULT_SERVICE_INTERFACE };
		}

		return urls;
	}

	public static String getShibbolethUrl() {

		String url = null;
		try {
			url = (String) (getClientConfiguration()
					.getProperty("shibbolethUrl"));
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

		if (StringUtils.isBlank(url)) {
			return SLCS.DEFAULT_SLCS_URL;
		}
		return url;
	}

	/**
	 * Call this if a user wants to remove a (server-side) template from his
	 * personal templates.
	 * 
	 * @param templateName
	 *            the name of the template
	 */
	public static void removeServerTemplate(final String templateName) {

		final String[] templates = config.getStringArray("serverTemplates");

		config.clearProperty("serverTemplates");
		for (final String template : templates) {
			if (!templateName.equals(template)) {
				config.addProperty("serverTemplates", template);
			}
		}
		try {
			config.save();
		} catch (final ConfigurationException e) {
			ClientPropertiesManager.myLogger
					.debug("Could not write config file: " + e.getMessage());
		}
	}

	/**
	 * Saves whether to check ("true") or not ("false") the
	 * "Advanced connection properties" checkbox the next time.
	 * 
	 * @param useHttpProxy
	 *            whether to check checkbox next time
	 */
	public static void saveDefaultHttpProxy(final String useHttpProxy) {
		try {
			getClientConfiguration().setProperty("httpProxy", useHttpProxy);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Saves the httpproxy server port for next time.
	 * 
	 * @param port
	 *            the port of the http proxy server (e.g. 3128)
	 */
	public static void saveDefaultHttpProxyPort(final String port) {
		try {
			getClientConfiguration().setProperty("httpProxyPort", port);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Saves the httpproxy server hostname for next time.
	 * 
	 * @param server
	 *            the hostname of the proxy server (e.g. "proxy.vpac.org")
	 */
	public static void saveDefaultHttpProxyServer(final String server) {
		try {
			getClientConfiguration().setProperty("httpProxyServer", server);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Saves the httpproxy username for next time.
	 * 
	 * @param username
	 *            the username to auththenticate against the httpproxy
	 */
	public static void saveDefaultHttpProxyUsername(final String username) {
		try {
			getClientConfiguration().setProperty("httpProxyUsername", username);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	public static void saveLastLoginType(LoginType lastLoginType) {

		try {
			getClientConfiguration().setProperty("lastLoginType",
					lastLoginType.toString());
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Stores the last used fqan for this user.
	 * 
	 * @param fqan
	 *            the fqan
	 */
	public static void saveLastUsedFqan(final String fqan) {
		try {
			getClientConfiguration().setProperty("lastUsedFqan", fqan);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	/**
	 * Saves the last selected tab.
	 * 
	 * @param selectedLoginPanel
	 *            the tab to display on startup
	 */
	public static void saveSelectedTab(final int selectedLoginPanel) {
		try {
			getClientConfiguration().setProperty("selectedTab",
					selectedLoginPanel);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	/**
	 * Call this to store the name of the last used shib idp.
	 * 
	 * @param idpName
	 *            the name of the idp
	 */
	public static void saveShibbolethIdp(final String idpName) {
		try {
			getClientConfiguration().setProperty("shibbolethIdp", idpName);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	/**
	 * Saves the last used shib username.
	 * 
	 * @param username
	 *            the username
	 */
	public static void saveShibbolethUsername(final String username) {
		try {
			getClientConfiguration()
					.setProperty("shibbolethUsername", username);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	public static void setAutoLogin(boolean selected) {

		// System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

		try {
			getClientConfiguration().setProperty(AUTO_LOGIN_KEY,
					new Boolean(selected).toString());
			getClientConfiguration().save();

			EventBus.publish(new ClientPropertiesEvent(AUTO_LOGIN_KEY,
					new Boolean(selected).toString()));

		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	public static void setConcurrentUploadThreads(int threads) {
		concurrent_upload_thread_dynamic = threads;
	}

	/**
	 * Sets the path to the executable for the default external application to
	 * handle this kind of file extension.
	 * 
	 * @param extension
	 *            the file extension (e.g. pdf)
	 * @param path
	 *            the path to the executable (e.g. /opt/acroread/bin/acroread)
	 */
	public static void setDefaultExternalApplication(final String extension,
			final String path) {
		try {
			getClientConfiguration().setProperty(extension, path);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Sets the ServiceInterface url that was used the last time the user
	 * successfully connected to one.
	 * 
	 * @param serviceInterfaceUrl
	 *            the url of the ServiceInterface
	 */
	public static void setDefaultServiceInterfaceUrl(
			final String serviceInterfaceUrl) {
		try {
			getClientConfiguration().setProperty("defaultServiceInterfaceUrl",
					serviceInterfaceUrl);
			addServiceInterfaceUrl(serviceInterfaceUrl);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	public static void setLastUsedFqan(String currentFqan) {

		try {
			getClientConfiguration().setProperty("lastUsedFqan", currentFqan);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	public static void setLastUsedLeftUrl(final String url) {
		try {
			getClientConfiguration().setProperty("lastUsedLeftUrl", url);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	public static void setLastUsedRightUrl(final String url) {
		try {
			getClientConfiguration().setProperty("lastUsedRightUrl", url);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	public static void setProperty(String lastBlenderFileDir, String string) {

		try {
			getClientConfiguration().setProperty(lastBlenderFileDir, string);
			getClientConfiguration().save();
		} catch (final ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private ClientPropertiesManager() {
	}

}
