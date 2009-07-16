package org.vpac.grisu.backend.model.job;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.globus.exec.utils.ManagedJobFactoryConstants;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.w3c.dom.Document;

import au.org.arcs.mds.Constants;
import au.org.arcs.mds.JsdlHelpers;

/**
 * The JobSubmissionManager class provides an interface between grisu and the
 * several grid middlewares. It takes a jsdl document as input, converts it into
 * the proper format and then submits the job to the proper endpoint. At the
 * moment only gt4 job submission is supported.
 * 
 * @author Markus Binsteiner
 * 
 */
public class JobSubmissionManager {

	static final Logger myLogger = Logger.getLogger(JobSubmissionManager.class
			.getName());

	private Map<String, JobSubmitter> submitters = new HashMap<String, JobSubmitter>();
	private ServiceInterface serviceInterface = null;

	/**
	 * Initializes the JobSubmissionManager with all supported
	 * {@link JobSubmitter}s.
	 * 
	 * @param submitters
	 *            the supported JobSubmitters
	 */
	public JobSubmissionManager(final ServiceInterface serviceInterface,
			final Map<String, JobSubmitter> submitters) {
		this.serviceInterface = serviceInterface;
		this.submitters = submitters;
	}

	/**
	 * Submits the job to the specified {@link JobSubmitter}.
	 * 
	 * @param submitter_name
	 *            the JobSubmitter
	 * @param job
	 *            the Job
	 * @return the (JobSubmitter-specific) handle to reconnect to the job later
	 * @throws ServerJobSubmissionException
	 *             if the job could not be submitted successful
	 */
	public final String submit(final String submitter_name, final Job job) {

		Document jsdl = null;
		jsdl = job.getJobDescription();
		JobSubmitter submitter = submitters.get(submitter_name);

		if (submitter == null) {
			throw new NoSuchJobSubmitterException("Can't find JobSubmitter: "
					+ submitter_name);
		}

		// String translatedJobDescription =
		// submitter.convertJobDescription(job);

		String host = JsdlHelpers.getCandidateHosts(jsdl)[0];
		// TODO change that once I know how to handle queues properly

		String queue = null;
		if (host.indexOf(":") != -1) {
			queue = host.substring(0, host.indexOf(":"));
			host = host.substring(host.indexOf(":") + 1);
		}
		myLogger.debug("Submission host is: " + host);

		// don't know whether factory type should be in here or in the
		// GT4Submitter (more likely the latter)
		String factoryType = null;
		if (host.indexOf("#") != -1) {
			factoryType = host.substring(host.indexOf("#") + 1);
			if (factoryType == null || factoryType.length() == 0) {
				factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.PBS;
			}
			host = host.substring(0, host.indexOf("#"));
		} else {
			factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.PBS;
		}
		job.addJobProperty(Constants.FACTORY_TYPE_KEY, factoryType);

		myLogger.debug("FactoryType is: " + factoryType);
		String submitHostEndpoint = submitter.getServerEndpoint(host);

		String handle = submitter.submit(serviceInterface, submitHostEndpoint,
				factoryType, job);

		job.setJobhandle(handle);
		// TODO remove that once I'm sure nobody is using it anymore
		job.setSubmissionHost(host);
		job.addJobProperty(Constants.SUBMISSION_HOST_KEY, host);
		job.setSubmissionType(submitter_name);
		job
				.addJobProperty(Constants.SUBMISSION_TYPE_KEY,
						submitter_name);
		if (queue != null && !"".equals(queue)) {
			job.getJobProperties().put(Constants.QUEUE_KEY, queue);
		}
		job.setStatus(JobConstants.EXTERNAL_HANDLE_READY);

		return handle;

	}

	/**
	 * Monitors the status of a job. Since the {@link JobSubmitter} that was
	 * used to submit the job is stored in the {@link Job#getSubmissionType()}
	 * property it does not have to be specified here again.
	 * 
	 * @param job
	 *            the job
	 * @return the status of the job (have a look at
	 *         {@link JobConstants#translateStatus(int)} for a human-readable
	 *         version of the status)
	 */
	public final int getJobStatus(final Job job) {

		JobSubmitter submitter = null;
		// if ( (//job.getStatus() >= JobConstants.EXTERNAL_HANDLE_READY &&
		// job.getStatus() < JobConstants.FINISHED_EITHER_WAY) //||
		// job.getStatus() == JobConstants.NO_SUCH_JOB
		// || job.getStatus() == Integer.MIN_VALUE || job.getStatus() ==
		// Integer.MAX_VALUE ) {
		if (job.getStatus() < JobConstants.FINISHED_EITHER_WAY) {
			submitter = submitters.get(job.getSubmissionType());

			if (submitter == null) {
				throw new NoSuchJobSubmitterException(
						"Can't find JobSubmitter: " + job.getSubmissionType());
			}
		} else {
			return job.getStatus();
		}

		return submitter.getJobStatus(job.getJobhandle(), job.getCredential());

	}

	/**
	 * Kills the job. Since the {@link JobSubmitter} that was used to submit the
	 * job is stored in the {@link Job#getSubmissionType()} property it does not
	 * have to be specified here again.
	 * 
	 * @param job
	 *            the job to kill
	 * @return the new status of the job. It may be worth checking whether the
	 *         job really was killed or not
	 */
	public final int killJob(final Job job) {

		JobSubmitter submitter = null;
		submitter = submitters.get(job.getSubmissionType());

		if (submitter == null) {
			// throw new NoSuchJobSubmitterException(
			// "Can't find JobSubmitter: " + job.getSubmissionType());
			myLogger
					.error("Can't find jobsubitter: " + job.getSubmissionType());
			return JobConstants.KILLED;
		}

		return submitter.killJob(job.getJobhandle(), job.getCredential());
	}

}
