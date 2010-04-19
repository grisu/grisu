package org.vpac.grisu.control;

import java.util.Map;

import javax.activation.DataHandler;
import javax.annotation.security.RolesAllowed;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlMimeType;

import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.NoValidCredentialException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoActionStatus;
import org.vpac.grisu.model.dto.DtoApplicationDetails;
import org.vpac.grisu.model.dto.DtoApplicationInfo;
import org.vpac.grisu.model.dto.DtoBatchJob;
import org.vpac.grisu.model.dto.DtoDataLocations;
import org.vpac.grisu.model.dto.DtoFolder;
import org.vpac.grisu.model.dto.DtoGridResources;
import org.vpac.grisu.model.dto.DtoHostsInfo;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;
import org.vpac.grisu.model.dto.DtoMountPoints;
import org.vpac.grisu.model.dto.DtoProperties;
import org.vpac.grisu.model.dto.DtoStringList;
import org.vpac.grisu.model.dto.DtoSubmissionLocations;

import au.org.arcs.jcommons.constants.Constants;

/**
 * This is the central interface of grisu. These are the methods the web service
 * provices for the clients to access. I tried to keep the number of methods as
 * small as possible but I'm sure I'll have to add a few methods in the future.
 * 
 * @author Markus Binsteiner
 * 
 */

@WebService(targetNamespace = "http://api.grisu.arcs.org.au/", serviceName = "GrisuService")
public interface ServiceInterface {

	String INTERFACE_VERSION = "0.3-SNAPSHOT";

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
	@POST
	@Path("actions/addJobProperties/{jobname}")
	@RolesAllowed("User")
	void addJobProperties(@PathParam("jobname") String jobname,
			@QueryParam("properties") DtoJob properties)
	throws NoSuchJobException;

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
	@POST
	@Path("actions/addJobProperty/{jobname}")
	@RolesAllowed("User")
	void addJobProperty(@PathParam("jobname") String jobname,
			@QueryParam("key") String key, @QueryParam("value") String value)
	throws NoSuchJobException;

	/**
	 * Adds the specified job to the mulitpartJob.
	 * 
	 * @param batchjobname
	 *            the batchjobname
	 * @param jobdescription
	 *            the jsdl string
	 */
	@RolesAllowed("User")
	String addJobToBatchJob(String batchjobname, String jobdescription)
	throws NoSuchJobException, JobPropertiesException;

	/**
	 * Archives this job to the specified url and deletes it from the database.
	 * 
	 * If target is null, the user property {@link Constants#DEFAULT_ARCHIVE_LOCATION} is used.
	 * 
	 * @param jobname the jobname
	 * @param target the url (of the parent dir) to archive the job to or null to use the (previously set) default archive location
	 * @return the url of the target directory
	 * @throws NoSuchJobException if no such job exists
	 * @throws JobPropertiesException if the job is not finished yet
	 * @throws RemoteFileSystemException if the archive location is not specified or there is some other kind of file related exception
	 */
	@POST
	@Path("actions/archiveJob/{jobname}")
	@RolesAllowed("User")
	String archiveJob(@PathParam("jobname") String jobname, @QueryParam("target") String target) throws NoSuchJobException, JobPropertiesException, RemoteFileSystemException;

	/**
	 * Distributes a remote input file to all the filesystems that are used in
	 * this batchjob.
	 * 
	 * Use this after you created all jobs for this batchjob.
	 * 
	 * @param batchJobname
	 *            the name of the batchjob
	 * @param inputFile
	 *            the url of the inputfile
	 * @param filename
	 *            the target filename
	 * @throws RemoteFileSystemException
	 *             if there is a problem copying / accessing the file
	 * @throws NoSuchJobException
	 *             if the specified batchjob doesn't exist
	 */
	@RolesAllowed("User")
	void copyBatchJobInputFile(String batchJobname, String inputFile,
			String filename) throws RemoteFileSystemException,
			NoSuchJobException;

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
	@POST
	@Path("actions/cp")
	@RolesAllowed("User")
	String cp(@QueryParam("sources") DtoStringList sources,
			@QueryParam("target") String target,
			@QueryParam("overwrite") boolean overwrite,
			@QueryParam("wait") boolean wait) throws RemoteFileSystemException;

