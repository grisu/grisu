package org.vpac.grisu.frontend.model.job;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.clientexceptions.FileTransferException;
import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.MultiPartJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoMultiPartJob;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class MultiPartJobObject {
	
	static final Logger myLogger = Logger
	.getLogger(MultiPartJobObject.class.getName());
	
	public static final int DEFAULT_JOB_CREATION_RETRIES = 5;
	
	public static final int DEFAULT_JOB_CREATION_THREADS = 5;
	
	private int concurrentJobCreationThreads = 0;
	
	private final ServiceInterface serviceInterface;
	
	private final String multiPartJobId;
	private String submissionFqan;
	
	private List<JobObject> jobs = new LinkedList<JobObject>();
	
	private Map<String, String> inputFiles = new HashMap<String, String>();
	
	private DtoMultiPartJob dtoMultiPartJob = null;
	
	private String[] sitesToInclude;
	private String[] sitesToExclude;
	
	private int maxWalltimeInSecondsAcrossJobs = 3600;
	private int defaultWalltime = 3600;
	
	private String defaultApplication = Constants.GENERIC_APPLICATION_NAME;
	private String defaultVersion = Constants.NO_VERSION_INDICATOR_STRING;
	
	private int defaultNoCpus = 1;
	
	private String[] allRemoteJobnames;

	/**
	 * Use this constructor if you want to create a multipartjob.
	 * 
	 * @param serviceInterface the serviceinterface
	 * @param multiPartJobId the id of the multipartjob
	 * @param submissionFqan the VO to use to submit the jobs of this multipartjob
	 * @throws MultiPartJobException if the multipartjob can't be created
	 */
	public MultiPartJobObject(ServiceInterface serviceInterface, String multiPartJobId, String submissionFqan) throws MultiPartJobException {
		this.serviceInterface = serviceInterface;
		this.multiPartJobId = multiPartJobId;
		this.submissionFqan = submissionFqan;
		
		dtoMultiPartJob = serviceInterface.createMultiPartJob(this.multiPartJobId, this.submissionFqan);
	}
	
	/**
	 * Use this constructor to create a MultiPartJobObject for a multipartjob that already exists on the backend.
	 * 
	 * @param serviceInterface the serviceinterface
	 * @param multiPartJobId the id of the multipartjob
	 * @param refreshJobStatusOnBackend whether to refresh the status of the jobs on the backend. might take quite a while...
	 * 
	 * @throws MultiPartJobException if one of the jobs of the multipartjob doesn't exist on the backend
	 * @throws NoSuchJobException if there is no such multipartjob on the backend
	 */
	public MultiPartJobObject(ServiceInterface serviceInterface, String multiPartJobId, boolean refreshJobStatusOnBackend) throws MultiPartJobException, NoSuchJobException {
		this.serviceInterface = serviceInterface;
		this.multiPartJobId = multiPartJobId;
		
		try {
			for ( DtoJob dtoJob : getMultiPartJob(refreshJobStatusOnBackend).getJobs().getAllJobs() ) {
				JobObject job = new JobObject(serviceInterface, dtoJob);
				jobs.add(job);
			}
		} catch (NoSuchJobException e) {
			throw new MultiPartJobException("Multipart job is not complete. Missing at least one job."+e.getLocalizedMessage());
//			e.printStackTrace();
//			return;
		}

	}
	
	public boolean isFinished() {
		try {
			return getMultiPartJob(true).allJobsFinished();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public boolean isSuccessful() {
		try {
			return getMultiPartJob(true).allJobsFinishedSuccessful();
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
	}
	
	private DtoMultiPartJob getMultiPartJob(boolean refresh) throws NoSuchJobException {
		
		if ( dtoMultiPartJob == null || refresh ) {
			dtoMultiPartJob = serviceInterface.getMultiPartJob(multiPartJobId, true);
		}
		return dtoMultiPartJob;
	}
	
	public void restartFailedJobs(FailedJobRestarter restarter) {
		
		if ( restarter == null ) {
			restarter = new FailedJobRestarter() {
				
				public void restartJob(JobObject job) throws JobSubmissionException {
					try {
						job.restartJob();
					} catch (JobPropertiesException e) {
						throw new JobSubmissionException("Can't resubmit job: "+e.getLocalizedMessage());
					}
				}
			};
		}
		
		try {
		for ( DtoJob dtoJob : getMultiPartJob(true).getFailedJobs().getAllJobs() ) {
			
			try {
				JobObject failedJob = new JobObject(serviceInterface, dtoJob.jobname());
			
				restarter.restartJob(failedJob);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public String getProgress(FailedJobRestarter restarter) {
		Date start = new Date();
		
		DtoMultiPartJob temp;
		try {
			temp = getMultiPartJob(true);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		Date end = new Date();
		
		StringBuffer output = new StringBuffer();
		
		output.append("Time to get all job status: "+(end.getTime()-start.getTime())/1000+" seconds."+"\n");


		
		output.append("Total number of jobs: "+temp.totalNumberOfJobs()+"\n");
		output.append("Waiting jobs: "+temp.numberOfWaitingJobs()+"\n");
		output.append("Active jobs: "+temp.numberOfRunningJobs()+"\n");
		output.append("Successful jobs: "+temp.numberOfSuccessfulJobs()+"\n");
		output.append("Failed jobs: "+temp.numberOfFailedJobs()+"\n");
		if ( temp.numberOfFailedJobs() > 0 ) {
			for ( DtoJob job : temp.getFailedJobs().getAllJobs() ) {
				output.append("\tJobname: "+job.jobname()+", Error: "+job.propertiesAsMap().get(Constants.ERROR_REASON)+"\n");	
			}
			
			if ( restarter != null ) {
				restartFailedJobs(restarter);
			}
			
		} else {
			output.append("\n");
		}
		output.append("Unsubmitted jobs: "+temp.numberOfUnsubmittedJobs()+"\n");
		
		return output.toString();
	}
	
	/**
	 * Monitors the status of all jobs of this multipartjob.
	 * 
	 * If you want to restart failed jobs while this is running, provide a {@link FailedJobRestarter}.
	 * @param sleeptimeinseconds how long between monitor runs
	 * @param enddate a date that indicates when the monitoring should stop. Use null if you want to monitor until all jobs are finished
	 * @param forceSuccess forces monitoring until all jobs are finished successful or enddate is reached. Only a valid option if a restarter is provided.
	 * @param restarter the restarter (or null if you don't want to restart failed jobs while monitoring)
	 * 
	 */
	public void monitorProgress(int sleeptimeinseconds, Date enddate, boolean forceSuccess, FailedJobRestarter restarter) {
		boolean finished = false;
		do {

			String progress = getProgress(restarter);
			
			DtoMultiPartJob temp;
			try {
				temp = getMultiPartJob(false);
			} catch (NoSuchJobException e) {
				throw new RuntimeException(e);
			}
			if ( forceSuccess && restarter != null ) {
				finished = temp.allJobsFinishedSuccessful();
			} else { 
				finished = temp.allJobsFinished();
			}

			if ( finished || (enddate != null && new Date().after(enddate)) ) {
				break;
			}
			
			for ( Date date : getLogMessages(false).keySet() ) {
				System.out.println(date.toString()+": "+getLogMessages(false).get(date));
			}
			
			System.out.println(progress);
			System.out.println();
			
			try {
				System.out.println("Sleeping for "+sleeptimeinseconds+" seconds...");
				Thread.sleep(sleeptimeinseconds*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while ( ! finished );
	}
	
	public String getFqan() {
		return submissionFqan;
	}
	
	public Map<String, String> getProperties() {
		try {
			return getMultiPartJob(false).propertiesAsMap();
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String pathToInputFiles() {
		try {
			return getMultiPartJob(false).pathToInputFiles();
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void addJob(JobObject job) {
		
		if ( jobs.contains(job) ) {
			throw new IllegalArgumentException("Job: "+job.getJobname()+" already part of this multiPartJob.");
		}
		
		if ( Arrays.binarySearch(getAllRemoteJobnames(), job.getJobname()) >= 0 ) {
			throw new IllegalArgumentException("Job: "+job.getJobname()+" already exists on the backend.");
		}
		
		if ( job.getWalltimeInSeconds() <= 0 ) {
			job.setWalltimeInSeconds(defaultWalltime);
		} else {
			if ( job.getWalltimeInSeconds() > maxWalltimeInSecondsAcrossJobs ) {
				maxWalltimeInSecondsAcrossJobs = job.getWalltimeInSeconds();
			}
		}
		this.jobs.add(job);
	}
	
	public SortedSet<GridResource> findBestResources() {
		
		Map<JobSubmissionProperty, String> properties = new HashMap<JobSubmissionProperty, String>();
		properties.put(JobSubmissionProperty.NO_CPUS, new Integer(defaultNoCpus).toString());
		properties.put(JobSubmissionProperty.APPLICATIONVERSION, defaultVersion);
		properties.put(JobSubmissionProperty.WALLTIME_IN_MINUTES, new Integer(defaultWalltime/60).toString());
		
		SortedSet<GridResource> result = GrisuRegistryManager.getDefault(serviceInterface).getApplicationInformation(defaultApplication).getBestSubmissionLocations(properties, getFqan());
		return result;
		
	}
	
	public void fillOrOverwriteSubmissionLocationsUsingMatchmaker() throws JobCreationException, JobSubmissionException {
		
		Map<String, Integer> submissionLocations = new TreeMap<String, Integer>();
		
		Long allWalltime = 0L;
		for ( JobObject job : this.jobs ) {
			allWalltime = allWalltime + job.getWalltimeInSeconds();
		}
		
		Map<GridResource, Long> resourcesToUse = new TreeMap<GridResource, Long>();
		List<Integer> ranks = new LinkedList<Integer>();
		Long allRanks = 0L;
		for ( GridResource resource : findBestResources() ) {
			
			if ( resource.getQueueName().contains("sque") ) {
				continue;
			}
			if ( sitesToInclude != null ) {
				
				for ( String site : sitesToInclude ) {
					if ( resource.getSiteName().toLowerCase().contains(site.toLowerCase()) ) {
						resourcesToUse.put(resource, new Long(0L));
						ranks.add(resource.getRank());
						allRanks = allRanks + resource.getRank();
						break;
					}
				}
				
			} else if ( sitesToExclude != null ) {
				
				boolean useSite = true;
				for ( String site : sitesToExclude ) {
					if ( resource.getSiteName().toLowerCase().contains(site.toLowerCase()) ) {
						useSite = false;
						break;
					}
				}
				if ( useSite ) {
					resourcesToUse.put(resource, new Long(0L));
					ranks.add(resource.getRank());
					allRanks = allRanks + resource.getRank();					
				}
				
			} else {
				resourcesToUse.put(resource, new Long(0L));
				ranks.add(resource.getRank());
				allRanks = allRanks + resource.getRank();
			}
		}
		
		myLogger.debug("Rank summary: "+allRanks);
		myLogger.debug("Walltime summary: "+allWalltime);
		
		//TODO change that later on so more than one frames can be included in one job
		for ( JobObject job : this.jobs ) {
			
			
			GridResource subLocResource = null;
			long oldWalltimeSummary = 0L;
			for ( GridResource resource : resourcesToUse.keySet() ) {
				
				long rankPercentage = (resource.getRank()*100)/(allRanks);
				long wallTimePercentage = ((job.getWalltimeInSeconds()+resourcesToUse.get(resource))*100)/(allWalltime);
				
				if ( rankPercentage >= wallTimePercentage ) {
					subLocResource = resource;
					oldWalltimeSummary = resourcesToUse.get(subLocResource);
					myLogger.debug("Rank percentage: "+rankPercentage+". Walltime percentage: "+wallTimePercentage+". Using resource: "+resource.getQueueName());
					break;
				} else {
					myLogger.debug("Rank percentage: "+rankPercentage+". Walltime percentage: "+wallTimePercentage+". Not using resource: "+resource.getQueueName());
				}
			}
			
			if ( subLocResource == null ) {
				subLocResource = resourcesToUse.keySet().iterator().next();
				myLogger.error("Couldn't find resource for job: "+job.getJobname());
			}
			
			String subLoc = SubmissionLocationHelpers.createSubmissionLocationString(subLocResource);
			Integer currentCount = submissionLocations.get(subLocResource.toString());
			if ( currentCount == null ) {
				currentCount = 0;
			}
			submissionLocations.put(subLocResource.toString(), currentCount+1);

			job.setSubmissionLocation(subLoc);
			resourcesToUse.put(subLocResource, oldWalltimeSummary+job.getWalltimeInSeconds());
		}
		
		myLogger.debug("Filled submissionlocations for multijob: "+multiPartJobId);
		myLogger.debug("Submitted jobs to:\t\t\tAmount");
		for ( String sl : submissionLocations.keySet() ) {
			myLogger.debug(sl+"\t\t\t\t"+submissionLocations.get(sl));
		}
		
	}
	
	
	public Map<String, String> getInputFiles() {
		return inputFiles;
	}
	
	public void addInputFile(String inputFile, String targetFilename) {
		inputFiles.put(inputFile, targetFilename);
	}
	
	public void addInputFile(String inputFile) {
		if ( FileManager.isLocal(inputFile) ) {
			inputFiles.put(inputFile, new File(inputFile).getName());
		} else {
			FileManager.getFilename(inputFile);
			inputFiles.put(inputFile, FileManager.getFilename(inputFile));
		}
	}
	
	public int getConcurrentJobCreationThreads() {
		if ( concurrentJobCreationThreads <= 0 ) {
			return DEFAULT_JOB_CREATION_THREADS;
		} else {
			return concurrentJobCreationThreads;
		}
	}
	
	public void setConcurrentJobCreationThreads(int threads) {
		this.concurrentJobCreationThreads = threads;
	}
	
	public List<JobObject> getJobs() {
		
		return this.jobs;
	}
	
	private void uploadInputFiles() throws RemoteFileSystemException, NoSuchJobException {
		
		for ( String inputFile : inputFiles.keySet() ) {
			if ( FileManager.isLocal(inputFile) ) {

				DataHandler dh = FileManager.createDataHandler(inputFile);
				serviceInterface.uploadInputFile(multiPartJobId, dh, inputFiles.get(inputFile));
			} else {
				serviceInterface.copyMultiPartJobInputFile(multiPartJobId, inputFile, inputFiles.get(inputFile));
			}
		}
	}
	
	public void submit() throws JobSubmissionException, NoSuchJobException {
		
		serviceInterface.submitMultiPartJob(multiPartJobId);
		
	}
	
	
	public void prepareAndCreateJobs() throws JobsException, BackendException {
		
		//TODO check whether any of the jobnames already exist
		
		
		myLogger.debug("Creating "+getJobs().size()+" jobs as part of multipartjob: "+multiPartJobId);
		ExecutorService executor = Executors.newFixedThreadPool(getConcurrentJobCreationThreads());
		
		final Map<JobObject, Exception> failedSubmissions = Collections.synchronizedMap(new HashMap<JobObject, Exception>());
		
		for ( final JobObject job : getJobs() ) {
			
			Thread createThread = new Thread() {
				public void run() {
					boolean success = false;
					Exception lastException = null;
						for ( int i=0; i<DEFAULT_JOB_CREATION_RETRIES; i++) {
							try {
//								myLogger.info("Creating job: "+job.getJobname());
//								job.createJob(submissionFqan);
								myLogger.info("Adding job: "+job.getJobname()+" to multipartjob: "+multiPartJobId);
								String jobname = serviceInterface.addJobToMultiPartJob(multiPartJobId, job.getJobDescriptionDocumentAsString());
								job.setJobname(jobname);
								success = true;
								break;
							} catch (Exception e) {
								e.printStackTrace();
								try {
									serviceInterface.kill(job.getJobname(), true);
								} catch (Exception e1) {
									// doesn't matter
								}
								lastException = e;
								myLogger.error(job.getJobname()+": "+e);
							}
						}
						if ( ! success ) {
							failedSubmissions.put(job, lastException);
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
		myLogger.debug("Finished creation of "+getJobs().size()+" jobs as part of multipartjob: "+multiPartJobId);
		
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
	
	public Map<Date, String> getLogMessages(boolean refresh) {
		
		try {
			return getMultiPartJob(refresh).messages();
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		
	}

	public void setSitesToInclude(String[] sites) {
		this.sitesToInclude = sites;
		this.sitesToExclude = null;
	}
	
	public void setSitesToExclude(String[] sites) {
		this.sitesToExclude = sites;
		this.sitesToInclude = null;
	}
	
	public int getMaxWalltimeInSeconds() {
		return maxWalltimeInSecondsAcrossJobs;
	}
	
	public int getDefaultWalltime() {
		return this.defaultWalltime;
	}

	public void setDefaultWalltimeInSeconds(int walltimeInSeconds) {
		this.defaultWalltime = walltimeInSeconds;
		this.maxWalltimeInSecondsAcrossJobs = walltimeInSeconds;
		
		for ( JobObject job : this.jobs ) {
			job.setWalltimeInSeconds(walltimeInSeconds);
		}
	}
	
	public String getDefaultApplication() {
		return defaultApplication;
	}
	
	public void setDefaultApplication(String defaultApplication) {
		this.defaultApplication = defaultApplication;
		
		for ( JobObject job : this.jobs ) {
			job.setApplication(defaultApplication);
		}
	}

	public String getDefaultVersion() {
		return defaultVersion;
	}

	public void setDefaultVersion(String defaultVersion) {
		this.defaultVersion = defaultVersion;

		for ( JobObject job : this.jobs ) {
			job.setApplicationVersion(defaultVersion);
		}
		
	}

	public int getDefaultNoCpus() {
		
		return defaultNoCpus;
	}

	public void setDefaultNoCpus(int defaultNoCpus) {
		this.defaultNoCpus = defaultNoCpus;

		for ( JobObject job : this.jobs ) {
			job.setCpus(defaultNoCpus);
		}
		
	}
	
	private String[] getAllRemoteJobnames() {
		if ( allRemoteJobnames == null ) {
			allRemoteJobnames = serviceInterface.getAllJobnames();
			Arrays.sort(allRemoteJobnames);
		}
		return allRemoteJobnames;
	}
	
}
