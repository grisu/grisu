package org.vpac.grisu.control.serviceInterfaces;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.vpac.grisu.backend.model.job.Job;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class RestartSpecificJobsRestartPolicy implements SubmitPolicy {

	private final Set<Job> allJobs;
	private final SortedSet<GridResource> allResources;
	private final Map<String, String> properties;

	private final SortedSet<GridResource> calculatedGridResources;
	private final Set<Job> calculatedJobs;

	public RestartSpecificJobsRestartPolicy(Set<Job> allJobs,
			SortedSet<GridResource> allResources, Map<String, String> properties) {

		this.allJobs = allJobs;
		this.allResources = allResources;
		this.properties = properties;

		calculatedJobs = new HashSet<Job>();
		String[] allJobnamesToRestart = properties.get(Constants.JOBNAMES_TO_RESTART).split(",");
		Arrays.sort(allJobnamesToRestart);

		for ( Job job : allJobs ) {
			if ( Arrays.binarySearch(allJobnamesToRestart, job.getJobname()) >= 0 ) {
				calculatedJobs.add(job);
			}
		}

		calculatedGridResources = new TreeSet<GridResource>();
		Map<String, GridResource> subLocMap = new HashMap<String, GridResource>();
		for ( GridResource gr : allResources ) {
			subLocMap.put(SubmissionLocationHelpers.createSubmissionLocationString(gr), gr);
		}

		for ( String submissionLocation : properties.get(Constants.SUBMISSIONLOCATIONS_TO_RESTART).split(",") ) {
			GridResource gr = subLocMap.get(submissionLocation);
			if ( gr != null ) {
				calculatedGridResources.add(gr);
			}
		}

	}


	public SortedSet<GridResource> getCalculatedGridResources() {
		return calculatedGridResources;
	}

	public Set<Job> getCalculatedJobs() {

		return calculatedJobs;
	}

}
