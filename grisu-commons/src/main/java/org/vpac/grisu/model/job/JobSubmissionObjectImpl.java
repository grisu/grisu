package org.vpac.grisu.model.job;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.utils.SeveralStringHelpers;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.vpac.grisu.utils.SimpleJsdlBuilder;
import org.w3c.dom.Document;

import au.org.arcs.mds.Constants;
import au.org.arcs.mds.JobSubmissionProperty;
import au.org.arcs.mds.JsdlHelpers;

/**
 * A class that helps creating a job.
 * 
 * This class is extended by the JobObject class in the grisu-client-slim
 * package which includes methods to create the job on the serviceinterface,
 * submit and monitor/control it.
 * 
 * @author Markus Binsteiner
 */
@Entity
public class JobSubmissionObjectImpl {

	private Long id;

	@Id
	@GeneratedValue
	private Long getId() {
		return this.id;
	}

	private void setId(final Long id) {
		this.id = id;
	}

	public final String getJobname() {
		return jobname;
	}

	public final void setJobname(final String jobname) {
		this.jobname = jobname;
	}

	public final String getApplication() {
		return application;
	}

	public final void setApplication(final String app) {
		this.application = app;
	}

	public final String getApplicationVersion() {
		if (StringUtils.isBlank(applicationVersion)) {
			return Constants.NO_VERSION_INDICATOR_STRING;
		}
		return applicationVersion;
	}

	public final void setApplicationVersion(final String appVersion) {
		this.applicationVersion = appVersion;
	}

	public final String getEmail_address() {
		return email_address;
	}

	public final void setEmail_address(final String email_address) {
		this.email_address = email_address;
	}

	public final boolean isEmail_on_job_start() {
		return email_on_job_start;
	}

	public final void setEmail_on_job_start(final boolean email_on_job_start) {
		this.email_on_job_start = email_on_job_start;
	}

	public final boolean isEmail_on_job_finish() {
		return email_on_job_finish;
	}

	public final void setEmail_on_job_finish(final boolean email_on_job_finish) {
		this.email_on_job_finish = email_on_job_finish;
	}

	public final int getCpus() {
		return cpus;
	}

	public final void setCpus(final int cpus) {
		this.cpus = cpus;
	}

	public final boolean isForce_single() {
		return force_single;
	}

	public final void setForce_single(final boolean force_single) {
		this.force_single = force_single;
		this.force_mpi = !force_mpi;
	}

	public final boolean isForce_mpi() {
		return force_mpi;
	}

	public final void setForce_mpi(final boolean force_mpi) {
		this.force_mpi = force_mpi;
		this.force_single = !force_mpi;
	}

	public final long getMemory() {
		return memory_in_bytes;
	}

	public final void setMemory(final long memory) {
		this.memory_in_bytes = memory;
	}

	public final int getWalltimeInSeconds() {
		return walltime_in_seconds;
	}

	public final void setWalltimeInSeconds(final int walltime) {
		this.walltime_in_seconds = walltime;
	}

	public final String[] getInputFileUrls() {
		return inputFileUrls.toArray(new String[] {});
	}

	public final void setInputFileUrls(final String[] inputFileUrls) {
		if (inputFileUrls != null) {
			this.inputFileUrls = new HashSet<String>(Arrays
					.asList(inputFileUrls));
		} else {
			this.inputFileUrls = new HashSet<String>();
		}
	}

	public final void addInputFileUrl(final String url) {
		this.inputFileUrls.add(url);
	}

	public final void addModule(final String module) {
		this.modules.add(module);
	}

	@Transient
	public final String getInputFileUrlsAsString() {
		if (inputFileUrls != null && inputFileUrls.size() != 0) {
			return StringUtils.join(inputFileUrls, ",");
		} else {
			return new String();
		}
	}

	public final String[] getModules() {
		return modules.toArray(new String[] {});
	}

	public final void setModules(final String[] modules) {
		if (modules != null) {
			this.modules = new HashSet<String>(Arrays.asList(modules));
		} else {
			this.modules = new HashSet<String>();
		}
	}

	@Transient
	public final String getModulesAsString() {
		if (modules != null && modules.size() != 0) {
			return StringUtils.join(modules, ",");
		} else {
			return new String();
		}
	}

	public final String getSubmissionLocation() {
		return submissionLocation;
	}

	public final void setSubmissionLocation(final String submissionLocation) {
		this.submissionLocation = submissionLocation;
	}

	@Column(nullable = false)
	public final String getCommandline() {
		return commandline;
	}

	public final void setCommandline(final String commandline) {
		this.commandline = commandline;
	}

	public final String getStderr() {
		return stderr;
	}

	public final void setStderr(final String stderr) {
		this.stderr = stderr;
	}

	public final String getStdout() {
		return stdout;
	}

	public final void setStdout(final String stdout) {
		this.stdout = stdout;
	}

	public final void setStdin(final String stdin) {
		this.stdin = stdin;
	}

	public final String getStdin() {
		return this.stdin;
	}

