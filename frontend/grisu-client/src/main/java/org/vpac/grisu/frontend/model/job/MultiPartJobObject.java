package org.vpac.grisu.frontend.model.job;

import java.awt.Event;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.MultiPartJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.clientexceptions.FileTransferException;
import org.vpac.grisu.frontend.control.clientexceptions.JobCreationException;
import org.vpac.grisu.frontend.model.events.MultiPartJobEvent;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoActionStatus;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoMultiPartJob;
import org.vpac.grisu.settings.ClientPropertiesManager;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class MultiPartJobObject {

	static final Logger myLogger = Logger.getLogger(MultiPartJobObject.class
			.getName());

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
	 * @param serviceInterface
	 *            the serviceinterface
	 * @param multiPartJobId
	 *            the id of the multipartjob
	 * @param submissionFqan
	 *            the VO to use to submit the jobs of this multipartjob
	 * @throws MultiPartJobException
	 *             if the multipartjob can't be created
	 */
	public MultiPartJobObject(ServiceInterface serviceInterface,
			String multiPartJobId, String submissionFqan, String defaultApplication, String defaultVersion)
			throws MultiPartJobException {
		this.serviceInterface = serviceInterface;
		this.multiPartJobId = multiPartJobId;
		this.submissionFqan = submissionFqan;

		dtoMultiPartJob = serviceInterface.createMultiPartJob(
				this.multiPartJobId, this.submissionFqan);

		if ( StringUtils.isBlank(defaultApplication) ) {
			defaultApplication = Constants.GENERIC_APPLICATION_NAME;
		}
		if ( StringUtils.isBlank(defaultVersion) ) {
			defaultVersion = Constants.NO_VERSION_INDICATOR_STRING;
		}
		setDefaultApplication(defaultApplication);
		setDefaultVersion(defaultVersion);
		try {
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.APPLICATIONNAME_KEY, defaultApplication);
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.APPLICATIONVERSION_KEY, defaultVersion);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		
	}

	/**
	 * Use this constructor to create a MultiPartJobObject for a multipartjob
	 * that already exists on the backend.
	 * 
	 * @param serviceInterface
	 *            the serviceinterface
	 * @param multiPartJobId
	 *            the id of the multipartjob
	 * @param refreshJobStatusOnBackend
	 *            whether to refresh the status of the jobs on the backend.
	 *            might take quite a while...
	 * 
	 * @throws MultiPartJobException
	 *             if one of the jobs of the multipartjob doesn't exist on the
	 *             backend
	 * @throws NoSuchJobException
	 *             if there is no such multipartjob on the backend
	 */
	public MultiPartJobObject(ServiceInterface serviceInterface,
			String multiPartJobId, boolean refreshJobStatusOnBackend)
			throws MultiPartJobException, NoSuchJobException {
		this.serviceInterface = serviceInterface;
		this.multiPartJobId = multiPartJobId;

		
		dtoMultiPartJob = getMultiPartJob(refreshJobStatusOnBackend);

		this.submissionFqan = dtoMultiPartJob.getSubmissionFqan(); 
		
		setDefaultApplication(serviceInterface.getJobProperty(this.multiPartJobId, Constants.APPLICATIONNAME_KEY));
		setDefaultVersion(serviceInterface.getJobProperty(this.multiPartJobId, Constants.APPLICATIONVERSION_KEY));
		
	}

	public boolean isFinished(boolean refresh) {
		try {
			return getMultiPartJob(refresh).isFinished();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public boolean isSuccessful() {
		return getMultiPartJob(true).allJobsFinishedSuccessful();
	}

	public void refresh() {
		getMultiPartJob(true);
	}
	
	private void refreshMultiPartJobStatus(boolean waitForRefreshToFinish) {
		
		String handle;
		try {
			handle = serviceInterface.refreshMultiPartJob(multiPartJobId);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		
		if ( waitForRefreshToFinish ) {
			
			DtoActionStatus status = serviceInterface.getActionStatus(handle);
			while ( ! status.isFinished() ) {
				try {
					Thread.sleep(ClientPropertiesManager.getJobStatusRecheckIntervall());
				} catch (InterruptedException e) {
					// doesn't happen
				}
				status = serviceInterface.getActionStatus(handle);
			}
		}
		
	}

	private DtoMultiPartJob getMultiPartJob(boolean refresh) {

		if (dtoMultiPartJob == null || refresh) {
			try {
				
				if ( refresh ) {
					refreshMultiPartJobStatus(true);
				}
				
				jobs.clear();
				dtoMultiPartJob = serviceInterface.getMultiPartJob(
						multiPartJobId);
				
				for (DtoJob dtoJob : dtoMultiPartJob.getJobs().getAllJobs()) {
					JobObject job = new JobObject(serviceInterface, dtoJob);
					jobs.add(job);
				}
			} catch (NoSuchJobException e) {
				throw new RuntimeException(e);
			}
		}
		return dtoMultiPartJob;
	}

	public void restartFailedJobs(FailedJobRestarter restarter) {

		if (restarter == null) {
			restarter = new FailedJobRestarter() {

				public void restartJob(JobObject job)
						throws JobSubmissionException {
					try {
						job.restartJob();
					} catch (JobPropertiesException e) {
						throw new JobSubmissionException("Can't resubmit job: "
								+ e.getLocalizedMessage());
					}
				}
			};
		}

		for (DtoJob dtoJob : getMultiPartJob(true).getFailedJobs().getAllJobs()) {

			JobObject failedJob = null;
			try {
				failedJob = new JobObject(serviceInterface, dtoJob.jobname());
				EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, "Restarting job " + failedJob.getJobname())+"...");
				restarter.restartJob(failedJob);
			} catch (Exception e) {
				if (failedJob != null) {
					EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, "Restarting of job " + failedJob.getJobname())+" failed: "+e.getLocalizedMessage());
				} else {
					EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, "Restarting failed: "+e.getLocalizedMessage()));					
				}
				e.printStackTrace();
			}
		}

	}

	public String getProgress(FailedJobRestarter restarter) {
		Date start = new Date();

		DtoMultiPartJob temp;
		temp = getMultiPartJob(false);

		Date end = new Date();

		StringBuffer output = new StringBuffer();

		output
				.append("Time to get all job status: "
						+ (end.getTime() - start.getTime()) / 1000
						+ " seconds." + "\n");

		output.append("Total number of jobs: " + temp.totalNumberOfJobs()
				+ "\n");
		output.append("Waiting jobs: " + temp.numberOfWaitingJobs() + "\n");
		output.append("Active jobs: " + temp.numberOfRunningJobs() + "\n");
		output.append("Successful jobs: " + temp.numberOfSuccessfulJobs()
				+ "\n");
		output.append("Failed jobs: " + temp.numberOfFailedJobs() + "\n");
		if (temp.numberOfFailedJobs() > 0) {
//			for (DtoJob job : temp.getFailedJobs().getAllJobs()) {
//				output.append("\tJobname: " + job.jobname() + ", Error: "
//						+ job.propertiesAsMap().get(Constants.ERROR_REASON)
//						+ "\n");
//			}

			if (restarter != null) {
				restartFailedJobs(restarter);
			}

		} else {
			output.append("\n");
		}
		output.append("Unsubmitted jobs: " + temp.numberOfUnsubmittedJobs()
				+ "\n");

		return output.toString();
	}

	/**
	 * Monitors the status of all jobs of this multipartjob.
	 * 
	 * If you want to restart failed jobs while this is running, provide a
	 * {@link FailedJobRestarter}.
	 * 
	 * @param sleeptimeinseconds
	 *            how long between monitor runs
	 * @param enddate
	 *            a date that indicates when the monitoring should stop. Use
	 *            null if you want to monitor until all jobs are finished
	 * @param forceSuccess
	 *            forces monitoring until all jobs are finished successful or
	 *            enddate is reached. Only a valid option if a restarter is
	 *            provided.
	 * @param restarter
	 *            the restarter (or null if you don't want to restart failed
	 *            jobs while monitoring)
	 * 
	 */
	public void monitorProgress(int sleeptimeinseconds, Date enddate,
			boolean forceSuccess, FailedJobRestarter restarter) {
		boolean finished = false;
		do {

			refresh();
			String progress = getProgress(restarter);

			DtoMultiPartJob temp;
			temp = getMultiPartJob(false);

			if (forceSuccess && restarter != null) {
				finished = temp.allJobsFinishedSuccessful();
			} else {
				finished = temp.isFinished();
			}

			if (finished || (enddate != null && new Date().after(enddate))) {
				break;
			}

			for (Date date : getLogMessages(false).keySet()) {
				System.out.println(date.toString() + ": "
						+ getLogMessages(false).get(date));
			}

			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, progress));

			try {
				EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, "Pausing monitoring for "+
						sleeptimeinseconds + " seconds..."));
				Thread.sleep(sleeptimeinseconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!finished);
	}

	public String getFqan() {
		return submissionFqan;
	}

	public Map<String, String> getProperties() {
		return getMultiPartJob(false).propertiesAsMap();
	}

	public String pathToInputFiles() {
		return getMultiPartJob(false).pathToInputFiles();
	}

	public void addJob(JobObject job) {

		if (jobs.contains(job)) {
			throw new IllegalArgumentException("Job: " + job.getJobname()
					+ " already part of this multiPartJob.");
		}

		if (Arrays.binarySearch(getAllRemoteJobnames(), job.getJobname()) >= 0) {
			throw new IllegalArgumentException("Job: " + job.getJobname()
					+ " already exists on the backend.");
		}

		if (job.getWalltimeInSeconds() <= 0) {
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, "Setting walltime for job " + job.getJobname()
					+ " to default walltime: " + defaultWalltime));
			job.setWalltimeInSeconds(defaultWalltime);
		} else {
			// fireJobStatusChange("Keeping walltime for job " +
			// job.getJobname()
			// + ": " + job.getWalltimeInSeconds());
			if (job.getWalltimeInSeconds() > maxWalltimeInSecondsAcrossJobs) {
				maxWalltimeInSecondsAcrossJobs = job.getWalltimeInSeconds();
			}
		}
		EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, 
				"Adding job " + job.getJobname() + " to multipartjob: " + this.multiPartJobId));
		this.jobs.add(job);
	}

	private SortedSet<GridResource> findBestResources() {

		Map<JobSubmissionProperty, String> properties = new HashMap<JobSubmissionProperty, String>();
		properties.put(JobSubmissionProperty.NO_CPUS,
				new Integer(defaultNoCpus).toString());
		properties
				.put(JobSubmissionProperty.APPLICATIONVERSION, defaultVersion);
		properties.put(JobSubmissionProperty.WALLTIME_IN_MINUTES, new Integer(
				maxWalltimeInSecondsAcrossJobs / 60).toString());

		SortedSet<GridResource> result = GrisuRegistryManager.getDefault(
				serviceInterface).getApplicationInformation(
				getDefaultApplication()).getBestSubmissionLocations(properties,
				getFqan());
		StringBuffer message = new StringBuffer(
				"Finding best resources for mulipartjob " + multiPartJobId
						+ " using:\n");
		message.append("Version: " + defaultVersion + "\n");
		message.append("Walltime in minutes: " + maxWalltimeInSecondsAcrossJobs
				/ 60 + "\n");
		message.append("No cpus: " + defaultNoCpus + "\n");
		EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, message.toString()));

		return result;

	}

	private void fillOrOverwriteSubmissionLocationsUsingMatchmaker()
			throws JobCreationException, JobSubmissionException {

		Map<String, Integer> submissionLocations = new TreeMap<String, Integer>();

		Long allWalltime = 0L;
		for (JobObject job : this.jobs) {
			allWalltime = allWalltime + job.getWalltimeInSeconds();
		}

		Map<GridResource, Long> resourcesToUse = new TreeMap<GridResource, Long>();
		List<Integer> ranks = new LinkedList<Integer>();
		Long allRanks = 0L;
		for (GridResource resource : findBestResources()) {

			if (sitesToInclude != null) {

				for (String site : sitesToInclude) {
					if (resource.getSiteName().toLowerCase().contains(
							site.toLowerCase())) {
						resourcesToUse.put(resource, new Long(0L));
						ranks.add(resource.getRank());
						allRanks = allRanks + resource.getRank();
						break;
					}
				}

			} else if (sitesToExclude != null) {

				boolean useSite = true;
				for (String site : sitesToExclude) {
					if (resource.getSiteName().toLowerCase().contains(
							site.toLowerCase())) {
						useSite = false;
						break;
					}
				}
				if (useSite) {
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

		myLogger.debug("Rank summary: " + allRanks);
		myLogger.debug("Walltime summary: " + allWalltime);

		GridResource[] resourceArray = resourcesToUse.keySet().toArray(
				new GridResource[] {});
		int lastIndex = 0;

		for (JobObject job : this.jobs) {

			GridResource subLocResource = null;
			long oldWalltimeSummary = 0L;

			for (int i = lastIndex; i < resourceArray.length * 2; i++) {
				int indexToUse = i;
				if (i >= resourceArray.length) {
					indexToUse = indexToUse - resourceArray.length;
				}

				GridResource resource = resourceArray[indexToUse];

				long rankPercentage = (resource.getRank() * 100) / (allRanks);
				long wallTimePercentage = ((job.getWalltimeInSeconds() + resourcesToUse
						.get(resource)) * 100)
						/ (allWalltime);

				if (rankPercentage >= wallTimePercentage) {
					subLocResource = resource;
					oldWalltimeSummary = resourcesToUse.get(subLocResource);
					myLogger.debug("Rank percentage: " + rankPercentage
							+ ". Walltime percentage: " + wallTimePercentage
							+ ". Using resource: " + resource.getQueueName());
					lastIndex = lastIndex + 1;
					if (lastIndex >= resourceArray.length) {
						lastIndex = 0;
					}
					break;
				} else {
					// myLogger.debug("Rank percentage: "+rankPercentage+". Walltime percentage: "+wallTimePercentage+". Not using resource: "+resource.getQueueName());
				}
			}

			if (subLocResource == null) {
				subLocResource = resourcesToUse.keySet().iterator().next();
				myLogger.error("Couldn't find resource for job: "
						+ job.getJobname());
			}

			String subLoc = SubmissionLocationHelpers
					.createSubmissionLocationString(subLocResource);
			Integer currentCount = submissionLocations.get(subLocResource
					.toString());
			if (currentCount == null) {
				currentCount = 0;
			}
			submissionLocations
					.put(subLocResource.toString(), currentCount + 1);

			job.setSubmissionLocation(subLoc);
			resourcesToUse.put(subLocResource, oldWalltimeSummary
					+ job.getWalltimeInSeconds());
		}

		StringBuffer message = new StringBuffer(
				"Filled submissionlocations for multijob: " + multiPartJobId
						+ "\n");
		message.append("Submitted jobs to:\t\t\tAmount\n");
		for (String sl : submissionLocations.keySet()) {
			message
					.append(sl + "\t\t\t\t" + submissionLocations.get(sl)
							+ "\n");
		}
		EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, message.toString()));

	}

	public Map<String, String> getInputFiles() {
		return inputFiles;
	}

	public void addInputFile(String inputFile, String targetFilename) {
		inputFiles.put(inputFile, targetFilename);
	}

	public void addInputFile(String inputFile) {
		if (FileManager.isLocal(inputFile)) {
			inputFiles.put(inputFile, new File(inputFile).getName());
		} else {
			FileManager.getFilename(inputFile);
			inputFiles.put(inputFile, FileManager.getFilename(inputFile));
		}
	}

	public int getConcurrentJobCreationThreads() {
		if (concurrentJobCreationThreads <= 0) {
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

	private void uploadInputFiles() throws FileTransferException, RemoteFileSystemException,
			NoSuchJobException {
		
		// uploading single job input files
		
		for ( JobObject job : getJobs() ) {
			job.stageFiles();
		}

		
		// uploading common job input files
		for (String inputFile : inputFiles.keySet()) {
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, 
			 "Uploading input file: " + inputFile
					+ " for multipartjob " + multiPartJobId));
			if (FileManager.isLocal(inputFile)) {

				DataHandler dh = FileManager.createDataHandler(inputFile);
				serviceInterface.uploadInputFile(multiPartJobId, dh, inputFiles
						.get(inputFile));
			} else {
				serviceInterface.copyMultiPartJobInputFile(multiPartJobId,
						inputFile, inputFiles.get(inputFile));
			}
		}
	}

	public void submit() throws JobSubmissionException, NoSuchJobException {

		EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, 
				"Submitting multipartjob " + multiPartJobId	+ " to backend..."));
		try {
			serviceInterface.submitJob(multiPartJobId);
		} catch (JobSubmissionException jse) {
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
			 "Job submitssion for multipartjob "
					+ multiPartJobId + " failed: " + jse.getLocalizedMessage()));
			throw jse;
		} catch (NoSuchJobException nsje) {
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, "Job submitssion for multipartjob "
					+ multiPartJobId + " failed: " + nsje.getLocalizedMessage()));
			throw nsje;
		}
		EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
		 "All jobs of multipartjob " + multiPartJobId
				+ " ready for submission. Continuing submission in background..."));
	}

	public void prepareAndCreateJobs(boolean optimize) throws JobsException, BackendException {

		// TODO check whether any of the jobnames already exist

		myLogger.debug("Creating " + getJobs().size()
				+ " jobs as part of multipartjob: " + multiPartJobId);
		EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, 
		 "Creating " + getJobs().size()
				+ " jobs as part of multipartjob: " + multiPartJobId));
		ExecutorService executor = Executors
				.newFixedThreadPool(getConcurrentJobCreationThreads());

		final Map<JobObject, Exception> failedSubmissions = Collections
				.synchronizedMap(new HashMap<JobObject, Exception>());

		for (final JobObject job : getJobs()) {

			Thread createThread = new Thread() {
				public void run() {
					boolean success = false;
					Exception lastException = null;
					for (int i = 0; i < DEFAULT_JOB_CREATION_RETRIES; i++) {
						try {
							// myLogger.info("Creating job: "+job.getJobname());
							// job.createJob(submissionFqan);
							myLogger.info("Adding job: " + job.getJobname()
									+ " to multipartjob: " + multiPartJobId);
							String jobname = serviceInterface
									.addJobToMultiPartJob(
											multiPartJobId,
											job
													.getJobDescriptionDocumentAsString());
							job.setJobname(jobname);
							job.updateJobDirectory();
							
							EventBus.publish(MultiPartJobObject.this.multiPartJobId, new MultiPartJobEvent(
									MultiPartJobObject.this, "Creation of job "
									+ job.getJobname() + " successful."));
							
							success = true;
							break;
						} catch (Exception e) {
							e.printStackTrace();
							EventBus.publish(MultiPartJobObject.this.multiPartJobId, new MultiPartJobEvent(
									MultiPartJobObject.this, "Creation of job "
									+ job.getJobname() + " failed: "
									+ e.getLocalizedMessage()));
							try {
								serviceInterface.kill(job.getJobname(), true);
							} catch (Exception e1) {
								// doesn't matter
							}
							lastException = e;
							myLogger.error(job.getJobname() + ": " + e);
						}
					}
					if (!success) {
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
		myLogger.debug("Finished creation of " + getJobs().size()
				+ " jobs as part of multipartjob: " + multiPartJobId);
		EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
				"Finished creation of " + getJobs().size()
				+ " jobs as part of multipartjob: " + multiPartJobId));

		if (failedSubmissions.size() > 0) {
			myLogger.error(failedSubmissions.size() + " submission failed...");
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
					"Not all jobs for multipartjob "
					+ multiPartJobId + " created successfully. Aborting..."));
			throw new JobsException(failedSubmissions);
		}

		if ( optimize ) {
		try {
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
					"Optimizing multipartjob: "
					+ multiPartJobId));
			serviceInterface.optimizeMultiPartJob(this.multiPartJobId);
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
					"Optimizing of multipartjob "
					+ multiPartJobId + " finished."));
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		}	
		
		try {
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
					"Uploading input files for multipartjob: "
					+ multiPartJobId));
			uploadInputFiles();
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, 
					"Uploading input files for multipartjob: "
					+ multiPartJobId + " finished."));
		} catch (Exception e) {
			EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
					"Uploading input files for multipartjob: "
					+ multiPartJobId + " failed: " + e.getLocalizedMessage()));
			throw new BackendException("Could not upload input files...", e);
		}
	}

	public void downloadResults(boolean onlyDownloadWhenFinished, File parentFolder, String[] patterns,
			boolean createSeperateFoldersForEveryJob, boolean prefixWithJobname)
			throws RemoteFileSystemException, FileTransferException,
			IOException {

		EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, "Checking and possibly downloading output files for multipartjob: "
				+ multiPartJobId + ". This might take a while..."));

		for (JobObject job : getJobs()) {
			
			if ( onlyDownloadWhenFinished && ! job.isFinished(false) ) {
				continue;
			}
			
			for (String child : job.listJobDirectory(0)) {

				boolean download = false;
				for (String pattern : patterns) {
					if (child.indexOf(pattern) >= 0) {
						download = true;
						break;
					}
				}

				if (download) {
					File cacheFile = null;
					
					boolean needsDownloading = false;
					
					try {
						needsDownloading = GrisuRegistryManager.getDefault(serviceInterface).getFileManager().needsDownloading(child);
					} catch (RuntimeException e) {
						myLogger.error("Could not access file "+child+": "+e.getLocalizedMessage());
						EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
								"Could not access file "+child+": "+e.getLocalizedMessage()));
						continue;
					}
					
					if ( needsDownloading ) {
						myLogger.debug("Downloading file: " + child);
						EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this,
								"Downloading file: " + child));
						try {
						cacheFile = GrisuRegistryManager.getDefault(
								serviceInterface).getFileManager()
								.downloadFile(child);
						} catch (Exception e) {
							myLogger.error("Could not download file "+child+": "+e.getLocalizedMessage());
							EventBus.publish(this.multiPartJobId, new MultiPartJobEvent(this, 
									"Could not download file "+child+": "+e.getLocalizedMessage()));
							continue;
						}
					} else {
						cacheFile = GrisuRegistryManager.getDefault(
								serviceInterface).getFileManager().getLocalCacheFile(child);
					}
						String targetfilename = null;
						if (prefixWithJobname) {
							targetfilename = job.getJobname() + "_"
									+ cacheFile.getName();
						} else {
							targetfilename = cacheFile.getName();
						}
						if (createSeperateFoldersForEveryJob) {
							FileUtils.copyFile(cacheFile, new File(new File(
									parentFolder, job.getJobname()),
									targetfilename));
						} else {
							FileUtils.copyFile(cacheFile, new File(
									parentFolder, targetfilename));
						}
				}
			}

		}

	}

	public Map<Date, String> getLogMessages(boolean refresh) {

		return getMultiPartJob(refresh).messages();

	}

	public void setSitesToInclude(String[] sites) {
		this.sitesToInclude = sites;
		this.sitesToExclude = null;
		
		try {
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.SITES_TO_INCLUDE_KEY, StringUtils.join(sites, ","));
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.SITES_TO_EXCLUDE_KEY, null);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
	}

	public void setSitesToExclude(String[] sites) {
		this.sitesToExclude = sites;
		this.sitesToInclude = null;

		try {
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.SITES_TO_EXCLUDE_KEY, StringUtils.join(sites, ","));
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.SITES_TO_INCLUDE_KEY, null);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
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
		
		try {
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.WALLTIME_IN_MINUTES_KEY, new Integer(walltimeInSeconds/60).toString());
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}

		for (JobObject job : this.jobs) {
			job.setWalltimeInSeconds(walltimeInSeconds);
		}
	}

	public String getDefaultApplication() {
		return defaultApplication;
	}

	public void setDefaultApplication(String defaultApplication) {
		this.defaultApplication = defaultApplication;

		try {
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.APPLICATIONNAME_KEY, defaultApplication);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		
		for (JobObject job : this.jobs) {
			job.setApplication(defaultApplication);
		}
	}

	public String getDefaultVersion() {
		return defaultVersion;
	}

	public void setDefaultVersion(String defaultVersion) {
		this.defaultVersion = defaultVersion;
		
		try {
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.APPLICATIONVERSION_KEY, defaultVersion);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}

		for (JobObject job : this.jobs) {
			job.setApplicationVersion(defaultVersion);
		}

	}

	public int getDefaultNoCpus() {

		return defaultNoCpus;
	}

	public void setDefaultNoCpus(int defaultNoCpus) {
		this.defaultNoCpus = defaultNoCpus;
		
		try {
			serviceInterface.addJobProperty(this.multiPartJobId, Constants.NO_CPUS_KEY, new Integer(defaultNoCpus).toString());
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}

		for (JobObject job : this.jobs) {
			job.setCpus(defaultNoCpus);
		}

	}

	private String[] getAllRemoteJobnames() {
		if (allRemoteJobnames == null) {
			allRemoteJobnames = serviceInterface.getAllJobnames(null).asArray();
			Arrays.sort(allRemoteJobnames);
		}
		return allRemoteJobnames;
	}


	public String getDetails() {

		DtoMultiPartJob temp = getMultiPartJob(false);

		StringBuffer buffer = new StringBuffer("Details:\n\n");

		buffer.append("Waiting jobs:\n");
		if (temp.numberOfWaitingJobs() == 0) {
			buffer.append("\tNo waiting jobs.\n");
		} else {
			for (DtoJob job : temp.waitingJobs()) {
				buffer.append("\t" + job.jobname() + ":\t"
						+ job.statusAsString() + " (submitted to: "
						+ job.jobProperty(Constants.SUBMISSION_SITE_KEY) + ", "
						+ job.jobProperty(Constants.QUEUE_KEY) + ")\n");
			}
		}
		buffer.append("Active jobs:\n");
		if (temp.numberOfRunningJobs() == 0) {
			buffer.append("\tNo active jobs.\n");
		} else {
			for (DtoJob job : temp.runningJobs()) {
				buffer.append("\t" + job.jobname() + ":\t"
						+ job.statusAsString() + " (submitted to: "
						+ job.jobProperty(Constants.SUBMISSION_SITE_KEY) + ", "
						+ job.jobProperty(Constants.QUEUE_KEY) + ")\n");
			}
		}
		buffer.append("Successful jobs:\n");
		if (temp.numberOfSuccessfulJobs() == 0) {
			buffer.append("\tNo successful jobs.\n");
		} else {
			for (DtoJob job : temp.successfulJobs()) {
				buffer.append("\t" + job.jobname() + ":\t"
						+ job.statusAsString() + " (submitted to: "
						+ job.jobProperty(Constants.SUBMISSION_SITE_KEY) + ", "
						+ job.jobProperty(Constants.QUEUE_KEY) + ")\n");
			}
		}
		buffer.append("Failed jobs:\n");
		if (temp.numberOfFailedJobs() == 0) {
			buffer.append("\tNo failed jobs.\n");
		} else {
			for (DtoJob job : temp.failedJobs()) {
				buffer.append("\t" + job.jobname() + ":\t"
						+ job.statusAsString() + " (submitted to: "
						+ job.jobProperty(Constants.SUBMISSION_SITE_KEY) + ", "
						+ job.jobProperty(Constants.QUEUE_KEY) + ")\n");
			}
		}

		buffer.append("\n");

		return buffer.toString();

	}
	
	public String getMultiPartJobId() {
		return multiPartJobId;
	}

	public void addJobProperty(String key, String value) {

		try {
			serviceInterface.addJobProperty(multiPartJobId, key, value);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
	}

	public String getJobProperty(String key) {

		try {
			return serviceInterface.getJobProperty(multiPartJobId, key);
		} catch (NoSuchJobException e) {
			throw new RuntimeException();
		}
	}
	
	public SortedSet<DtoJob> runningJobs() {
		return getMultiPartJob(false).runningJobs();
	}
	public int numberOfRunningJobs() {
		return getMultiPartJob(false).numberOfRunningJobs();
	}
	
	public SortedSet<DtoJob> waitingJobs() {
		return getMultiPartJob(false).waitingJobs();
	}
	public int numberOfWaitingJobs() {
		return getMultiPartJob(false).numberOfWaitingJobs();
	}
	
	public SortedSet<DtoJob> finishedJobs() {
		return getMultiPartJob(false).finishedJobs();
	}
	public int numberOfFinishedJobs() {
		return getMultiPartJob(false).numberOfFinishedJobs();
	}
	
	public SortedSet<DtoJob> failedJobs() {
		return getMultiPartJob(false).failedJobs();
	}
	public int numberOfFailedJobs() {
		return getMultiPartJob(false).numberOfFailedJobs();
	}
	
	public SortedSet<DtoJob> successfulJobs() {
		return getMultiPartJob(false).successfulJobs();
	}
	public int numberOfSuccessfulJobs() {
		return getMultiPartJob(false).numberOfSuccessfulJobs();
	}
	
	public SortedSet<DtoJob> unsubmittedJobs() {
		return getMultiPartJob(false).unsubmittedJobs();
	}
	public int numberOfUnsubmittedJobs() {
		return getMultiPartJob(false).numberOfUnsubmittedJobs();
	}
	

}
