package org.vpac.grisu.control.serviceInterfaces;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.vpac.grisu.backend.model.job.Job;

import au.org.arcs.jcommons.interfaces.GridResource;

public interface JobDistributor {
	
	public Map<String, Integer> distributeJobs(Set<Job> alljobs, SortedSet<GridResource> allAvailableResources);

}