	/**
	 * Creates a batchjob on the server.
	 * 
	 * A batchjob is just a collection of jobs that belong together to make them
	 * more easily managable.
	 * 
	 * @param batchJobname
	 *            the id (name) of the batchjob
	 * @param fqan
	 *            the vo to use
	 * @throws JobPropertiesException
	 */
	@RolesAllowed("User")
	DtoBatchJob createBatchJob(String batchJobname, String fqan)
	throws BatchJobException;

	/**
	 * This method calls {@link #createJobUsingMap(Map, String, String)}
	 * internally with a map of job properties that are extracted from the jsdl
	 * document.
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
	@POST
	@Path("actions/createJobUsingJsdl")
	@RolesAllowed("User")
	String createJob(@QueryParam("jsdl") String jsdl,
			@QueryParam("fqan") String fqan,
			@QueryParam("method") String jobnameCreationMethod)
	throws JobPropertiesException;

	/**
	 * Deletes a remote file.
	 * 
	 * @param file
	 *            the file to delete
	 * @throws RemoteFileSystemException
	 *             if the filesystem could not be accessed
	 */
	@RolesAllowed("User")
	@POST
	@Path("actions/delete")
	void deleteFile(@QueryParam("url") String url)
	throws RemoteFileSystemException;

	/**
	 * Deletes a bunch of remote files.
	 * 
	 * This doesn't throw an exception if the deletion of one file fails.
	 * 
	 * @param files
	 *            the files to delete
	 */
	@RolesAllowed("User")
	@POST
	@Path("actions/deleteFiles")
	void deleteFiles(@QueryParam("urls") DtoStringList files);

	/**
	 * Lists all the mountpoints of the user's virtual filesystem.
	 * 
	 * @return all the MountPoints
	 */
	@GET
	@Path("user/allMountpoints")
	@RolesAllowed("User")
	DtoMountPoints df();

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
	@XmlMimeType("application/octet-stream")
	@POST
	@RolesAllowed("User")
	@Path("actions/download")
	DataHandler download(@QueryParam("filename") String filename)
	throws RemoteFileSystemException;

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
	@POST
	@Path("user/files/fileExists")
	@RolesAllowed("User")
	boolean fileExists(String file) throws RemoteFileSystemException;

	// ---------------------------------------------------------------------------------------------------
	//
	// Grid environment information methods
	//
	// ---------------------------------------------------------------------------------------------------

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
	@POST
	@Path("info/submissionlocations/forJsdlAndFqan")
	DtoGridResources findMatchingSubmissionLocationsUsingJsdl(
			@QueryParam("jsdl") String jsdl, @QueryParam("fqan") String fqan,
			@QueryParam("exclude") boolean exclude);

	/**
	 * Takes a jsdl template and returns a list of submission locations that
	 * match the requirements. The order of the list is determined by the
	 * underlying ranking algorithm.
	 * 
	 * @param jobProperties
	 *            the job Properties (have alook at the
	 *            {@link EnunciateServiceInterface} interface for supported
	 *            keys)
	 * @param fqan
	 *            the fqan to use to submit the job
	 * @return a list of matching submissionLoctations
	 */
	@POST
	@Path("info/submissionlocations/forJobPropertiesAndFqan")
	DtoGridResources findMatchingSubmissionLocationsUsingMap(
			@QueryParam("jobProperties") DtoJob jobProperties,
			@QueryParam("fqan") String fqan,
			@QueryParam("exclude") boolean exclude);

	/**
	 * Returns the current status of an ongoing action.
	 * 
	 * This is not stored in the database, so you can only access a status for
	 * an action that was created in the same session.
	 * 
	 * @param actionHandle
	 *            the (unique) handle of the action (e.g. the jobname or target
	 *            url)
	 * @return the status object
	 */
	@RolesAllowed("User")
	@Path("user/status/{handle}")
	DtoActionStatus getActionStatus(@PathParam("handle") String actionHandle);

	/**
	 * Returns all applications that are available grid-wide or at certain
	 * sites.
	 * 
	 * @param sites
	 *            all the sites you want to query or null for a grid-wide search
	 * @return all applications
	 */
	@POST
	@Path("info/applications")
	DtoStringList getAllAvailableApplications(
			@QueryParam("sites") DtoStringList sites);

