package org.vpac.grisu.client.control.clientexceptions;

public class JobCreationException extends Exception {

	public JobCreationException(final String message) {
		super(message);
	}

	public JobCreationException(final String message, final Exception e) {
		super(message, e);
	}

}
