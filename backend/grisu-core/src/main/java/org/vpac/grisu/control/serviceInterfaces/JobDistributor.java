package org.vpac.grisu.control.serviceInterfaces;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.vpac.grisu.backend.model.job.Job;
import org.vpac.grisu.control.ServiceInterface;

import au.org.arcs.jcommons.interfaces.GridResource;

public interface JobDistributor {
	
	public Map<String, Integer> distributeJobs(Set<Job> allJobs, SortedSet<GridResource> allAvailableResources);

}