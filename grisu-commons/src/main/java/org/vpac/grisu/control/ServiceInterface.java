package org.vpac.grisu.control;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.NoValidCredentialException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.w3c.dom.Document;

import au.org.arcs.mds.GridResource;
import au.org.arcs.mds.JobSubmissionProperty;

/**
 * This is the central interface of grisu. These are the methods the web service
 * provices for the clients to access. I tried to keep the number of methods as
 * small as possible but I'm sure I'll have to add a few methods in the future.
 * 
 * @author Markus Binsteiner
 * 
 */
public interface ServiceInterface {

	double INTERFACE_VERSION = 8;

	// job creation method names
	String FORCE_NAME_METHOD = "force-name";
	String UUID_NAME_METHOD = "uuid";
	String TIMESTAMP_METHOD = "timestamp";

	// ---------------------------------------------------------------------------------------------------
	// 
	// General grisu specific methods
	//
	// ---------------------------------------------------------------------------------------------------
	double getInterfaceVersion();

	/**
	 * Starts a session. For some service interfaces this could be just a dummy
	 * method.
	 * 
	 * @param username
	 *            the username (probably for myproxy credential)
	 * @param password
	 *            the password (probably for myproxy credential)
	 * @throws NoValidCredentialException
	 *             if the login was not successful
	 */
	void login(String username, char[] password);

	/**
	 * Logout of the service. Performs housekeeping tasks and usually deletes
	 * the Credential.
	 * 
	 * @return a logout message
	 */
	String logout();

	/**
	 * Lists all applications that are supported by this deployment of a service
	 * interface. Basically it's a list of all the application where the service
	 * interface has got a template jsdl.
	 * 
	 * @return a list of all applications
	 */
	String[] listHostedApplicationTemplates();

	/**
	 * Gets the template Document for this application.
	 * 
	 * @param application
	 *            the name of the application
	 * @return a jsdl template document
	 * @throws NoSuchTemplateException
	 *             if a template for that particular application does not exist
	 */
	Document getTemplate(String application)
			throws NoSuchTemplateException;

	/**
	 * Gets a template Document for a particular version of an application.
	 * 
	 * @param application
	 *            the name of the application
	 * @param version
	 *            the version of the application
	 * @return a jsdl template document
	 * @throws NoSuchTemplateException
	 *             if a template for that particular application/version
	 *             combination does not exist
	 */
	Document getTemplate(String application, String version)
			throws NoSuchTemplateException;

	/**
	 * Submit a support request to the default person.
	 * 
	 * @param subject
	 *            a short summary of the problem
	 * @param description
	 *            the description of the problem
	 */
	void submitSupportRequest(String subject, String description);

	/**
	 * Returns an array of strings that are associated with this key. The
	 * developer can store all kinds of stuff he wants to associate with the
	 * user. Might be useful for history and such.
	 * 
	 * Not yet implemented though.
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	String getUserProperty(String key);

	/**
	 * Returns system or user specific information messages since a specific
	 * date
	 * 
	 * This isn't used yet...
	 * 
	 * @param date
	 *            the date from which to show messages or null for unread
	 *            messages
	 * @return a xml document which contains the messages
	 */
	Document getMessagesSince(Date date);

	/**
	 * Returns the end time of the credential used.
	 * 
	 * @return the end time
	 */
	long getCredentialEndTime();

	/**
	 * Can be used to inform the frontend what the backend is doing at the
	 * moment and what the bloody hell is taking so long... (like
	 * file-cross-staging...)
	 * 
	 * @param handle
	 *            the name of the action to monitor. This can be either a
	 *            jobname or a filetransfer handle
	 * @return the current status of any backend activity
	 */
	String getCurrentStatusMessage(String handle);

	// ---------------------------------------------------------------------------------------------------
	// 
	// Grid environment information methods
	//
	// ---------------------------------------------------------------------------------------------------

	/**
	 * Returns the name of the site this host belongs to.
	 * 
	 * @param host
	 *            the host
	 * @return the site
	 */

	String getSite(String host);

