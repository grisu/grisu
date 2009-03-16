

package org.vpac.grisu.client.control.utils;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.ServiceInterfaceFactory;
import org.vpac.grisu.control.Environment;

/**
 * Manages the $HOME/.grisu/grisu.config file.
 * 
 * @author Markus Binsteiner
 *
 */
public class ClientPropertiesManager {
	
	public static final int DEFAULT_TIMEOUT = 0;

	public static PropertiesConfiguration config = null;

	static final Logger myLogger = Logger.getLogger(ClientPropertiesManager.class
			.getName());

	/**
	 * Retrieves the configuration parameters from the properties file
	 * @return the configuration
	 * @throws ConfigurationException if the file could not be read/parsed
	 */
	public static PropertiesConfiguration getClientConfiguration()
			throws ConfigurationException {
		if (config == null) {
			File grisuDir = Environment.getGrisuDirectory();
			config = new PropertiesConfiguration(new File(grisuDir,
					"grisu.config"));
		}
		return config;
	}

	/**
	 * Returns all serviceInterface urls that the user connected to successfully in the past
	 * @return the urls of the ServiceInterfaces
	 */
	public static String[] getServiceInterfaceUrls() {

		String[] urls = null;

		try {
			urls = getClientConfiguration().getStringArray(
					"serviceInterfaceUrl");
		} catch (ConfigurationException e1) {
//			myLogger.debug("Problem with config file: " + e1.getMessage());
			return new String[] { ServiceInterfaceFactory.DEFAULT_SERVICE_INTERFACE };
		}

		if (urls.length == 0) {
			config.setProperty("serviceInterfaceUrl",
					ServiceInterfaceFactory.DEFAULT_SERVICE_INTERFACE);
			try {
				config.save();
			} catch (ConfigurationException e) {
				ClientPropertiesManager.myLogger
						.debug("Could not write config file: " + e.getMessage());
			}
			return new String[] { ServiceInterfaceFactory.DEFAULT_SERVICE_INTERFACE };
		}

		return urls;
	}
	
	public static String[] getServerTemplates() {
		
		String[] templates = null;
		
		try {
			templates = getClientConfiguration().getStringArray("serverTemplates");
		} catch ( ConfigurationException ce ) {
//			myLogger.debug("Problem with config file: " + ce.getMessage());
			return new String[] { };
		}
		
		if ( templates.length == 0 ) {
			myLogger.debug("No server templates found.");
			return new String[]{};
		}
		return templates;
	}
	
	public static void addServerTemplate(String templateName) {

		boolean alreadyThere = false;
		for (String url : config.getStringArray("serverTemplates")) {
			if (url.equals(templateName)) {
				alreadyThere = true;
				break;
			}

		}

		if (!alreadyThere) {
			config.addProperty("serverTemplates", templateName);
			try {
				config.save();
			} catch (ConfigurationException e) {
				ClientPropertiesManager.myLogger
						.debug("Could not write config file: " + e.getMessage());
			}
		}
	}
	
	public static void removeServerTemplate(String templateName) {
		
		String[] templates = config.getStringArray("serverTemplates");

		config.clearProperty("serverTemplates");
		for ( String template : templates ) {
			if ( ! templateName.equals(template) ) {
				config.addProperty("serverTemplates", template);
			}
		}
		try {
			config.save();
		} catch (ConfigurationException e) {
			ClientPropertiesManager.myLogger
				.debug("Could not write config file: " + e.getMessage());
		}
	}

	/**
	 * Use this if you want to add the url to the list of previously used
	 * @param serviceInterfaceUrl the url of the ServiceInterface
	 */
	public static void addServiceInterfaceUrl(String serviceInterfaceUrl) {

		boolean alreadyThere = false;
		for (String url : config.getStringArray("serviceInterfaceUrl")) {
			if (url.equals(serviceInterfaceUrl)) {
				alreadyThere = true;
				break;
			}

		}

		if (!alreadyThere) {
			config.addProperty("serviceInterfaceUrl", serviceInterfaceUrl);
			try {
				config.save();
			} catch (ConfigurationException e) {
				ClientPropertiesManager.myLogger
						.debug("Could not write config file: " + e.getMessage());
			}
		}
	}


