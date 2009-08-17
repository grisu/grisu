package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="multiPartJobs")
public class DtoMultiPartJobs {
	
	/**
	 * The list of jobs.
	 */
	private List<DtoMultiPartJob> allJobs = new LinkedList<DtoMultiPartJob>();
	
	public DtoMultiPartJobs(List<DtoMultiPartJob> allJobs) {
		this.allJobs = allJobs;
	}
	
	public DtoMultiPartJobs() {
	}

	@XmlElement(name="multiPartJob")
	public List<DtoMultiPartJob> getAllJobs() {
		return allJobs;
	}

	public void setAllJobs(List<DtoMultiPartJob> allJobs) {
		this.allJobs = allJobs;
	}
	
	public void addJob(DtoMultiPartJob job) {
		this.allJobs.add(job);
	}

}
