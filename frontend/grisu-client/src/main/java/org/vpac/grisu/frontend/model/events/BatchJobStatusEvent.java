package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class BatchJobStatusEvent {

	private final int oldStatus;
	private final int newStatus;

	private final BatchJobObject job;

	public BatchJobStatusEvent(BatchJobObject job, int oldStatus, int newStatus) {
		this.job = job;
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}

	public BatchJobObject getJob() {
		return this.job;
	}

	public int getNewStatus() {
		return this.newStatus;
	}

	public int getOldStatus() {
		return this.oldStatus;
	}

	@Override
	public String toString() {
		return "BatchJob status changed from \""
				+ JobConstants.translateStatus(oldStatus) + "\" to \""
				+ JobConstants.translateStatus(newStatus) + "\" for job "
				+ job.getJobname();
	}

}
