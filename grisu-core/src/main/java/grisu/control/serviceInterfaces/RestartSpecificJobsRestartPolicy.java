package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.grin.model.resources.Queue;

import java.util.Set;
import java.util.SortedSet;

public class RestartSpecificJobsRestartPolicy implements SubmitPolicy {

	public SortedSet<Queue> getCalculatedGridResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Job> getCalculatedJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	// private final Set<Job> allJobs;
	// private final SortedSet<GridResource> allResources;
	// private final Map<String, String> properties;
	//
	// private final SortedSet<GridResource> calculatedGridResources;
	// private final Set<Job> calculatedJobs;
	//
	// public RestartSpecificJobsRestartPolicy(Set<Job> allJobs,
	// SortedSet<GridResource> allResources, Map<String, String> properties) {
	//
	// this.allJobs = allJobs;
	// this.allResources = allResources;
	// this.properties = properties;
	//
	// calculatedJobs = new HashSet<Job>();
	// final String[] allJobnamesToRestart = properties.get(
	// Constants.JOBNAMES_TO_RESTART).split(",");
	// Arrays.sort(allJobnamesToRestart);
	//
	// for (final Job job : allJobs) {
	// if (Arrays.binarySearch(allJobnamesToRestart, job.getJobname()) >= 0) {
	// calculatedJobs.add(job);
	// }
	// }
	//
	// calculatedGridResources = new TreeSet<GridResource>();
	// final Map<String, GridResource> subLocMap = new HashMap<String,
	// GridResource>();
	// for (final GridResource gr : allResources) {
	// subLocMap.put(SubmissionLocationHelpers
	// .createSubmissionLocationString(gr), gr);
	// }
	//
	// for (final String submissionLocation : properties.get(
	// Constants.SUBMISSIONLOCATIONS_TO_RESTART).split(",")) {
	// final GridResource gr = subLocMap.get(submissionLocation);
	// if (gr != null) {
	// calculatedGridResources.add(gr);
	// }
	// }
	//
	// }
	//
	// public SortedSet<GridResource> getCalculatedGridResources() {
	// return calculatedGridResources;
	// }
	//
	// public Set<Job> getCalculatedJobs() {
	//
	// return calculatedJobs;
	// }

}
