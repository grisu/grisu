package org.vpac.grisu.frontend.model.job;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;

public class DoubleWalltimeJobRestarter implements FailedJobRestarter {
	
	static final Logger myLogger = Logger.getLogger(MultiPartJobObject.class.getName());

	public void restartJob(JobObject failedJob) throws JobSubmissionException {

		int walltime = failedJob.getWalltimeInSeconds();
		
		failedJob.setWalltimeInSeconds(walltime * 2);
	
		myLogger.info("Restarting job: "+failedJob.getJobname()+" with walltime: "+failedJob.getWalltimeInSeconds());
		try {
			failedJob.restartJob();
		} catch (JobPropertiesException e) {
			throw new JobSubmissionException("Could not restart job: "+e.getLocalizedMessage());
		}
		
	}

}
