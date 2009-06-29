package org.vpac.grisu.client.model;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.JobSubmissionException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.files.FileHelper;
import org.vpac.grisu.control.files.FileTransferException;
import org.vpac.grisu.js.model.JobPropertiesException;
import org.vpac.grisu.js.model.JobSubmissionObjectImpl;
import org.w3c.dom.Document;

public class JobObject extends JobSubmissionObjectImpl {

	static final Logger myLogger = Logger.getLogger(JobObject.class.getName());

	private final ServiceInterface serviceInterface;
	private final FileHelper fileHelper;

	private int status = JobConstants.UNDEFINED;
	
	private String jobDirectory;


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

	public void createJob(String fqan) throws JobPropertiesException {

		setJobname(serviceInterface.createJob(getJobDescriptionDocument(),
				fqan, "force-name"));

		try {
			jobDirectory = serviceInterface.getJobProperty(getJobname(),
					ServiceInterface.JOBDIRECTORY_KEY);
			getStatus(true);
		} catch (NoSuchJobException e) {
			// TODO that should never happen
			e.printStackTrace();
		}

	}

	public void submitJob() throws JobSubmissionException {

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

		serviceInterface.submitJob(getJobname());
		getStatus(true);
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
	synchronized public void addValueListener(JobStatusChangeListener l) {
		if (jobStatusChangeListeners == null)
			jobStatusChangeListeners = new Vector<JobStatusChangeListener>();
		jobStatusChangeListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeValueListener(JobStatusChangeListener l) {
		if (jobStatusChangeListeners == null) {
			jobStatusChangeListeners = new Vector<JobStatusChangeListener>();
		}
		jobStatusChangeListeners.removeElement(l);
	}
	
}
