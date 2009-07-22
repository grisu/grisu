package org.vpac.grisu.control.exceptions;

import javax.xml.ws.WebFault;

import au.org.arcs.mds.JobSubmissionProperty;

@WebFault(faultBean="org.vpac.grisu.control.jaxws.exceptions.JobPropertiesException")
public class JobPropertiesException extends Exception {

	private JobSubmissionProperty reason = null;

	public JobPropertiesException(final JobSubmissionProperty reason, final String message) {
		super(message);
		this.reason = reason;
	}

}
