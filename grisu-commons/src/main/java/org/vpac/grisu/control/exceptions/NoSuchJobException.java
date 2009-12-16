package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.vpac.grisu.control.jaxws.exceptions.NoSuchJobException")
public class NoSuchJobException extends Exception {

	public NoSuchJobException(final String message) {
		super(message);
	}

}
