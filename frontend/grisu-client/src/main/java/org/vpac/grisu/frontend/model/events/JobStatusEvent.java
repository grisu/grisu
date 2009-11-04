package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.model.job.JobObject;

public class JobStatusEvent {
	
	private int oldStatus;
	private int newStatus;
	
	private JobObject job;
	
	public JobStatusEvent(JobObject job, int oldStatus, int newStatus) {
		this.job = job;
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}
	
	public int getOldStatus() {
		return this.oldStatus;
	}
	
	public int getNewStatus() {
		return this.newStatus;
	}
	
	public JobObject getJob() {
		return this.job;
	}

}
