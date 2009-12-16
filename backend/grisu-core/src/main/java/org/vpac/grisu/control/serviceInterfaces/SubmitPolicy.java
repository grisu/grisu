package org.vpac.grisu.control.serviceInterfaces;

import java.util.Set;
import java.util.SortedSet;

import org.vpac.grisu.backend.model.job.Job;

import au.org.arcs.jcommons.interfaces.GridResource;

public interface SubmitPolicy {

	public SortedSet<GridResource> getCalculatedGridResources();

	public Set<Job> getCalculatedJobs();

}
