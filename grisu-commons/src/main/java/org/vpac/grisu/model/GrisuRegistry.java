package org.vpac.grisu.model;

import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.model.info.ResourceInformation;
import org.vpac.grisu.model.info.UserApplicationInformation;
import org.vpac.historyRepeater.HistoryManager;
import org.vpac.historyRepeater.SimpleHistoryManager;

public interface GrisuRegistry {
	
	public static final String GRISU_HISTORY_FILENAME = "grisu.history";

	/**
	 * Sets the {@link UserEnvironmentManager} for this registry object.
	 * 
	 * @param ui
	 *            the UserEnvironmentManager
	 */
	public abstract void setUserEnvironmentManager(
			final UserEnvironmentManager ui);

	/**
	 * Gets the UserApplicationInformationObject for the specified application.
	 * 
	 * If an UserApplicationInformationObject for this application was already
	 * specified, a cached version will be returned.
	 * 
	 * @param applicationName
	 *            the name of the application
	 * @return the information object for this application and user
	 */
	public abstract UserApplicationInformation getUserApplicationInformation(
			final String applicationName);

	/**
	 * Gets the ApplicationInformationObject for the specified application.
	 * 
	 * If an ApplicationInformationObject for this application was already
	 * specified, a cached version will be returned.
	 * 
	 * @param applicationName
	 *            the name of the application
	 * @return the information object for this application
	 */
	public abstract ApplicationInformation getApplicationInformation(
			final String applicationName);

	/**
	 * Returns the management object for this users enironment.
	 * 
	 * @return the UserEnvironmentManager object for this user
	 */
	public abstract UserEnvironmentManager getUserEnvironmentManager();

	/**
	 * Returns the resource information object that can be used to get
	 * information about the resources in this grid.
	 * 
	 * @return the resource information object
	 */
	public abstract ResourceInformation getResourceInformation();

	/**
	 * Returns the history manager object for this user. By default it returns
	 * an object of the {@link SimpleHistoryManager} class which uses the
	 * grisu.history file in the grisu directory to store the users history.
	 * 
	 * @return the history manager object
	 */
	public abstract HistoryManager getHistoryManager();

	/**
	 * Returns an object to help with file(-transfer) related things.
	 * 
	 * @return the file manager
	 */
	public abstract FileManager getFileManager();

}