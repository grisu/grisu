package grisu.backend.model.job;

import grith.jgrith.cred.Cred;

/**
 * Extend this abstract class if you want to write a connector to a new
 * middleware.
 * 
 * @author markus
 * 
 */
public abstract class JobSubmitter {

	// /**
	// * This one has to create the {@link JobSubmitter} specific job
	// description out of a jsdl document.
	// *
	// * @param jsdl the job description in jsdl format
	// * @returnthe job description in the native format of this JobSubmitter
	// */
	// abstract protected String createJobSubmissionDescription(Document jsdl);

	/**
	 * Monitors the job with the specified (JobSubmitter-specific) jobhandle.
	 * 
	 * @param job
	 *            the job object
	 * 
	 * @param cred
	 *            the credential to authenticate yourself as being authorized to
	 *            kill the job
	 * @return the status of the job
	 */
	public abstract int getJobStatus(Job job, Cred cred);

	// public String convertJobDescription(Job job) {
	//
	// String converted =
	// createJobSubmissionDescription(job.getJobDescription());
	//
	// return converted;
	// }

	/**
	 * Processes a server name to get the proper endpoint string.
	 * 
	 * @param server
	 *            the server name (something like ng2.vpac.org)
	 * @return the endpoint (maybe
	 *         https://ng2.vpac.org:8443/wsrf/services/ManagedJobFactoryService)
	 */
	public abstract String getServerEndpoint(String server);

	/**
	 * Kills the job with the specified (JobSubmitter-specific) jobhandle.
	 * 
	 * @param job
	 *            the job
	 * @param cred
	 *            the credential to authenticate yourself as being authorized to
	 *            kill the job
	 * @return the new status of the job
	 */
	public abstract int killJob(Job job, Cred cred);

	/**
	 * Used to submit the job to the specified host.
	 * 
	 * @param infoManager
	 *            an informationManager
	 * @param host
	 *            the host (something like ng2.vpac.org)
	 * @param job
	 *            the job
	 * @return the (JobSubmitter-specific) handle to the job
	 * @throws ServerJobSubmissionException
	 */
	protected abstract String submit(
			String host, String factoryType, Job job)
					throws ServerJobSubmissionException;

}
