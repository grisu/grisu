package org.vpac.grisu.model.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.vpac.grisu.control.JobConstants;

import au.org.arcs.jcommons.constants.Constants;

@XmlRootElement(name="multiPartJob")
public class DtoMultiPartJob {
	
	private String multiPartJobId;
	
	private DtoJobs jobs = new DtoJobs();

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
	
	private DtoLogMessages messages = new DtoLogMessages();

	@XmlElement(name="logMessages")
	public DtoLogMessages getMessages() {
		return messages;
	}

	public void setMessages(DtoLogMessages messages) {
		this.messages = messages;
	}
	
	public Map<Date, String> messages() {
		Map<Date, String> result = new TreeMap<Date, String>();
		
		for ( DtoLogMessage m : getMessages().getMessages() ) {
			result.put(m.getDate(), m.getMessage());
		}
		return result;
	}

	/**
	 * The list of job properties.
	 */
	private List<DtoJobProperty> properties = new LinkedList<DtoJobProperty>();
//	/**
//	 * The status of the job. Be aware that, depending on how you queried for this job, this can be stale information.
//	 */
//	private int status;
	
	public DtoJobs failedJobs = new DtoJobs();
	
	@XmlElement(name="failedJobs")
	public DtoJobs getFailedJobs() {
		return failedJobs;
	}

	public void setFailedJobs(DtoJobs failedJobs) {
		this.failedJobs = failedJobs;
	}
	
	@XmlElement(name="jobproperty")
	public List<DtoJobProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<DtoJobProperty> properties) {
		this.properties = properties;
	}
	
	public DtoJob retrieveJob(String jobname) {
		return getJobs().retrieveJob(jobname);
	}
	
	public Map<String, String> propertiesAsMap() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		for ( DtoJobProperty prop : getProperties() ) {
			map.put(prop.getKey(), prop.getValue());
		}
		
		return map;
	}

	public String pathToInputFiles() {
		
		return propertiesAsMap().get(Constants.RELATIVE_PATH_FROM_JOBDIR);
		
	}
	
	
	@XmlElement(name="jobs")
	public DtoJobs getJobs() {
		return jobs;
	}

	public void setJobs(DtoJobs jobs) {
		this.jobs = jobs;
	}
	
	public synchronized void addJob(DtoJob job) {
		this.jobs.addJob(job);
	}
	
	public int totalNumberOfJobs() {
		return this.jobs.getAllJobs().size();
	}
	
	
	public boolean allJobsFinished() {
		
		if ( totalNumberOfJobs() == numberOfFinishedJobs() ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean allJobsFinishedSuccessful() {
		
		if ( allJobsFinished() && totalNumberOfJobs() == numberOfSuccessfulJobs() ) {
			return true;
		} else {
			return false;
		}
	}

	public int numberOfRunningJobs() {
		int running = 0;
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() == JobConstants.ACTIVE ) {
				running = running + 1;
			}
		}
		return running;
	}
	
	public int numberOfWaitingJobs() {
		int waiting = 0;
		for (DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() == JobConstants.PENDING ) {
				waiting = waiting + 1;
			}
		}
		return waiting;
	}
	
	public int numberOfFinishedJobs() {
		int finished = 0;
		for (DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() >= JobConstants.FINISHED_EITHER_WAY ) {
				finished = finished + 1;
			}
		}
		return finished;
	}
	
	public int numberOfFailedJobs() {
		int failed = 0;
		for (DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() >= JobConstants.FINISHED_EITHER_WAY && job.getStatus() != JobConstants.DONE ) {
				failed = failed + 1;
			}
		}
		return failed;
	}
	
	public int numberOfSuccessfulJobs() {
		int successful = 0;
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() == JobConstants.DONE ) {
				successful = successful + 1;
			}
		}
		return successful;
	}
	
	public int numberOfUnsubmittedJobs() {
		int unsubmitted = 0;
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() <= JobConstants.UNSUBMITTED ) {
				unsubmitted = unsubmitted + 1;
			}
		}
		return unsubmitted;
	}
	

}
