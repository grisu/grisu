package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.vpac.grisu.control.jaxws.exceptions.JobSubmissionException")
public class JobSubmissionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JobSubmissionException(final String message) {
		super(message);
	}

	public JobSubmissionException(final String message, final Exception e) {
		super(message, e);
	}

}
