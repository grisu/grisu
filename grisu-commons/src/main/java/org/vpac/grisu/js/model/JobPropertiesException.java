package org.vpac.grisu.js.model;


public class JobPropertiesException extends Exception {
	
	
	private JobSubmissionProperty reason = null;
	
	public JobPropertiesException(JobSubmissionProperty reason, String message) {
		super(message);
		this.reason = reason;
	}

}
