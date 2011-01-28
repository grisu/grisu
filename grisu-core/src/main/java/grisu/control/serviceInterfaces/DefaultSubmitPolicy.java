package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;


import au.org.arcs.jcommons.interfaces.GridResource;

public class DefaultSubmitPolicy implements SubmitPolicy {

	private final Set<Job> allJobs;
	private final SortedSet<GridResource> allResources;

	public DefaultSubmitPolicy(Set<Job> allJobs,
			SortedSet<GridResource> allResources, Map<String, String> properties) {
		this.allJobs = allJobs;
		this.allResources = allResources;
	}

	public SortedSet<GridResource> getCalculatedGridResources() {

		return allResources;
	}

	public Set<Job> getCalculatedJobs() {

		return allJobs;
	}

}
