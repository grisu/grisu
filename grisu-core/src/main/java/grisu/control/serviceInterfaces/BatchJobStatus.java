package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.control.JobConstants;
import grisu.jcommons.constants.Constants;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;



public class BatchJobStatus {

	private final Set<Job> allJobs;

	private List<Job> failedJobs;

	private List<Job> doneJobs;

	private List<Job> waitingJobs;

	private List<Job> runningJobs;

	private List<Job> readyJobs;

	private List<String> failedSubLocs;
	private List<String> doneSubLocs;
	private List<String> waitingSubLocs;
	private List<String> runningSubLocs;

	public BatchJobStatus(Set<Job> jobs) {
		this.allJobs = jobs;
		init();
	}

	private void addDone(Job job) {

		doneJobs.add(job);
		doneSubLocs.add(job.getJobProperty(Constants.SUBMISSIONLOCATION_KEY));
	}

	private void addFailed(Job job) {

		failedJobs.add(job);
		failedSubLocs.add(job.getJobProperty(Constants.SUBMISSIONLOCATION_KEY));
	}

	private void addReadyJob(Job job) {
		readyJobs.add(job);
	}

	private void addRunning(Job job) {

		runningJobs.add(job);
		runningSubLocs
				.add(job.getJobProperty(Constants.SUBMISSIONLOCATION_KEY));
	}

	private void addWaiting(Job job) {

		waitingJobs.add(job);
		waitingSubLocs
				.add(job.getJobProperty(Constants.SUBMISSIONLOCATION_KEY));
	}

	public List<Job> getDoneJobs() {
		return doneJobs;
	}

	public List<String> getDoneSubLocs() {
		return doneSubLocs;
	}

	public List<Job> getFailedJobs() {
		return failedJobs;
	}

	public List<String> getFailedSubLocs() {
		return failedSubLocs;
	}

	public List<Job> getReadyJobs() {
		return readyJobs;
	}

	public List<Job> getRunningJobs() {
		return runningJobs;
	}

	public List<String> getRunningSubLocs() {
		return runningSubLocs;
	}

	public List<Job> getWaitingJobs() {
		return waitingJobs;
	}

	public List<String> getWaitingSubLocs() {
		return waitingSubLocs;
	}

	private void init() {
		failedJobs = new LinkedList<Job>();
		doneJobs = new LinkedList<Job>();
		waitingJobs = new LinkedList<Job>();
		runningJobs = new LinkedList<Job>();
		readyJobs = new LinkedList<Job>();

		failedSubLocs = new LinkedList<String>();
		doneSubLocs = new LinkedList<String>();
		waitingSubLocs = new LinkedList<String>();
		runningSubLocs = new LinkedList<String>();

		for (final Job job : allJobs) {

			if (JobConstants.DONE == job.getStatus()) {
				addDone(job);
			} else if (JobConstants.FINISHED_EITHER_WAY <= job.getStatus()) {
				addFailed(job);
			} else if (JobConstants.UNSUBMITTED > job.getStatus()) {
				addReadyJob(job);
			} else if (JobConstants.PENDING >= job.getStatus()) {
				addWaiting(job);
			} else {
				addRunning(job);
			}
		}

		doneSubLocs.removeAll(failedSubLocs);
		runningSubLocs.removeAll(failedSubLocs);
		waitingSubLocs.removeAll(failedSubLocs);
		waitingSubLocs.removeAll(doneSubLocs);
		waitingSubLocs.removeAll(runningSubLocs);
	}

}