	/**
	 * This returns a map of all hosts that the information provider has listed
	 * and the site that they belong to as value. This method is mainly there
	 * because of performance reasons so clients can calculate possible
	 * submission locations easier.
	 * 
	 * @return a map with all possible hostnames and the respective sites they
	 *         belong to
	 */

	Map<String, String> getAllHosts();

	/**
	 * Calculates the executionfilesystem for this user at the specified site if
	 * the user submits a job. Something like gsiftp://ngdata.vpac.org/markus/
	 * (without the fqan part). This is one of the filesystems that are
	 * specified by the MountPoints a user has.
	 * 
	 * @param site
	 *            the site
	 * @return the root folder of the filesystem that is both accessible via
	 *         gridftp and the cluster
	 */
	//	
	// public String getExecutionFileSystem(String site);

	/**
	 * Calculates the job directory using the grisu-server.properties file, the
	 * vo information and the submissionlocation the job is supposed to run.
	 * 
	 * @param jobname
	 *            the name you want for your job
	 * @param submissionLocation
	 *            the submissionLocation for this site in the format
	 *            queue:host[#porttype]
	 * @param fqan
	 *            the vo for which the job shall run (or null for a non-vo job)
	 * @return the absolute working directory of the job if it is possible to
	 *         create it with the specified name
	 */
	@Deprecated String calculateAbsoluteJobDirectory(String jobname,
			String submissionLocation, String fqan);

	/**
	 * Calculates the job directory using the grisu-server.properties file and
	 * the information whether this is a vo or non-vo job. Useful if you want to
	 * know where to stage your files.
	 * 
	 * This is always something like grisu-jobs/whatever_job/
	 * 
	 * @param jobname
	 *            the name of the job
	 * @return the directory where all job related files are located. the return
	 *         path is relative to the execution file system. That's why it's
	 *         possible to call that method before the job is actually
	 *         submitted.
	 */
	@Deprecated String calculateRelativeJobDirectory(String jobname);

	/**
	 * Queries for all submission locations on the grid. Returns an array of
	 * Strings in the format: <queuename>:<submissionHost>[#porttype] (porttype
	 * can be ommitted if it's pbs.
	 * 
	 * @return all queues grid-wide
	 */

	String[] getAllSubmissionLocations();

	/**
	 * Returns all submission locations for this VO. Needed for better
	 * performance.
	 * 
	 * @param fqan
	 *            the VO
	 * @return all submission locations
	 */
	String[] getAllSubmissionLocations(String fqan);

	/**
	 * Returns all sites/queues that support this application. If "null" is
	 * provided, this method returns all available submission queues.
	 * 
	 * The format of the output a String for each submission location which
	 * looks like: <queuename>:<submissionHost>[#porttype] (porttype can be
	 * ommitted if it's pbs.
	 * 
	 * @param application
	 *            the application.
	 * @return all sites that support this application.
	 */
	String[] getSubmissionLocationsForApplication(String application);

	/**
	 * Returns all sites/queues that support this version of this application.
	 * 
	 * The format of the output a String for each submission location which
	 * looks like: <queuename>:<submissionHost>[#porttype] (porttype can be
	 * ommitted if it's pbs.
	 * 
	 * @param application
	 *            the application.
	 * @param version
	 *            the version
	 * @return all sites that support this application.
	 */

	String[] getSubmissionLocationsForApplication(String application,
			String version);

	/**
	 * Returns all sites/queues that support this version of this application if
	 * the job is submitted with the specified fqan.
	 * 
	 * The format of the output a String for each submission location which
	 * looks like: <queuename>:<submissionHost>[#porttype] (porttype can be
	 * ommitted if it's pbs.
	 * 
	 * @param application
	 *            the application.
	 * @param version
	 *            the version
	 * @param fqan
	 *            the fqan
	 * @return all sites that support this application.
	 */

	String[] getSubmissionLocationsForApplication(String application,
			String version, String fqan);

