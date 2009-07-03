package org.vpac.grisu.client.model;

import java.io.File;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.FileHelpers;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.JobSubmissionException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.files.FileHelper;
import org.vpac.grisu.control.files.FileTransferException;
import org.vpac.grisu.js.model.JobCreatedProperty;
import org.vpac.grisu.js.model.JobPropertiesException;
import org.vpac.grisu.js.model.JobSubmissionObjectImpl;
import org.vpac.grisu.js.model.JobSubmissionProperty;
import org.w3c.dom.Document;

public class JobObject extends JobSubmissionObjectImpl {

	static final Logger myLogger = Logger.getLogger(JobObject.class.getName());

	private final ServiceInterface serviceInterface;
	private final FileHelper fileHelper;

	private int status = JobConstants.UNDEFINED;
	
	private Map<String, String> allJobProperties;
	
	private String jobDirectory;


	public JobObject(ServiceInterface si, String jobname) throws NoSuchJobException {
		
		super(si.getJsldDocument(jobname));
		this.serviceInterface = si;
		this.jobname = jobname;
		this.fileHelper = new FileHelper(serviceInterface);
		
		getStatus(true);
		
	}
	
	public JobObject(ServiceInterface si) {
		super();
		this.serviceInterface = si;
		this.fileHelper = new FileHelper(serviceInterface);
	}

	public JobObject(ServiceInterface si, Map<String, String> jobProperties) {
		super(jobProperties);
		this.serviceInterface = si;
		this.fileHelper = new FileHelper(serviceInterface);
	}

	public JobObject(ServiceInterface si, Document jsdl) {
		super(jsdl);
		this.serviceInterface = si;
		this.fileHelper = new FileHelper(serviceInterface);
	}

	public String createJob(String fqan) throws JobPropertiesException {

		return createJob(fqan, ServiceInterface.FORCE_NAME_METHOD);
	}
	
	public String createJob(String fqan, String jobnameCreationMethod) throws JobPropertiesException {
		
		setJobname(serviceInterface.createJob(getJobDescriptionDocument(),
				fqan, jobnameCreationMethod));

		try {
			jobDirectory = serviceInterface.getJobProperty(getJobname(),
					ServiceInterface.JOBDIRECTORY_KEY);
			getStatus(true);
		} catch (NoSuchJobException e) {
			fireJobStatusChange(this.status, JobConstants.NO_SUCH_JOB);
		}

		return this.jobname;
	}

	public void submitJob() throws JobSubmissionException {
		
		if ( status == JobConstants.UNDEFINED ) {
			throw new IllegalStateException("Job state "+JobConstants.translateStatus(JobConstants.UNDEFINED)+". Can't submit job.");
		}

		if ( getInputFileUrls() != null && getInputFileUrls().length > 0 ) {
			setStatus(JobConstants.INPUT_FILES_UPLOADING);
		}
		
		// stage in local files
		for (String inputFile : getInputFileUrls()) {
			if ( FileHelper.isLocal(inputFile) ) {
			try {
				fileHelper.uploadFile(inputFile, jobDirectory);
			} catch (FileTransferException e) {
				throw new JobSubmissionException("Could not stage-in file: "
						+ inputFile, e);
			}
			}
		}

		if ( getInputFileUrls() != null && getInputFileUrls().length > 0 ) {
			setStatus(JobConstants.INPUT_FILES_UPLOADED);
		}
		
		serviceInterface.submitJob(getJobname());
		getStatus(true);
	}
	
	private void setStatus(int newStatus) {
		
		int oldstatus = this.status;
		this.status = newStatus;
		
		if ( oldstatus != this.status ) {
			fireJobStatusChange(oldstatus, this.status);
		}
		
	}

	public int getStatus(boolean forceRefresh) {
		if (forceRefresh) {
			int oldStatus = this.status;
			this.status = serviceInterface.getJobStatus(getJobname());
			if ( this.status != oldStatus ) {
				fireJobStatusChange(oldStatus, this.status);
			}
		}
		return this.status;
	}

	public String getStatusString(boolean forceRefresh) {
		return JobConstants.translateStatus(getStatus(forceRefresh));
	}
	
	public void kill(boolean clean) throws JobException {
		
		if ( getStatus(false) == JobConstants.UNDEFINED ) {
			throw new IllegalStateException("Job status "+JobConstants.translateStatus(JobConstants.UNDEFINED)+". Can't kill job yet.");
		}
		
		try {
			this.serviceInterface.kill(this.jobname, clean);
		} catch (Exception e) {
			throw new JobException(this, "Could not kill/clean job.", e);
		} 
		
	}
	
