package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.jcommons.interfaces.GridResource;
import grisu.jcommons.utils.JsdlHelpers;
import grisu.jcommons.utils.SubmissionLocationHelpers;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;



public class EqualJobDistributor implements JobDistributor {

	public Map<String, Integer> distributeJobs(Set<Job> allJobs,
			SortedSet<GridResource> allAvailableResources) {

		final Map<String, Integer> submissionLocations = new TreeMap<String, Integer>();

		int i = 0;

		final GridResource[] allResources = allAvailableResources
				.toArray(new GridResource[] {});

		for (final Job job : allJobs) {

			final String subLoc = SubmissionLocationHelpers
					.createSubmissionLocationString(allResources[i]);
			JsdlHelpers.setCandidateHosts(job.getJobDescription(),
					new String[] { subLoc });

			Integer currentCount = submissionLocations.get(subLoc);
			if (currentCount == null) {
				currentCount = 0;
			}
			submissionLocations.put(subLoc, currentCount + 1);

			i = i + 1;
			if (i >= allResources.length) {
				i = 0;
			}

		}

		return submissionLocations;
	}

}
