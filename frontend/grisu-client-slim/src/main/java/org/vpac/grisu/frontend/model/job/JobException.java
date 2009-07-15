package org.vpac.grisu.frontend.model.job;


/**
 * An exception that is thrown if something goes wrong with a job.
 * 
 * @author Markus Binsteiner
 *
 */
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
