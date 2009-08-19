package org.vpac.grisu.settings;

import java.io.File;

import org.apache.commons.lang.StringUtils;

/**
 * This class manages the location/values of some required files/environment
 * variables.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class Environment {

	private Environment() {
	}
	
	private static final String GRISU_DEFAULT_DIRECTORY = System
			.getProperty("user.home")
			+ File.separator + ".grisu";

	private static String USER_SET_GRISU_DIRECTORY = null;

	private static String GLOBUS_HOME;
	private static File GRISU_DIRECTORY;

	public static String getTemplateDirectory() {
		return getGrisuDirectory() + File.separator + "templates";
	}

	public static String getCacheDirName() {
		return "cache";
	}

	public static String getAvailableTemplatesDirectory() {
		return getGrisuDirectory() + File.separator + "templates_available";
	}

	public static String getGlobusHome() {
		
		if (StringUtils.isBlank(GLOBUS_HOME)) {
			
			GLOBUS_HOME = getGrisuDirectory() + File.separator + "globus";
			
		}
		return GLOBUS_HOME;
	}

	public static String getGrisuPluginDirectory() {
		return getGrisuDirectory() + File.separator + "plugins";
	}

	public static String getAxisClientConfig() {
		return getGlobusHome() + File.separator + "client-config.wsdd";
	}

	/**
	 * For some jobs/applications it is useful to cache output files locally so
	 * they don't have to be transferred over and over again.
	 * 
	 * @return the location of the local directory where all job output files
	 *         are chached (in subdirectories named after the jobname)
	 */
	public static File getLocalJobCacheDirectory() {

		File dir = new File(getGrisuDirectory(), "jobs");
		dir.mkdirs();
		return dir;
	}

	public static void setGrisuDirectory(String path) {

		if (GRISU_DIRECTORY != null) {
			throw new RuntimeException(
					"Can't set grisu directory because it was already accessed once after the start of this application...");
		}

		USER_SET_GRISU_DIRECTORY = path;
	}

	/**
	 * This one returns the location where grisu specific config/cache files are
	 * stored. If it does not exist it gets created.
	 * 
	 * @return the location of grisu specific config/cache files
	 */
	public static File getGrisuDirectory() {

		if (GRISU_DIRECTORY == null) {


			File grisuDir = null;
			if (StringUtils.isNotBlank(USER_SET_GRISU_DIRECTORY)) {
				grisuDir = new File(USER_SET_GRISU_DIRECTORY);
			} else {
				grisuDir = new File(GRISU_DEFAULT_DIRECTORY);
			}

			if (!grisuDir.exists()) {
				grisuDir.mkdirs();
			}
			GRISU_DIRECTORY = grisuDir;
		}
		return GRISU_DIRECTORY;
	}

	/**
	 * The location where the remote filesystems are cached locally.
	 * 
	 * @return the root of the local cache
	 */
	public static File getGrisuLocalCacheRoot() {
		File root = new File(getGrisuDirectory(), getCacheDirName());
		if (!root.exists()) {
			if (!root.mkdirs()) {
				if (!root.exists()) {
					throw new RuntimeException(
							"Could not create local cache root directory: "
									+ root.getAbsolutePath()
									+ ". Please check the permissions.");
				}
			}
		}
		return root;
	}

}