	/**
	 * Returns a map of all versions and all submission locations of this
	 * application. The key of the map is the version, and the
	 * submissionlocations are the values. If there is more than one
	 * submissionLocation for a version, then they are seperated via commas.
	 * 
	 * @param application the name of the application
	 * @return a map with all versions of the application as key and the
	 *         submissionLocations as comma
	 */
	Map<String, String> getSubmissionLocationsPerVersionOfApplication(
			String application);

	/**
	 * Checks the available data locations for the specified site and VO.
	 * 
	 * @param fqan
	 *            the VO
	 * @return a map of datalocations for this vo with the root url of the
	 *         location as key (e.g. gsiftp://brecca.vpac.monash.edu.au:2811 and
	 *         the paths that are accessible for this VO there as values (e.g.
	 *         /home/grid-admin)
	 */

	Map<String, String[]> getDataLocationsForVO(String fqan);

	/**
	 * Returns an array of the versions of the specified application that a site
	 * supports.
	 * 
	 * @param application
	 *            the application
	 * @param site
	 *            the site
	 * @return the supported versions
	 */

	String[] getVersionsOfApplicationOnSite(String application,
			String site);

	String[] getVersionsOfApplicationOnSubmissionLocation(
			String application, String submissionLocation);

	//	
	// public String[] getVersionsOfApplicationOnSubmissionLocation(String
	// application, String submissionLocation, String fqan);

	/**
	 * Returns an array of the gridftp servers for the specified submission
	 * locations.
	 * 
	 * @param subLoc
	 *            the submission location
	 *            (queuename@cluster:contactstring#jobmanager)
	 * @return the gridftp servers
	 */

	String[] getStagingFileSystemForSubmissionLocation(String subLoc);

	/**
	 * Returns all fqans of the user for the vo's that are configured on the
	 * machine where this serviceinterface is hosted.
	 * 
	 * @return all fqans of the user
	 */

	String[] getFqans();

	/**
	 * Checks the current certificate and returns its' dn.
	 * 
	 * @return the dn of the users' certificate
	 */

	String getDN();

	/**
	 * I don't know whether this one should sit on the web service side or the
	 * client side. Anyway, here it is for now. It tells the client all sites a
	 * job can be submitted to.
	 * 
	 * @return all sites
	 */

	String[] getAllSites();

	/**
	 * Returns information about the staging filesystem for the specified site
	 * (would return something like: gsiftp://ngdata.vpac.org). Used for
	 * mounting MountPoints in the first place.
	 * 
	 * @param site
	 *            the site you are interested in
	 * @return the filesystem
	 */
	//	
	// public String getStagingFileSystem(String site);

	/**
	 * Returns all applications that are available grid-wide or at certain
	 * sites.
	 * 
	 * @param sites
	 *            all the sites you want to query or null for a grid-wide search
	 * @return all applications
	 */

	String[] getAllAvailableApplications(String[] sites);

	/**
	 * Returns all the details that are know about this version of the
	 * application. The return will look something like this: module=namd/2
	 * executable=/usr/local/bin/namd2 or whatever.
	 * 
	 * @param application
	 *            the name of the application
	 * @param version
	 *            the version of the application
	 * @param site
	 *            the site where you want to run the application
	 * @return details about the applications
	 */

	Map<String, String> getApplicationDetails(String application,
			String version, String site);

	/**
	 * Returns all the details that are know about the default version of the
	 * application. The return will look something like this: module=namd/2
	 * executable=/usr/local/bin/namd2 or whatever.
	 * 
	 * @param application
	 *            the name of the application
	 * @param site_or_submissionLocation
	 *            the site where you want to run the application, you can also
	 *            specify a submissionlocation (but this will be slower
	 *            possibly)
	 * @return details about the applications
	 */

	Map<String, String> getApplicationDetails(String application,
			String site_or_submissionLocation);

	/**
	 * Takes a jsdl template and returns a list of submission locations that
	 * match the requirements. The order of the list is determined by the
	 * underlying ranking algorithm.
	 * 
	 * @param jsdl
	 *            the jdsl file
	 * @param fqan
	 *            the fqan to use to submit the job
	 * @return a list of matching submissionLoctations
	 */
	List<GridResource> findMatchingSubmissionLocations(Document jsdl,
			String fqan);

