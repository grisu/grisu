package org.vpac.grisu.js.model;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.ServiceInterface;

public enum JobSubmissionProperty {
	
	/**
	 * The (grisu-backend unique) name of this job. Defaults to "grisu_job".
	 */
	JOBNAME(ServiceInterface.JOBNAME_KEY, "grisu_job"),
	/**
	 * The name of the application for this job. If you don't want to use mds for this job, specify the value of {@link ServiceInterface#GENERIC_APPLICATION_NAME} here. Defaults to null.
	 */
	APPLICATIONNAME(ServiceInterface.APPLICATIONNAME_KEY, ""),
	/**
	 * The version of the application for this job. If you want the Grisu backend to pick a version, specify the value of {@link ServiceInterface#NO_VERSION_INDICATOR_STRING} here. 
	 */
	APPLICATIONVERSION(ServiceInterface.APPLICATIONVERSION_KEY, ServiceInterface.NO_VERSION_INDICATOR_STRING),
	/**
	 * The numbers of cpus for this job. If you don't specify {@link #FORCE_SINGLE} or {@link #FORCE_MPI} here, the Grisu backend will auto-select a jobtype. Defaults to 1.
	 */
	NO_CPUS(ServiceInterface.NO_CPUS_KEY, "1"),
	/**
	 * Forces a "single" type job, even if you specify more than one cpus in {@link #NO_CPUS}. Defaults to false.
	 */
	FORCE_SINGLE(ServiceInterface.FORCE_SINGLE_KEY, "false"),
	/**
	 * Forces a "mpi" type job, even if you specify only one cpu in {@link #NO_CPUS}. Defaults to false
	 */
	FORCE_MPI(ServiceInterface.FORCE_MPI_KEY, "false"),
	/**
	 * The minimum amount of memory that this job needs. Defaults to 0.
	 */
	MEMORY_IN_B(ServiceInterface.MEMORY_IN_B_KEY, ""),
	/**
	 * The email address to use when specifying {@link #EMAIL_ON_START} or {@link #EMAIL_ON_FINISH}. 
	 */
	EMAIL_ADDRESS(ServiceInterface.EMAIL_ADDRESS_KEY, ""),
	/**
	 * Specifies whether you want an email sent to you after the job started on the cluster. Defaults to false. 
	 */
	EMAIL_ON_START(ServiceInterface.EMAIL_ON_START_KEY, "false"),
	/**
	 * Specifies whether you want an email sent to you after the job finished on the cluster. Defaults to false. 
	 */
	EMAIL_ON_FINISH(ServiceInterface.EMAIL_ON_FINISH_KEY, "false"),
	/**
	 * The walltime of your jobs in minutes. Defaults to 1440 (1 day).
	 */
	WALLTIME_IN_MINUTES(ServiceInterface.WALLTIME_IN_MINUTES_KEY, "1440"),
	/**
	 * The commandline to run on the cluster (e.g. "java -version"). The backend will try to figure out which {@link #APPLICATIONNAME} to set (if you didn't set one) according to the executable you specify here. 
	 */
	COMMANDLINE(ServiceInterface.COMMANDLINE_KEY, ""),
	/**
	 * The name of the stdout file. Defaults to stdout.txt.
	 */
	STDOUT(ServiceInterface.STDOUT_KEY, "stdout.txt"),
	/**
	 * The name of the stderr file. Defaults to stderr.txt. 
	 */
	STDERR(ServiceInterface.STDERR_KEY, "stderr.txt"),
	/**
	 * The name of the stdIn file. Defaults to null. 
	 */
	STDIN(ServiceInterface.STDIN_KEY, ""),
	/**
	 * The name of the submission location to submit this job to. Use the {@link GrisuRegistry} object and it's childs to find one. If you don't specify one, the backend will try to find the best one for you. efaults to null.
	 */
	SUBMISSIONLOCATION(ServiceInterface.SUBMISSIONLOCATION_KEY, ""),
	/**
	 * A comma seperated list of urls of input files. You can specify local or remote files here. Defaults to null. 
	 */
	INPUT_FILE_URLS(ServiceInterface.INPUT_FILE_URLS_KEY, ""),
	/**
	 * A comma-seperated list of modules you want to load. This should only be used in if you know what you are doing. Defaults to null.
	 */
	MODULES(ServiceInterface.MODULES_KEY, "");

	private static final Map<String, JobSubmissionProperty> stringToJobPropertyMap = new HashMap<String, JobSubmissionProperty>();
	static {
		for (JobSubmissionProperty jp : values() ) {
			stringToJobPropertyMap.put(jp.toString(), jp);
		}
	}
	
	private final String keyName;
	private final String defaultValue;
	
	JobSubmissionProperty(String keyName, String defaultValue) {
		this.keyName = keyName;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String toString() {
		return this.keyName;
	}
	
	public String defaultValue() {
		return this.defaultValue;
	}
	
	public static JobSubmissionProperty fromString(String key) {
		return stringToJobPropertyMap.get(key);
	}
}