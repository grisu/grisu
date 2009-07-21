package org.vpac.grisu.model.job;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;

import au.org.arcs.mds.Constants;

/**
 * Job properties that are available after a job is created on the backend.
 * 
 * @author Markus Binsteiner
 * 
 */
public enum JobCreatedProperty {

	/**
	 * The name of the queue.
	 */
	QUEUE(Constants.QUEUE_KEY),
	/**
	 * The hostname the job is (or is going to be) submitted to (e.g.
	 * ng2.vpac.org).
	 */
	SUBMISSION_HOST(Constants.SUBMISSION_HOST_KEY),
	/**
	 * The full url of the jobdirectory where the job is (or will be) running.
	 */
	JOBDIRECTORY(Constants.JOBDIRECTORY_KEY),
	/**
	 * The factory type of the endpoint where the job is (or will be) running.
	 */
	FACTORY_TYPE(Constants.FACTORY_TYPE_KEY),
	/**
	 * The full path of the local workingdirectory on the cluster where the job
	 * is (or will be) running.
	 */
	WORKINGDIRECTORY(Constants.WORKINGDIRECTORY_KEY),
	/**
	 * The fqan that is used to submit this job.
	 */
	FQAN(Constants.FQAN_KEY),
	/**
	 * The staging filesystem that is used to stage-in files for this job.
	 */
	STAGING_FILE_SYSTEM(Constants.STAGING_FILE_SYSTEM_KEY),
	/**
	 * The name of the site where the job is (or will be) running.
	 */
	SUBMISSION_SITE(Constants.SUBMISSION_SITE_KEY);

	private static final Map<String, JobCreatedProperty> stringToJobPropertyMap = new HashMap<String, JobCreatedProperty>();
	static {
		for (JobCreatedProperty jp : values()) {
			stringToJobPropertyMap.put(jp.toString(), jp);
		}
	}

	private final String keyName;

	JobCreatedProperty(final String keyName) {
		this.keyName = keyName;
	}

	@Override
	public String toString() {
		return this.keyName;
	}

	/**
	 * Returns the enum for this keyname.
	 * 
	 * @param key
	 *            the keyname
	 * @return the enum
	 */
	public static JobCreatedProperty fromString(final String key) {
		return stringToJobPropertyMap.get(key);
	}

}
