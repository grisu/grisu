package org.vpac.grisu.model.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.vpac.grisu.control.JobConstants;

import au.org.arcs.jcommons.constants.Constants;

@XmlRootElement(name="multiPartJob")
public class DtoBatchJob {
	
	private String batchJobname;
	private String submissionFqan;
	
	private boolean isFinished = true;
	
	private DtoJobs jobs = new DtoJobs();

	public DtoBatchJob() {
	}
	
	public DtoBatchJob(String batchJobname) {
		this.batchJobname = batchJobname;
	}
	
	@XmlAttribute(name="finished")
	public boolean isFinished() {
		return this.isFinished;
	}
	
	public void setFinished(boolean f) {
		this.isFinished = f;
	}
	
//	@XmlAttribute(name="failed")
//	public boolean isFailed() {
//		return this.isFailed;
//	}
//	
//	public void setFailed(boolean f) {
//		this.isFailed = f;
//	}
	
	@XmlElement(name="fqan")
	public String getSubmissionFqan() {
		return this.submissionFqan;
	}
	
	public void setSubmissionFqan(String fqan) {
		this.submissionFqan = fqan;
	}

	@XmlElement(name="batchJobname")
	public String getbatchJobname() {
		return batchJobname;
	}

	public void setbatchJobname(String batchJobname) {
		this.batchJobname = batchJobname;
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
	
	private DtoJobs failedJobs = new DtoJobs();
	
	@XmlElement(name="failedJob")
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
		if ( job.getStatus() < JobConstants.FINISHED_EITHER_WAY ) {
			this.isFinished = false;
		}
	}
	
	public int totalNumberOfJobs() {
		return this.jobs.getAllJobs().size();
	}
	
	
//	public boolean allJobsFinished() {
//		
//		if ( totalNumberOfJobs() == numberOfFinishedJobs() ) {
//			return true;
//		} else {
//			return false;
//		}
//		
//	}
	
	public boolean allJobsFinishedSuccessful() {
		
		if ( ! isFinished() ) {
			return false;
		}
		if ( totalNumberOfJobs() == numberOfSuccessfulJobs() ) {
			return true;
		} else {
			return false;
		}
	}
	
	public SortedSet<DtoJob> runningJobs() {

		SortedSet<DtoJob> result = new TreeSet<DtoJob>();
		
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() == JobConstants.ACTIVE ) {
				result.add(job);
			}
		}
		
		return result;
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
	
	public SortedSet<DtoJob> waitingJobs() {

		SortedSet<DtoJob> result = new TreeSet<DtoJob>();
		
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() >= JobConstants.UNSUBMITTED && job.getStatus() <= JobConstants.PENDING ) {
				result.add(job);
			}
		}
		
		return result;
	}
	
	public int numberOfWaitingJobs() {
		int waiting = 0;
		for (DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() >= JobConstants.UNSUBMITTED && job.getStatus() <= JobConstants.PENDING ) {
				waiting = waiting + 1;
			}
		}
		return waiting;
	}
	
	public SortedSet<DtoJob> finishedJobs() {

		SortedSet<DtoJob> result = new TreeSet<DtoJob>();
		
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() >= JobConstants.FINISHED_EITHER_WAY ) {
				result.add(job);
			}
		}
		
		return result;
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
	
	public SortedSet<DtoJob> failedJobs() {

		SortedSet<DtoJob> result = new TreeSet<DtoJob>();
		
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() >= JobConstants.FINISHED_EITHER_WAY && job.getStatus() != JobConstants.DONE ) {
				result.add(job);
			}
		}
		
		return result;
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
	
	public SortedSet<DtoJob> successfulJobs() {

		SortedSet<DtoJob> result = new TreeSet<DtoJob>();
		
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() == JobConstants.DONE ) {
				result.add(job);
			}
		}
		
		return result;
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
	
	public SortedSet<DtoJob> unsubmittedJobs() {

		SortedSet<DtoJob> result = new TreeSet<DtoJob>();
		
		for ( DtoJob job : jobs.getAllJobs() ) {
			if ( job.getStatus() <= JobConstants.UNSUBMITTED ) {
				result.add(job);
			}
		}
		
		return result;
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
