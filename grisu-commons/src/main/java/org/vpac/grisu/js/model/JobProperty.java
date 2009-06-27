package org.vpac.grisu.js.model;

import java.util.HashMap;
import java.util.Map;

public enum JobProperty {
	
	JOBNAME("jobname", "grisu_default_job_name"),
	APPLICATIONNAME("application", ""),
	APPLICATIONVERSION("version", ""),
	NO_CPUS("cpus", "1"),
	FORCE_SINGLE("force_single", "false"),
	FORCE_MPI("force_mpi", "false"),
	MEMORY_IN_B("memory", ""),
	EMAIL_ADDRESS("email_address", ""),
	EMAIL_ON_START("email_on_start", "false"),
	EMAIL_ON_FINISH("email_on_finish", "false"),
	WALLTIME_IN_MINUTES("walltime", "600"),
	COMMANDLINE("commandline", ""),
	STDOUT("stdout", "stdout.txt"),
	STDERR("stderr", "stderr.txt"),
	STDIN("stdin", ""),
	SUBMISSIONLOCATION("submissionlocation", ""),
	INPUT_FILE_URLS("input_files", "");

	private static final Map<String, JobProperty> stringToJobPropertyMap = new HashMap<String, JobProperty>();
	static {
		for (JobProperty jp : values() ) {
			stringToJobPropertyMap.put(jp.toString(), jp);
		}
	}
	
	private final String keyName;
	private final String defaultValue;
	
	JobProperty(String keyName, String defaultValue) {
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
	
	public static JobProperty fromString(String key) {
		return stringToJobPropertyMap.get(key);
	}
}
