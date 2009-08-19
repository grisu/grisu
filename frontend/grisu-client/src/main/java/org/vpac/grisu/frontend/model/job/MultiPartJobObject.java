package org.vpac.grisu.frontend.model.job;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.clientexceptions.FileTransferException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.MultiPartJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoMultiPartJob;

import au.org.arcs.jcommons.constants.Constants;

public class MultiPartJobObject {
	
	static final Logger myLogger = Logger
	.getLogger(MultiPartJobObject.class.getName());
	
	
	public static final int DEFAULT_JOB_CREATION_THREADS = 5;
	
	private int concurrentJobCreationThreads = 0;
	
	private final ServiceInterface serviceInterface;
	
	private final String multiPartJobId;
	private String submissionFqan;
	
	private List<JobObject> jobs = new LinkedList<JobObject>();
	
	private Set<String> inputFiles = new HashSet<String>();
	
	private DtoMultiPartJob dtoMultiPartJob = null;
	
	public MultiPartJobObject(ServiceInterface serviceInterface, String multiPartJobId, String submissionFqan) throws MultiPartJobException {
		this.serviceInterface = serviceInterface;
		this.multiPartJobId = multiPartJobId;
		this.submissionFqan = submissionFqan;
		
		dtoMultiPartJob = serviceInterface.createMultiPartJob(multiPartJobId);
	}
	
	public MultiPartJobObject(ServiceInterface serviceInterface, String multiPartJobId) throws NoSuchJobException {
		this.serviceInterface = serviceInterface;
		this.multiPartJobId = multiPartJobId;
		
		dtoMultiPartJob = serviceInterface.getMultiPartJob(multiPartJobId, true);
		
		for ( DtoJob dtoJob : dtoMultiPartJob.getJobs().getAllJobs() ) {
			JobObject job = new JobObject(serviceInterface, dtoJob.propertiesAsMap().get(Constants.JOBNAME_KEY));
			jobs.add(job);
		}
	}
	
