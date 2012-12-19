package grisu.backend.model.job.gt5;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.globus.gram.GramJob;
import org.globus.gram.GramJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gram5JobListener implements GramJobListener {

	static final Logger myLogger = LoggerFactory.getLogger(GT5Submitter.class
			.getName());

	private static Gram5JobListener l = new Gram5JobListener();

	public static Gram5JobListener getJobListener() {
		return l;
	}

	private final Map<String, Integer> statuses;

	private final Map<String, Integer> errors;
	private final Map<String, Integer> exitCodes;

	private Gram5JobListener() {
		statuses = new ConcurrentHashMap<String, Integer>();
		errors = new ConcurrentHashMap<String, Integer>();
		exitCodes = new ConcurrentHashMap<String, Integer>();
	}

	public Integer getError(String handle) {
		return errors.remove(handle);
	}

	public Integer getStatus(String handle) {
		return statuses.remove(handle);
	}
	
	public Integer getExitCode(String handle) {
		return statuses.remove(handle);
	}

	public void statusChanged(GramJob job) {
                int jobStatus = job.getStatus();
                String jobId = job.getIDAsString();
                int exitCode = job.getExitCode();
                myLogger.debug("job status changed to " + jobStatus);
                try {
                    if ((jobStatus == GramJob.STATUS_DONE) || (jobStatus == GramJob.STATUS_FAILED)){
                        job.signal(GramJob.SIGNAL_COMMIT_END);
                    }
                    // Only set status if signal sending succeeded to have grisu
                    // so that GT5Submitter goes to Gram in an attempt to get job status
                    statuses.put(jobId, jobStatus);
                    errors.put(jobId, job.getError());
                    exitCodes.put(jobId, exitCode);
                } catch (Exception e) {
                    String state = job.getStatusAsString();
                    myLogger.warn("Failed to send COMMIT_END to job " + jobId + " in state " + state, e);
                }
	}

}
