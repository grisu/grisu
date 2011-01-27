package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.model.job.JobObject;

public class JobCleanedEvent {

	private final JobObject job;

	public JobCleanedEvent(JobObject job) {
		this.job = job;
	}

	public JobObject getJob() {
		return job;
	}

}
