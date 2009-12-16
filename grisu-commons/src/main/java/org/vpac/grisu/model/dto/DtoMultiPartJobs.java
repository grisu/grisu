package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "multiPartJobs")
public class DtoMultiPartJobs {

	/**
	 * The list of jobs.
	 */
	private List<DtoBatchJob> allJobs = new LinkedList<DtoBatchJob>();

	public DtoMultiPartJobs() {
	}

	public DtoMultiPartJobs(List<DtoBatchJob> allJobs) {
		this.allJobs = allJobs;
	}

	public void addJob(DtoBatchJob job) {
		this.allJobs.add(job);
	}

	@XmlElement(name = "multiPartJob")
	public List<DtoBatchJob> getAllJobs() {
		return allJobs;
	}

	public void setAllJobs(List<DtoBatchJob> allJobs) {
		this.allJobs = allJobs;
	}

}