	/**
	 * Returns a list of all batch jobnames that are currently stored on this
	 * backend
	 * 
	 * @return all batchjobnames
	 */
	@RolesAllowed("User")
	DtoStringList getAllBatchJobnames(String application);

	/**
	 * This returns a map of all hosts that the information provider has listed
	 * and the site that they belong to as value. This method is mainly there
	 * because of performance reasons so clients can calculate possible
	 * submission locations easier.
	 * 
	 * @return a map with all possible hostnames and the respective sites they
	 *         belong to
	 */
	@GET
	@Path("info/hosts/allHosts")
	DtoHostsInfo getAllHosts();

	/**
	 * Returns a list of all jobnames that are currently stored on this backend.
	 * 
	 * Doesn't include batchjobs.
	 * 
	 * @return all jobnames
	 */
	@GET
	@Path("user/alljobnames/{application}")
	DtoStringList getAllJobnames(@PathParam("application") String application);

	/**
	 * I don't know whether this one should sit on the web service side or the
	 * client side. Anyway, here it is for now. It tells the client all sites a
	 * job can be submitted to.
	 * 
	 * @return all sites
	 */
	@GET
	@Path("info/allSites")
	DtoStringList getAllSites();

	/**
	 * Queries for all submission locations on the grid. Returns an array of
	 * Strings in the format: <queuename>:<submissionHost>[#porttype] (porttype
	 * can be ommitted if it's pbs.
	 * 
	 * @return all queues grid-wide
	 */
	@GET
	@Path("info/submissionlocations")
	DtoSubmissionLocations getAllSubmissionLocations();

	/**
	 * Returns all submission locations for this VO. Needed for better
	 * performance.
	 * 
	 * @param fqan
	 *            the VO
	 * @return all submission locations
	 */
	@GET
	@Path("vo/{fqan}/submissionlocations")
	DtoSubmissionLocations getAllSubmissionLocationsForFqan(
			@PathParam("fqan") String fqan);

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
	@GET
	@Path("info/application/{application}/{version}/{site}")
	DtoApplicationDetails getApplicationDetailsForVersionAndSubmissionLocation(
			@PathParam("application") String application,
			@PathParam("version") String version, @PathParam("site") String site);

	/**
	 * Returns the {@link DtoBatchJob} with the specified name.
	 * 
	 * This method doesn't refresh the jobs that belong to this batchjob. Call
	 * {@link #refreshBatchJobStatus(String)} and monitor the action status for
	 * this until it finishes before you retrieve the batchjob if you want an
	 * up-to-date version of the batchjob.
	 * 
	 * @return the batchjob
	 */
	@RolesAllowed("User")
	DtoBatchJob getBatchJob(String batchJobname) throws NoSuchJobException;

	/**
	 * Gets the users bookmarks
	 * 
	 * @param alias
	 */
	@RolesAllowed("User")
	@GET
	@Path("user/bookmarks")
	DtoProperties getBookmarks();

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
	@RolesAllowed("User")
	@POST
	@Path("user/files/childrenfilenames")
	DtoStringList getChildrenFileNames(@QueryParam("url") String url,
			@QueryParam("onlyFiles") boolean onlyFiles)
	throws RemoteFileSystemException;

	/**
	 * Returns the end time of the credential used.
	 * 
	 * @return the end time or -1 if the endtime couldn't be determined
	 */
	@RolesAllowed("User")
	@GET
	@Path("user/session/credentialendtime")
	long getCredentialEndTime();

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
	@GET
	@Path("info/{fqan}/datalocations")
	DtoDataLocations getDataLocationsForVO(@PathParam("fqan") String fqan);

	/**
	 * Checks the current certificate and returns its' dn.
	 * 
	 * @return the dn of the users' certificate
	 */
	@GET
	@RolesAllowed("User")
	@Path("user/dn")
	@Produces("text/plain")
	String getDN();

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
	@RolesAllowed("User")
	@POST
	@Path("user/files/filesize")
	long getFileSize(@QueryParam("url") String url)
	throws RemoteFileSystemException;

	/**
	 * Returns all fqans of the user for the vo's that are configured on the
	 * machine where this serviceinterface is hosted.
	 * 
	 * @return all fqans of the user
	 */
	@GET
	@Path("user/fqans")
	@RolesAllowed("User")
	DtoStringList getFqans();