	public Map<String, String> getAllJobProperties() {
		
		if ( getStatus(false) == JobConstants.UNDEFINED ) {
			throw new IllegalStateException("Job status "+JobConstants.translateStatus(JobConstants.UNDEFINED)+". Can't access job properties yet.");
		}
		
		if ( allJobProperties == null ) {
			try {
				allJobProperties = serviceInterface.getAllJobProperties(getJobname());
			} catch (Exception e) {
				throw new JobException(this, "Could not get jobproperties.", e);
			}
		}
		return allJobProperties;
		
	}
	
	public String getJobDirectoryUrl() {
		
		if ( this.getStatus(false) == JobConstants.UNDEFINED ) {
			throw new IllegalStateException("Job status "+JobConstants.translateStatus(JobConstants.UNDEFINED)+". Can't get jobdirectory yet.");
		}
		
		if ( jobDirectory == null ) {
			String url = getAllJobProperties().get(JobCreatedProperty.JOBDIRECTORY.toString());
			jobDirectory = url;
		}
		
		return jobDirectory;
	}
	
	public File getStdOutFile() {
		
		if ( getStatus(false) <= JobConstants.ACTIVE ) {
			if ( getStatus(true) < JobConstants.ACTIVE ) {
				throw new IllegalStateException("Job not started yet. No stdout file exists.");
			}
		}
//		
		String url;
		try {
			url = getJobDirectoryUrl() + "/" + serviceInterface.getJobProperty(getJobname(), JobSubmissionProperty.STDOUT.toString());
		} catch (NoSuchJobException e) {
			throw new JobException(this, "Could not get stdout url.", e);
		}
		
		File stdoutFile = null;
		try {
			stdoutFile = fileHelper.downloadFile(url);
		} catch (Exception e) {
			throw new JobException(this, "Could not download stdout file.", e);
		}
		
		return stdoutFile;
	}
	
	public String getStdOutContent() {
		
		String result;
		try {
			result = FileHelpers.readFromFileWithException(getStdOutFile());
		} catch (Exception e) {
			throw new JobException(this, "Could not read stdout file.", e);
		}
		
		return result;
		
	}
	
	public String getStdErrContent() {
		
		String result;
		try {
			result = FileHelpers.readFromFileWithException(getStdErrFile());
		} catch (Exception e) {
			throw new JobException(this, "Could not read stdout file.", e);
		}
		
		return result;
		
	}
	
	public File getStdErrFile() {
		
		if ( getStatus(false) <= JobConstants.ACTIVE ) {
			if ( getStatus(true) < JobConstants.ACTIVE ) {
				throw new IllegalStateException("Job not started yet. No stderr file exists.");
			}
		}
//		
		String url;
		try {
			url = getJobDirectoryUrl() + "/" + serviceInterface.getJobProperty(getJobname(), JobSubmissionProperty.STDERR.toString());
		} catch (NoSuchJobException e) {
			throw new JobException(this, "Could not get stdout url.", e);
		}
		
		File stderrFile = null;
		try {
			fileHelper.downloadFile(url);
		} catch (Exception e) {
			throw new JobException(this, "Could not download stderr file.", e);
		}
		
		return stderrFile;
	}
	
	
	
	
	// event stuff
	// ========================================================
	private Vector<JobStatusChangeListener> jobStatusChangeListeners;

	private void fireJobStatusChange(int oldStatus, int newStatus) {
		
		myLogger.debug("Fire job status change event.");
		// if we have no mountPointsListeners, do nothing...
		if (jobStatusChangeListeners != null && !jobStatusChangeListeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<JobStatusChangeListener> valueChangedTargets;
			synchronized (this) {
				valueChangedTargets = (Vector<JobStatusChangeListener>)jobStatusChangeListeners.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration<JobStatusChangeListener> e = valueChangedTargets.elements();
			while (e.hasMoreElements()) {
				JobStatusChangeListener valueChanged_l = e.nextElement();
				valueChanged_l.jobStatusChanged(this, oldStatus, newStatus);
			}
		}
	}

	// register a listener
	synchronized public void addJobStatusChangeListener(JobStatusChangeListener l) {
		if (jobStatusChangeListeners == null)
			jobStatusChangeListeners = new Vector<JobStatusChangeListener>();
		jobStatusChangeListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeJobStatusChangeListener(JobStatusChangeListener l) {
		if (jobStatusChangeListeners == null) {
			jobStatusChangeListeners = new Vector<JobStatusChangeListener>();
		}
		jobStatusChangeListeners.removeElement(l);
	}

}
