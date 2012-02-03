package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.grin.model.resources.Queue;

import java.util.Set;
import java.util.SortedSet;

public class DefaultSubmitPolicy implements SubmitPolicy {

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
	//
	// public DefaultSubmitPolicy(Set<Job> allJobs,
	// SortedSet<GridResource> allResources, Map<String, String> properties) {
	// this.allJobs = allJobs;
	// this.allResources = allResources;
	// }
	//
	// public SortedSet<GridResource> getCalculatedGridResources() {
	//
	// return allResources;
	// }
	//
	// public Set<Job> getCalculatedJobs() {
	//
	// return allJobs;
	// }

}
