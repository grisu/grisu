package org.vpac.grisu.control.exceptions;

import au.org.arcs.mds.JobSubmissionProperty;

public class JobPropertiesException extends Exception {

	private JobSubmissionProperty reason = null;

	public JobPropertiesException(final JobSubmissionProperty reason, final String message) {
		super(message);
		this.reason = reason;
	}

}
