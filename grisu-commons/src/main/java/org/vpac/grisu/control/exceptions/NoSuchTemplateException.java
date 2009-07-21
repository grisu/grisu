package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean="org.vpac.grisu.control.jaxws.exceptions.NoSuchTemplateException")
public class NoSuchTemplateException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchTemplateException(final String message) {
		super(message);
	}

}
