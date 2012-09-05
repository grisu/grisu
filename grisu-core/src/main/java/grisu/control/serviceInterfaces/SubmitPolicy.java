package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;
import grisu.jcommons.model.info.Queue;

import java.util.Set;
import java.util.SortedSet;

public interface SubmitPolicy {

	public SortedSet<Queue> getCalculatedGridResources();

	public Set<Job> getCalculatedJobs();

}
