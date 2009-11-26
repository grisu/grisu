package org.vpac.grisu.settings;

import java.io.File;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Manages the $HOME/.grisu/grisu-server.config file.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class ServerPropertiesManager {
	
	private ServerPropertiesManager() {
	}

	/**
	 * Default myproxy lifetime: 3600 seconds.
	 */
	public static final int DEFAULT_MYPROXY_LIFETIME_IN_SECONDS = 3600;
	/**
	 * Default minimum myproxy lifetime before it gets refreshed: 600 seconds.
	 */
	public static final int DEFAULT_MIN_PROXY_LIFETIME_BEFORE_REFRESH = 600;
	/**
	 * Default concurrent threads to query job status per user: 5
	 */
	public static final int DEFAULT_CONCURRENT_JOB_STATUS_THREADS_PER_USER = 2;
	/**
	 * Default concurrent threads when submitting the parts of a multipartjob: 5 
	 */
	public static final int DEFAULT_CONCURRENT_JOB_SUBMISSION_THREADS_PER_USER = 2;
	/**
	 * Default directory name used as parent for the jobdirectories.
	 */
	public static final String DEFAULT_JOB_DIR_NAME = "grisu-dir";
	
	public static final int DEFAULT_TIME_INBETWEEN_STATUS_CHECKS_FOR_THE_SAME_JOB_IN_SECONDS = 60; 
	
//	public static final String DEFAULT_MULTIPARTJOB_DIR_NAME = "grisu-multijob-dir";

	private static PropertiesConfiguration config = null;

	static final Logger myLogger = Logger
			.getLogger(ServerPropertiesManager.class.getName());
	
	private static final int DEFAULT_CONCURRENT_JOB_SUBMISSION_RETRIES = 5;

	/**
	 * Retrieves the configuration parameters from the properties file.
	 * 
	 * @return the configuration
	 * @throws ConfigurationException
	 *             if the file could not be read/parsed
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

	/**
	 * Returns the path to the directory where the debug information for this
	 * backend is stored.
	 * 
	 * @return the path to the debug directory.
	 */
	public static String getDebugDirectory() {
		
		File dir = new File(Environment.getGrisuDirectory(), "debug");
		
		return dir.getAbsolutePath();
	}

	/**
	 * Checks whether the debug mode is enabled or not.
	 * 
	 * @return true if debug is enabled, false if not
	 */
	public static boolean getDebugModeOn() {
		boolean debug = false;

		try {
			try {
				debug = getServerConfiguration().getBoolean("debug");
			} catch (NoSuchElementException e) {
				// doesn't matter
				myLogger.debug(e);
			}
			if (debug) {
				// try to create debug directory
				File debugDir = new File(getDebugDirectory());
				if (!debugDir.exists()) {
					debugDir.mkdir();
				}

				if (!debugDir.exists()) {
					myLogger
							.error("Can't create debug directory. Turning debug mode off.");
					debug = false;
				}
			}
		} catch (ConfigurationException e) {
			// myLogger.error("Problem with config file: " + e.getMessage());
			myLogger.debug(e);
		}
		return debug;
	}

	/**
	 * Returns the name of the directory in which grisu jobs are located
	 * remotely.
	 * 
	 * @return the name of the direcotory in which grisu stores jobs or null if
	 *         the jobs should be stored in the root home directory.
	 */
	public static String getGrisuJobDirectoryName() {

		String jobDirName = null;
		try {
			jobDirName = getServerConfiguration().getString("jobDirName");

			if ("none".equals(jobDirName.toLowerCase())) {
				jobDirName = null;
			}

		} catch (Exception e) {
			jobDirName = null;
		}

		if (jobDirName == null) {
			jobDirName = DEFAULT_JOB_DIR_NAME;
		}

		return jobDirName;
	}
	
//	/**
//	 * Returns the name of the directory in which grisu jobs are located
//	 * remotely.
//	 * 
//	 * @return the name of the direcotory in which grisu stores jobs or null if
//	 *         the jobs should be stored in the root home directory.
//	 */
//	public static String getGrisuMultiPartJobDirectoryName() {
//
//		String jobDirName = null;
//		try {
//			jobDirName = getServerConfiguration().getString("multiPartJobDirName");
//
//			if ("none".equals(jobDirName.toLowerCase())) {
//				jobDirName = null;
//			}
//
//		} catch (Exception e) {
//			jobDirName = null;
//		}
//
//		if (jobDirName == null) {
//			jobDirName = DEFAULT_MULTIPARTJOB_DIR_NAME;
//		}
//
//		return jobDirName;
//	}

	/**
	 * Returns the lifetime of a delegated proxy that is retrieved from myproxy.
	 * 
	 * @return the lifetime in seconds
	 */
	public static int getMyProxyLifetime() {
		int lifetime_in_seconds = -1;
		try {
			lifetime_in_seconds = Integer.parseInt(getServerConfiguration()
					.getString("myProxyLifetime"));

		} catch (Exception e) {
			// myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_MYPROXY_LIFETIME_IN_SECONDS;
		}
		if (lifetime_in_seconds == -1) {
			return DEFAULT_MYPROXY_LIFETIME_IN_SECONDS;
		}
		return lifetime_in_seconds;
	}

	/**
	 * Returns the minimum lifetime of a proxy before the backend tries to
	 * retrieve a fresh one.
	 * 
	 * @return the minimum lifetime in seconds
	 */
	public static int getMinProxyLifetimeBeforeGettingNewProxy() {
		int lifetime_in_seconds = -1;
		try {
			lifetime_in_seconds = Integer.parseInt(getServerConfiguration()
					.getString("minProxyLifetimeBeforeRefresh"));

		} catch (Exception e) {
			// myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_MIN_PROXY_LIFETIME_BEFORE_REFRESH;
		}
		if (lifetime_in_seconds == -1) {
			return DEFAULT_MIN_PROXY_LIFETIME_BEFORE_REFRESH;
		}
		return lifetime_in_seconds;
	}

	/**
	 * Returns the number of concurrent threads that are querying job status per user.
	 * 
	 * @return the number of concurrent threads
	 */
	public static int getConcurrentJobStatusThreadsPerUser() {
		int concurrentThreads = -1;
		try {
			concurrentThreads = Integer.parseInt(getServerConfiguration()
					.getString("concurrentJobStatusThreads"));

		} catch (Exception e) {
			// myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_CONCURRENT_JOB_STATUS_THREADS_PER_USER;
		}
		if (concurrentThreads == -1) {
			return DEFAULT_CONCURRENT_JOB_STATUS_THREADS_PER_USER;
		}
		return concurrentThreads;
	}
	
	/**
	 * Returns the (forced) wait time in seconds inbetween status checks for the same job.
	 * 
	 * @return wait time in seconds
	 */
	public static int getWaitTimeBetweenJobStatusChecks() {
		int waitTimeInSeconds = -1;
		try {
			waitTimeInSeconds = Integer.parseInt(getServerConfiguration()
					.getString("waitTimeInbetweenJobStatusChecks"));

		} catch (Exception e) {
			// myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_TIME_INBETWEEN_STATUS_CHECKS_FOR_THE_SAME_JOB_IN_SECONDS;
		}
		if (waitTimeInSeconds == -1) {
			return DEFAULT_TIME_INBETWEEN_STATUS_CHECKS_FOR_THE_SAME_JOB_IN_SECONDS;
		}
		return waitTimeInSeconds;
	}
	
	/**
	 * Returns the number of concurrent threads that are submitting (multi-)jobs status per user.
	 * 
	 * @return the number of concurrent threads
	 */
	public static int getConcurrentMultiPartJobSubmitThreadsPerUser() {
		int concurrentThreads = -1;
		try {
			concurrentThreads = Integer.parseInt(getServerConfiguration()
					.getString("concurrentMultiPartJobSubmitThreads"));

		} catch (Exception e) {
			// myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_CONCURRENT_JOB_SUBMISSION_THREADS_PER_USER;
		}
		if (concurrentThreads == -1) {
			return DEFAULT_CONCURRENT_JOB_SUBMISSION_THREADS_PER_USER;
		}
		return concurrentThreads;
	}
	
	/**
	 * Checks whether the default (hsqldb) database configuration should be
	 * used.
	 * 
	 * @return true if default, false if not.
	 */
	public static boolean useDefaultDatabase() {

		try {
			String dbtype = getServerConfiguration().getString("databaseType");

			if (dbtype == null || dbtype.length() == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}

	}

	/**
	 * Returns the type of database that should be used. At the moment hsqldb
	 * and mysql are supported.
	 * 
	 * @return the db type
	 */
	public static String getDatabaseType() {

		String dbType;
		try {
			dbType = getServerConfiguration().getString("databaseType");
			return dbType;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * The url to connect to the database.
	 * 
	 * @return the url
	 */
	public static String getDatabaseConnectionUrl() {
		String dbUrl;
		try {
			dbUrl = getServerConfiguration().getString("databaseConnectionUrl");
			return dbUrl;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the database username.
	 * 
	 * @return the db username
	 */
	public static String getDatabaseUsername() {
		String dbUsername;
		try {
			dbUsername = getServerConfiguration().getString("databaseUsername");
			return dbUsername;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * The database password.
	 * 
	 * @return the password
	 */
	public static String getDatabasePassword() {
		String dbPassword;
		try {
			dbPassword = getServerConfiguration().getString("databasePassword");
			return dbPassword;
		} catch (Exception e) {
			return null;
		}
	}

	public static int getJobSubmissionRetries() {
		
		int retries = -1;
		try {
			retries = Integer.parseInt(getServerConfiguration()
					.getString("globusJobSubmissionRetries"));

		} catch (Exception e) {
			// myLogger.error("Problem with config file: " + e.getMessage());
			return DEFAULT_CONCURRENT_JOB_SUBMISSION_RETRIES;
		}
		if (retries == -1) {
			return DEFAULT_CONCURRENT_JOB_SUBMISSION_RETRIES;
		}
		return retries;
	}

}
