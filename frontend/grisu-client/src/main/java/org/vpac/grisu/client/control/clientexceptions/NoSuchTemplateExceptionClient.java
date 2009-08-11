package org.vpac.grisu.client.control.clientexceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean="org.vpac.grisu.control.jaxws.exceptions.NoSuchTemplateException")
public class NoSuchTemplateExceptionClient extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchTemplateExceptionClient(final String message) {
		super(message);
	}

}
