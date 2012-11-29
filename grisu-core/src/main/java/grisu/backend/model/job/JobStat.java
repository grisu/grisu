package grisu.backend.model.job;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.simpleframework.xml.Root;

@Entity
@Table(name = "job_statistics")
public class JobStat {
	
	// for hibernate
	private Long id;
	
	private Long jobHibernateId;
	
	private String jobname;
	
	private String dn;
	
	private String fqan;
	
	private boolean active = true;
	
//	private int cpus;
//	private long memory;
//	private long virtMemory;
	
	
	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	// for hibernate
	private void setId(Long id) {
		this.id = id;
	}

	private int status = -1000;
	
	private String submissionType = null;
	
	private Map<String, String> properties = Collections
	.synchronizedMap(new HashMap<String, String>());
	
	private boolean isBatchJob = false;
	
	private Map<Long, String> logMessages = Collections
			.synchronizedMap(new TreeMap<Long, String>());
	
	public String getDn() {
		return dn;
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
	
	@Column(nullable = false)
	public Long getJobHibernateId() {
		return jobHibernateId;
	}

	public String getJobname() {
		return jobname;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	public Map<Long, String> getLogMessages() {
		return logMessages;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(length = 1000)
	public Map<String, String> getProperties() {
		return properties;
	}

	public int getStatus() {
		return status;
	}

	public String getSubmissionType() {
		return submissionType;
	}

	public boolean isBatchJob() {
		return isBatchJob;
	}

	public void setBatchJob(boolean isBatchJob) {
		this.isBatchJob = isBatchJob;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public void setFqan(String fqan) {
		this.fqan = fqan;
	}

	public void setJobHibernateId(Long jobHibernateId) {
		this.jobHibernateId = jobHibernateId;
	}

	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	public void setLogMessages(Map<Long, String> logMessages) {
		this.logMessages = logMessages;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setSubmissionType(String submissionType) {
		this.submissionType = submissionType;
	}
	

}
