package grisu.model.dto;

import grisu.jcommons.constants.Constants;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * A wrapper object that holds a list of {@link DtoJob} objects.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "jobs")
public class DtoJobs {

	/**
	 * The list of jobs.
	 */
	private SortedSet<DtoJob> allJobs = Collections
			.synchronizedSortedSet(new TreeSet<DtoJob>());

	public void addJob(DtoJob job) {
		if (job == null) {
			// TODO what to do here?
		} else {
			this.allJobs.add(job);
		}
	}

	@XmlElement(name = "job")
	public SortedSet<DtoJob> getAllJobs() {
		return allJobs;
	}

	public DtoJob retrieveJob(String jobname) {
		for (final DtoJob job : allJobs) {
			if (jobname
					.equals(job.propertiesAsMap().get(Constants.JOBNAME_KEY))) {
				return job;
			}
		}
		return null;
	}

	public void setAllJobs(SortedSet<DtoJob> allJobs) {
		if (allJobs == null) {
			throw new RuntimeException();
		}
		this.allJobs = allJobs;
	}

}