	public void monitorProgress() throws NoSuchJobException {
		boolean allJobsFinished = false;
		do {
			Date start = new Date();
			dtoMultiPartJob = serviceInterface.getMultiPartJob(multiPartJobId, true);
			Date end = new Date();
			
			System.out.println("Time to get all job status: "+(end.getTime()-start.getTime())/1000+" seconds.");
			allJobsFinished = dtoMultiPartJob.allJobsFinished();
			
			System.out.println("Total number of jobs: "+dtoMultiPartJob.totalNumberOfJobs());
			System.out.println("Waiting jobs: "+dtoMultiPartJob.numberOfWaitingJobs());
			System.out.println("Active jobs: "+dtoMultiPartJob.numberOfRunningJobs());
			System.out.println("Successful jobs: "+dtoMultiPartJob.numberOfSuccessfulJobs());
			System.out.println("Failed jobs: "+dtoMultiPartJob.numberOfFailedJobs()+" ");
			if ( dtoMultiPartJob.numberOfFailedJobs() > 0 ) {
				for ( String jobname : dtoMultiPartJob.getFailedJobs() ) {
					System.out.println("\tJobname: "+jobname+", Error: "+dtoMultiPartJob.retrieveJob(jobname).propertiesAsMap().get(Constants.ERROR_REASON));	
				}
				
			} else {
				System.out.println();
			}
			System.out.println("Unsubmitted jobs: "+dtoMultiPartJob.numberOfUnsubmittedJobs());

			System.out.println();
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while ( ! allJobsFinished );
	}
	
	public String getFqan() {
		return submissionFqan;
	}
	
	public Map<String, String> getProperties() {
		return dtoMultiPartJob.propertiesAsMap();
	}
	
	public String pathToInputFiles() {
		return dtoMultiPartJob.pathToInputFiles();
	}
	
	public void addJob(JobObject job) {
		
		if ( jobs.contains(job) ) {
			throw new IllegalArgumentException("Job: "+job.getJobname()+" already part of this multiPartJob.");
		}
		this.jobs.add(job);
	}
	
	public Set<String> getInputFiles() {
		return inputFiles;
	}
	
	public void addInputFile(String inputFile) {
		inputFiles.add(inputFile);
	}
	
	public int getConcurrentJobCreationThreads() {
		if ( concurrentJobCreationThreads <= 0 ) {
			return DEFAULT_JOB_CREATION_THREADS;
		} else {
			return concurrentJobCreationThreads;
		}
	}
	
	public List<JobObject> getJobs() {
		
		return this.jobs;
	}
	
	private void uploadInputFiles() throws RemoteFileSystemException, NoSuchJobException {
		
		for ( String inputFile : inputFiles ) {
			if ( FileManager.isLocal(inputFile) ) {

				DataHandler dh = FileManager.createDataHandler(inputFile);
				serviceInterface.uploadMultiPartJobInputFile(multiPartJobId, dh, new File(inputFile).getName());
			} else {
				String filename = inputFile.substring(0, inputFile.lastIndexOf("/"));
				serviceInterface.copyMultiPartJobInputFile(multiPartJobId, inputFile, filename);
			}
		}
	}
	
	public void submit() throws JobSubmissionException, NoSuchJobException {
		
		serviceInterface.submitMultiPartJob(multiPartJobId);
		
	}
	
	
	public void prepareAndCreateJobs() throws JobsException, BackendException {
		
		// check whether any of the jobnames already exist
		
		
		myLogger.debug("Creating "+getJobs().size()+" jobs as part of multipartjob: "+multiPartJobId);
		ExecutorService executor = Executors.newFixedThreadPool(getConcurrentJobCreationThreads());
		
		final Map<JobObject, Exception> failedSubmissions = Collections.synchronizedMap(new HashMap<JobObject, Exception>());
		
		for ( final JobObject job : getJobs() ) {
			
			Thread createThread = new Thread() {
				public void run() {
					try {
						job.createJob(submissionFqan);
						serviceInterface.addJobToMultiPartJob(multiPartJobId, job.getJobname());
					} catch (Exception e) {
						myLogger.error(e);
						failedSubmissions.put(job, e);
					}
				}
			};
			executor.execute(createThread);
		}
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(7200, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			myLogger.error(e);
			throw new RuntimeException("Job creation executor interrupted...");
		}
		myLogger.debug("Finished submission of "+getJobs().size()+" jobs as part of multipartjob: "+multiPartJobId);
		
		if ( failedSubmissions.size() > 0 ) {
			myLogger.error(failedSubmissions.size()+" submission failed...");
			throw new JobsException(failedSubmissions);
		}
		
		try {
			uploadInputFiles();
		} catch (Exception e) {
			throw new BackendException("Could not upload input files...", e);
		}
	}
	
	public void downloadResults(File parentFolder, String[] patterns, boolean createSeperateFoldersForEveryJob, boolean prefixWithJobname) throws RemoteFileSystemException, FileTransferException, IOException {
		
		for ( JobObject job : getJobs() ) {
			for ( String child : job.listJobDirectory(0) ) {
				
				boolean download = false;
				for ( String pattern : patterns ) {
					if ( child.indexOf(pattern) >= 0 ) {
						download = true;
						break;
					}
				}
				
				if ( download ) {
					myLogger.debug("Downloading file: "+child);
					File cacheFile = GrisuRegistryManager.getDefault(serviceInterface).getFileManager().downloadFile(child);
					String targetfilename = null;
					if ( prefixWithJobname ) {
						targetfilename = job.getJobname()+"_"+cacheFile.getName();
					} else {
						targetfilename = cacheFile.getName();
					}
					if ( createSeperateFoldersForEveryJob ) {
						FileUtils.copyFile(cacheFile, new File(new File(parentFolder, job.getJobname()), targetfilename));
					} else {
						FileUtils.copyFile(cacheFile, new File(parentFolder, targetfilename));
					}
				}
			}
			
		}
		
	}

}
