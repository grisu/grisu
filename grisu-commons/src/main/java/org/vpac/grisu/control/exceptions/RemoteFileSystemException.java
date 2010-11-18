package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.vpac.grisu.control.jaxws.exceptions.RemoteFileSystemException")
public class RemoteFileSystemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RemoteFileSystemException(Exception e) {
		super(e);
	}

	public RemoteFileSystemException(final String message) {
		super(message);
	}

}
