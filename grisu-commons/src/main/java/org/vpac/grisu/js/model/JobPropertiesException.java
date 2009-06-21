package org.vpac.grisu.js.model;


public class JobPropertiesException extends Exception {
	
	
	private JobProperty reason = null;
	
	public JobPropertiesException(JobProperty reason, String message) {
		super(message);
		this.reason = reason;
	}

}
