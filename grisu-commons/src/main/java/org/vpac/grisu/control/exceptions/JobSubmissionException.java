package org.vpac.grisu.control.exceptions;

public class JobSubmissionException extends Exception {

	public JobSubmissionException(final String message) {
		super(message);
	}

	public JobSubmissionException(final String message, final Exception e) {
		super(message, e);
	}

}
