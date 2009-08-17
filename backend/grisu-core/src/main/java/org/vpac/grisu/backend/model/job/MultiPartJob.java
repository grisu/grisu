package org.vpac.grisu.backend.model.job;

import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.log4j.Logger;
import org.hibernate.annotations.CollectionOfElements;

@Entity
public class MultiPartJob {
	
	// for hibernate
	private Long id;
	
	static final Logger myLogger = Logger.getLogger(Job.class.getName());
	
	// the user's dn
	private String dn = null;
	
	private Map<String, Job> jobs = new TreeMap<String, Job>();
	
	private String multiPartJobId;
	
	public MultiPartJob(String dn, String multiPartJobId) {
		this.dn = dn;
		this.multiPartJobId = multiPartJobId;
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
	
	public void addJob(Job job) {
		this.jobs.put(job.getJobname(), job);
	}
	
	public void removeJob(Job job) {
		this.jobs.remove(job.getJobname());
	}
	
	public void removeJob(String jobname) {
		this.jobs.remove(jobname);
	}
	
	public Map<String, Job> getJobs() {
		return jobs;
	}
	
	@CollectionOfElements(fetch = FetchType.EAGER)
	protected void setJobs(Map<String, Job> jobs) {
		this.jobs = jobs;
	}

}
