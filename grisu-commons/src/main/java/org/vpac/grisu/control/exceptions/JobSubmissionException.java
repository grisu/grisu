package org.vpac.grisu.control.exceptions;

public class JobSubmissionException extends Exception {

	public JobSubmissionException(String message) {
		super(message);
	}

	public JobSubmissionException(String message, Exception e) {
		super(message, e);
	}

}
