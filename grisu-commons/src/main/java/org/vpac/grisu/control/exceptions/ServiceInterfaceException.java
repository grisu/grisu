package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.vpac.grisu.control.jaxws.exceptions.ServiceInterfaceException")
public class ServiceInterfaceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceInterfaceException(final String string, final Exception e) {
		super(string, e);
	}

}
