package org.vpac.grisu.client.model;


public class JobException extends RuntimeException {
	
	private JobObject jo;
	
	public JobException(JobObject jo, String message) {
		super(message);
		this.jo = jo;
	}
	
	public JobException(JobObject jobObject, String string, Exception e) {
		super(string, e);
		this.jo = jobObject;
	}

	public JobObject getJobObject() {
		return this.jo;
	}

}
