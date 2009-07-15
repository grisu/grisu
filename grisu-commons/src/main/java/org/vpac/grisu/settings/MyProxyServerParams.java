package org.vpac.grisu.settings;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Class to help with the myproxy server parameters.
 * 
 * @author Markus Binsteiner
 * 
 */
public class MyProxyServerParams {

	public static PropertiesConfiguration config = null;

	static final Logger myLogger = Logger.getLogger(MyProxyServerParams.class
			.getName());

	/**
	 * Default myproxy server url. Points to myproxy2.arcs.org.au.
	 */
	public static final String DEFAULT_MYPROXY_SERVER = "myproxy2.arcs.org.au";
	/**
	 * Default myproxy server port. Default is 443.
	 */
	public static final int DEFAULT_MYPROXY_PORT = 443;

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
			File grisuDir = Environment.getGrisuDirectory();
			config = new PropertiesConfiguration(new File(grisuDir,
					"grisu.config"));
		}
		return config;
	}

	/**
	 * Get the myproxy server hostname to use.
	 * 
	 * @return the myproxy server hostname
	 */
	public static String getMyProxyServer() {
		String myProxyServer = "";
		try {
			myProxyServer = getClientConfiguration().getString("myProxyServer");

		} catch (ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
		if (myProxyServer == null || "".equals(myProxyServer)) {
			myProxyServer = DEFAULT_MYPROXY_SERVER;
		}

		return myProxyServer;
	}

	/**
	 * Get the myproxy server port to use.
	 * 
	 * @return the myproxy server port.
	 */
	public static int getMyProxyPort() {
		int myProxyPort = -1;
		try {
			myProxyPort = Integer.parseInt(getClientConfiguration().getString(
					"myProxyPort"));

		} catch (Exception e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
			return DEFAULT_MYPROXY_PORT;
		}
		if (myProxyPort == -1) {
			return DEFAULT_MYPROXY_PORT;
		}

		return myProxyPort;
	}

	/**
	 * Saves the MyProxy username for next time.
	 * 
	 * @param username
	 *            the username
	 */
	public static void saveDefaultMyProxyUsername(String username) {
		try {
			getClientConfiguration().setProperty("myProxyUsername", username);
			getClientConfiguration().save();
		} catch (ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}

	}

	/**
	 * Gets the username for MyProxy that was used the last time.
	 * 
	 * @return the MyProxy username
	 */
	public static String loadDefaultMyProxyUsername() {
		String username = "";
		try {
			username = (String) (getClientConfiguration()
					.getProperty("myProxyUsername"));
		} catch (ConfigurationException e) {
			myLogger.debug("Problem with config file: " + e.getMessage());
		}
		return username;
	}

}
