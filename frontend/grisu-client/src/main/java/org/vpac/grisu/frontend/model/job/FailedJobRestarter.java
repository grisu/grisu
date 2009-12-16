package org.vpac.grisu.frontend.model.job;

import org.vpac.grisu.control.exceptions.JobSubmissionException;

public interface FailedJobRestarter {

	public void restartJob(JobObject job) throws JobSubmissionException;

}
