package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.vpac.grisu.control.jaxws.exceptions.BatchJobException")
public class BatchJobException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BatchJobException(String message) {
		super(message);
	}

}
