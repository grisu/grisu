package grisu.frontend.model.events;

import grisu.frontend.model.job.BatchJobObject;

public class BatchJobEvent {

	private final BatchJobObject mpjo;
	private final String message;

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
