

package org.vpac.grisu.control.utils;

import java.io.File;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.Environment;

/**
 * Manages the $HOME/.grisu/grisu-server.config file.
 * 
 * @author Markus Binsteiner
 *
 */
public class ServerPropertiesManager {
	
	public static final String DEFAULT_MYPROXY_SERVER = "myproxy2.arcs.org.au";
	public static final int DEFAULT_MYPROXY_PORT = 443;
	public static final int DEFAULT_MYPROXY_LIFETIME_IN_SECONDS = 3600;
	public static final int DEFAULT_MIN_PROXY_LIFETIME_BEFORE_REFRESH = 600;
	public static final String DEFAULT_JOB_DIR_NAME = "grisu-local-job-dir";

	public static PropertiesConfiguration config = null;

	static final Logger myLogger = Logger.getLogger(ServerPropertiesManager.class
			.getName());

	/**
	 * Retrieves the configuration parameters from the properties file
	 * @return the configuration
	 * @throws ConfigurationException if the file could not be read/parsed
	 */
	public static PropertiesConfiguration getServerConfiguration()
			throws ConfigurationException {
		if (config == null) {
			File grisuDir = Environment.getGrisuDirectory();
			config = new PropertiesConfiguration(new File(grisuDir,
					"grisu-server.config"));
		}
		return config;
	}
	
	public static String getDebugDirectory() {
		return new File(Environment.getGrisuDirectory(), "debug").getAbsolutePath();
	}
	
	public static boolean getMDSenabled() {
		boolean mdsenabled = false;
		
		try {
			try {
				mdsenabled = getServerConfiguration().getBoolean("mds-enabled");
			} catch (NoSuchElementException e) {
				// doesn't matter
			}
		} catch (ConfigurationException e) {
//			myLogger.error("Problem with config file: " + e.getMessage());
		}
		return mdsenabled;
	}
	
	public static boolean getDebugModeOn() {
		boolean debug = false;
		
		try {
			try {
				debug = getServerConfiguration().getBoolean("debug");
			} catch (NoSuchElementException e) {
				// doesn't matter
			}
			if ( debug ) {
				// try to create debug directory
				File debugDir = new File(getDebugDirectory());
				if ( ! debugDir.exists() ) {
					debugDir.mkdir();
				}
				
				if ( ! debugDir.exists() ) {
					myLogger.error("Can't create debug directory. Turning debug mode off.");
					debug = false;
				}
			}
		} catch (ConfigurationException e) {
//			myLogger.error("Problem with config file: " + e.getMessage());
		}
		return debug;
	}
	
	/**
	 * Returns the name of the directory in which grisu jobs are located remotely. 
	 * @return the name of the direcotory in which grisu stores jobs or null if the jobs should be stored in the root
	 * home directory.
	 */
	public static String getGrisuJobDirectoryName() {
		
		String jobDirName = null;
		try {
			jobDirName = getServerConfiguration().getString("jobDirName");

				if ( "none".equals(jobDirName.toLowerCase()) ) {
					jobDirName = null;
				}

		} catch (Exception e) {
			jobDirName = null;
		}
		
		if ( jobDirName == null ) { 
			jobDirName = DEFAULT_JOB_DIR_NAME;
		} 
			
		return jobDirName;
	}


	public static String getMyProxyServer() {
		String myProxyServer = "";
		try {
			myProxyServer = getServerConfiguration().getString("myProxyServer");
			
		} catch (ConfigurationException e) {
//			myLogger.error("Problem with config file: " + e.getMessage());
		}
		if ( myProxyServer == null || "".equals(myProxyServer) ) {
			myProxyServer = DEFAULT_MYPROXY_SERVER; 
		}
		
		return myProxyServer;
	}
	
	public static int getMyProxyPort() {
		int myProxyPort = -1;
		try {
			myProxyPort = Integer.parseInt(getServerConfiguration().getString("myProxyPort"));
			
		} catch (Exception e) {
//			myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_MYPROXY_PORT;
		}
		if ( myProxyPort == -1 ) 
			return DEFAULT_MYPROXY_PORT;
		
		return myProxyPort;
	}
	
	public static int getMyProxyLifetime() {
		int lifetime_in_seconds = -1;
		try {
			lifetime_in_seconds = Integer.parseInt(getServerConfiguration().getString("myProxyLifetime"));
			
		} catch (Exception e) {
//			myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_MYPROXY_LIFETIME_IN_SECONDS;
		}
		if ( lifetime_in_seconds == -1 ) 
			return DEFAULT_MYPROXY_LIFETIME_IN_SECONDS;
		
		return lifetime_in_seconds;
	}
	
	public static int getMinProxyLifetimeBeforeGettingNewProxy() {
		int lifetime_in_seconds = -1;
		try {
			lifetime_in_seconds = Integer.parseInt(getServerConfiguration().getString("minProxyLifetimeBeforeRefresh"));
			
		} catch (Exception e) {
//			myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_MIN_PROXY_LIFETIME_BEFORE_REFRESH;
		}
		if ( lifetime_in_seconds == -1 ) 
			return DEFAULT_MIN_PROXY_LIFETIME_BEFORE_REFRESH;
		
		return lifetime_in_seconds;
	}

}
