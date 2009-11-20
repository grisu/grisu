package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean="org.vpac.grisu.control.jaxws.exceptions.MultiPartJobException")
public class BatchJobException extends Exception {
	
	public BatchJobException(String message) {
		super(message);
	}

}
