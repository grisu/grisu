package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.model.job.JobObject;

public class NewJobEvent {

	private final JobObject job;

	public NewJobEvent(JobObject job) {
		this.job = job;
	}

	public JobObject getJob() {
		return this.job;
	}

}
