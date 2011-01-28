package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;

import java.util.Set;
import java.util.SortedSet;


import au.org.arcs.jcommons.interfaces.GridResource;

public interface SubmitPolicy {

	public SortedSet<GridResource> getCalculatedGridResources();

	public Set<Job> getCalculatedJobs();

}
