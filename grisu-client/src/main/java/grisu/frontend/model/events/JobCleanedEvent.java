package grisu.frontend.model.events;

import grisu.frontend.model.job.GrisuJob;

public class JobCleanedEvent {

	private final GrisuJob job;

	public JobCleanedEvent(GrisuJob job) {
		this.job = job;
	}

	public GrisuJob getJob() {
		return job;
	}

}