	// ---------------------------------------------------------------------------------------------------
	//
	// Filesystem methods
	//
	// ---------------------------------------------------------------------------------------------------

	// ---------------------------------------------------------------------------------------------------
	//
	// General grisu specific methods
	//
	// ---------------------------------------------------------------------------------------------------
	/**
	 * The version of the serviceInterface for this backend.
	 * 
	 * @return the version
	 */
	@GET
	@Path("interfaceVersion")
	String getInterfaceVersion();

	/**
	 * Returns the job details.
	 * 
	 * Does not refresh the job status.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @return the job properties
	 * @throws NoSuchJobException
	 *             if no such job exists
	 */
	@POST
	@Path("user/getJobDetails/{jobname}")
	@RolesAllowed("User")
	DtoJob getJob(@PathParam("jobname") String jobname)
	throws NoSuchJobException;

	/**
	 * Return the value of a property that is stored along with a job.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param key
	 *            the key for the value you are interested in
	 * @return the value
	 * @throws NoSuchJobException
	 *             if no such job exists
	 */
	@POST
	@Path("user/getJobProperty/{jobname}/{key}")
	@RolesAllowed("User")
	String getJobProperty(@PathParam("jobname") String jobname,
			@PathParam("key") String key) throws NoSuchJobException;

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
	@POST
	@Path("user/jobs/status/{jobname}")
	@RolesAllowed("User")
	int getJobStatus(@PathParam("jobname") String jobname);

	/**
	 * Returns the jsdl document that was used to create this job.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @return the jsdl document
	 * @throws NoSuchJobException
	 *             if no such job exists
	 */
	@GET
	@Path("user/getJsdl/{jobname}")
	@RolesAllowed("User")
	@Produces("text/xml")
	String getJsdlDocument(@PathParam("jobname") String jobname)
	throws NoSuchJobException;

	/**
	 * Returns the mountpoint that is used to acccess this uri.
	 * 
	 * @param uri
	 *            the uri
	 * @return the mountpoint or null if no mountpoint can be found
	 */
	@POST
	@RolesAllowed("User")
	@Path("user/mountpointForUrl")
	MountPoint getMountPointForUri(@QueryParam("url") String url);

	/**
	 * Returns the name of the site this host belongs to.
	 * 
	 * @param host
	 *            the host
	 * @return the site
	 */
	@GET
	@Path("info/hosts/{host}/site")
	@Produces("text/plain")
	String getSite(@PathParam("host") String host);

	/**
	 * Returns an array of the gridftp servers for the specified submission
	 * locations.
	 * 
	 * @param subLoc
	 *            the submission location
	 *            (queuename@cluster:contactstring#jobmanager)
	 * @return the gridftp servers
	 */
	@GET
	@Path("info/stagingfilesystems/{subloc}")
	DtoStringList getStagingFileSystemForSubmissionLocation(
			@PathParam("subloc") String subLoc);

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
	@GET
	@Path("info/{application}/allversions/submissionlocations")
	DtoSubmissionLocations getSubmissionLocationsForApplication(
			@PathParam("application") String application);

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
	@GET
	@Path("info/{application}/version/{version}/submissionlocations")
	DtoSubmissionLocations getSubmissionLocationsForApplicationAndVersion(
			@PathParam("application") String application,
			@PathParam("version") String version);

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
	@GET
	@Path("info/{application}/version/{version}/fqan/{fqan}/submissionlocations")
	DtoSubmissionLocations getSubmissionLocationsForApplicationAndVersionAndFqan(
			@PathParam("application") String application,
			@PathParam("version") String version, @PathParam("fqan") String fqan);

	/**
	 * Returns a map of all versions and all submission locations of this
	 * application. The key of the map is the version, and the
	 * submissionlocations are the values. If there is more than one
	 * submissionLocation for a version, then they are seperated via commas.
	 * 
	 * @param application
	 *            the name of the application
	 * @return a map with all versions of the application as key and the
	 *         submissionLocations as comma
	 */
	@GET
	@Path("info/{application}/submissionlocations")
	DtoApplicationInfo getSubmissionLocationsPerVersionOfApplication(
			@PathParam("application") String application);

