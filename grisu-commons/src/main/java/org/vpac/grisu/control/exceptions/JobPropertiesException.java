package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

import au.org.arcs.jcommons.constants.JobSubmissionProperty;

@WebFault(faultBean="org.vpac.grisu.control.jaxws.exceptions.JobPropertiesException")
public class JobPropertiesException extends Exception {


	public JobPropertiesException(final String message) {
		super(message);
	}

}