	/**
	 * Takes a jsdl template and returns a list of submission locations that
	 * match the requirements. The order of the list is determined by the
	 * underlying ranking algorithm.
	 * 
	 * @param jobProperties
	 *            the job Properties (have alook at the {@link ServiceInterface}
	 *            interface for supported keys)
	 * @param fqan
	 *            the fqan to use to submit the job
	 * @return a list of matching submissionLoctations
	 */
	List<GridResource> findMatchingSubmissionLocations(
			Map<String, String> jobProperties, String fqan);

	// ---------------------------------------------------------------------------------------------------
	// 
	// Filesystem methods
	//
	// ---------------------------------------------------------------------------------------------------

	/**
	 * Mounts a filesystem so a user can easily move stuff around on the
	 * ServiceInterface.
	 * 
	 * @param url
	 *            the url of the filesystem to mount (e.g.
	 *            gsiftp://ngdata.vpac.org/home/san04/markus)
	 * @param mountpoint
	 *            the mountpoint (has to be in the root directory: /ngdata.vpac
	 *            is ok, /vpac/ngdata is not
	 * @param useHomeDirectoryOnThisFileSystemIfPossible
	 *            use the users' home directory on this file system if possible
	 * @return the new mountpoint
	 * @throws RemoteFileSystemException
	 *             if the remote filesystem could not be mounted/connected to
	 */

	MountPoint mount(String url, String mountpoint,
			boolean useHomeDirectoryOnThisFileSystemIfPossible)
			throws RemoteFileSystemException;

	/**
	 * Mounts a filesystem so a user can easily move stuff around on the
	 * ServiceInterface.
	 * 
	 * @param url
	 *            the url of the filesystem to mount (e.g.
	 *            gsiftp://ngdata.vpac.org/home/san04/markus)
	 * @param mountpoint
	 *            the mountpoint (has to be in the root directory: /ngdata.vpac
	 *            is ok, /vpac/ngdata is not
	 * @param fqan
	 *            use a vomsproxy with this fqan to connect to the mounted
	 *            filesystem
	 * @param useHomeDirectoryOnThisFileSystemIfPossible
	 *            use the users' home directory on this file system if possible
	 * @return the new MountPoint
	 * @throws RemoteFileSystemException
	 *             if the remote filesystem could not be mounted/connected to
	 */

	MountPoint mount(String url, String mountpoint, String fqan,
			boolean useHomeDirectoryOnThisFileSystemIfPossible)
			throws RemoteFileSystemException;

	/**
	 * Unmounts a filesystem.
	 * 
	 * @param mountpoint
	 *            the mountpoint
	 * @return whether it worked or not
	 */

	void umount(String mountpoint);

	/**
	 * Lists all the mountpoints of the user's virtual filesystem.
	 * 
	 * @return all the MountPoints
	 */

	MountPoint[] df();

	/**
	 * Returns the mountpoint that is used to acccess this uri.
	 * 
	 * @param uri
	 *            the uri
	 * @return the mountpoint or null if no mountpoint can be found
	 */

	MountPoint getMountPointForUri(String uri);

	/**
	 * Upload a {@link DataSource} to the users' virtual filesystem.
	 * 
	 * @param file
	 *            the (local) file you want to upload
	 * @param filename
	 *            the location you want the file upload to
	 * @param return_absolute_url
	 *            whether you want the new location of the file absolute or
	 *            "user-space" style
	 * @return the new path of the uploaded file or null if the upload failed
	 * @throws RemoteFileSystemException
	 *             if the remote (target) filesystem could not be connected /
	 *             mounted / is not writeable
	 */

	String upload(DataSource file, String filename,
			boolean return_absolute_url) throws RemoteFileSystemException;
	//	
	// public String uploadByteArray(byte[] file, String filename, boolean
	// return_absolute_url) throws RemoteFileSystemException, VomsException;

