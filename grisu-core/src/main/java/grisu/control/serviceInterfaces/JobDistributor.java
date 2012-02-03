package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.grin.model.resources.Queue;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public interface JobDistributor {

	public Map<String, Integer> distributeJobs(Set<Job> allJobs,
			SortedSet<Queue> allAvailableResources);

}