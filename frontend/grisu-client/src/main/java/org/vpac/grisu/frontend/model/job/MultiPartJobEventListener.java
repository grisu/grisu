package org.vpac.grisu.frontend.model.job;

/**
 * A listener for status change events in {@link JobObject}s.
 * 
 * @author Markus Binsteiner
 */
public interface MultiPartJobEventListener {

	/**
	 * Called if the status of the {@link JobObject} changed.
	 * 
	 * @param job
	 *            the job
	 * @param oldStatus
	 *            the old status
	 * @param newStatus
	 *            the new status
	 */
	void eventOccured(MultiPartJobObject job, String eventMessage);

}
