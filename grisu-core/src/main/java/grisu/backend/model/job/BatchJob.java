package grisu.backend.model.job;

import grisu.backend.hibernate.JobDAO;
import grisu.control.JobConstants;
import grisu.control.exceptions.NoSuchJobException;
import grisu.jcommons.constants.Constants;
import grisu.model.dto.DtoBatchJob;
import grisu.model.dto.DtoJob;
import grisu.model.dto.DtoJobs;
import grisu.model.dto.DtoLogMessages;
import grisu.model.dto.DtoProperty;
import grisu.settings.ServerPropertiesManager;

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

import org.hibernate.annotations.CollectionOfElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class BatchJob {

	// for hibernate
	private Long id;

	// the status of the job
	private int status = -1000;

	protected JobDAO jobdao = new JobDAO();

	protected Set<String> inputFiles = new HashSet<String>();

	private Map<String, String> jobProperties = new HashMap<String, String>();

	static final Logger myLogger = LoggerFactory.getLogger(BatchJob.class
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

	// for hibernate
	private BatchJob() {

	}

	public BatchJob(String dn, String batchJobname, String fqan) {
		this.dn = dn;
		this.batchJobname = batchJobname;
		this.fqan = fqan;
	}

	public synchronized void addFailedJob(String job) {
		getFailedJobs().add(job);
	}

	public void addInputFile(String inputFilename) {
		this.inputFiles.add(inputFilename);
	}

	public void addJob(String jobname) throws NoSuchJobException {
		final Job job = getJob(jobname);
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

	public void addJobProperties(final Map<String, String> properties) {
		this.jobProperties.putAll(properties);
	}

	public void addJobProperty(final String key, final String value) {
		this.jobProperties.put(key, value);
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

	public DtoBatchJob createDtoMultiPartJob() throws NoSuchJobException {

		final DtoBatchJob result = new DtoBatchJob(this.getBatchJobname());

		result.setSubmissionFqan(this.getFqan());
		result.setProperties(DtoProperty.dtoPropertiesFromMap(this
				.getJobProperties()));

		result.setMessages(DtoLogMessages.createLogMessages(this
				.getLogMessages()));

		final ExecutorService executor = Executors
				.newFixedThreadPool(ServerPropertiesManager
						.getConcurrentJobStatusThreadsPerUser());

		for (final Job job : getJobs()) {

			final Thread thread = new Thread() {
				@Override
				public void run() {

					DtoJob dtoJob = null;

					dtoJob = DtoJob.createJob(job.getStatus(),
							job.getJobProperties(), job.getInputFiles(),
							job.getLogMessages(), job.isArchived());
					result.addJob(dtoJob);
				}
			};
			executor.execute(thread);
		}

		executor.shutdown();
		try {
			executor.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}

		// boolean finished = true;
		final DtoJobs dtoJobs = new DtoJobs();
		for (final String jobname : getFailedJobs()) {
			final DtoJob job = result.retrieveJob(jobname);
			dtoJobs.addJob(job);
		}
		result.setFailedJobs(dtoJobs);

		// result.setStatus(this.getStatus());
		final int s = result.getPercentFinished().intValue();
		if (s >= 100) {
			if (dtoJobs.getAllJobs().size() > 0) {
				result.setStatus(JobConstants.FAILED);
			} else {
				result.setStatus(JobConstants.DONE);
			}
		} else {
			result.setStatus(s);
		}

		return result;
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Set<String> getAllUsedMountPoints() {
		return this.usedMountPoints;
	}

	public String getBatchJobname() {
		return batchJobname;
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

	// @Transient
	@CollectionOfElements(fetch = FetchType.EAGER)
	public Set<String> getFailedJobs() {
		return failedJobs;
	}

	public String getFqan() {
		return fqan;
	}

	// hibernate
	@Id
	@GeneratedValue
	private Long getId() {
		return id;
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Set<String> getInputFiles() {
		return this.inputFiles;
	}

	protected Job getJob(final String jobname) throws NoSuchJobException {

		final Job job = jobdao.findJobByDN(getDn(), jobname);
		return job;
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Map<String, String> getJobProperties() {
		return jobProperties;
	}

	@Transient
	public String getJobProperty(final String key) {
		return this.jobProperties.get(key);
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public synchronized Set<Job> getJobs() {
		return jobs;
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Map<Long, String> getLogMessages() {
		return logMessages;
	}

	/**
	 * Gets the status of the job. This does not ask the responsible
	 * {@link JobSubmitter} about the status but the database. So take care to
	 * refresh the job status before using this.
	 * 
	 * @return the status of the job
	 */
	@Column(nullable = false)
	public int getStatus() {
		return status;
	}

	public void recalculateAllUsedMountPoints() {

		this.usedMountPoints.clear();
		for (final Job job : getJobs()) {
			usedMountPoints.add(job.getJobProperty(Constants.MOUNTPOINT_KEY));
		}

	}

	public synchronized void removeFailedJob(String jobname) {
		getFailedJobs().remove(jobname);
	}

	public synchronized void removeJob(Job job) {
		this.jobs.remove(job);
		this.failedJobs.remove(job);
	}

	// for hibernate
	private void setAllUsedMountPoints(Set<String> usedMountPoints) {
		this.usedMountPoints = usedMountPoints;
	}

	private void setBatchJobname(String id) {
		this.batchJobname = id;
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

	private void setFailedJobs(Set<String> failedJobs) {
		this.failedJobs = failedJobs;
	}

	private void setFqan(String fqan) {
		this.fqan = fqan;
	}

	// hibernate
	protected void setId(final Long id) {
		this.id = id;
	}

	private void setInputFiles(Set<String> inputfiles) {
		this.inputFiles = inputFiles;
	}

	private void setJobProperties(final Map<String, String> jobProperties) {
		this.jobProperties = jobProperties;
	}

	protected void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}

	private void setLogMessages(Map<Long, String> logMessages) {
		this.logMessages = logMessages;
	}

	/**
	 * Sets the current status of this job. Only a {@link JobSubmitter} should
	 * use this method.
	 * 
	 * @param status
	 */
	public void setStatus(final int status) {
		this.status = status;
	}

}
