package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.model.job.JobObject;

public class JobKilledEvent {

	private final JobObject job;

	public JobKilledEvent(JobObject job) {
		this.job = job;
	}

	public JobObject getJob() {
		return job;
	}

}
