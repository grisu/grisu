package org.vpac.grisu.backend.model.job;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.CollectionOfElements;
import org.vpac.grisu.backend.hibernate.JobDAO;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobProperty;
import org.vpac.grisu.model.dto.DtoJobs;
import org.vpac.grisu.model.dto.DtoLogMessages;
import org.vpac.grisu.model.dto.DtoMultiPartJob;
import org.vpac.grisu.settings.ServerPropertiesManager;

import au.org.arcs.jcommons.constants.Constants;

@Entity
public class MultiPartJob {

	protected Job getJob(final String jobname) throws NoSuchJobException {

		Job job = jobdao.findJobByDN(getDn(), jobname);
		return job;
	}

	// for hibernate
	private Long id;

	protected JobDAO jobdao = new JobDAO();

	protected Set<String> inputFiles = new HashSet<String>();

	private Map<String, String> jobProperties = new HashMap<String, String>();

	static final Logger myLogger = Logger.getLogger(MultiPartJob.class
			.getName());

	// the user's dn
	private String dn = null;

	private Set<Job> jobs = new HashSet<Job>();

	private Set<String> failedJobs = new HashSet<String>();

	private Set<String> usedMountPoints = new HashSet<String>();

	private String multiPartJobId;

	private Map<Date, String> logMessages = new TreeMap<Date, String>();

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Map<Date, String> getLogMessages() {
		return logMessages;
	}

	private void setLogMessages(Map<Date, String> logMessages) {
		this.logMessages = logMessages;
	}

	public void addLogMessage(String message) {
		this.logMessages.put(new Date(), message);
	}

	public MultiPartJob(String dn, String multiPartJobId) {
		this.dn = dn;
		this.multiPartJobId = multiPartJobId;
	}

	// for hibernate
	private MultiPartJob() {

	}

	// hibernate
	@Id
	@GeneratedValue
	private Long getId() {
		return id;
	}

	// hibernate
	protected void setId(final Long id) {
		this.id = id;
	}

	/**
	 * The dn of the user who created/submits this job.
	 * 
	 * @return the dn
	 */
	@Column(nullable = false)
	public String getDn() {
		return dn;
	}

	/**
	 * Sets the dn of the user who submits this job. Should be only used by
	 * hibernate
	 * 
	 * @param dn
	 *            the dn
	 */
	protected void setDn(final String dn) {
		this.dn = dn;
	}

	public String getMultiPartJobId() {
		return multiPartJobId;
	}

	private void setMultiPartJobId(String id) {
		this.multiPartJobId = id;
	}

	public void addJob(String jobname) throws NoSuchJobException {
		Job job = getJob(jobname);
		this.jobs.add(job);
		// if ( job.getStatus() >= JobConstants.FINISHED_EITHER_WAY &&
		// job.getStatus() != JobConstants.DONE ) {
		// addFailedJob(job);
		// }
		usedMountPoints.add(job.getJobProperty(Constants.MOUNTPOINT_KEY));
		job.addJobProperty(Constants.MULTIJOB_NAME, multiPartJobId);
		jobdao.saveOrUpdate(job);
	}

	public void removeJob(Job job) {
		this.jobs.remove(job);
		this.failedJobs.remove(job);
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Set<Job> getJobs() {
		return jobs;
	}

	protected void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}

	// @Transient
	@CollectionOfElements(fetch = FetchType.EAGER)
	public Set<String> getFailedJobs() {
		return failedJobs;
	}

	private void setFailedJobs(Set<String> failedJobs) {
		this.failedJobs = failedJobs;
	}

	public synchronized void addFailedJob(String job) {
		getFailedJobs().add(job);
	}

	public synchronized void removeFailedJob(String jobname) {
		getFailedJobs().remove(jobname);
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Set<String> getAllUsedMountPoints() {
		return this.usedMountPoints;
	}

	// for hibernate
	private void setAllUsedMountPoints(Set<String> usedMountPoints) {
		this.usedMountPoints = usedMountPoints;
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Set<String> getInputFiles() {
		return this.inputFiles;
	}

	private void setInputFiles(Set<String> inputfiles) {
		this.inputFiles = inputFiles;
	}

	public void addInputFile(String inputFilename) {
		this.inputFiles.add(inputFilename);
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Map<String, String> getJobProperties() {
		return jobProperties;
	}

	private void setJobProperties(final Map<String, String> jobProperties) {
		this.jobProperties = jobProperties;
	}

	public void addJobProperty(final String key, final String value) {
		this.jobProperties.put(key, value);
	}

	public void addJobProperties(final Map<String, String> properties) {
		this.jobProperties.putAll(properties);
	}

	@Transient
	public String getJobProperty(final String key) {
		return this.jobProperties.get(key);
	}

	public DtoMultiPartJob createDtoMultiPartJob() throws NoSuchJobException {

		final DtoMultiPartJob result = new DtoMultiPartJob(this
				.getMultiPartJobId());
		result.setProperties(DtoJobProperty.dtoJobPropertiesFromMap(this
				.getJobProperties()));

		result.setMessages(DtoLogMessages.createLogMessages(this
				.getLogMessages()));

		for (final Job job : getJobs()) {

			DtoJob dtoJob = null;

			int oldStatus = job.getStatus();

			dtoJob = DtoJob.createJob(job.getStatus(), job.getJobProperties());
			result.addJob(dtoJob);
		}

		DtoJobs dtoJobs = new DtoJobs();
		for (String jobname : getFailedJobs()) {
			Job job = getJob(jobname);
			dtoJobs.addJob(DtoJob.createJob(job.getStatus(), job
					.getJobProperties()));
		}
		result.setFailedJobs(dtoJobs);

		return result;
	}

}
