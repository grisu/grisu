package org.vpac.grisu.js.model;

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

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.js.control.SimpleJsdlBuilder;
import org.vpac.grisu.js.model.utils.JsdlHelpers;
import org.w3c.dom.Document;

@Entity
public class JobSubmissionObjectImpl {
	
	private Long id;
	
	@Id
	@GeneratedValue
	private Long getId() {
		return this.id;
	}
	private void setId(Long id) {
		this.id = id;
	}

	public String getJobname() {
		return jobname;
	}

	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public String getEmail_address() {
		return email_address;
	}

	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

	public boolean isEmail_on_job_start() {
		return email_on_job_start;
	}

	public void setEmail_on_job_start(boolean email_on_job_start) {
		this.email_on_job_start = email_on_job_start;
	}

	public boolean isEmail_on_job_finish() {
		return email_on_job_finish;
	}

	public void setEmail_on_job_finish(boolean email_on_job_finish) {
		this.email_on_job_finish = email_on_job_finish;
	}

	public int getCpus() {
		return cpus;
	}

	public void setCpus(int cpus) {
		this.cpus = cpus;
	}

	public boolean isForce_single() {
		return force_single;
	}

	public void setForce_single(boolean force_single) {
		this.force_single = force_single;
		this.force_mpi = !force_mpi;
	}

	public boolean isForce_mpi() {
		return force_mpi;
	}

	public void setForce_mpi(boolean force_mpi) {
		this.force_mpi = force_mpi;
		this.force_single = !force_mpi;
	}

	public long getMemory() {
		return memory_in_bytes;
	}

	public void setMemory(long memory) {
		this.memory_in_bytes = memory;
	}

	public int getWalltimeInSeconds() {
		return walltime_in_seconds;
	}

	public void setWalltimeInSeconds(int walltime) {
		this.walltime_in_seconds = walltime;
	}

	public String[] getInputFileUrls() {
		return inputFileUrls.toArray(new String[]{});
	}

	public void setInputFileUrls(String[] inputFileUrls) {
		if ( inputFileUrls != null ) {
			this.inputFileUrls = new HashSet<String>(Arrays.asList(inputFileUrls));
		} else {
			this.inputFileUrls = new HashSet<String>();
		}
	}
	
	public void addInputFileUrl(String url) {
		this.inputFileUrls.add(url);
	}
	
	public void addModule(String module) {
		this.modules.add(module);
	}

	@Transient
	public String getInputFileUrlsAsString() {
		if (inputFileUrls != null && inputFileUrls.size() != 0) {
			return StringUtils.join(inputFileUrls, ",");
		} else {
			return new String();
		}
	}
	
	public String[] getModules() {
		return modules.toArray(new String[]{});
	}
	
	public void setModules(String[] modules) {
		if ( modules != null ) {
			this.modules = new HashSet<String>(Arrays.asList(modules));
		} else {
			this.modules = new HashSet<String>();
		}
	}
	@Transient
	public String getModulesAsString() {
		if ( modules != null && modules.size() != 0 ) {
			return StringUtils.join(modules, ",");
		} else {
			return new String();
		}
	}

	public String getSubmissionLocation() {
		return submissionLocation;
	}

	public void setSubmissionLocation(String submissionLocation) {
		this.submissionLocation = submissionLocation;
	}

	@Column(nullable=false)
	public String getCommandline() {
		return commandline;
	}

	public void setCommandline(String commandline) {
		this.commandline = commandline;
	}

	public String getStderr() {
		return stderr;
	}

	public void setStderr(String stderr) {
		this.stderr = stderr;
	}

	public String getStdout() {
		return stdout;
	}

	public void setStdout(String stdout) {
		this.stdout = stdout;
	}

	public void setStdin(String stdin) {
		this.stdin = stdin;
	}

	public String getStdin() {
		return this.stdin;
	}

	protected String jobname;
	private String application;
	private String applicationVersion;
	private String email_address;
	private boolean email_on_job_start = false;
	private boolean email_on_job_finish = false;
	private int cpus = 1;
	private boolean force_single = false;
	private boolean force_mpi = false;
	private long memory_in_bytes = 0;;
	private int walltime_in_seconds = 0;
	private Set<String> inputFileUrls = new HashSet<String>();
	private Set<String> modules = new HashSet<String>();
	private String submissionLocation;
	private String commandline;
	private String stderr;
	private String stdout;
	private String stdin;


	public JobSubmissionObjectImpl() {
	}
	