	/**
	 * Gets the template Document for this application.
	 * 
	 * @param application
	 *            the name of the application
	 * @return a jsdl template document
	 * @throws NoSuchTemplateException
	 *             if a template for that particular application does not exist
	 */
	String getTemplate(String application) throws NoSuchTemplateException;

	/**
	 * Returns a list of all applications that are currently used for (non-batch-)jobs.
	 * 
	 * @return the list of applications
	 */
	@GET
	@Path("user/allUsedApplications")
	DtoStringList getUsedApplications();

	/**
	 * Returns a list of all applications that are currently used for batchjobs.
	 * 
	 * @return the list of applications
	 */
	@GET
	@Path("user/allUsedApplicationsBatch")
	DtoStringList getUsedApplicationsBatch();

	/**
	 * Gets all the properties stored for the current user.
	 * 
	 * @return all userproperties
	 */
	@RolesAllowed("User")
	@GET
	@Path("user/allproperties")
	DtoProperties getUserProperties();

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
	@RolesAllowed("User")
	@GET
	@Path("user/properties/{key}")
	@Produces("text/plain")
	String getUserProperty(@PathParam("key") String key);

	/**
	 * Returns an array of the versions of the specified application that a
	 * submissionlocation supports.
	 * 
	 * @param application
	 *            the application
	 * @param site
	 *            the site
	 * @return the supported versions
	 */
	@GET
	@Path("info/application/{application}/{submissionLocation}/versions")
	DtoStringList getVersionsOfApplicationOnSubmissionLocation(
			@PathParam("application") String application,
			@PathParam("submissionLocation") String submissionLocation);

	// ---------------------------------------------------------------------------------------------------
	//
	// Job management methods
	//
	// ---------------------------------------------------------------------------------------------------

	/**
	 * Checks whether the specified file is a folder or not.
	 * 
	 * @param file
	 *            the file
	 * @return true - if folder; false - if not
	 * @throws RemoteFileSystemException
	 *             if the files can't be accessed
	 */
	@RolesAllowed("User")
	@POST
	@Path("user/files/isFolder")
	boolean isFolder(String file) throws RemoteFileSystemException;

	/**
	 * Deletes the whole jobdirectory (if specified) and if successful, the job
	 * from the database.
	 * 
	 * Also works with batchjobs.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param clean
	 *            whether to clean/delete the jobdirectory if possible
	 * @throws RemoteFileSystemException
	 *             if the files can't be deleted
	 * @throws NoSuchJobException
	 *             if no such job exists
	 * @throws BatchJobException
	 *             if the job is part of a batchjob
	 */
	@POST
	@Path("actions/killJob/{jobname}")
	@RolesAllowed("User")
	void kill(@PathParam("jobname") String jobname,
			@QueryParam("clean") boolean clean)
	throws RemoteFileSystemException, NoSuchJobException,
	BatchJobException;

	/**
	 * Deletes the whole jobdirectory (if specified) and if successful, the job
	 * from the database.
	 * 
	 * This one doesn't throw an exception if something goes wrong.
	 * 
	 * @param jobnames
	 *            a list of jobs to kill
	 * @param clean
	 *            whether to clean/delete the jobdirectory if possible
	 */
	@POST
	@Path("actions/killJobs")
	@RolesAllowed("User")
	void killJobs(@QueryParam("jobnames") DtoStringList jobnames,
			@QueryParam("clean") boolean clean);

	/**
	 * Returns the date when the file was last modified.
	 * 
	 * @param remoteFile
	 *            the file to check
	 * @return the last modified date
	 * @throws RemoteFileSystemException
	 *             if the file could not be accessed
	 */
	@RolesAllowed("User")
	@POST
	@Path("user/files/lastModified")
	long lastModified(@QueryParam("url") String url)
	throws RemoteFileSystemException;

	// void createAndSubmitJob(@PathParam("jobname") String jobname) throws
	// JobSubmissionException, JobPropertiesException;

	/**
	 * Lists all applications that are supported by this deployment of a service
	 * interface. Basically it's a list of all the application where the service
	 * interface has got a template jsdl.
	 * 
	 * @return a list of all applications
	 */
	String[] listHostedApplicationTemplates();