	//	
	// public String uploadByteArray(byte[] source, String filename,
	// boolean return_absolute_url, int offset, int length) throws
	// RemoteFileSystemException,
	// VomsException;

	/**
	 * Download a file to the client.
	 * 
	 * @param filename
	 *            the filename of the file either absolute or "user-space" url
	 * @return the data
	 * @throws RemoteFileSystemException
	 *             if the remote (source) file system could not be conntacted
	 *             /mounted / is not readable
	 */

	DataSource download(String filename)
			throws RemoteFileSystemException;

	//	
	// public byte[] downloadByteArray(String filename) throws
	// RemoteFileSystemException, VomsException;

	//	
	// public byte[] downloadByteArray(String filename, int offset, int length)
	// throws RemoteFileSystemException, VomsException;

	/**
	 * Lists the content of the specified directory.
	 * 
	 * @param directory
	 *            the directory you want to have a listing of. This has to be an
	 *            absolute path (either something like: /ngdata_vpac/file.txt or
	 *            gsiftp://ngdata.vpac.org/home/san04/markus/file.txt
	 * @param recursion_level
	 *            the level of recursion for the directory listing, use -1 for
	 *            infinite but beware, the filelisting can take a long, long
	 *            time. Usually you would specify 1 and fill your filetree on
	 *            the clientside on demand.
	 * @param absolute_url
	 *            whether the returned url of the files (within the xml) should
	 *            be absolute (true) or relative (aka mounted - false)
	 * @return the content of the directory or null if the directory is empty.
	 *         If the specified directory is a file, only information about this
	 *         one file is returned.
	 * @throws RemoteFileSystemException
	 *             if the remote directory could not be read/mounted
	 */

	Document ls(String directory, int recursion_level,
			boolean absolute_url) throws RemoteFileSystemException;

	String ls_string(String directory, int recursion_level,
			boolean absolute_url) throws RemoteFileSystemException;

	/**
	 * Copies one file to another location (recursively if it's a directory).
	 * 
	 * @param source
	 *            the source file
	 * @param target
	 *            the target file
	 * @param overwrite
	 *            whether to overwrite a possible target file
	 * @param waitForFileTransferToFinish
	 *            whether to wait for the file transfer to finish (true) or
	 *            whether to run the filetransfer in the background
	 * @return the filetransfer handle
	 * @throws RemoteFileSystemException
	 *             if the remote source file system could not be read/mounted or
	 *             the remote target file system could not be written to
	 */

	String cp(String source, String target, boolean overwrite,
			boolean waitForFileTransferToFinish)
			throws RemoteFileSystemException;

	/**
	 * Checks whether the specified file is a folder or not.
	 * 
	 * @param file
	 *            the file
	 * @return true - if folder; false - if not
	 * @throws RemoteFileSystemException
	 *             if the files can't be accessed
	 */

	/**
	 * Checks whether the specified file/folder exists.
	 * 
	 * @param file
	 *            the file or folder
	 * @return true - exists, false - doesn't exist
	 * @throws RemoteFileSystemException
	 *             if the file system can't be accessed to determine whether the
	 *             file exists
	 */
	boolean fileExists(String file) throws RemoteFileSystemException;

	boolean isFolder(String file) throws RemoteFileSystemException;

	/**
	 * Finds all children files for the specified folder. Useful if you want to
	 * download a whole foldertree. Use with caution because that can be very
	 * slow for big folders.
	 * 
	 * @param folder
	 *            the folder in question
	 * @param onlyFiles
	 *            whether only files should be returned (true) or folders too
	 *            (false).
	 * @return all filenames of the folders' children
	 * @throws RemoteFileSystemException
	 *             if the folder can't be accessed/read
	 */

	String[] getChildrenFiles(String folder, boolean onlyFiles)
			throws RemoteFileSystemException;

	/**
	 * Returns the size of the file in bytes. This will probably replaced in a
	 * future version with a more generic method to get file properties.
	 * Something like public Map<String, String> getFileSize(String[]
	 * propertyNames)...
	 * 
	 * @param file
	 *            the url of the file
	 * @return the size of the file in bytes
	 * @throws RemoteFileSystemException
	 *             if the file can't be accessed
	 */

