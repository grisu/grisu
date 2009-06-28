package org.vpac.grisu.control;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.vpac.grisu.control.exceptions.JobDescriptionNotValidException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.NoValidCredentialException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.control.exceptions.ServerJobSubmissionException;
import org.vpac.grisu.control.exceptions.VomsException;
import org.vpac.grisu.fs.model.MountPoint;
import org.vpac.grisu.model.GridResource;
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
	public String getCurrentStatusMessage();
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
	public MountPoint mount(String url, String mountpoint, boolean useHomeDirectoryOnThisFileSystemIfPossible) throws RemoteFileSystemException, VomsException;
	@WebMethod(operationName="mount2")
	public MountPoint mount(String url, String mountpoint, String fqan, boolean useHomeDirectoryOnThisFileSystemIfPossible) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public void umount(String mountpoint);
	@WebMethod
	public MountPoint[] df();
	@WebMethod
	public MountPoint getMountPointForUri(String uri);
	@WebMethod
	public String upload(DataSource file, String filename, boolean return_absolute_url) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public DataSource download(String filename) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public Document ls(String directory, int recursion_level, boolean absolute_url) throws RemoteFileSystemException;
	@WebMethod
	public String ls_string(String directory, int recursion_level, boolean absolute_url) throws RemoteFileSystemException;
	@WebMethod
	public String cp(String source, String target, boolean overwrite, boolean return_absolute_url) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public boolean fileExists(String file) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public boolean isFolder(String file) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public String[] getChildrenFiles(String folder, boolean onlyFiles) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public long getFileSize(String file) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public long lastModified(String remoteFile) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public boolean mkdir(String folder) throws  RemoteFileSystemException, VomsException; 
	@WebMethod
	public void deleteFile(String file) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public void deleteFiles(String[] files) throws RemoteFileSystemException, VomsException;
	@WebMethod
	public Document ps();
	@WebMethod 
	public String ps_string();
	@WebMethod
	public String[] getAllJobnames();
	@WebMethod
	public String createJob(String jobname, int createJobNameMethod) throws JobCreationException;
	@WebMethod
	public void setJobDescription(String jobname, Document jsdl) throws JobDescriptionNotValidException, NoSuchJobException;
	@WebMethod
	public void setJobDescription_string(String jobname, String jsdl) throws JobDescriptionNotValidException, NoSuchJobException;
	@WebMethod
	public void submitJob(String jobname, String fqan) throws ServerJobSubmissionException, NoValidCredentialException, RemoteFileSystemException, VomsException, NoSuchJobException;
	@WebMethod
	public String getJobDirectory(String jobname) throws NoSuchJobException;
	@WebMethod
	public int getJobStatus(String jobname);
	@WebMethod
	public Document getJobDetails(String jobname) throws NoSuchJobException;
	@WebMethod
	public String getJobDetails_string(String jobname) throws NoSuchJobException;
	@WebMethod
	public void kill(String jobname, boolean clean) throws RemoteFileSystemException, VomsException, NoSuchJobException;
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
