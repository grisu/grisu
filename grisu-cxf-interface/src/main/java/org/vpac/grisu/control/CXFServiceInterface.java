package org.vpac.grisu.control;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.NoValidCredentialException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.info.GridResource;
import org.w3c.dom.Document;

/**
   CXF version of ServiceInterface
 */
@WebService
public interface CXFServiceInterface extends ServiceInterface{
	
	@WebMethod
	public double getInterfaceVersion();
	@WebMethod
	public void login(String username, char[] password) throws NoValidCredentialException;
	@WebMethod
	public String logout();
	@WebMethod
	public String[] listHostedApplicationTemplates();
	@WebMethod(operationName="getTemplate1")
	public Document getTemplate(String application) throws NoSuchTemplateException;
	@WebMethod(operationName="getTemplate2")
	public Document getTemplate(String application, String version) throws NoSuchTemplateException;
	@WebMethod
	public void submitSupportRequest(String subject, String description);
	@WebMethod
	public String getUserProperty(String key);
	@WebMethod
	public Document getMessagesSince(Date date);
	@WebMethod
	public long getCredentialEndTime();
	@WebMethod
	public String getCurrentStatusMessage(String handle);
	@WebMethod
	public String getSite(String host);
	@WebMethod
	public Map<String, String> getAllHosts();
	@WebMethod
	public String calculateAbsoluteJobDirectory(String jobname, String submissionLocation, String fqan);
	@WebMethod
	public String calculateRelativeJobDirectory(String jobname);
	@WebMethod(operationName="getAllSubmissionLocations1")
	public String[] getAllSubmissionLocations();
	@WebMethod(operationName="getAllSubmissionLocations2")
	public String[] getAllSubmissionLocations(String fqan);
	@WebMethod(operationName="getAllSubmissionLocationsForApplication1")
	public String[] getSubmissionLocationsForApplication(String application);
	@WebMethod(operationName="getAllSubmissionLocationsForApplication2")
	public String[] getSubmissionLocationsForApplication(String application, String version);
	@WebMethod(operationName="getAllSubmissionLocationsForApplication3")
	public String[] getSubmissionLocationsForApplication(String application, String version, String fqan);
	@WebMethod
	public Map<String, String> getSubmissionLocationsPerVersionOfApplication(String application);
	@WebMethod
	public Map<String, String[]> getDataLocationsForVO(String fqan);
	@WebMethod
	public String[] getVersionsOfApplicationOnSite(String application, String site);
	@WebMethod
	public String[] getVersionsOfApplicationOnSubmissionLocation(String application, String submissionLocation);
	@WebMethod
	public String[] getStagingFileSystemForSubmissionLocation(String subLoc);
	@WebMethod
	public String[] getFqans();
	@WebMethod
	public String getDN();
	@WebMethod
	public String[] getAllSites();
	@WebMethod
	public String[] getAllAvailableApplications(String[] sites);
	@WebMethod(operationName="getApplicationDetails1")
	public Map<String, String> getApplicationDetails(String application, String version, String site);
	@WebMethod(operationName="getApplicationDetails2")
	public Map<String, String> getApplicationDetails(String application, String site_or_submissionLocation);
	@WebMethod
	public List<GridResource> findMatchingSubmissionLocations(Document jsdl, String fqan);
	@WebMethod(operationName="mount1")
	public MountPoint mount(String url, String mountpoint, boolean useHomeDirectoryOnThisFileSystemIfPossible) throws RemoteFileSystemException;
	@WebMethod(operationName="mount2")
	public MountPoint mount(String url, String mountpoint, String fqan, boolean useHomeDirectoryOnThisFileSystemIfPossible) throws RemoteFileSystemException;
	@WebMethod
	public void umount(String mountpoint);
	@WebMethod
	public MountPoint[] df();
	@WebMethod
	public MountPoint getMountPointForUri(String uri);
	@WebMethod
	public String upload(DataSource file, String filename, boolean return_absolute_url) throws RemoteFileSystemException;
	@WebMethod
	public DataSource download(String filename) throws RemoteFileSystemException;
	@WebMethod
	public Document ls(String directory, int recursion_level, boolean absolute_url) throws RemoteFileSystemException;
	@WebMethod
	public String ls_string(String directory, int recursion_level, boolean absolute_url) throws RemoteFileSystemException;
	@WebMethod
	public String cp(String source, String target, boolean overwrite, boolean return_absolute_url) throws RemoteFileSystemException;
	@WebMethod
	public boolean fileExists(String file) throws RemoteFileSystemException;
	@WebMethod
	public boolean isFolder(String file) throws RemoteFileSystemException;
	@WebMethod
	public String[] getChildrenFiles(String folder, boolean onlyFiles) throws RemoteFileSystemException;
	@WebMethod
	public long getFileSize(String file) throws RemoteFileSystemException;
	@WebMethod
	public long lastModified(String remoteFile) throws RemoteFileSystemException;
	@WebMethod
	public boolean mkdir(String folder) throws  RemoteFileSystemException; 
	@WebMethod
	public void deleteFile(String file) throws RemoteFileSystemException;
	@WebMethod
	public void deleteFiles(String[] files) throws RemoteFileSystemException;
	@WebMethod
	public Document ps();
	@WebMethod 
	public String ps_string();
	@WebMethod
	public String[] getAllJobnames();
	@WebMethod(operationName="createJobOld")
	public String createJob(String jobname, int createJobNameMethod);
	@WebMethod(operationName="createJobUsingJsdl")
	public String createJob(Document jsdl, String fqan, String jobnameCreationMethod) throws JobPropertiesException;
	@WebMethod(operationName="createJobUsingPropertiesMap")
	public String createJob(Map<String, String> jobProperties, String fqan, String jobnameCreationMethod) throws JobPropertiesException;
	@WebMethod
	public void setJobDescription(String jobname, Document jsdl) throws NoSuchJobException;
	@WebMethod
	public void setJobDescription_string(String jobname, String jsdl) throws NoSuchJobException;
	@WebMethod(operationName="submitJobOld")
	public void submitJob(String jobname, String fqan) throws NoValidCredentialException, RemoteFileSystemException, NoSuchJobException;
	@WebMethod
	public void submitJob(String jobname) throws JobSubmissionException;
	@WebMethod
	public String getJobDirectory(String jobname) throws NoSuchJobException;
	@WebMethod
	public int getJobStatus(String jobname);
	@WebMethod
	public Document getJobDetails(String jobname) throws NoSuchJobException;
	@WebMethod
	public String getJobDetails_string(String jobname) throws NoSuchJobException;
	@WebMethod
	public void kill(String jobname, boolean clean) throws RemoteFileSystemException, NoSuchJobException;
	@WebMethod
	public void addJobProperty(String jobname, String key, String value) throws NoSuchJobException;
	@WebMethod
	public void addJobProperties(String jobname, Map<String, String> properties) throws NoSuchJobException;
	@WebMethod
	public String getJobProperty(String jobname, String key) throws NoSuchJobException;
	@WebMethod
	public Map<String, String> getAllJobProperties(String jobname) throws NoSuchJobException;
	@WebMethod
	public String getJobFqan(String jobname) throws NoSuchJobException;
}
