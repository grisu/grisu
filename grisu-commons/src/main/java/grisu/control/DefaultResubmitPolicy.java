package grisu.control;

import grisu.jcommons.constants.Constants;
import grisu.model.dto.DtoProperties;

import java.util.HashMap;
import java.util.Map;

public class DefaultResubmitPolicy implements ResubmitPolicy {

	/**
	 * Restart already done jobs. Defaults to false.
	 */
	public static final String RESTART_DONE_JOBS = "restartDoneJobs";
	/**
	 * Restart jobs that are either unsubmitted or pending. Defaults to false.
	 * 
	 * Note: along with this you also have to specify one of the
	 * RESTART_RESTART_WAITING_JOBS_ON_XXX_LOCATIONS.
	 */
	public static final String RESTART_WAITING_JOBS = "restartWaitingJobs";
	/**
	 * Restart failed jobs. Defaults to true.
	 */
	public static final String RESTART_FAILED_JOBS = "restartFailedJobs";
	/**
	 * Restart jobs that are running at the moment. Defaults to false.
	 */
	public static final String RESTART_RUNNING_JOBS = "restartRunningJobs";
	/**
	 * Start (newly) ready jobs. Defaults to true. You shouldn't need this.
	 */
	public static final String START_NEWLY_READY_JOBS = "startReadyJobs";
	/**
	 * Queue restart-jobs to locations where jobs have succuessfully run
	 * already. Defaults to true.
	 */
	public static final String RESTART_TO_DONE_SUBMISSION_LOCATIONS = "restartToDoneSubLocs";
	/**
	 * Queue restart-jobs to locations where jobs are waiting in the queue and
	 * no job run so far. Defaults to false.
	 */
	public static final String RESTART_TO_WAITING_SUBMISSION_LOCATIONS = "restartToWaitingSubLocs";
	/**
	 * Queue restart-jobs to locations where jobs have failed. Defaults to
	 * false.
	 */
	public static final String RESTART_TO_FAILED_SUBMISSION_LOCATIONS = "restartToFailedSubLocs";
	/**
	 * Queue restart-jobs to locations where jobs are running at the moment.
	 * Defaults to true.
	 */
	public static final String RESTART_TO_RUNNING_SUBMISSION_LOCATIONS = "restartToRunningSubLocs";
	/**
	 * Restart jobs that are currently waiting on a location with currently
	 * running jobs. Defaults to false.
	 */
	public static final String RESTART_WAITING_JOBS_ON_RUNNING_LOCATIONS = "restartWaitingJobsOnRunningSubLocs";
	/**
	 * Restart jobs that are currently waiting on a location where jobs already
	 * run succesfully. Defaults to false.
	 */
	public static final String RESTART_WAITING_JOBS_ON_DONE_LOCATIONS = "restartWaitingJobsOnDoneSubLocs";
	/**
	 * Restart jobs that are currently waiting on a location with at least one
	 * failed job. Defaults to true.
	 */
	public static final String RESTART_WAITING_JOBS_ON_FAILED_LOCATIONS = "restartWaitingJobsOnFailedSubLocs";
	/**
	 * Restart jobs that are currently waiting on a location where no job run so
	 * far. Default to true.
	 */
	public static final String RESTART_WAITING_JOBS_ON_WAITING_LOCATIONS = "restartWaitingJobsOnWaitingLocations";
	/**
	 * Convenience option to specify to restart restart-jobs to all available
	 * locations, regardless of whether they have finished or failed or waiting
	 * jobs. Defaults to false.
	 */
	public static final String RESTART_TO_ALL_LOCATIONS = "submitToAllLocations";

	private final Map<String, Boolean> properties = new HashMap<String, Boolean>();

	public String getName() {

		return Constants.SUBMIT_POLICY_RESTART_DEFAULT;
	}

	public DtoProperties getProperties() {

		final Map<String, String> temp = new HashMap<String, String>();

		for (final String key : properties.keySet()) {
			temp.put(key, properties.get(key).toString());
		}

		return DtoProperties.createProperties(temp);

	}

	public void setProperty(String key, Boolean value) {
		properties.put(key, value);
	}

}
