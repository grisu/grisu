package org.vpac.grisu.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.swing.tree.TreeModel;

import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.StatusException;
import org.vpac.grisu.model.dto.DtoBatchJob;
import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.model.files.GlazedFile;
import org.vpac.grisu.model.status.StatusObject;

/**
 * Wrapps information about the user and the available resources to him
 * grid-wide.
 * 
 * @author Markus Binsteiner
 * 
 */
public interface UserEnvironmentManager {

	/**
	 * Adds a fqan listener to this environment manager.
	 * 
	 * @param listener
	 *            the listener
	 */
	void addFqanListener(FqanListener listener);

	/**
	 * Calculates a unique jobname.
	 * 
	 * Looks up all existing jobs and appends a number if the string already
	 * exists.
	 * 
	 * @param name
	 *            the base-name
	 * @return the unique jobname
	 */
	String calculateUniqueJobname(String name);

	/**
	 * All of the users fqans.
	 * 
	 * @return the fqans
	 */
	String[] getAllAvailableFqans();

	/**
	 * Returns all fqans for which there is an available submission location for
	 * the specified application
	 * 
	 * @param application
	 *            the application
	 * @return the list of fqans
	 */
	Set<String> getAllAvailableFqansForApplication(String application);

	/**
	 * Calculates all sites that are available for the user.
	 * 
	 * @return all sites
	 */
	SortedSet<String> getAllAvailableSites();

	/**
	 * All the submissionLocations the user has got access to with all his
	 * fqans.
	 * 
	 * @return all submissionLocations
	 */
	Set<String> getAllAvailableSubmissionLocations();

	/**
	 * A convenience method to get a list of all unique groupnames (shorter
	 * aliases for the fqans) that are available for the user.
	 * 
	 * @return the unique groupnames
	 */
	String[] getAllAvailableUniqueGroupnames();

	/**
	 * Returns the DtoBatchJob object with the specified jobname.
	 * 
	 * @param jobname
	 *            the name of the batchjob
	 * @param refreshBatchJob
	 *            whether to refresh the status of the batchjob
	 * @return the DtoBatchJob object
	 * @throws NoSuchJobException
	 *             if no such job exists
	 */
	DtoBatchJob getBatchJob(String jobname, boolean refreshBatchJob)
			throws NoSuchJobException;

	SortedSet<DtoBatchJob> getBatchJobs(String application, boolean refresh);

	/**
	 * Gets the list of the users bookmarks.
	 * 
	 * @return the users bookmarks.
	 */
	Map<String, String> getBookmarks();

	List<FileSystemItem> getBookmarksFilesystems();

	/**
	 * Returns a list of all currently used applications for a user.
	 * 
	 * All currently used applications means applications where there is at
	 * least one job in the backend database.
	 * 
	 * @param refresh
	 *            whether to refresh the list
	 * @return the list of applications
	 */
	SortedSet<String> getCurrentApplications(boolean refresh);

	/**
	 * Returns a list of all currently used applications for batchjobs for a
	 * user.
	 * 
	 * All currently used applications means applications where there is at
	 * least one batchjob in the backend database.
	 * 
	 * @param refresh
	 *            whether to refresh the list
	 * @return the list of applications
	 */
	SortedSet<String> getCurrentApplicationsBatch(boolean refresh);

	/**
	 * Returns all batchjobnames of the user.
	 * 
	 * 
	 * @param refresh
	 *            whether to refresh the list or not
	 * @return all batchjobnames
	 */
	SortedSet<String> getCurrentBatchJobnames(boolean refresh);

	/**
	 * Returns all batchjobnames of the user where the batchjob is of the
	 * specified application.
	 * 
	 * @param application
	 *            the application of the batchjobs
	 * @param refresh
	 *            whether to refresh the list of batchjobnames for this
	 *            application
	 * @return the batchjob names
	 */
	SortedSet<String> getCurrentBatchJobnames(String application,
			boolean refresh);

	/**
	 * Returns the currently set fqan.
	 * 
	 * @return the fqan
	 */
	String getCurrentFqan();

	/**
	 * Returns all jobnames (for single jobs) of the user.
	 * 
	 * @param refresh
	 *            whether to refresh the list or not
	 * @return all jobnames
	 */
	SortedSet<String> getCurrentJobnames(boolean refresh);

	/**
	 * Returns all jobnames of the user where the (single) job is of the
	 * specified application.
	 * 
	 * @param application
	 *            the application of the jobs
	 * @param refresh
	 *            whether to refresh the list
	 * 
	 * @return the jobnames
	 */
	SortedSet<String> getCurrentJobnames(String application, boolean refresh);

	/**
	 * Returns the filesystem which is associated with the specified url.
	 * 
	 * @param url
	 *            the url
	 * @return the filesystem or null if no filsystem can be found
	 */
	FileSystemItem getFileSystemForUrl(String url);