	private String jobname;
	private String application;
	private String applicationVersion;
	private String email_address;
	private boolean email_on_job_start = false;
	private boolean email_on_job_finish = false;
	private int cpus = 1;
	private boolean force_single = false;
	private boolean force_mpi = false;
	private long memory_in_bytes = 0;;
	private int walltime_in_seconds = 300;
	private Set<String> inputFileUrls = new HashSet<String>();
	private Set<String> modules = new HashSet<String>();
	private String submissionLocation;
	private String commandline;
	private String stderr;
	private String stdout;
	private String stdin;

	public JobSubmissionObjectImpl() {
	}

	private boolean checkForBoolean(final String booleanString) {
		if (booleanString == null) {
			return false;
		}

		if ("true".equals(booleanString.toLowerCase())
				|| "on".equals(booleanString.toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}

	public JobSubmissionObjectImpl(final Map<String, String> jobProperties) {

		this.jobname = jobProperties.get(JobSubmissionProperty.JOBNAME
				.toString());
		this.application = jobProperties
				.get(JobSubmissionProperty.APPLICATIONNAME.toString());
		this.applicationVersion = jobProperties
				.get(JobSubmissionProperty.APPLICATIONVERSION.toString());
		this.email_address = jobProperties
				.get(JobSubmissionProperty.EMAIL_ADDRESS.toString());
		this.email_on_job_start = checkForBoolean(jobProperties
				.get(JobSubmissionProperty.EMAIL_ON_START.toString()));
		this.email_on_job_finish = checkForBoolean(jobProperties
				.get(JobSubmissionProperty.EMAIL_ON_FINISH.toString()));
		try {
			this.cpus = Integer.parseInt(jobProperties
					.get(JobSubmissionProperty.NO_CPUS.toString()));
		} catch (NumberFormatException e) {
			this.cpus = 1;
		}
		this.force_single = checkForBoolean(jobProperties
				.get(JobSubmissionProperty.FORCE_SINGLE.toString()));
		this.force_mpi = checkForBoolean(jobProperties
				.get(JobSubmissionProperty.FORCE_MPI.toString()));
		try {
			this.memory_in_bytes = Integer.parseInt(jobProperties
					.get(JobSubmissionProperty.MEMORY_IN_B.toString()));
		} catch (NumberFormatException e) {
			this.memory_in_bytes = 0;
		}
		try {
			this.walltime_in_seconds = Integer.parseInt(jobProperties
					.get(JobSubmissionProperty.WALLTIME_IN_MINUTES.toString()));
		} catch (NumberFormatException e) {
			this.walltime_in_seconds = 0;
		}

		String temp = jobProperties.get(JobSubmissionProperty.INPUT_FILE_URLS
				.toString());
		if (temp != null && temp.length() > 0) {
			setInputFileUrls(temp.split(","));
		}

		temp = jobProperties.get(JobSubmissionProperty.MODULES.toString());
		if (temp != null && temp.length() > 0) {
			setModules(temp.split(","));
		}

		this.submissionLocation = jobProperties
				.get(JobSubmissionProperty.SUBMISSIONLOCATION.toString());
		this.commandline = jobProperties.get(JobSubmissionProperty.COMMANDLINE
				.toString());
		this.stderr = jobProperties
				.get(JobSubmissionProperty.STDERR.toString());
		this.stdout = jobProperties
				.get(JobSubmissionProperty.STDOUT.toString());
		this.stdin = jobProperties.get(JobSubmissionProperty.STDIN.toString());

	}

	public JobSubmissionObjectImpl(final Document jsdl) {

		jobname = JsdlHelpers.getJobname(jsdl);
		application = JsdlHelpers.getApplicationName(jsdl);
		applicationVersion = JsdlHelpers.getApplicationVersion(jsdl);
		email_address = JsdlHelpers.getEmail(jsdl);
		email_on_job_start = JsdlHelpers.getSendEmailOnJobStart(jsdl);
		email_on_job_finish = JsdlHelpers.getSendEmailOnJobFinish(jsdl);
		cpus = JsdlHelpers.getProcessorCount(jsdl);
		String jobTypeString = JsdlHelpers.getArcsJobType(jsdl);
		if (jobTypeString != null) {
			if (jobTypeString.toLowerCase().equals(
					JobSubmissionProperty.FORCE_SINGLE.defaultValue())) {
				force_single = true;
				force_mpi = false;
			} else if (jobTypeString.toLowerCase().equals(
					JobSubmissionProperty.FORCE_SINGLE.defaultValue())) {
				force_single = false;
				force_mpi = true;
			} else {
				force_single = false;
				force_mpi = false;
			}
		} else {
			force_single = false;
			force_mpi = false;
		}
		memory_in_bytes = JsdlHelpers.getTotalMemoryRequirement(jsdl);
		walltime_in_seconds = JsdlHelpers.getWalltime(jsdl);
		setInputFileUrls(JsdlHelpers.getInputFileUrls(jsdl));
		setModules(JsdlHelpers.getModules(jsdl));
		String[] candidateHosts = JsdlHelpers.getCandidateHosts(jsdl);
		if (candidateHosts != null && candidateHosts.length > 0) {
			submissionLocation = candidateHosts[0];
		}
		String executable = JsdlHelpers.getPosixApplicationExecutable(jsdl);
		String[] arguments = JsdlHelpers.getPosixApplicationArguments(jsdl);
		StringBuffer tempBuffer = new StringBuffer(executable);
		if (arguments != null) {
			for (String arg : arguments) {
				tempBuffer.append(" " + arg);
			}
		}
		commandline = tempBuffer.toString();
		stderr = JsdlHelpers.getPosixStandardError(jsdl);
		stdout = JsdlHelpers.getPosixStandardOutput(jsdl);
		stdin = JsdlHelpers.getPosixStandardInput(jsdl);
	}

	@Transient
	public final Map<JobSubmissionProperty, String> getJobSubmissionPropertyMap() {

		Map<JobSubmissionProperty, String> jobProperties = new HashMap<JobSubmissionProperty, String>();
		jobProperties.put(JobSubmissionProperty.JOBNAME, jobname);
		jobProperties.put(JobSubmissionProperty.APPLICATIONNAME, application);
		jobProperties.put(JobSubmissionProperty.APPLICATIONVERSION,
				applicationVersion);
		jobProperties.put(JobSubmissionProperty.COMMANDLINE, commandline);
		jobProperties.put(JobSubmissionProperty.EMAIL_ADDRESS, email_address);
		if (email_on_job_start) {
			jobProperties.put(JobSubmissionProperty.EMAIL_ON_START, "true");
		} else {
			jobProperties.put(JobSubmissionProperty.EMAIL_ON_START, "false");
		}
		if (email_on_job_finish) {
			jobProperties.put(JobSubmissionProperty.EMAIL_ON_FINISH, "true");
		} else {
			jobProperties.put(JobSubmissionProperty.EMAIL_ON_FINISH, "false");
		}
		if (force_single) {
			jobProperties.put(JobSubmissionProperty.FORCE_SINGLE, "true");
			jobProperties.put(JobSubmissionProperty.FORCE_MPI, "false");
		} else if (force_mpi) {
			jobProperties.put(JobSubmissionProperty.FORCE_SINGLE, "false");
			jobProperties.put(JobSubmissionProperty.FORCE_MPI, "true");
		}
		jobProperties.put(JobSubmissionProperty.INPUT_FILE_URLS,
				getInputFileUrlsAsString());
		jobProperties.put(JobSubmissionProperty.MODULES, getModulesAsString());
		jobProperties.put(JobSubmissionProperty.MEMORY_IN_B, new Long(
				memory_in_bytes).toString());
		jobProperties.put(JobSubmissionProperty.NO_CPUS, new Integer(cpus)
				.toString());
		jobProperties.put(JobSubmissionProperty.STDERR, stderr);
		jobProperties.put(JobSubmissionProperty.STDOUT, stdout);
		jobProperties.put(JobSubmissionProperty.SUBMISSIONLOCATION,
				submissionLocation);
		jobProperties.put(JobSubmissionProperty.WALLTIME_IN_MINUTES,
				new Integer(walltime_in_seconds).toString());

		return jobProperties;
	}

	@Transient
	public final Map<String, String> getStringJobSubmissionPropertyMap() {

		Map<String, String> stringPropertyMap = new HashMap<String, String>();
		Map<JobSubmissionProperty, String> jobPropertyMap = getJobSubmissionPropertyMap();

		for (JobSubmissionProperty jp : jobPropertyMap.keySet()) {
			String value = jobPropertyMap.get(jp);
			stringPropertyMap.put(jp.toString(), value);

		}
		return stringPropertyMap;
	}

	@Transient
	public final Document getJobDescriptionDocument() throws JobPropertiesException {

		checkValidity();

		Map<JobSubmissionProperty, String> jobProperties = getJobSubmissionPropertyMap();

		Document jsdl = SimpleJsdlBuilder.buildJsdl(jobProperties);

		return jsdl;

	}
	
	@Transient
	protected final String getJobDescriptionDocumentAsString() throws JobPropertiesException {
		
		String jsdlString = null;
		jsdlString = SeveralXMLHelpers.toString(getJobDescriptionDocument());

		return jsdlString;
	}

	private void checkValidity() throws JobPropertiesException {

		if (commandline == null || commandline.length() == 0) {
			throw new JobPropertiesException(JobSubmissionProperty.COMMANDLINE,
					"Commandline not specified.");
		}

	}

	public static void main(final String[] args) throws JobPropertiesException {

		JobSubmissionObjectImpl jso = new JobSubmissionObjectImpl();

		jso.setJobname("testJobName");
		jso.setApplication("testApplication");
		jso.setApplicationVersion("testVersion");
		jso.setCommandline("java -testcommandline -argument2");
		jso.setCpus(1);
		jso.setWalltimeInSeconds(400);
		jso.setEmail_address("testEmailAddress");
		jso.setEmail_on_job_start(true);
		jso.setEmail_on_job_finish(true);
		jso.setForce_mpi(true);
		jso.setForce_single(false);
		jso.setInputFileUrls(new String[] { "file:///temp/test",
				"gsiftp://ng2.vpac.org/tmp/test" });
		jso.setMemory(0);

		jso.getJobDescriptionDocument();

	}

}
