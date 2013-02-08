package grisu.frontend.model.job;

import java.util.Map;

public class JobsException extends Exception {

	private final Map<GrisuJob, Exception> failures;

	public JobsException(Map<GrisuJob, Exception> failures) {
		super();
		this.failures = failures;
	}

	public JobsException(String message, Map<GrisuJob, Exception> failures) {
		super(message);
		this.failures = failures;
	}

	public Map<GrisuJob, Exception> getFailures() {
		return failures;
	}

}
