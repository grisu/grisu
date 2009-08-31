package org.vpac.grisu.frontend.control.login;

public class LoginException extends Exception {

	public LoginException(final String message, final Exception e) {
		super(message, e);
	}
	
	public LoginException(String message) {
		super(message);
	}

}
