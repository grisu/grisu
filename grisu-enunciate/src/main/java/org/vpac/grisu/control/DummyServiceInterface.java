package org.vpac.grisu.control;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.w3c.dom.Document;

import au.org.arcs.mds.GridResource;

public class DummyServiceInterface implements ServiceInterface {
	
	private EnunciateServiceInterface esi;
	
	public DummyServiceInterface(EnunciateServiceInterface esi) {
		this.esi = esi;
	}

	public void addJobProperties(String jobname, Map<String, String> properties)
			throws NoSuchJobException {
		// TODO Auto-generated method stub

	}

	public void addJobProperty(String jobname, String key, String value)
			throws NoSuchJobException {
		// TODO Auto-generated method stub

	}

	public String calculateAbsoluteJobDirectory(String jobname,
			String submissionLocation, String fqan) {
		// TODO Auto-generated method stub
		return null;
	}

	public String calculateRelativeJobDirectory(String jobname) {
		// TODO Auto-generated method stub
		return null;
	}

	public String cp(String source, String target, boolean overwrite,
			boolean waitForFileTransferToFinish)
			throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public String createJob(String jobname, int createJobNameMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createJob(Map<String, String> jobProperties, String fqan,
			String jobnameCreationMethod) throws JobPropertiesException {
		// TODO Auto-generated method stub
		return null;
	}

	public String createJob(Document jsdl, String fqan,
			String jobnameCreationMethod) throws JobPropertiesException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteFile(String file) throws RemoteFileSystemException {
		// TODO Auto-generated method stub

	}

	public void deleteFiles(String[] files) throws RemoteFileSystemException {
		// TODO Auto-generated method stub

	}

	public MountPoint[] df() {
		// TODO Auto-generated method stub
		return null;
	}

	public DataSource download(String filename)
			throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean fileExists(String file) throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return false;
	}

	public List<GridResource> findMatchingSubmissionLocations(Document jsdl,
			String fqan) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<GridResource> findMatchingSubmissionLocations(
			Map<String, String> jobProperties, String fqan) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAllAvailableApplications(String[] sites) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getAllHosts() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getAllJobProperties(String jobname)
			throws NoSuchJobException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAllJobnames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAllSites() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAllSubmissionLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAllSubmissionLocations(String fqan) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getApplicationDetails(String application,
			String version, String site) {
		return esi.getApplicationDetailsForVersionAndSite(application, version, site);
	}

	public Map<String, String> getApplicationDetails(String application,
			String siteOrSubmissionLocation) {
		return esi.getApplicationDetailsForSite(application, siteOrSubmissionLocation);
	}

	public String[] getChildrenFiles(String folder, boolean onlyFiles)
			throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getCredentialEndTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getCurrentStatusMessage(String handle) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDN() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String[]> getDataLocationsForVO(String fqan) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getFileSize(String file) throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getFqans() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getInterfaceVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Document getJobDetails(String jobname) throws NoSuchJobException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJobDetails_string(String jobname)
			throws NoSuchJobException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJobDirectory(String jobname) throws NoSuchJobException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJobFqan(String jobname) throws NoSuchJobException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJobProperty(String jobname, String key)
			throws NoSuchJobException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getJobStatus(String jobname) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Document getJsldDocument(String jobname) throws NoSuchJobException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getMessagesSince(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	public MountPoint getMountPointForUri(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSite(String host) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getStagingFileSystemForSubmissionLocation(String subLoc) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getSubmissionLocationsForApplication(String application) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getSubmissionLocationsForApplication(String application,
			String version) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getSubmissionLocationsForApplication(String application,
			String version, String fqan) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getSubmissionLocationsPerVersionOfApplication(
			String application) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getTemplate(String application)
			throws NoSuchTemplateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getTemplate(String application, String version)
			throws NoSuchTemplateException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getVersionsOfApplicationOnSite(String application,
			String site) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getVersionsOfApplicationOnSubmissionLocation(
			String application, String submissionLocation) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isFolder(String file) throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return false;
	}

	public void kill(String jobname, boolean clean)
			throws RemoteFileSystemException, NoSuchJobException {
		// TODO Auto-generated method stub

	}

	public long lastModified(String remoteFile)
			throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] listHostedApplicationTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	public void login(String username, char[] password) {
		// TODO Auto-generated method stub

	}

	public String logout() {
		// TODO Auto-generated method stub
		return null;
	}

	public Document ls(String directory, int recursionLevel, boolean absoluteUrl)
			throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public String ls_string(String directory, int recursionLevel,
			boolean absoluteUrl) throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean mkdir(String folder) throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return false;
	}

	public MountPoint mount(String url, String mountpoint,
			boolean useHomeDirectoryOnThisFileSystemIfPossible)
			throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public MountPoint mount(String url, String mountpoint, String fqan,
			boolean useHomeDirectoryOnThisFileSystemIfPossible)
			throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document ps() {
		// TODO Auto-generated method stub
		return null;
	}

	public String ps_string() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setJobDescription(String jobname, Document jsdl)
			throws NoSuchJobException {
		// TODO Auto-generated method stub

	}

	public void setJobDescription_string(String jobname, String jsdl)
			throws NoSuchJobException {
		// TODO Auto-generated method stub

	}

	public void submitJob(String jobname, String fqan)
			throws RemoteFileSystemException, NoSuchJobException {
		// TODO Auto-generated method stub

	}

	public void submitJob(String jobname) throws JobSubmissionException {
		// TODO Auto-generated method stub

	}

	public void submitSupportRequest(String subject, String description) {
		// TODO Auto-generated method stub

	}

	public void umount(String mountpoint) {
		// TODO Auto-generated method stub

	}

	public String upload(DataSource file, String filename,
			boolean returnAbsoluteUrl) throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

}
