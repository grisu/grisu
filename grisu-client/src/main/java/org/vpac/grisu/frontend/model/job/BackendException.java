package org.vpac.grisu.frontend.model.job;

public class BackendException extends Exception {

	public BackendException(String message, Exception e) {
		super(message, e);
	}

}
