package org.vpac.grisu.frontend.model.job;

/**
 * A listener for status change events in {@link JobObject}s.
 * 
 * @author Markus Binsteiner
 */
public interface JobStatusChangeListener {
	
	/**
	 * Called if the status of the {@link JobObject} changed.
	 * 
	 * @param job the job
	 * @param oldStatus the old status
	 * @param newStatus the new status
	 */
	public void jobStatusChanged(JobObject job, int oldStatus, int newStatus);

}
