package org.vpac.grisu.client.model;

public class JobException extends Exception {
	
	private JobObject jo;
	
	public JobException(JobObject jo, String message) {
		super(message);
		this.jo = jo;
	}
	
	public JobObject getJobObject() {
		return this.jo;
	}

}
