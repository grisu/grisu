package org.vpac.grisu.model.dto;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="multiPartJob")
public class DtoMultiPartJob {
	
	private String multiPartJobId;
	
	private Set<String> jobnames = new TreeSet<String>();
	
	public DtoMultiPartJob() {
	}
	
	public DtoMultiPartJob(String multiPartJobId) {
		this.multiPartJobId = multiPartJobId;
	}

	@XmlElement(name="multiPartJobId")
	public String getMultiPartJobId() {
		return multiPartJobId;
	}

	public void setMultiPartJobId(String multiPartJobId) {
		this.multiPartJobId = multiPartJobId;
	}

	
	@XmlElement(name="jobname")
	public Set<String> getJobnames() {
		return jobnames;
	}

	public void setJobnames(Set<String> jobnames) {
		this.jobnames = jobnames;
	}
	
	
	

}