	long getFileSize(String file) throws RemoteFileSystemException;

	// for testing
	// public void downloadFolder(String folder) throws
	// RemoteFileSystemException, VomsException;

	/**
	 * Returns the date when the file was last modified.
	 * 
	 * @param remoteFile
	 *            the file to check
	 * @return the last modified date
	 * @throws RemoteFileSystemException if the file could not be accessed
	 */
	long lastModified(String remoteFile)
			throws RemoteFileSystemException;

	/**
	 * Creates the specified folder (and it's parent folders if they don't
	 * exist).
	 * 
	 * @param folder
	 *            the url of the folder
	 * @return true - if the folder has been created successfully, false - if
	 *         the folder already existed or could not be created
	 * @throws RemoteFileSystemException
	 *             if the filesystem could not be accessed
	 */
	boolean mkdir(String folder) throws RemoteFileSystemException;

	/**
	 * Deletes a remote file.
	 * 
	 * @param file
	 *            the file to delete
	 * @throws RemoteFileSystemException
	 *             if the filesystem could not be accessed
	 */
	void deleteFile(String file) throws RemoteFileSystemException;

	/**
	 * Deletes a bunch of remote files.
	 * 
	 * @param files
	 *            the files to delete
	 * @throws RemoteFileSystemException
	 *             if the filesystem could not be accessed
	 */
	void deleteFiles(String[] files) throws RemoteFileSystemException;

	// ---------------------------------------------------------------------------------------------------
	// 
	// Job management methods
	//
	// ---------------------------------------------------------------------------------------------------

	/**
	 * Returns a xml document that contains all the jobs of the user with
	 * information about the jobs.
	 * 
	 * @return xml formated information about all the users jobs
	 */

	Document ps();

	String ps_string();

	String[] getAllJobnames();

	/**
	 * Creates a job with an autogenerated jobname that uses the selected
	 * createJobNameMethod and the provided jobname. After figuring out a free
	 * jobname, the job is saved in the db. You need to call setJobDescription
	 * explicitly if you use this method to create a job.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param createJobNameMethod
	 *            the method how to autogenerete the name of the job
	 * @return the (autogenerated) jobname
	 * @deprecated don't use that anymore for your client, use
	 *             {@link #createJob(Map, String, String)} or
	 *             {@link #createJob(Document, String, String)} instead.
	 */
	String createJob(String jobname, int createJobNameMethod);

	/**
	 * Creates a job using the jobProperties that are specified in the map and
	 * the vo that should be used to submit the job.
	 * 
	 * Internally, this validates all the jobproperties, tries to auto-fill
	 * properties that are not specified (maybe version or submissionlocation).
	 * For a list of valid job property keynames have a look here:
	 * {@link JobSubmissionProperty}. If not all required job properties can be
	 * calculated, this method throws a {@link JobPropertiesException}.
	 * 
	 * @param jobProperties
	 *            a map of all job properties
	 * @param fqan
	 *            the vo to use to submit the job
	 * @param jobnameCreationMethod
	 *            the method to use to (possibly) auto-calculate the jobname (if
	 *            one with the specfied jobname in the jobProperties already
	 *            exists). This defaults to "force-name" if you specify null.
	 * @return the name of the job (auto-calculated or not) which is used as a
	 *         handle
	 * @throws JobPropertiesException
	 *             if the job could not be created (maybe because the jobname
	 *             already exists and force-jobname is specified as jobname
	 *             creation method).
	 */
	String createJob(Map<String, String> jobProperties, String fqan,
			String jobnameCreationMethod) throws JobPropertiesException;

