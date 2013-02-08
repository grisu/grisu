package grisu.frontend.model.job;

import grisu.control.exceptions.JobSubmissionException;

public interface FailedJobRestarter {

	public void restartJob(GrisuJob job) throws JobSubmissionException;

}
