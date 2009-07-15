package org.vpac.grisu.backend.model.job;

public class NoSuchJobSubmitterException extends RuntimeException {

	public NoSuchJobSubmitterException(final String message) {
		super(message);
	}

}