	/**
	 * Returns a list of all the available filesystems of a user.
	 * 
	 * Includes local filesystems, bookmarks and remote ones.
	 * 
	 * @return the filesystems
	 */
	List<FileSystemItem> getFileSystems();

	/**
	 * Translate back the unique String from {@link #getUniqueGroupname(String)}
	 * .
	 * 
	 * @param uniqueGroupname
	 *            the unique groupname
	 * @return the full fqan
	 */
	String getFullFqan(String uniqueGroupname);

	/**
	 * Returns a {@link TreeModel} that can be used to traverse through a group
	 * based view of all the users filesystems.
	 * 
	 * @param root
	 *            the root of the view or null for a view with all the users
	 *            groups at root level
	 * 
	 * @return the model
	 */
	TreeModel getGroupTreeFileModel(GlazedFile root);

	/**
	 * Returns all local filesystems.
	 * 
	 * @return all local filesystems
	 */
	List<FileSystemItem> getLocalFileSystems();

	/**
	 * Returns the mountPoint for this alias.
	 * 
	 * @param alias
	 *            the alias
	 * @return the mountpoint or null if no mountpoint with this alias was found
	 */
	MountPoint getMountPointForAlias(String url);

	/**
	 * Returns the mountpoint that is used to access the specified url.
	 * 
	 * @param url
	 *            the url
	 * @return the mountpoint or null if no mountpoint could be found
	 */
	MountPoint getMountPointForUrl(String url);

	/**
	 * Get all the users' mountpoints.
	 * 
	 * @return all mountpoints
	 */
	MountPoint[] getMountPoints();

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
	 * @param site
	 *            the site
	 * @return the mountpoints
	 */
	SortedSet<MountPoint> getMountPointsForSite(String site);

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
	 * Retrieves a user property from the backend.
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	String getProperty(String key);

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
	MountPoint getRecommendedMountPoint(String submissionLocation, String fqan);

	List<FileSystemItem> getRemoteSites();

	/**
	 * This method translates the provided fqan into the shortest possible
	 * unique groupname, starting from the last token.
	 * 
	 * For example, if a user is a member of the following VOs:<br>
	 * :/ARCS<br>
	 * :/ARCS/BeSTGRID<br>
	 * :/ARCS/BeSTGRID/Bio/<br>
	 * :/ARCS/BeSTGRID/Bio/Project<br>
	 * :/ARCS/BeSTGRID/Bio/Project2<br>
	 * :/ARCS/BeSTGRID/Project<br>
	 * :/ARCS/BeSTGRID/Drugs/Project<br>
	 * <br>
	 * this would be the result:<br>
	 * :/ARCS/BeSTRID -> BeSTGRID :/ARCS/BeSTGRID/Bio/Project2 -> Project2
	 * :/ARCS/BeSTGRID/Bio/Project -> Bio/Project :/ARCS/BeSTGRID/Project ->
	 * BeSTGRID/Project :/ARCS/BeSTGRID/Drugs/Project -> Drugs/Project
	 * 
	 * @param fqan
	 *            the fqan to shorten
	 * @return the short, unique group name
	 */
	String getUniqueGroupname(String fqan);

	/**
	 * Returns whether the provided string is a mountpoint alias of one of the
	 * users' available mountpoints.
	 * 
	 * @param string
	 *            the string
	 * @return whether the string is a mountpoint alias or not
	 */
	boolean isMountPointAlias(String string);

	/**
	 * Returns whether the specified url is the root url of one of the users
	 * mountpoints.
	 * 
	 * @param rootUrl
	 *            the url
	 * @return whether the url is a the root of a mountpoint or not
	 */
	boolean isMountPointRoot(String rootUrl);

	/**
	 * Removes a fqan listener from this environment manager.
	 * 
	 * @param listener
	 *            the listener
	 */
	void removeFqanListener(FqanListener listener);

	/**
	 * Adds a bookmark to the users bookmarks.
	 * 
	 * Use null if you want to delete a bookmark.
	 * 
	 * @param alias
	 *            the alias
	 * @param url
	 *            the url
	 * 
	 * @return the filesystemitem that represents the bookmark
	 */
	FileSystemItem setBookmark(String alias, String url);

	/**
	 * Sets the currently used fqan.
	 * 
	 * @param currentFqan
	 *            the fqan
	 */
	void setCurrentFqan(String currentFqan);

	/**
	 * Sets a user property on the backend.
	 * 
	 * @param key
	 * @param value
	 */
	void setProperty(String key, String value);

	/**
	 * Convenience method to wait for an action (like batchJob submission) to
	 * finish.
	 * 
	 * @param handle
	 *            the action handle
	 * @return the statusObject
	 * @throws InterruptedException
	 * @throws StatusException
	 */
	StatusObject waitForActionToFinish(String handle)
			throws InterruptedException, StatusException;

}
