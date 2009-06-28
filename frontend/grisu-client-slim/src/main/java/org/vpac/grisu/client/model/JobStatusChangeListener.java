package org.vpac.grisu.client.model;

public interface JobStatusChangeListener {
	
	public void jobStatusChanged(JobObject job, int oldStatus, int newStatus);

}
