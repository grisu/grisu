package org.vpac.grisu.model;

import java.util.Set;

/**
 * Wrapps information about the user and the available resources to him
 * grid-wide.
 * 
 * @author Markus Binsteiner
 * 
 */
public interface UserEnvironmentManager {

	/**
	 * All of the users fqans.
	 * 
	 * @return the fqans
	 */
	public String[] getAllAvailableFqans();

	/**
	 * All the submissionLocations the user has got access to with all his
	 * fqans.
	 * 
	 * @return all submissionLocations
	 */
	public Set<String> getAllAvailableSubmissionLocations();

	/**
	 * Gets all the mountpoints for this particular VO.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the mountpoints
	 */
	public Set<MountPoint> getMountPoints(String fqan);

	/**
	 * Get all the users' mountpoints.
	 * 
	 * @return all mountpoints
	 */
	public MountPoint[] getMountPoints();

	/**
	 * Calculates all mountpoints for the combination of submission location and
	 * fqan.
	 * 
	 * @param submissionLocation
	 *            the submission location
	 * @param fqan
	 *            the fqan
	 * @return a list of all mountpoints
	 */
	public Set<MountPoint> getMountPointsForSubmissionLocationAndFqan(
			String submissionLocation, String fqan);

	/**
	 * A list of all mountpoints that are connected to the specified submission
	 * location.
	 * 
	 * @param submissionLocation
	 *            the submission location
	 * @return the list of mountpoints
	 */
	public Set<MountPoint> getMountPointsForSubmissionLocation(
			String submissionLocation);

	/**
	 * Calculates all sites that are available for the user.
	 * 
	 * @return all sites
	 */
	public Set<String> getAllAvailableSites();

	/**
	 * Returns a recommended mountpoint for the specified combination of
	 * submission location and fqan.
	 * 
	 * @param submissionLocation
	 *            the submission location
	 * @param fqan
	 *            the fqan
	 * @return the mountpoint or mull if no mountpoint could be found
	 */
	public MountPoint getRecommendedMountPoint(String submissionLocation,
			String fqan);

	/**
	 * Returns the mountpoint that is used to access the specified url.
	 * 
	 * @param url
	 *            the url
	 * @return the mountpoint or null if no mountpoint could be found
	 */
	public MountPoint getMountPointForUrl(String url);

	/**
	 * Returns the currently set fqan.
	 * 
	 * @return the fqan
	 */
	public String getCurrentFqan();

	/**
	 * Sets the currently used fqan.
	 * 
	 * @param currentFqan
	 *            the fqan
	 */
	public void setCurrentFqan(String currentFqan);

	/**
	 * Adds a fqan listener to this environment manager.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addFqanListener(FqanListener listener);

	/**
	 * Removes a fqan listener from this environment manager.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeFqanListener(FqanListener listener);

}
