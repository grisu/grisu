package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.jcommons.model.info.Queue;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public class EqualJobDistributor implements JobDistributor {

	public Map<String, Integer> distributeJobs(
			Set<Job> allJobs,
			SortedSet<Queue> allAvailableResources) {
		// TODO Auto-generated method stub
		return null;
	}

	// public Map<String, Integer> distributeJobs(Set<Job> allJobs,
	// SortedSet<GridResource> allAvailableResources) {
	//
	// final Map<String, Integer> submissionLocations = new TreeMap<String,
	// Integer>();
	//
	// int i = 0;
	//
	// final GridResource[] allResources = allAvailableResources
	// .toArray(new GridResource[] {});
	//
	// for (final Job job : allJobs) {
	//
	// final String subLoc = SubmissionLocationHelpers
	// .createSubmissionLocationString(allResources[i]);
	// JsdlHelpers.setCandidateHosts(job.getJobDescription(),
	// new String[] { subLoc });
	//
	// Integer currentCount = submissionLocations.get(subLoc);
	// if (currentCount == null) {
	// currentCount = 0;
	// }
	// submissionLocations.put(subLoc, currentCount + 1);
	//
	// i = i + 1;
	// if (i >= allResources.length) {
	// i = 0;
	// }
	//
	// }
	//
	// return submissionLocations;
	// }

}