	/**
	 * This method calls {@link #createJob(Map, String, String)} internally with
	 * a map of job properties that are extracted from the jsdl document.
	 * 
	 * @param jsdl
	 *            a jsdl document
	 * @param fqan
	 *            the vo tu use to submit the job
	 * @param jobnameCreationMethod
	 *            the method to use to (possibly) auto-calculate the jobname (if
	 *            one with the specfied jobname in the jobProperties already
	 *            exists). This defaults to "force-name" if you specify null.
	 * @return the name of the job (auto-calculated or not) which is used as a
	 *         handle
	 * @throws JobPropertiesException
	 *             if the job could not be created (maybe because the jobname
	 *             already exists and force-jobname is specified as jobname
	 *             creation method).
	 */
	String createJob(Document jsdl, String fqan,
			String jobnameCreationMethod) throws JobPropertiesException;

	/**
	 * Sets the jobdescription for the job. It's only needed if you used
	 * {@link #createJob(String, int)} to create a job. If there is a jobname
	 * specified in the jobdescription it will be replaced with the actual
	 * jobname.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param jsdl
	 *            the job description
	 * @throws NoSuchJobException if no such job exists
	 * @Deprecated don't use that anymore. Use
	 *             {@link #createJob(Map, String, String)} or
	 *             {@link #createJob(Document, String, String)} to create jobs
	 *             from now on.
	 */
	@Deprecated void setJobDescription(String jobname, Document jsdl)
			throws NoSuchJobException;

	/**
	 * @param jobname the name of the job
	 * @param jsdl the job description
	 * @throws NoSuchJobException if no such job exists
	 * @deprecated
	 */
	void setJobDescription_string(String jobname, String jsdl)
			throws NoSuchJobException;

	// /**
	// * Sets the jobdescription if you want to submit a globus-style multijob.
	// That only works if the
	// * submission backend supports multijobs.
	// * @param jobname the name of the job
	// * @param jsdl the job description
	// * @throws JobDescriptionNotValidException on of the job descriptions is
	// not vailid
	// * @throws NoSuchJobException no job with this jobname is created in the
	// database
	// */
	//	
	// public void setJobDescription(String jobname, Document[] jsdl) throws
	// JobDescriptionNotValidException, NoSuchJobException;
	/**
	 * Submits the job but does not use a credential that is stored in the
	 * database but the users default one.
	 * 
	 * @param jobname
	 *            the name of the job to submit
	 * @param fqan the fqan
	 * @throws ServerJobSubmissionException
	 *             if the job could not be submitted
	 * @throws NoValidCredentialException
	 *             if the specified credential is not valid (anymore)
	 * @throws RemoteFileSystemException
	 *             if the job folder could not be created on the remote
	 *             filesystem
	 * @throws NoSuchJobException if no such job exists 
	 * @Deprecated Don't use that anymore. Use {@link #submitJob(String)} from
	 *             now on.
	 */
	@Deprecated void submitJob(String jobname, String fqan)
			throws RemoteFileSystemException,
			NoSuchJobException;

	/**
	 * Submits the job that was prepared before using
	 * {@link #createJob(Map, String, String)} or
	 * {@link #createJob(Document, String, String)} to the specified submission
	 * location.
	 * 
	 * @param jobname
	 *            the jobname
	 * @throws JobSubmissionException
	 *             if the job could not submitted
	 */
	void submitJob(String jobname) throws JobSubmissionException;

	/**
	 * Returns the job directory. This one only works if the job was submitted
	 * already. Otherwise it'll return null;
	 * 
	 * @param jobname
	 *            the name of the submitted job
	 * @return the (absolute) job directory
	 * @throws NoSuchJobException if no such job exists 
	 * @Deprecated don't use that anymore. Use {
	 *             {@link #getJobProperty(String, String)} instead.
	 */
	@Deprecated String getJobDirectory(String jobname) throws NoSuchJobException;

	/**
	 * Method to query the status of a job. The String representation of the
	 * status can be obtained by calling
	 * {@link JobConstants#translateStatus(int)}
	 * 
	 * @param jobname
	 *            the name of the job to query
	 * @return the status of the job
	 * @throws NoSuchJobException
	 *             if no job with the specified jobname exists
	 */

	int getJobStatus(String jobname);

	/**
	 * Gets detailed information about the job with the specified jobname.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @return detailed information about the job
	 * @throws NoSuchJobException if no such job exists
	 */
	Document getJobDetails(String jobname) throws NoSuchJobException;

