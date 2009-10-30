package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean="org.vpac.grisu.control.jaxws.exceptions.MultiPartJobException")
public class MultiPartJobException extends Exception {
	
	public MultiPartJobException(String message) {
		super(message);
	}

}
