package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.model.job.MultiPartJobObject;

public class MultiPartJobEvent {
	
	private MultiPartJobObject mpjo;
	private String message;
	
	public MultiPartJobEvent(MultiPartJobObject mpjo, String message) {
		this.mpjo = mpjo;
		this.message = message;
	}
	
	public MultiPartJobObject getMultiPartJob() {
		return this.mpjo;
	}
	
	public String getMessage() {
		return this.message;
	}

}
