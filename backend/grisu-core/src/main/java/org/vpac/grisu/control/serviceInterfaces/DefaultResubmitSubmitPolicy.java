package org.vpac.grisu.control.serviceInterfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.vpac.grisu.backend.model.job.Job;
import org.vpac.grisu.control.DefaultResubmitPolicy;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class DefaultResubmitSubmitPolicy implements SubmitPolicy {

	private SortedSet<GridResource> calculatedResources;
	private Set<Job> calculatedJobs;

	private final Set<Job> allJobs;
	private final SortedSet<GridResource> allResources;
	private final BatchJobStatus bjs;

	private final Map<String, String> initProperties = new HashMap<String, String>();

	private final Map<String, GridResource> cacheGridResourceMap = new HashMap<String, GridResource>();

	public DefaultResubmitSubmitPolicy(Set<Job> allJobs,
			SortedSet<GridResource> allResources) {
		this(allJobs, allResources, null);
	}

	public DefaultResubmitSubmitPolicy(Set<Job> allJobs,
			SortedSet<GridResource> allResources, Map<String, String> properties) {
		this.allJobs = allJobs;
		this.allResources = allResources;

		// init properties with defaults
		setSubmitToRunningLocations(true);
		setSubmitToDoneLocations(true);
		setResubmitFailedJobs(true);
		setSubmitReadyJobs(true);
		setResubmitWaitingJobs(false);
		setResubmitWaitingJobsOnFailedLocations(true);
		setResubmitWaitingJobsOnWaitingLocations(true);

		if (properties != null) {
			// overwrite defaults
			for (String key : properties.keySet()) {
				initProperties.put(key, properties.get(key));
			}
		}

		for (GridResource gr : allResources) {
			cacheGridResourceMap.put(SubmissionLocationHelpers
					.createSubmissionLocationString(gr), gr);
		}

		bjs = new BatchJobStatus(getAllJobs());

	}

	public Set<Job> getAllJobs() {
		return allJobs;
	}

	public SortedSet<GridResource> getAllResources() {
		return allResources;
	}

	public SortedSet<GridResource> getCalculatedGridResources() {
		if (calculatedResources == null) {

			calculatedResources = new TreeSet<GridResource>();

			if (isSubmitToAllLocations()) {

				calculatedResources = allResources;

			} else {

				if (isSubmitToDoneLocations()) {
					for (String subLoc : bjs.getDoneSubLocs()) {

						if (bjs.getFailedSubLocs().contains(subLoc)) {
							// at least one job failed on this subLoc
							continue;
						}

						GridResource temp = cacheGridResourceMap.get(subLoc);

						if (temp != null) {
							calculatedResources.add(temp);
						}
					}
				}
				if (isSubmitToFailedLocations()) {
					for (String subLoc : bjs.getFailedSubLocs()) {
						GridResource temp = cacheGridResourceMap.get(subLoc);

						if (temp != null) {
							calculatedResources.add(temp);
						}
					}
				}
				if (isSubmitToRunningLocations()) {
					for (String subLoc : bjs.getRunningSubLocs()) {

						if (bjs.getDoneSubLocs().contains(subLoc)
								|| bjs.getFailedJobs().contains(subLoc)) {
							// at least one job already finished or failed on
							// this subloc
							continue;
						}
						GridResource temp = cacheGridResourceMap.get(subLoc);

						if (temp != null) {
							calculatedResources.add(temp);
						}
					}
				}
				if (isSubmitToWaitingLocations()) {
					for (String subLoc : bjs.getRunningSubLocs()) {

						if (bjs.getDoneSubLocs().contains(subLoc)
								|| bjs.getRunningSubLocs().contains(subLoc)
								|| bjs.getFailedSubLocs().contains(subLoc)) {
							// at least one job already finished here or is
							// running or failed on this subloc
							continue;
						}

						GridResource temp = cacheGridResourceMap.get(subLoc);

						if (temp != null) {
							calculatedResources.add(temp);
						}
					}
				}
			}

		}
		return calculatedResources;
	}

	public Set<Job> getCalculatedJobs() {

		if (calculatedJobs == null) {

			calculatedJobs = new HashSet<Job>();
			if (isResubmitDoneJobs()) {
				for (Job job : bjs.getDoneJobs()) {
					calculatedJobs.add(job);
				}
			}
			if (isResubmitFailedJobs()) {
				for (Job job : bjs.getFailedJobs()) {
					calculatedJobs.add(job);
				}
			}
			if (isResubmitRunningJobs()) {
				for (Job job : bjs.getRunningJobs()) {
					calculatedJobs.add(job);
				}
			}
			if (isResubmitWaitingJobs()) {

				for (Job job : bjs.getWaitingJobs()) {

					String currentSubLoc = job
					.getJobProperty(Constants.SUBMISSIONLOCATION_KEY);
					if (isResubmitWaitingJobsOnDoneLocations()
							&& bjs.getDoneSubLocs().contains(currentSubLoc)) {
						calculatedJobs.add(job);
					} else if (isResubmitWaitingJobsOnFailedLocations()
							&& bjs.getFailedSubLocs().contains(currentSubLoc)) {
						calculatedJobs.add(job);
					} else if (isResubmitWaitingJobsOnRunningLocations()
							&& bjs.getRunningSubLocs().contains(currentSubLoc)) {
						calculatedJobs.add(job);
					} else if (isResubmitWaitingJobsOnWaitingLocations()
							&& bjs.getWaitingSubLocs().contains(currentSubLoc)) {
						calculatedJobs.add(job);
					}

				}
			}
			if (isSubmitReadyJobs()) {
				for (Job job : bjs.getReadyJobs()) {
					calculatedJobs.add(job);
				}
			}

		}
		return calculatedJobs;
	}

	public boolean isResubmitDoneJobs() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_DONE_JOBS));
	}

	public boolean isResubmitFailedJobs() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_FAILED_JOBS));
	}

	public boolean isResubmitRunningJobs() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_RUNNING_JOBS));
	}

	public boolean isResubmitWaitingJobs() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_WAITING_JOBS));
	}

	public boolean isResubmitWaitingJobsOnDoneLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_WAITING_JOBS_ON_DONE_LOCATIONS));
	}

	public boolean isResubmitWaitingJobsOnFailedLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_WAITING_JOBS_ON_FAILED_LOCATIONS));
	}

	public boolean isResubmitWaitingJobsOnRunningLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_WAITING_JOBS_ON_RUNNING_LOCATIONS));
	}

	public boolean isResubmitWaitingJobsOnWaitingLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_WAITING_JOBS_ON_WAITING_LOCATIONS));
	}

	public boolean isSubmitReadyJobs() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.START_NEWLY_READY_JOBS));
	}

	public boolean isSubmitToAllLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_TO_ALL_LOCATIONS));
	}

	public boolean isSubmitToDoneLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_TO_DONE_SUBMISSION_LOCATIONS));
	}

	public boolean isSubmitToFailedLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_TO_FAILED_SUBMISSION_LOCATIONS));
	}

	public boolean isSubmitToRunningLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_TO_RUNNING_SUBMISSION_LOCATIONS));
	}

	public boolean isSubmitToWaitingLocations() {
		return Boolean.parseBoolean(initProperties
				.get(DefaultResubmitPolicy.RESTART_TO_WAITING_SUBMISSION_LOCATIONS));
	}

	public void recalculate() {

		calculatedJobs = null;
		calculatedResources = null;

	}

	public void setResubmitDoneJobs(Boolean resubmitDoneJobs) {
		initProperties.put(DefaultResubmitPolicy.RESTART_DONE_JOBS, resubmitDoneJobs
				.toString());
	}

	public void setResubmitFailedJobs(Boolean resubmitFailedJobs) {
		initProperties.put(DefaultResubmitPolicy.RESTART_FAILED_JOBS,
				resubmitFailedJobs.toString());
	}

	public void setResubmitRunningJobs(Boolean resubmitRunningJobs) {
		initProperties.put(DefaultResubmitPolicy.RESTART_RUNNING_JOBS,
				resubmitRunningJobs.toString());
	}

	public void setResubmitWaitingJobs(Boolean resubmitWaitingJobs) {
		initProperties.put(DefaultResubmitPolicy.RESTART_WAITING_JOBS,
				resubmitWaitingJobs.toString());
	}

	public void setResubmitWaitingJobsOnDoneLocations(Boolean restart) {
		initProperties.put(
				DefaultResubmitPolicy.RESTART_WAITING_JOBS_ON_DONE_LOCATIONS, restart
				.toString());
	}

	public void setResubmitWaitingJobsOnFailedLocations(Boolean restart) {
		initProperties.put(
				DefaultResubmitPolicy.RESTART_WAITING_JOBS_ON_FAILED_LOCATIONS,
				restart.toString());
	}

	public void setResubmitWaitingJobsOnRunningLocations(Boolean restart) {
		initProperties.put(
				DefaultResubmitPolicy.RESTART_WAITING_JOBS_ON_RUNNING_LOCATIONS,
				restart.toString());
	}

	public void setResubmitWaitingJobsOnWaitingLocations(Boolean restart) {
		initProperties.put(
				DefaultResubmitPolicy.RESTART_WAITING_JOBS_ON_WAITING_LOCATIONS,
				restart.toString());
	}

	public void setSubmitReadyJobs(Boolean submitReadyJobs) {
		initProperties.put(DefaultResubmitPolicy.START_NEWLY_READY_JOBS,
				submitReadyJobs.toString());
	}

	public void setSubmitToAllLocations(Boolean all) {
		initProperties.put(DefaultResubmitPolicy.RESTART_TO_ALL_LOCATIONS, all
				.toString());
	}

	public void setSubmitToDoneLocations(Boolean submitToDoneLocations) {
		initProperties.put(DefaultResubmitPolicy.RESTART_TO_DONE_SUBMISSION_LOCATIONS,
				submitToDoneLocations.toString());
	}

	public void setSubmitToFailedLocations(Boolean submitToFailedLocations) {
		initProperties.put(
				DefaultResubmitPolicy.RESTART_TO_FAILED_SUBMISSION_LOCATIONS,
				submitToFailedLocations.toString());
	}

	public void setSubmitToRunningLocations(Boolean submitToRunningLocations) {
		initProperties.put(
				DefaultResubmitPolicy.RESTART_TO_RUNNING_SUBMISSION_LOCATIONS,
				submitToRunningLocations.toString());
	}

	public void setSubmitToWaitingLocations(Boolean submitToWaitingLocations) {
		initProperties.put(
				DefaultResubmitPolicy.RESTART_TO_WAITING_SUBMISSION_LOCATIONS,
				submitToWaitingLocations.toString());
	}

}
