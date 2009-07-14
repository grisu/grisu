package org.vpac.grisu.control.exceptions;

import org.vpac.grisu.model.job.JobSubmissionProperty;


public class JobPropertiesException extends Exception {
	
	
	private JobSubmissionProperty reason = null;
	
	public JobPropertiesException(JobSubmissionProperty reason, String message) {
		super(message);
		this.reason = reason;
	}

}
