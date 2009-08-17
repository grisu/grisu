package org.vpac.grisu.backend.model.job;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.CollectionOfElements;

import au.org.arcs.jcommons.constants.Constants;

@Entity
public class MultiPartJob {
	
	// for hibernate
	private Long id;
	
	static final Logger myLogger = Logger.getLogger(MultiPartJob.class.getName());
	
	// the user's dn
	private String dn = null;
	
	private Set<String> jobnames = new HashSet<String>();
	
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
	
	public void addJob(String jobname) {
		this.jobnames.add(jobname);
	}
	
	public void removeJob(String jobname) {
		this.jobnames.remove(jobname);
	}
	

	@CollectionOfElements(fetch = FetchType.EAGER)
	public Set<String> getJobnames() {
		return jobnames;
	}
	
	protected void setJobnames(Set<String> jobnames) {
		this.jobnames = jobnames;
	}
	
//	@Transient
//	public String[] findAllUsedMountPoints() {
//		
//		Set<String> result = new HashSet<String>();
//		for ( String jobname : jobnames ) {
//			result.add(jobname.getJobProperty(Constants.MOUNTPOINT_KEY));
//		}
//		return result.toArray(new String[]{});
//	}

}
