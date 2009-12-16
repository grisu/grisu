package org.vpac.grisu.control.serviceInterfaces;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.vpac.grisu.backend.model.job.Job;

import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.JsdlHelpers;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class EqualJobDistributor implements JobDistributor {

	public Map<String, Integer> distributeJobs(Set<Job> allJobs,
			SortedSet<GridResource> allAvailableResources) {

		Map<String, Integer> submissionLocations = new TreeMap<String, Integer>();

		int i = 0;

		GridResource[] allResources = allAvailableResources
				.toArray(new GridResource[] {});

		for (Job job : allJobs) {

			String subLoc = SubmissionLocationHelpers
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
