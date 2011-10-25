package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.jcommons.interfaces.GridResource;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public interface JobDistributor {

	public Map<String, Integer> distributeJobs(Set<Job> allJobs,
			SortedSet<GridResource> allAvailableResources);

}