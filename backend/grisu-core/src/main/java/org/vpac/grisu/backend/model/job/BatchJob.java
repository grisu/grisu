package org.vpac.grisu.backend.model.job;

import java.util.Collections;
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
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.model.dto.DtoBatchJob;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobProperty;
import org.vpac.grisu.model.dto.DtoJobs;
import org.vpac.grisu.model.dto.DtoLogMessages;
import org.vpac.grisu.settings.ServerPropertiesManager;

import au.org.arcs.jcommons.constants.Constants;

@Entity
public class BatchJob {

	protected Job getJob(final String jobname) throws NoSuchJobException {

		Job job = jobdao.findJobByDN(getDn(), jobname);
		return job;
	}

	// for hibernate
	private Long id;

	protected JobDAO jobdao = new JobDAO();

	protected Set<String> inputFiles = new HashSet<String>();

	private Map<String, String> jobProperties = new HashMap<String, String>();

	static final Logger myLogger = Logger.getLogger(BatchJob.class
			.getName());

	// the user's dn
	private String dn = null;

	private Set<Job> jobs = new HashSet<Job>();

	private Set<String> failedJobs = new HashSet<String>();

	private Set<String> usedMountPoints = new HashSet<String>();

	private String batchJobname;
	
	private String fqan = null;


	private Map<Long, String> logMessages = Collections
			.synchronizedMap(new TreeMap<Long, String>());

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Map<Long, String> getLogMessages() {
		return logMessages;
	}

	private void setLogMessages(Map<Long, String> logMessages) {
		this.logMessages = logMessages;
	}

	public synchronized void addLogMessage(String message) {
		Long now = new Date().getTime();
		//
		while (this.logMessages.containsKey(now)) {
			now = now + 1;
		}
		this.logMessages.put(now, message);
		// System.out.println("NOW: "+now.toString()+"   "+now);
		// this.logMessages.put(UUID.randomUUID().toString(), message);

	}

	public BatchJob(String dn, String batchJobname, String fqan) {
		this.dn = dn;
		this.batchJobname = batchJobname;
		this.fqan = fqan;
	}
	
	public String getFqan() {
		return fqan;
	}

	private void setFqan(String fqan) {
		this.fqan = fqan;
	}
	// for hibernate
	private BatchJob() {

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

	public String getBatchJobname() {
		return batchJobname;
	}

	private void setBatchJobname(String id) {
		this.batchJobname = id;
	}

	public void addJob(String jobname) throws NoSuchJobException {
		Job job = getJob(jobname);
		this.jobs.add(job);
		// if ( job.getStatus() >= JobConstants.FINISHED_EITHER_WAY &&
		// job.getStatus() != JobConstants.DONE ) {
		// addFailedJob(job);
		// }
		usedMountPoints.add(job.getJobProperty(Constants.MOUNTPOINT_KEY));
		job.addJobProperty(Constants.BATCHJOB_NAME, batchJobname);
		job.setBatchJob(true);
		jobdao.saveOrUpdate(job);
	}

	public synchronized void removeJob(Job job) {
		this.jobs.remove(job);
		this.failedJobs.remove(job);
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public synchronized Set<Job> getJobs() {
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
	
	public void recalculateAllUsedMountPoints() {
		
		this.usedMountPoints.clear();
		for ( Job job : getJobs() ) {
			usedMountPoints.add(job.getJobProperty(Constants.MOUNTPOINT_KEY));
		}
		
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

	public DtoBatchJob createDtoMultiPartJob() throws NoSuchJobException {

		final DtoBatchJob result = new DtoBatchJob(this
				.getBatchJobname());
		
	
		result.setSubmissionFqan(this.getFqan());
		result.setProperties(DtoJobProperty.dtoJobPropertiesFromMap(this
				.getJobProperties()));

		result.setMessages(DtoLogMessages.createLogMessages(this
				.getLogMessages()));

		ExecutorService executor = Executors
				.newFixedThreadPool(ServerPropertiesManager
						.getConcurrentJobStatusThreadsPerUser());

		for (final Job job : getJobs()) {

			Thread thread = new Thread() {
				public void run() {

					DtoJob dtoJob = null;

					dtoJob = DtoJob.createJob(job.getStatus(), job
							.getJobProperties(), job.getLogMessages());
					result.addJob(dtoJob);
				}
			};
			executor.execute(thread);
		}

		executor.shutdown();
		try {
			executor.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		boolean finished = true;
		DtoJobs dtoJobs = new DtoJobs();
		for (String jobname : getFailedJobs()) {
			DtoJob job = result.retrieveJob(jobname);
			dtoJobs.addJob(job);
		}
		result.setFailedJobs(dtoJobs);

		return result;
	}
	


}
