package org.vpac.grisu.client.model;

import java.util.Map;

import org.vpac.grisu.client.control.files.FileHelper;
import org.vpac.grisu.client.control.files.FileTransferException;
import org.vpac.grisu.control.JobSubmissionException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.js.model.JobPropertiesException;
import org.vpac.grisu.js.model.JobSubmissionObjectImpl;
import org.w3c.dom.Document;

public class JobObject extends JobSubmissionObjectImpl {
	
	private final ServiceInterface serviceInterface;
	private final FileHelper fileHelper;
	
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
	
		setJobname(serviceInterface.createJob(getJobDescriptionDocument(), fqan, "force-name"));
		
		try {
			jobDirectory = serviceInterface.getJobProperty(getJobname(), ServiceInterface.JOBDIRECTORY_KEY);
		} catch (NoSuchJobException e) {
			// TODO that should never happen
			e.printStackTrace();
		}
		
	}
	
	public void submitJob() throws JobSubmissionException {
		
		// stage in local files
		for ( String inputFile : getInputFileUrls() ) {
			try {
				fileHelper.uploadFile(inputFile, jobDirectory);
			} catch (FileTransferException e) {
				throw new JobSubmissionException("Could not stage-in file: "+inputFile, e);
			}
		}
		
		serviceInterface.submitJob(getJobname());
	}
	
}
