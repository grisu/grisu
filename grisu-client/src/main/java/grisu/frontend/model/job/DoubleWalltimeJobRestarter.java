package grisu.frontend.model.job;

import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.JobSubmissionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoubleWalltimeJobRestarter implements FailedJobRestarter {

	static final Logger myLogger = LoggerFactory.getLogger(BatchJobObject.class
			.getName());

	public void restartJob(GrisuJob failedJob) throws JobSubmissionException {

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