	public static void saveShibbolethUsername(String username) {
		try {
			getClientConfiguration().setProperty("shibbolethUsername", username);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	public static String getSavedShibbolethUsername() {
		String username = null;
		try {
			username = (String) (getClientConfiguration()
					.getProperty("shibbolethUsername"));
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
		return username;
	}
	
	public static void saveShibbolethIdp(String idpName) {
		try {
			getClientConfiguration().setProperty("shibbolethIdp", idpName);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}
	
	public static String getSavedShibbolethIdp() {
		String idp = null;
		try {
			idp = (String) (getClientConfiguration()
					.getProperty("shibbolethIdp"));
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
		return idp;
	}

	/**
	 * Saves the httpproxy username for next time
	 * @param username the username to auththenticate against the httpproxy
	 */
	public static void saveDefaultHttpProxyUsername(String username) {
		try {
			getClientConfiguration().setProperty("httpProxyUsername", username);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Saves the httpproxy server hostname for next time
	 * @param server the hostname of the proxy server (e.g. "proxy.vpac.org")
	 */
	public static void saveDefaultHttpProxyServer(String server) {
		try {
			getClientConfiguration().setProperty("httpProxyServer", server);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Saves whether to check ("true") or not ("false") the "Advanced connection properties" checkbox the next time.
	 * @param useHttpProxy whether to check checkbox next time
	 */
	public static void saveDefaultHttpProxy(String useHttpProxy) {
		try {
			getClientConfiguration().setProperty("httpProxy", useHttpProxy);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Saves the httpproxy server port for next time
	 * @param useHttpProxy the port of the http proxy server (e.g. 3128)
	 */
	public static void saveDefaultHttpProxyPort(String port) {
		try {
			getClientConfiguration().setProperty("httpProxyPort", port);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}
	
	public static void saveLastUsedFqan(String fqan) {
		try {
			getClientConfiguration().setProperty("lastUsedFqan", fqan);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	public static String getLastUsedFqan() { 
		String fqan = null;
		try {
			fqan = (String) (getClientConfiguration()
					.getProperty("lastUsedFqan"));
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
		return fqan;
	}
	
	/**
	 * Saves the last selected tab
	 * 
	 * @param selectedLoginPanel the tab to display on startup
	 */
	public static void saveSelectedTab(int selectedLoginPanel) { 
		try {
			getClientConfiguration().setProperty("selectedTab", selectedLoginPanel);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}

	/**
	 * Loads the last selected tab: certificate or myproxy
	 * @return the index of the tab
	 */
	public static int getLastSelectedTab() {
		int tab = -1;
		try {
			tab = Integer.parseInt(getClientConfiguration().getString("selectedTab"));
			
		} catch (Exception e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
			return 0;
		}
		if ( tab == -1 ) 
			return 0;
		
		return tab;
	}



	/**
	 * Sets the ServiceInterface url that was used the last time the user successfully connected to one.
	 * @param serviceInterfaceUrl the url of the ServiceInterface
	 */
	public static void setDefaultServiceInterfaceUrl(String serviceInterfaceUrl) {
		try {
			getClientConfiguration().setProperty("defaultServiceInterfaceUrl",
					serviceInterfaceUrl);
			addServiceInterfaceUrl(serviceInterfaceUrl);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
	}
	
	public static String getDefaultServiceInterfaceUrl() {
		String defaultUrl = null;
		try {
			defaultUrl = getClientConfiguration().getString("defaultServiceInterfaceUrl");
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: "+e.getMessage());
		}
		
		if ( defaultUrl == null || "".equals(defaultUrl) ) {
			defaultUrl = null;
		}
		return defaultUrl;
	}
	


	public static long getConnectionTimeoutInMS() {
		long timeout = -1;
		try {
			timeout = Long.parseLong(getClientConfiguration().getString("connectionTimeout"));
			
		} catch (Exception e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_TIMEOUT;
		}
		if ( timeout == -1 ) 
			return DEFAULT_TIMEOUT;
		
		return timeout;
	}
	
	public static void setDefaultExternalApplication(String extension,
			String path) {
		try {
			getClientConfiguration().setProperty(extension, path);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
//			myLogger.debug("Problem with config file: " + e.getMessage());
		}
		
	}
	
	public static String getDefaultExternalApplication(String extension) {
		String path = null;
		try {
			path = getClientConfiguration().getString(extension);
		} catch (ConfigurationException ce) {
//			myLogger.debug("Problem with config file: " + ce.getMessage());
		}
		if ( path == null || "".equals(path) || ! new File(path).exists() ) {
			return null;
		}
		return path;
	}

}
