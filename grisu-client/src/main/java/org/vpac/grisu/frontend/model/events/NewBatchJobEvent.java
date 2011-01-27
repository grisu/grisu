package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class NewBatchJobEvent {

	private final BatchJobObject job;

	public NewBatchJobEvent(BatchJobObject job) {
		this.job = job;
	}

	public BatchJobObject getBatchJob() {
		return this.job;
	}

}
