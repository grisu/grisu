package org.vpac.grisu.backend.model.job;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	
	static final Logger myLogger = Logger.getLogger(MultiPartJob.class.getName());
	
	// the user's dn
	private String dn = null;
	
	private Map<String, Integer> jobnames = new TreeMap<String, Integer>();
	
	private Map<String, String> failedJobs = new HashMap<String, String>();
	
	private Set<String> usedMountPoints = new HashSet<String>();
	
	private String multiPartJobId;
	
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
		this.jobnames.put(jobname, job.getStatus());
		if ( job.getStatus() >= JobConstants.FINISHED_EITHER_WAY && job.getStatus() != JobConstants.DONE ) {
			addFailedJob(jobname, "Job finished with status: "+JobConstants.translateStatus(job.getStatus()));
		}
		usedMountPoints.add(job.getJobProperty(Constants.MOUNTPOINT_KEY));
		job.addJobProperty(Constants.MULTIJOB_NAME, multiPartJobId);
		jobdao.saveOrUpdate(job);
	}
	
	public void removeJob(String jobname) {
		this.jobnames.remove(jobname);
	}
	

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Map<String, Integer> getJobnames() {
		return jobnames;
	}
	
	protected void setJobnames(Map<String, Integer> jobnames) {
		this.jobnames = jobnames;
	}
	
	@CollectionOfElements(fetch = FetchType.EAGER)
	public Map<String, String> getFailedJobs() {
		return failedJobs;
	}
	
	private void setFailedJobs(Map<String, String> failedJobs) {
		this.failedJobs = failedJobs;
	}
	
	public synchronized void addFailedJob(String job, String message) {
		if ( ! this.failedJobs.keySet().contains(job) ) {
			this.failedJobs.put(job, message);
		}
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

}