	/**
	 * Starts a session. For some service interfaces this could be just a dummy
	 * method. Ideally a char[] would be used for the password, but jax-ws
	 * doesn't support this.
	 * 
	 * @param username
	 *            the username (probably for myproxy credential)
	 * @param password
	 *            the password (probably for myproxy credential)
	 * @throws NoValidCredentialException
	 *             if the login was not successful
	 */
	@POST
	@Path("login")
	@Consumes("text/plain")
	void login(@QueryParam("username") String username,
			@QueryParam("password") String password);

	/**
	 * Logout of the service. Performs housekeeping tasks and usually deletes
	 * the Credential.
	 * 
	 * @return a logout message
	 */
	@POST
	@Path("logout")
	@Produces("text/plain")
	String logout();

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
	@POST
	@Path("user/listDirectory")
	@RolesAllowed("User")
	DtoFolder ls(@QueryParam("url") String url,
			@QueryParam("recursionLevel") int recursionLevel)
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
	@RolesAllowed("User")
	@POST
	@Path("actions/mkdir")
	boolean mkdir(@QueryParam("url") String url)
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
	@POST
	@Path("actions/mount")
	MountPoint mount(
			@QueryParam("url") String url,
			@QueryParam("alias") String alias,
			@QueryParam("fqan") String fqan,
			@QueryParam("useHomeDir") boolean useHomeDirectoryOnThisFileSystemIfPossible)
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
	 * @param useHomeDirectoryOnThisFileSystemIfPossible
	 *            use the users' home directory on this file system if possible
	 * @return the new mountpoint
	 * @throws RemoteFileSystemException
	 *             if the remote filesystem could not be mounted/connected to
	 */
	@POST
	@Path("actions/mountWithoutFqan")
	MountPoint mountWithoutFqan(
			@QueryParam("url") String url,
			@QueryParam("alias") String alias,
			@QueryParam("useHomeDir") boolean useHomeDirectoryOnThisFileSystemIfPossible)
	throws RemoteFileSystemException;

	/**
	 * Returns a xml document that contains all the jobs of the user with
	 * information about the jobs.
	 * 
	 * 
	 * @param refreshJobStatus
	 *            whether to refresh the status of all the jobs. This can take
	 *            quite some time.
	 * 
	 * @return xml formated information about all the users jobs
	 */
	@GET
	@Path("user/alljobs/{application}/{refresh}")
	@RolesAllowed("User")
	DtoJobs ps(@PathParam("application") String application,
			@PathParam("refresh") boolean refreshJobStatus);

	/**
	 * Tries to figure out the best submission locations for all the jobs that
	 * this batchpartjob consists of.
	 * 
	 * Call this after you added all jobs to the batchjob and before you
	 * upload/crosstage any files. It will overwrite possibly specified
	 * submission locations on jobs.
	 * 
	 * @param batchjobname
	 *            the name of the batchjob
	 * @throws NoSuchJobException
	 *             if there is no batchjob with such an id
	 * @throws JobPropertiesException
	 *             if one of the jobs can't be re-created
	 */
	@RolesAllowed("User")
	void redistributeBatchJob(String batchjobname)
	throws NoSuchJobException, JobPropertiesException;

	/**
	 * Refreshes the status of all jobs that belong to this batchjob.
	 * 
	 * This returns immediately. You need to watch the action status if you want
	 * to know when all sub-jobs are refreshed.
	 * 
	 * @param batchJobname
	 *            the name of the batchjob
	 * @return the action status handle for this. Use
	 *         {@link #getActionStatus(String)} to find out when this is
	 *         finished.
	 * @throws NoSuchJobException
	 *             if there is no batchjob with the specified id
	 */
	String refreshBatchJobStatus(String batchJobname) throws NoSuchJobException;

	/**
	 * Removes the specified job from the batchJob.
	 * 
	 * @param batchJobname
	 *            the batchJobname
	 * @param jobname
	 *            the jobname
	 */
	@RolesAllowed("User")
	void removeJobFromBatchJob(String batchJobname, String jobname)
	throws NoSuchJobException;

