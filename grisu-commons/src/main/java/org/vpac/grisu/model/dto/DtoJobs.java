package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper object that holds a list of {@link DtoJob} objects.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="jobs")
public class DtoJobs {
	
	/**
	 * The list of jobs.
	 */
	private List<DtoJob> allJobs = new LinkedList<DtoJob>();

	@XmlElement(name="job")
	public List<DtoJob> getAllJobs() {
		return allJobs;
	}

	public void setAllJobs(List<DtoJob> allJobs) {
		this.allJobs = allJobs;
	}
	
	public void addJob(DtoJob job) {
		this.allJobs.add(job);
	}
	

}
