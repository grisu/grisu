package grisu.frontend.model.job;

import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.JobSubmissionException;

import org.apache.log4j.Logger;

public class DoubleWalltimeJobRestarter implements FailedJobRestarter {

	static final Logger myLogger = Logger.getLogger(BatchJobObject.class
			.getName());

	public void restartJob(JobObject failedJob) throws JobSubmissionException {

		final int walltime = failedJob.getWalltimeInSeconds();

		failedJob.setWalltimeInSeconds(walltime * 2);

		myLogger.info("Restarting job: " + failedJob.getJobname()
				+ " with walltime: " + failedJob.getWalltimeInSeconds());
		try {
			failedJob.restartJob();
		} catch (final JobPropertiesException e) {
			throw new JobSubmissionException("Could not restart job: "
					+ e.getLocalizedMessage());
		}

	}

}
