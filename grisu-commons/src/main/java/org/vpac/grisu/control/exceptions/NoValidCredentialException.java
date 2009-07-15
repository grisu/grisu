package org.vpac.grisu.control.exceptions;

import java.io.Serializable;

public class NoValidCredentialException extends RuntimeException implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private String message;

	public NoValidCredentialException(final String message) {
		super(message);
		this.message = message;
	}

	public final String getMessage() {
		return message;
	}

}
