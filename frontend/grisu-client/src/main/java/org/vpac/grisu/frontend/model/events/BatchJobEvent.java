package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class BatchJobEvent {

	private BatchJobObject mpjo;
	private String message;

	public BatchJobEvent(BatchJobObject mpjo, String message) {
		this.mpjo = mpjo;
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public BatchJobObject getMultiPartJob() {
		return this.mpjo;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}

}
