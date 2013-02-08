package grisu.frontend.model.events;

import grisu.frontend.model.job.GrisuJob;

public class NewJobEvent {

	private final GrisuJob job;

	public NewJobEvent(GrisuJob job) {
		this.job = job;
	}

	public GrisuJob getJob() {
		return this.job;
	}

}