	/**
	 * Gets detailed information about the job with the specified jobname.
	 * 
	 * @param jobname the name of the job
	 * @return detailed information about the job
	 * @throws NoSuchJobException if no such job exists
	 */
	String getJobDetails_string(String jobname)
			throws NoSuchJobException;

	/**
	 * Deletes the whole jobdirectory and if successful, the job from the
	 * database.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param clean
	 *            whether to clean/delete the jobdirectory if possible
	 * @throws RemoteFileSystemException
	 *             if the files can't be deleted
	 * @throws NoSuchJobException if no such job exists
	 */

	void kill(String jobname, boolean clean)
			throws RemoteFileSystemException, NoSuchJobException;

	/**
	 * If you want to store certain values along with the job which can be used
	 * after the job is finished. For example the name of an output directory
	 * that is stored in one of the input files. That way you don't have to
	 * download the input file again and parse it.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param key
	 *            the key for the value you want to add
	 * @param value
	 *            the value
	 * @throws NoSuchJobException
	 *             if there is no job with this jobname in the database
	 */

	void addJobProperty(String jobname, String key, String value)
			throws NoSuchJobException;

	/**
	 * Adds multiple job propeties in one go.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param properties
	 *            the properties you want to connect to the job
	 * @throws NoSuchJobException
	 *             if there is no job with this jobname in the database
	 */

	void addJobProperties(String jobname, Map<String, String> properties)
			throws NoSuchJobException;

	/**
	 * Return the value of a property that is stored along with a job.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param key
	 *            the key for the value you are interested in
	 * @return the value
	 * @throws NoSuchJobException if no such job exists
	 */

	String getJobProperty(String jobname, String key)
			throws NoSuchJobException;

	/**
	 * Returns all job properties as a Map.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @return the job properties
	 * @throws NoSuchJobException if no such job exists
	 */

	Map<String, String> getAllJobProperties(String jobname)
			throws NoSuchJobException;

	/**
	 * Returns the fqan that was used to submit the job.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @return the fqan
	 * @throws NoSuchJobException if no such job exists
	 * @Deprecated don't use that anymore. Use {
	 *             {@link #getJobProperty(String, String)} instead.
	 */
	@Deprecated String getJobFqan(String jobname) throws NoSuchJobException;

	Document getJsldDocument(String jobname) throws NoSuchJobException;

	// ---------------------------------------------------------------------------------------------------
	// 
	// Possible additional methods
	//
	// ---------------------------------------------------------------------------------------------------

	// /**
	// * This is not used at the moment. Use on your own risk.
	// *
	// * Saves the ServiceInterfaces default credential (which can be a local
	// * proxy or a myproxy credential or whatever...) to the database so the
	// * ServiceInterface can use it to submit jobs. It's mainly intended for
	// use if
	// * we later decide to implement workflows and something has to be done
	// * after the job finishes. For a normal job we are ok with a credential
	// that only is valid until
	// * the job is submitted to the queuing system. If the user wants to
	// retrieve the data
	// * afterwards he just has to create a new credential.
	// *
	// * Usually it's the credential that is used to log on to the service or a
	// * local grid proxy.
	// *
	// * @return the id of the credential or null if it did not work
	// * @throws NoValidCredentialException if the credential is not valid
	// (anymore)
	// */
	//	
	// public Long saveDefaultCredentialToDatabase() throws
	// NoValidCredentialException;

	// /**
	// * Gets all available versions of a particular application
	// * @param application the name of the application
	// * @return a list of all versions of this application (default version is
	// on index 0) or null if no version is available
	// * @throws NoSuchTemplateException if a template for that particular
	// application does not exist
	// */
	//	
	// public String[] getVersions(String application) throws
	// NoSuchTemplateException;

	// /**
	// * Returns the default version of the specified application (I reckon this
	// would usually be the
	// * latest version).
	// *
	// * @param application the application
	// * @param site the site
	// * @return the version
	// */
	// public String getDefaultVersionOfApplicationOnSite(String application,
	// String site);

}
