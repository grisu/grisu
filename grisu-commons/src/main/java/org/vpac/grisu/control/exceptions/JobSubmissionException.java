package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean="org.vpac.grisu.control.jaxws.exceptions.JobSubmissionException")
public class JobSubmissionException extends Exception {

	public JobSubmissionException(final String message) {
		super(message);
	}

	public JobSubmissionException(final String message, final Exception e) {
		super(message, e);
	}

}
