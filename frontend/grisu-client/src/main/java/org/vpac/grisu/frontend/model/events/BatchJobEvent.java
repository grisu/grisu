package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class BatchJobEvent {
	
	private BatchJobObject mpjo;
	private String message;
	
	public BatchJobEvent(BatchJobObject mpjo, String message) {
		this.mpjo = mpjo;
		this.message = message;
	}
	
	public BatchJobObject getMultiPartJob() {
		return this.mpjo;
	}
	
	public String getMessage() {
		return this.message;
	}

}