	/**
	 * Restarts a batch job.
	 * 
	 * Depending on the restart policy and the supplied properties, the backend
	 * will calculate which jobs to restart and how.
	 * 
	 * @param jobname
	 *            the batchjobname
	 * @param restartPolicy
	 *            the restart policy //TODO not implemented yet, only default
	 *            one supported
	 * @param properties
	 *            the restart policy properties //TODO not implemented yet, only
	 *            default one supported
	 * @return the job distribution figures of the re-submitted jobs
	 * @throws NoSuchJobException
	 *             if no such batchjob exists
	 * @throws JobPropertiesException
	 *             if one of the jobs can't be recreated or if the job
	 *             submission of this batch job is still ongoing in the
	 *             background
	 */
	@RolesAllowed("User")
	DtoProperties restartBatchJob(final String jobname, String restartPolicy,
			DtoProperties properties) throws NoSuchJobException,
			JobPropertiesException;

	/**
	 * Resubmit a job. Kills the old one if it's still running.
	 * 
	 * This uses the same job properties as the old job. If you want some of the
	 * properties changed, you need to provide an updated jsdl file. Be aware
	 * that not all properties can be changed (for example you can't change the
	 * filesystem the job runs on or the fqan). Have a look at the implemenation
	 * of this method to find out what can't be changed and what not. Anyway,
	 * use this with caution and prefer to just submit a new job if possible.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param changedJsdl
	 *            the updated jsdl or null (if you want to re-run the same job)
	 * @throws JobSubmissionException
	 *             if the job could not be resubmitted
	 * @throws NoSuchJobException
	 *             if no job with the specified jobname exists
	 */
	@RolesAllowed("User")
	void restartJob(final String jobname, String changedJsdl)
	throws JobSubmissionException, NoSuchJobException;

	@RolesAllowed("User")
	@POST
	@Path("/user/setbookmark/{alias}")
	void setBookmark(@PathParam("alias") String alias,
			@QueryParam("value") String value);

	/**
	 * Sets a user property.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	@RolesAllowed("User")
	@POST
	@Path("/user/setproperty/{key}")
	void setUserProperty(@PathParam("key") String key,
			@QueryParam("value") String value);

	/**
	 * Submits the job that was prepared before using
	 * {@link #createJobUsingMap(Map, String, String)} or
	 * {@link #createJob(String, String, String)} to the specified submission
	 * location.
	 * 
	 * @param jobname
	 *            the jobname
	 * @throws JobSubmissionException
	 *             if the job could not submitted
	 * @throws NoSuchJobException
	 *             if no such job exists
	 */
	@POST
	@Path("actions/submitJob/{jobname}")
	@RolesAllowed("User")
	void submitJob(@PathParam("jobname") String jobname)
	throws JobSubmissionException, NoSuchJobException;

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
	 * Unmounts a filesystem.
	 * 
	 * @param mountpoint
	 *            the mountpoint
	 * @return whether it worked or not
	 */
	@POST
	@Path("actions/umount")
	void umount(@QueryParam("alias") String alias);

	/**
	 * Upload a {@link DataSource} to the users' virtual filesystem.
	 *
	 * @param file
	 *            the (local) file you want to upload
	 * @param filename
	 *            the location you want the file upload to
	 * @return the new path of the uploaded file or null if the upload failed
	 * @throws RemoteFileSystemException
	 *             if the remote (target) filesystem could not be connected /
	 *             mounted / is not writeable
	 */
	@POST
	@RolesAllowed("User")
	@Path("actions/upload")
	@Produces("text/plain")
	String upload(@XmlMimeType("application/octet-stream") DataHandler file,
			@QueryParam("filename") String filename)
	throws RemoteFileSystemException;

	/**
	 * Uploads input file for job or distributes an input file to all the
	 * filesystems that are used in a batchjob if job is batchjob.
	 * 
	 * In case of single job: this gets put in the jobdirectory of the job and
	 * waits until the copying finished
	 * 
	 * In case of batchjob: You need to reverence to the input file using
	 * relative paths in the commandline you specify in the jobs that need this
	 * inputfile. Use this after you created all jobs for this batchjob. In the
	 * case of batchjob this method returns immediately after the file upload
	 * and you have to monitor the progress using the
	 * {@link #getActionStatus(String)} method if you want to know when the file
	 * is copied to all targets.
	 * 
	 * @param jobname
	 *            the jobname
	 * @param inputFile
	 *            the inputfile
	 */
	@RolesAllowed("User")
	void uploadInputFile(String jobname, DataHandler inputFile,
			String relativePath) throws RemoteFileSystemException,
			NoSuchJobException;

}
