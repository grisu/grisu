package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.jcommons.interfaces.GridResource;

import java.util.Set;
import java.util.SortedSet;

public interface SubmitPolicy {

	public SortedSet<GridResource> getCalculatedGridResources();

	public Set<Job> getCalculatedJobs();

}