	private boolean checkForBoolean(String booleanString) {
		if ( booleanString == null ) {
			return false;
		}

	
		if ( "true".equals(booleanString.toLowerCase()) || "on".equals(booleanString.toLowerCase()) ) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public JobSubmissionObjectImpl(Map<String, String> jobProperties) {
		
		this.jobname = jobProperties.get(JobProperty.JOBNAME.toString());
		this.application = jobProperties.get(JobProperty.APPLICATIONNAME.toString());
		this.applicationVersion = jobProperties.get(JobProperty.APPLICATIONVERSION.toString());
		this.email_address = jobProperties.get(JobProperty.EMAIL_ADDRESS.toString());
		this.email_on_job_start = checkForBoolean(jobProperties.get(JobProperty.EMAIL_ON_START.toString()));
		this.email_on_job_finish = checkForBoolean(jobProperties.get(JobProperty.EMAIL_ON_FINISH.toString()));
		try {
			this.cpus = Integer.parseInt(jobProperties.get(JobProperty.NO_CPUS.toString()));
		} catch (NumberFormatException e) {
			this.cpus = 1;
		}
		this.force_single = checkForBoolean(jobProperties.get(JobProperty.FORCE_SINGLE.toString()));
		this.force_mpi = checkForBoolean(jobProperties.get(JobProperty.FORCE_MPI.toString()));
		try {
			this.memory_in_bytes = Integer.parseInt(jobProperties.get(JobProperty.MEMORY_IN_B.toString()));
		} catch (NumberFormatException e) {
			this.memory_in_bytes = 0;
		}
		try {
			this.walltime_in_seconds = Integer.parseInt(jobProperties.get(JobProperty.WALLTIME_IN_MINUTES.toString()));
		} catch (NumberFormatException e) {
			this.walltime_in_seconds = 0;
		}
		
		String temp = jobProperties.get(JobProperty.INPUT_FILE_URLS.toString());
		if ( temp != null && temp.length() > 0 ) {
			setInputFileUrls(temp.split(","));
		}
		
		temp = jobProperties.get(JobProperty.MODULES.toString());
		if ( temp != null && temp.length() > 0 ) {
			setModules(temp.split(","));
		}
		
		this.submissionLocation = jobProperties.get(JobProperty.SUBMISSIONLOCATION.toString());
		this.commandline = jobProperties.get(JobProperty.COMMANDLINE.toString());
		this.stderr = jobProperties.get(JobProperty.STDERR.toString());
		this.stdout = jobProperties.get(JobProperty.STDOUT.toString());
		this.stdin = jobProperties.get(JobProperty.STDIN.toString());
		
	}
	
	
	public JobSubmissionObjectImpl(Document jsdl) {

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
					JobProperty.FORCE_SINGLE.defaultValue())) {
				force_single = true;
				force_mpi = false;
			} else if (jobTypeString.toLowerCase().equals(
					JobProperty.FORCE_SINGLE.defaultValue())) {
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
	public Map<JobProperty, String> getJobPropertyMap() {

		Map<JobProperty, String> jobProperties = new HashMap<JobProperty, String>();
		jobProperties.put(JobProperty.JOBNAME, jobname);
		jobProperties.put(JobProperty.APPLICATIONNAME, application);
		jobProperties.put(JobProperty.APPLICATIONVERSION, applicationVersion);
		jobProperties.put(JobProperty.COMMANDLINE, commandline);
		jobProperties.put(JobProperty.EMAIL_ADDRESS, email_address);
		if (email_on_job_start) {
			jobProperties.put(JobProperty.EMAIL_ON_START, "true");
		} else {
			jobProperties.put(JobProperty.EMAIL_ON_START, "false");
		}
		if (email_on_job_finish) {
			jobProperties.put(JobProperty.EMAIL_ON_FINISH, "true");
		} else {
			jobProperties.put(JobProperty.EMAIL_ON_FINISH, "false");
		}
		if (force_single) {
			jobProperties.put(JobProperty.FORCE_SINGLE, "true");
			jobProperties.put(JobProperty.FORCE_MPI, "false");
		} else if (force_mpi) {
			jobProperties.put(JobProperty.FORCE_SINGLE, "false");
			jobProperties.put(JobProperty.FORCE_MPI, "true");
		}
		jobProperties.put(JobProperty.INPUT_FILE_URLS,
				getInputFileUrlsAsString());
		jobProperties.put(JobProperty.MODULES, getModulesAsString());
		jobProperties.put(JobProperty.MEMORY_IN_B, new Long(memory_in_bytes)
				.toString());
		jobProperties.put(JobProperty.NO_CPUS, new Integer(cpus).toString());
		jobProperties.put(JobProperty.STDERR, stderr);
		jobProperties.put(JobProperty.STDOUT, stdout);
		jobProperties.put(JobProperty.SUBMISSIONLOCATION, submissionLocation);
		jobProperties.put(JobProperty.WALLTIME_IN_MINUTES,
				new Integer(walltime_in_seconds).toString());

		return jobProperties;
	}
	
	@Transient
	public Map<String, String> getStringJobPropertyMap() {

		Map<String, String> stringPropertyMap = new HashMap<String, String>();
		Map<JobProperty, String> jobPropertyMap = getJobPropertyMap();
		
		for ( JobProperty jp : jobPropertyMap.keySet() ) {
			String value = jobPropertyMap.get(jp);
			stringPropertyMap.put(jp.toString(), value);
			
		}
		return stringPropertyMap;
	}


	@Transient
	public Document getJobDescriptionDocument() throws JobPropertiesException {
		
		checkValidity();

		Map<JobProperty, String> jobProperties = getJobPropertyMap();

		Document jsdl = SimpleJsdlBuilder.buildJsdl(jobProperties);

		return jsdl;

	}
	
	private void checkValidity() throws JobPropertiesException {
		
		if ( commandline == null || commandline.length() == 0 ) {
			throw new JobPropertiesException(JobProperty.COMMANDLINE, "Commandline not specified.");
		}
		
	}

	public static void main(String[] args) throws JobPropertiesException {

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
