package org.vpac.grisu.frontend.model.job;

import java.util.Map;

public class JobsException extends Exception {

	private Map<JobObject, Exception> failures;

	public JobsException(Map<JobObject, Exception> failures) {
		super();
		this.failures = failures;
	}

	public JobsException(String message, Map<JobObject, Exception> failures) {
		super(message);
		this.failures = failures;
	}

	public Map<JobObject, Exception> getFailures() {
		return failures;
	}

}
