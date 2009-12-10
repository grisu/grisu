package org.vpac.grisu.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.model.files.GlazedFile;

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
	String[] getAllAvailableFqans();

	/**
	 * All the submissionLocations the user has got access to with all his
	 * fqans.
	 * 
	 * @return all submissionLocations
	 */
	Set<String> getAllAvailableSubmissionLocations();

	/**
	 * Gets all the mountpoints for this particular VO.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the mountpoints
	 */
	Set<MountPoint> getMountPoints(String fqan);
	
	/**
	 * Gets all mountpoints for a site.
	 * 
	 * @param site the site
	 * @return the mountpoints
	 */
	SortedSet<MountPoint> getMountPointsForSite(String site);

	/**
	 * Get all the users' mountpoints.
	 * 
	 * @return all mountpoints
	 */
	MountPoint[] getMountPoints();

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
	Set<MountPoint> getMountPointsForSubmissionLocationAndFqan(
			String submissionLocation, String fqan);

	/**
	 * A list of all mountpoints that are connected to the specified submission
	 * location.
	 * 
	 * @param submissionLocation
	 *            the submission location
	 * @return the list of mountpoints
	 */
	Set<MountPoint> getMountPointsForSubmissionLocation(
			String submissionLocation);

	/**
	 * Calculates all sites that are available for the user.
	 * 
	 * @return all sites
	 */
	SortedSet<String> getAllAvailableSites();

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
	MountPoint getRecommendedMountPoint(String submissionLocation,
			String fqan);

	/**
	 * Returns the mountpoint that is used to access the specified url.
	 * 
	 * @param url
	 *            the url
	 * @return the mountpoint or null if no mountpoint could be found
	 */
	MountPoint getMountPointForUrl(String url);

	/**
	 * Returns the currently set fqan.
	 * 
	 * @return the fqan
	 */
	String getCurrentFqan();

	/**
	 * Sets the currently used fqan.
	 * 
	 * @param currentFqan
	 *            the fqan
	 */
	void setCurrentFqan(String currentFqan);

	/**
	 * Adds a fqan listener to this environment manager.
	 * 
	 * @param listener
	 *            the listener
	 */
	void addFqanListener(FqanListener listener);

	/**
	 * Removes a fqan listener from this environment manager.
	 * 
	 * @param listener
	 *            the listener
	 */
	void removeFqanListener(FqanListener listener);

	/**
	 * Returns whether the provided string is a mountpoint alias of one of the users' available mountpoints.
	 * 
	 * @param string the string
	 * @return whether the string is a mountpoint alias or not
	 */
	boolean isMountPointAlias(String string);

	/**
	 * Returns the mountPoint for this alias.
	 * 
	 * @param alias the alias
	 * @return the mountpoint or null if no mountpoint with this alias was found
	 */
	MountPoint getMountPointForAlias(String url);

	/**
	 * Returns whether the specified url is the root url of one of the users mountpoints.
	 * 
	 * @param rootUrl the url
	 * @return whether the url is a the root of a mountpoint or not
	 */
	boolean isMountPointRoot(String rootUrl);
	
	/**
	 * Sets a user property on the backend.
	 * 
	 * @param key
	 * @param value
	 */
	void setProperty(String key, String value);
	
	/**
	 * Retrieves a user property from the backend.
	 * 
	 * @param key the key 
	 * @return the value
	 */
	String getProperty(String key);
	
	/**
	 * Adds a bookmark to the users bookmarks.
	 * 
	 * Use null if you want to delete a bookmark.
	 * 
	 * @param alias the alias
	 * @param url the url
	 */
	void setBookmark(String alias, String url);
	
	/**
	 * Gets the list of the users bookmarks.
	 * 
	 * @return the users bookmarks.
	 */
	Map<String, String> getBookmarks();
	
	/**
	 * Returns a list of all the available filesystems of a user.
	 * 
	 * Includes local filesystems, bookmarks and remote ones.
	 * 
	 * @return the filesystems
	 */
	List<FileSystemItem> getFileSystems();
	
	List<FileSystemItem> getLocalFileSystems();
	
	List<FileSystemItem> getBookmarksFilesystems();
	
	List<FileSystemItem> getRemoteSites();
	
	FileSystemItem getFileSystemForUrl(String url);

	/**
	 * Calculates a unique jobname.
	 * 
	 * Looks up all existing jobs and appends a number if the string already exists.
	 * 
	 * @param name the base-name
	 * @return the unique jobname
	 */
	String calculateUniqueJobname(String name);
	
	SortedSet<String> getCurrentBatchJobnames();
	
	SortedSet<String> getCurrentJobnames();

}
