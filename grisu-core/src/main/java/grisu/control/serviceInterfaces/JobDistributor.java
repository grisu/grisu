package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;


import au.org.arcs.jcommons.interfaces.GridResource;

public interface JobDistributor {

	public Map<String, Integer> distributeJobs(Set<Job> allJobs,
			SortedSet<GridResource> allAvailableResources);

}