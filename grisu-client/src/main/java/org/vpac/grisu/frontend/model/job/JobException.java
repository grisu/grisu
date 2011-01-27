package org.vpac.grisu.frontend.model.job;

/**
 * An exception that is thrown if something goes wrong with a job.
 * 
 * @author Markus Binsteiner
 * 
 */
public class JobException extends RuntimeException {

	private final JobObject jo;

	public JobException(final JobObject jo, final String message) {
		super(message);
		this.jo = jo;
	}

	public JobException(final JobObject jobObject, final String string,
			final Exception e) {
		super(string, e);
		this.jo = jobObject;
	}

	public final JobObject getJobObject() {
		return this.jo;
	}

}
