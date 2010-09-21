package org.vpac.grisu.control.exceptions;

import java.io.Serializable;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.vpac.grisu.control.jaxws.exceptions.NoValidCredentialException")
public class NoValidCredentialException extends RuntimeException implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private final String message;

	public NoValidCredentialException(final String message) {
		super(message);
		this.message = message;
	}

	@Override
	public final String getMessage() {
		return message;
	}

}
