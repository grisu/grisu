package org.vpac.grisu.frontend.model.job;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.vpac.grisu.control.DefaultResubmitPolicy;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ResubmitPolicy;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.SpecificJobsResubmitPolicy;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.clientexceptions.FileTransferException;
import org.vpac.grisu.frontend.control.jobMonitoring.RunningJobManager;
import org.vpac.grisu.frontend.model.events.BatchJobEvent;
import org.vpac.grisu.frontend.model.events.BatchJobKilledEvent;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoActionStatus;
import org.vpac.grisu.model.dto.DtoBatchJob;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.job.JobMonitoringObject;
import org.vpac.grisu.model.status.StatusObject;
import org.vpac.grisu.settings.ClientPropertiesManager;

import au.org.arcs.jcommons.constants.Constants;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.google.common.collect.ImmutableList;

/**
 * @author markus
 *
 */
/**
 * @author markus
 * 
 */
public class BatchJobObject implements JobMonitoringObject,
Comparable<BatchJobObject> {

	static final Logger myLogger = Logger.getLogger(BatchJobObject.class
			.getName());

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	// properties
	public static final String STATUS = "status";
	public static final String FINISHED = "finished";
	public static final String FAILED = "failed";
	public static final String REFRESHING = "refreshing";
	public static final String RESUBMITTING = "resubmitting";
	public static final String NUMBER_OF_FAILED_JOBS = "numberOfFailedJobs";
	public static final String NUMBER_OF_FINISHED_JOBS = "numberOfFinishedJobs";
	public static final String NUMBER_OF_RUNNING_JOBS = "numberOfRunningJobs";
	public static final String NUMBER_OF_WAITING_JOBS = "numberOfWaitingJobs";
	public static final String NUMBER_OF_SUCCESSFULL_JOBS = "numberOfSuccessfulJobs";
	public static final String NUMBER_OF_UNSUBMITTED_JOBS = "numberOfUnsubmittedJobs";
	public static final String TOTAL_NUMBER_OF_JOBS = "totalNumberOfJobs";

	public static final int UNDEFINED = Integer.MIN_VALUE;

	public static final int DEFAULT_JOB_CREATION_RETRIES = 5;

	public static final int DEFAULT_JOB_CREATION_THREADS = 5;


	private Thread refreshThread;


	private int concurrentJobCreationThreads = 0;

	private int concurrentFileUploadThreads = 0;

	private final ServiceInterface serviceInterface;
	private final String batchJobname;

	private String submissionFqan;

	private final EventList<JobObject> jobs = new BasicEventList<JobObject>();

	private final List<JobObject> newlyAddedJobs = Collections.synchronizedList(new LinkedList<JobObject>());

	private final Map<String, String> inputFiles = new HashMap<String, String>();

	private DtoBatchJob dtoMultiPartJob = null;
	private String[] submissionLocationsToInclude;

	private String[] submissionLocationsToExclude;
	private int maxWalltimeInSecondsAcrossJobs = 3600;

	private int defaultWalltime = 3600;
	private String defaultApplication = Constants.GENERIC_APPLICATION_NAME;

	private String defaultVersion = Constants.NO_VERSION_INDICATOR_STRING;

	private int defaultNoCpus = 1;

	private String[] allRemoteJobnames;

	private boolean isRefreshing = false;
	private boolean isResubmitting = false;
	private boolean isBeingKilled = false;
	private boolean isKilled = false;

	private String optimizationResult = null;

	/**
	 * Use this constructor to create a MultiPartJobObject for a multipartjob
	 * that already exists on the backend.
	 * 
	 * @param serviceInterface
	 *            the serviceinterface
	 * @param batchJobname
	 *            the id of the multipartjob
	 * @param refreshJobStatusOnBackend
	 *            whether to refresh the status of the jobs on the backend.
	 *            might take quite a while...
	 * 
	 * @throws BatchJobException
	 *             if one of the jobs of the multipartjob doesn't exist on the
	 *             backend
	 * @throws NoSuchJobException
	 *             if there is no such multipartjob on the backend
	 */
	public BatchJobObject(ServiceInterface serviceInterface,
			String batchJobname, boolean refreshJobStatusOnBackend)
	throws BatchJobException, NoSuchJobException {

		this.serviceInterface = serviceInterface;
		this.batchJobname = batchJobname;
		try {

			dtoMultiPartJob = getWrappedDtoBatchJob(refreshJobStatusOnBackend);

			if ( dtoMultiPartJob == null ) {
				throw new NoSuchJobException("Could not access batchjob "+batchJobname+" on the backend.");
			}

			this.submissionFqan = dtoMultiPartJob.getSubmissionFqan();

			setDefaultApplication(serviceInterface.getJobProperty(
					this.batchJobname, Constants.APPLICATIONNAME_KEY));
			setDefaultVersion(serviceInterface.getJobProperty(
					this.batchJobname, Constants.APPLICATIONVERSION_KEY));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	/**
	 * Use this constructor if you want to create a new multipartjob.
	 * 
	 * @param serviceInterface
	 *            the serviceinterface
	 * @param batchJobname
	 *            the id of the multipartjob
	 * @param submissionFqan
	 *            the VO to use to submit the jobs of this multipartjob
	 * @throws BatchJobException
	 *             if the multipartjob can't be created
	 */
	public BatchJobObject(ServiceInterface serviceInterface,
			String batchJobname, String submissionFqan,
			String defaultApplication, String defaultVersion)
	throws BatchJobException {
		this.serviceInterface = serviceInterface;
		this.batchJobname = batchJobname;
		this.submissionFqan = submissionFqan;

		dtoMultiPartJob = serviceInterface.createBatchJob(this.batchJobname,
				this.submissionFqan);

		if (StringUtils.isBlank(defaultApplication)) {
			defaultApplication = Constants.GENERIC_APPLICATION_NAME;
		}
		if (StringUtils.isBlank(defaultVersion)) {
			defaultVersion = Constants.NO_VERSION_INDICATOR_STRING;
		}
		setDefaultApplication(defaultApplication);
		setDefaultVersion(defaultVersion);

	}

	/**
	 * Adds an input file to the pool of shared input files for this multipart
	 * job.
	 * 
	 * Those get staged in to the common directory on every site that runs parts
	 * of this multipartjob. You can access the relative path from each job
	 * directory via the {@link #pathToInputFiles()} method. The original
	 * filename is used.
	 * 
	 * @param inputFile
	 *            the input file
	 */
	public void addInputFile(String inputFile) {

		inputFile = FileManager.ensureUriFormat(inputFile);

		if (FileManager.isLocal(inputFile)) {
			inputFiles.put(inputFile, new File(inputFile).getName());
		} else {
			FileManager.getFilename(inputFile);
			inputFiles.put(inputFile, FileManager.getFilename(inputFile));
		}
	}

	/**
	 * Adds an input file to the pool of shared input files for this multipart
	 * job.
	 * 
	 * Those get staged in to the common directory on every site that runs parts
	 * of this multipartjob. You can access the relative path from each job
	 * directory via the {@link #pathToInputFiles()} method.
	 * 
	 * @param inputFile
	 *            the input file
	 * @param targetFilename
	 *            the filename in the common directory
	 */
	public void addInputFile(String inputFile, String targetFilename) {
		inputFile = FileManager.ensureUriFormat(inputFile);
		inputFiles.put(inputFile, targetFilename);
	}

	/**
	 * Adds a new job object to this multipart job.
	 * 
	 * @param job
	 *            the new job object
	 */
	public void addJob(JobObject job) throws IllegalArgumentException {

		if (getJobs().contains(job)) {
			throw new IllegalArgumentException("Job: " + job.getJobname()
					+ " already part of this multiPartJob.");
		}

		if (Arrays.binarySearch(getAllRemoteJobnames(), job.getJobname()) >= 0) {
			throw new IllegalArgumentException("Job: " + job.getJobname()
					+ " already exists on the backend.");
		}

		if (job.getWalltimeInSeconds() <= 0) {
			EventBus.publish(this.batchJobname, new BatchJobEvent(this,
					"Setting walltime for job " + job.getJobname()
					+ " to default walltime: " + defaultWalltime));
			job.setWalltimeInSeconds(defaultWalltime);
		} else {

			if (job.getWalltimeInSeconds() > maxWalltimeInSecondsAcrossJobs) {
				maxWalltimeInSecondsAcrossJobs = job.getWalltimeInSeconds();
			}
		}
		EventBus.publish(this.batchJobname, new BatchJobEvent(this,
				"Adding job " + job.getJobname()));
		int oldNo = this.getJobs().size();
		this.getJobs().add(job);
		this.getNewlyAddedJobs().add(job);

		pcs.firePropertyChange("totalNumberOfJobs", oldNo, this.getJobs().size());
		pcs.firePropertyChange("jobs", null, getJobs());

	}

	/**
	 * Adds a job property to this job.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addJobProperty(String key, String value) {

		try {
			serviceInterface.addJobProperty(batchJobname, key, value);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	private void checkInterruptedStatus(ExecutorService executor,
			List<Future<?>> tasks) throws InterruptedException {

		if (Thread.currentThread().isInterrupted()) {
			executor.shutdownNow();

			for (Future<?> f : tasks) {
				f.cancel(true);
			}

			throw new InterruptedException("Upload input files interrupted.");
		}

	}

	public int compareTo(BatchJobObject o) {

		return this.getJobname().compareTo(o.getJobname());
	}

	public Set<String> currentlyUsedSubmissionLocations() {

		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new HashSet<String>();
		}
		return temp.currentlyUsedSubmissionLocations();
	}

	/**
	 * Downloads all the required results for this batch job.
	 * 
	 * @param onlyDownloadWhenFinished
	 *            only download results if the (single) job is finished.
	 * @param parentFolder
	 *            the folder to download all the results to
	 * @param patterns
	 *            a list of patterns that specify which files to download
	 * @param createSeperateFoldersForEveryJob
	 *            whether to create a seperete folder for every job (true) or
	 *            download everything into the same folder (false)
	 * @param prefixWithJobname
	 *            whether to prefix downloaded results with the jobname it
	 *            belongs to (true) or not (false)
	 * @throws RemoteFileSystemException
	 *             if a remote filetransfer fails
	 * @throws FileTransferException
	 *             if a filetransfer fails
	 * @throws IOException
	 *             if a file can't be saved
	 */
	public void downloadResults(boolean onlyDownloadWhenFinished,
			File parentFolder, String[] patterns,
			boolean createSeperateFoldersForEveryJob, boolean prefixWithJobname)
	throws RemoteFileSystemException, FileTransferException,
	IOException {

		EventBus.publish(this.batchJobname, new BatchJobEvent(this,
				"Checking and possibly downloading output files for batchjob: "
				+ batchJobname + ". This might take a while..."));

		for (JobObject job : getJobs()) {

			if (onlyDownloadWhenFinished && !job.isFinished(false)) {
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
						needsDownloading = GrisuRegistryManager.getDefault(
								serviceInterface).getFileManager()
								.needsDownloading(child);
					} catch (RuntimeException e) {
						myLogger.error("Could not access file " + child + ": "
								+ e.getLocalizedMessage());
						EventBus.publish(this.batchJobname, new BatchJobEvent(
								this, "Could not access file " + child + ": "
								+ e.getLocalizedMessage()));
						continue;
					}

					if (needsDownloading) {
						myLogger.debug("Downloading file: " + child);
						EventBus.publish(this.batchJobname, new BatchJobEvent(
								this, "Downloading file: " + child));
						try {
							cacheFile = GrisuRegistryManager.getDefault(
									serviceInterface).getFileManager()
									.downloadFile(child);
						} catch (Exception e) {
							myLogger.error("Could not download file " + child
									+ ": " + e.getLocalizedMessage());
							EventBus.publish(this.batchJobname,
									new BatchJobEvent(this,
											"Could not download file " + child
											+ ": "
											+ e.getLocalizedMessage()));
							continue;
						}
					} else {
						cacheFile = GrisuRegistryManager.getDefault(
								serviceInterface).getFileManager()
								.getLocalCacheFile(child);
					}
					String targetfilename = null;
					if (prefixWithJobname) {
						targetfilename = job.getJobname() + "_"
						+ cacheFile.getName();
					} else {
						targetfilename = cacheFile.getName();
					}
					if (createSeperateFoldersForEveryJob) {
						FileUtils.copyFile(cacheFile,
								new File(new File(parentFolder, job
										.getJobname()), targetfilename));
					} else {
						FileUtils.copyFile(cacheFile, new File(parentFolder,
								targetfilename));
					}
				}
			}

		}

	}

	/**
	 * Returns all failed jobs.
	 * 
	 * @return all failed jobs
	 */
	public SortedSet<DtoJob> failedJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new TreeSet<DtoJob>();
		}
		return temp.failedJobs();
	}

	/**
	 * Returns all finished jobs.
	 * 
	 * @return all finished jobs
	 */
	public SortedSet<DtoJob> finishedJobs() {
		return getWrappedDtoBatchJob(false).finishedJobs();
	}

	private String[] getAllRemoteJobnames() {
		if (allRemoteJobnames == null) {
			allRemoteJobnames = serviceInterface.getAllJobnames(null).asArray();
			Arrays.sort(allRemoteJobnames);
		}
		return allRemoteJobnames;
	}

	public String getApplication() {
		return getDefaultApplication();
	}

	/**
	 * This method returns how many input file upload threads run at the same
	 * time.
	 * 
	 * @return the number of threads
	 */
	public int getConcurrentInputFileUploadThreads() {
		if (concurrentFileUploadThreads <= 0) {
			return ClientPropertiesManager.getConcurrentUploadThreads();
		} else {
			return concurrentFileUploadThreads;
		}
	}

	/**
	 * This method returns how many job submission threads run at the same time.
	 * 
	 * @return the number of threads
	 */
	public int getConcurrentJobCreationThreads() {
		if (concurrentJobCreationThreads <= 0) {
			return DEFAULT_JOB_CREATION_THREADS;
		} else {
			return concurrentJobCreationThreads;
		}
	}

	public Set<String> getCurrentlyRunningOrSuccessfullSubmissionLocations() {

		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new HashSet<String>();
		}
		return temp.currentlyRunningOrSuccessfullSubmissionLocations();
	}

	public Set<String> getCurrentlyUsedSubmissionLocations() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new HashSet<String>();
		}
		return temp.currentlyUsedSubmissionLocations();
	}

	/**
	 * Returns the default application for this multipart job.
	 * 
	 * @return the default application
	 */
	public String getDefaultApplication() {
		return defaultApplication;
	}

	/**
	 * Gets the default number of cpus.
	 * 
	 * This is used internally to use mds to calculate job distribution.
	 * 
	 * @return the default number of cpus across jobs
	 */
	public int getDefaultNoCpus() {

		return defaultNoCpus;
	}

	/**
	 * Gets the default version for this multipart job.
	 * 
	 * This is used internally to use mds to calculate job distribution.
	 * 
	 * @return the default version
	 */
	public String getDefaultVersion() {
		return defaultVersion;
	}

	/**
	 * If a defaultWalltime is set, this method returns it.
	 * 
	 * @return the default walltime.
	 */
	public int getDefaultWalltime() {
		return this.defaultWalltime;
	}

	/**
	 * A summary of the status of this job.
	 * 
	 * @return a job status detail string
	 */
	public String getDetails() {

		DtoBatchJob temp = getWrappedDtoBatchJob(false);

		if (temp == null ) {
			return "Could not find job.";
		}

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

	/**
	 * Returns the fqan that is used for this multipartjob.
	 * 
	 * @return the fqan
	 */
	public String getFqan() {
		return submissionFqan;
	}

	/**
	 * Returns all the input files that are shared among the jobs of this
	 * multipart job.
	 * 
	 * @return the urls of all the input files (local & remote)
	 */
	public Map<String, String> getInputFiles() {
		return inputFiles;
	}

	/**
	 * The name of this batchjob.
	 * 
	 * @return the id
	 */
	public String getJobname() {
		return batchJobname;
	}

	/**
	 * Retrieves a list of all jobs that are part of this multipart job.
	 * 
	 * @return all jobs
	 */
	public EventList<JobObject> getJobs() {

		return this.jobs;
	}

	/**
	 * Returns all the log messages for this batchjob.
	 * 
	 * @param refresh
	 *            whether to refresh the job on the backend or not
	 * @return the log messages
	 */
	public Map<Date, String> getLogMessages(boolean refresh) {

		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new TreeMap<Date, String>();
		}
		return temp.messages();

	}

	/**
	 * Gets the maximum walltime for this multipartjob.
	 * 
	 * This is used internally to calculate the job distribution. If it is not
	 * set manually the largest single job walltime is used.
	 * 
	 * @return the max walltime in seconds
	 */
	public int getMaxWalltimeInSeconds() {
		return maxWalltimeInSecondsAcrossJobs;
	}

	private List<JobObject> getNewlyAddedJobs() {

		return newlyAddedJobs;

	}

	/**
	 * The number of failed jobs for this multipart job.
	 * 
	 * @return the number of failed jobs
	 */
	public int getNumberOfFailedJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return 0;
		}
		return temp.numberOfFailedJobs();
	}

	/**
	 * The number of finished jobs for this multipart job.
	 * 
	 * @return the number of finished jobs
	 */
	public int getNumberOfFinishedJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return 0;
		}
		return temp.numberOfFinishedJobs();
	}

	/**
	 * The number of running jobs for this multipart job.
	 * 
	 * @return the number of running jobs
	 */
	public int getNumberOfRunningJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return 0;
		}
		return temp.numberOfRunningJobs();
	}

	/**
	 * The number of successful jobs for this multipart job.
	 * 
	 * @return the number of successful jobs
	 */
	public int getNumberOfSuccessfulJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return 0;
		}
		return temp.numberOfSuccessfulJobs();
	}

	/**
	 * The number of unsubmitted jobs for this multipart job.
	 * 
	 * @return the number of unsubmitted jobs
	 */
	public int getNumberOfUnsubmittedJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return 0;
		}
		return temp.numberOfUnsubmittedJobs();
	}

	/**
	 * The number of waiting jobs for this multipart job.
	 * 
	 * @return the number of waiting jobs
	 */
	public int getNumberOfWaitingJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return 0;
		}
		return temp.numberOfWaitingJobs();
	}

	/**
	 * Info about how many jobs were submitted to which submission location.
	 * 
	 * You need to call this sometime after the
	 * {@link #prepareAndCreateJobs(boolean)} method. You need to use the same
	 * BatchJobObject object where you called this method. If you re-create the
	 * object, this info will be lost.
	 * 
	 * @return info about job distribution
	 */
	public String getOptimizationResult() {
		return optimizationResult;
	}

	/**
	 * Displays a summary of the job status.
	 * 
	 * @param restarter
	 *            an (optional) FailedJobRestarter to restart failed jobs or
	 *            null
	 * @return the progress summary
	 */
	public String getProgress() {

		DtoBatchJob temp;
		temp = getWrappedDtoBatchJob(false);

		if ( temp == null ) {
			return "Could not access job.";
		}

		StringBuffer output = new StringBuffer();

		output.append("Total number of jobs: " + temp.totalNumberOfJobs()
				+ "\n");
		output.append("Waiting jobs: " + temp.numberOfWaitingJobs() + "\n");
		output.append("Active jobs: " + temp.numberOfRunningJobs() + "\n");
		output.append("Successful jobs: " + temp.numberOfSuccessfulJobs()
				+ "\n");
		output.append("Failed jobs: " + temp.numberOfFailedJobs() + "\n");
		if (temp.numberOfFailedJobs() > 0) {

			//			if (restarter != null) {
			//				restartFailedJobs(restarter);
			//			}

		} else {
			output.append("\n");
		}
		output.append("Unsubmitted jobs: " + temp.numberOfUnsubmittedJobs()
				+ "\n");

		return output.toString();
	}

	/**
	 * Returns all the properties for this multipartjob.
	 * 
	 * This method doesn't refresh the underlying object, you might want to do
	 * that yourself in some cases.
	 * 
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new TreeMap<String, String>();
		}
		return temp.propertiesAsMap();
	}

	/**
	 * Gets a job property for this job.
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	public String getProperty(String key) {

		try {
			return serviceInterface.getJobProperty(batchJobname, key);
		} catch (NoSuchJobException e) {
			throw new RuntimeException();
		}
	}

	public int getStatus(boolean refresh) {

		DtoBatchJob temp = getWrappedDtoBatchJob(refresh);
		if ( temp == null ) {
			return JobConstants.UNDEFINED;
		}
		return temp.getStatus();
	}

	public Map<Integer, Map<String, Integer>> getStatusMap() {

		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new TreeMap<Integer, Map<String, Integer>>();
		}
		return temp.statusMap();

	}

	public int getTotalNumberOfJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return 0;
		}
		return temp.totalNumberOfJobs();
	}

	private DtoBatchJob getWrappedDtoBatchJob(boolean refresh) {
		return getWrappedDtoBatchJob(refresh, true);
	}

	private DtoBatchJob getWrappedDtoBatchJob(final boolean refresh,
			final boolean waitForRefresh) {

		if ( isKilled || isBeingKilled ) {
			myLogger.debug("Job is or is being killed. Not updating dtoobject...");
			return dtoMultiPartJob;
		}

		if ( ( (dtoMultiPartJob == null) || (! isRefreshing() && refresh)) || (refreshThread == null) || ((refreshThread != null) && ! refreshThread.isAlive()) ) {

			if ((dtoMultiPartJob == null) || refresh ) {

				refreshThread = new Thread() {
					@Override
					public void run() {
						try {
							if (refresh) {
								try {
									refreshMultiPartJobStatus(true);
								} catch (InterruptedException e) {
									return;
								}
							}

							// getJobs().clear();
							int oldTotalJobs = UNDEFINED;
							int oldStatus = UNDEFINED;
							int oldRunningJobs = UNDEFINED;
							int oldWaitingJobs = UNDEFINED;
							int oldFinishedJobs = UNDEFINED;
							int oldFailedJobs = UNDEFINED;
							int oldSuccessJobs = UNDEFINED;
							int oldUnsubmittedJobs = UNDEFINED;
							boolean oldFailed = false;
							boolean oldFinished = false;

							if (dtoMultiPartJob != null) {
								oldTotalJobs = dtoMultiPartJob.totalNumberOfJobs();
								oldRunningJobs = dtoMultiPartJob
								.numberOfRunningJobs();
								oldWaitingJobs = dtoMultiPartJob
								.numberOfWaitingJobs();
								oldFinishedJobs = dtoMultiPartJob
								.numberOfFinishedJobs();
								oldFailedJobs = dtoMultiPartJob
								.numberOfFailedJobs();
								oldSuccessJobs = dtoMultiPartJob
								.numberOfSuccessfulJobs();
								oldUnsubmittedJobs = dtoMultiPartJob
								.numberOfUnsubmittedJobs();
								oldStatus = dtoMultiPartJob.getStatus();
								oldFailed = dtoMultiPartJob.failed();
								oldFinished = dtoMultiPartJob.isFinished();
							}

							dtoMultiPartJob = serviceInterface
							.getBatchJob(batchJobname);

							pcs.firePropertyChange(TOTAL_NUMBER_OF_JOBS,
									oldTotalJobs, dtoMultiPartJob
									.totalNumberOfJobs());
							pcs.firePropertyChange(STATUS, oldStatus,
									dtoMultiPartJob.getStatus());
							pcs.firePropertyChange(FAILED, oldFailed,
									dtoMultiPartJob.failed());
							pcs.firePropertyChange(FINISHED, oldFinished,
									dtoMultiPartJob.isFinished());
							pcs.firePropertyChange(NUMBER_OF_RUNNING_JOBS,
									oldRunningJobs, dtoMultiPartJob
									.numberOfRunningJobs());
							pcs.firePropertyChange(NUMBER_OF_WAITING_JOBS,
									oldWaitingJobs, dtoMultiPartJob
									.numberOfWaitingJobs());
							pcs.firePropertyChange(NUMBER_OF_FINISHED_JOBS,
									oldFinishedJobs, dtoMultiPartJob
									.numberOfFinishedJobs());
							pcs.firePropertyChange(NUMBER_OF_FAILED_JOBS,
									oldFailedJobs, dtoMultiPartJob
									.numberOfFailedJobs());
							pcs.firePropertyChange(NUMBER_OF_SUCCESSFULL_JOBS,
									oldSuccessJobs, dtoMultiPartJob
									.numberOfSuccessfulJobs());
							pcs.firePropertyChange(NUMBER_OF_UNSUBMITTED_JOBS,
									oldUnsubmittedJobs, dtoMultiPartJob
									.numberOfUnsubmittedJobs());

							RunningJobManager.updateJobList(serviceInterface,
									getJobs(), dtoMultiPartJob.getJobs());

						} catch (NoSuchJobException e) {
							e.printStackTrace();
							if ( isBeingKilled || isKilled ) {
								return;
							}
							throw new RuntimeException(e);
						}
					}
				};

				refreshThread.start();
			}

			if ( (refreshThread != null) && ((dtoMultiPartJob == null) || waitForRefresh)) {
				try {
					refreshThread.join();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return dtoMultiPartJob;
	}

	public boolean isBatchJob() {
		return true;
	}
	public boolean isBeingKilled() {
		return isBeingKilled;
	}

	/**
	 * Returns whether all jobs within this multipart job are finished (failed
	 * or not).
	 * 
	 * @param refresh
	 *            whether to refresh all jobs on the backend
	 * @return whether all jobs are finished or not.
	 */
	public boolean isFinished(boolean refresh) {
		try {
			DtoBatchJob temp = getWrappedDtoBatchJob(refresh);
			if ( temp == null ) {
				return false;
			}
			return temp.isFinished();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public boolean isKilled() {
		return isKilled;
	}

	public boolean isRefreshing() {
		return this.isRefreshing;
	}

	public boolean isResubmitting() {
		return isResubmitting;
	}

	/**
	 * Returns whether all jobs within this multipart job finished successfully.
	 * 
	 * @param refresh
	 *            whether to refresh all jobs on the backend
	 * @return whether all jobs finished successfully
	 */
	public boolean isSuccessful(boolean refresh) {
		DtoBatchJob temp = getWrappedDtoBatchJob(refresh);
		if ( temp == null ) {
			return false;
		}
		return temp.allJobsFinishedSuccessful();
	}

	public void kill(boolean clean, boolean waitForCompletion) {

		if ( refreshThread != null ) {
			try {
				refreshThread.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		setIsBeingKilled(true);
		try {
			serviceInterface.kill(this.getJobname(), clean);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Thread waitThread = new Thread() {
			@Override
			public void run() {
				StatusObject statusO = new StatusObject(serviceInterface, getJobname());
				try {
					statusO.waitForActionToFinish(3, false, true);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				dtoMultiPartJob = null;
				setIsBeingKilled(false);
				isKilled = true;
				pcs.firePropertyChange("killed", false, true);

				EventBus.publish(new BatchJobKilledEvent(getJobname(), getApplication()));

			}
		};

		waitThread.start();

		if ( waitForCompletion ) {
			try {
				waitThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


	}

	/**
	 * Monitors the status of all jobs of this multipartjob.
	 * 
	 * If you want to restart failed jobs while this is running, provide a
	 * {@link FailedJobRestarter}. Prints out a lot of verbose information.
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
			boolean forceSuccess) {
		boolean finished = false;
		do {

			refresh();
			String progress = getProgress();

			DtoBatchJob temp;
			temp = getWrappedDtoBatchJob(false);

			if (forceSuccess) {
				finished = temp.allJobsFinishedSuccessful();
			} else {
				finished = temp.isFinished();
			}

			if (finished || ((enddate != null) && new Date().after(enddate))) {
				break;
			}

			System.out.println(progress);

			for (Date date : getLogMessages(false).keySet()) {
				System.out.println(date.toString() + ": "
						+ getLogMessages(false).get(date));
			}

			EventBus.publish(this.batchJobname, new BatchJobEvent(this,
					progress));

			try {
				EventBus.publish(this.batchJobname, new BatchJobEvent(this,
						"Pausing monitoring for " + sleeptimeinseconds
						+ " seconds..."));
				Thread.sleep(sleeptimeinseconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!finished);
	}

	/**
	 * Calculates the relative path from each of the job directories to the
	 * common input file directory for this multipart job.
	 * 
	 * This can be used to create the commandline for each of the part jobs.
	 * 
	 * @return the absolute path
	 */
	public String pathToInputFiles() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			throw new RuntimeException("Could not access batchjob "+batchJobname);
		}
		return temp.pathToInputFiles();
	}

	/**
	 * Prepares all jobs and creates them on the backend.
	 * 
	 * Internally, this method first adds all the jobs to the batchjob on the
	 * backend. Then you can choose to optimize the batchpart job which means
	 * the backend will recalulate all the submission locations based on the
	 * number of total jobs and it will try to distribute them according to load
	 * to all the available sites. After that all the input files are staged in.
	 * 
	 * @param optimize
	 *            whether to optimize the job distribution (true) or use the
	 *            submission locations you specified (or which are set by
	 *            default -- false)
	 * @throws JobsException
	 *             if one or more jobs can't be created
	 * @throws BackendException
	 *             if something fails on the backend
	 * @throws InterruptedException
	 */
	public void prepareAndCreateJobs(boolean optimize) throws JobsException,
	BackendException, InterruptedException {
		prepareAndCreateJobs(optimize, true);
	}


	private void prepareAndCreateJobs(boolean optimize, boolean uploadCommonFiles) throws JobsException,
	BackendException, InterruptedException {

		// TODO check whether any of the jobnames already exist

		myLogger.debug("Creating " + getNewlyAddedJobs().size()
				+ " jobs as part of batchjob: " + batchJobname);
		EventBus.publish(this.batchJobname, new BatchJobEvent(this, "Creating "
				+ getNewlyAddedJobs().size() + " jobs"));
		ExecutorService executor = Executors
		.newFixedThreadPool(getConcurrentJobCreationThreads());

		final Map<JobObject, Exception> failedSubmissions = Collections
		.synchronizedMap(new HashMap<JobObject, Exception>());

		for (final JobObject job : ImmutableList.copyOf(getNewlyAddedJobs())) {

			if (Thread.interrupted()) {
				executor.shutdownNow();
				throw new InterruptedException(
				"Interrupted while creating jobs...");
			}

			Thread createThread = new Thread() {
				@Override
				public void run() {
					boolean success = false;
					Exception lastException = null;
					for (int i = 0; i < DEFAULT_JOB_CREATION_RETRIES; i++) {
						try {

							myLogger.info("Adding job: " + job.getJobname()
									+ " to batchjob: " + batchJobname);

							String jobname = null;
							EventBus
							.publish(
									BatchJobObject.this.batchJobname,
									new BatchJobEvent(
											BatchJobObject.this,
											"Adding job "
											+ job.getJobname()
											+ " to batchjob on backend."));
							jobname = serviceInterface.addJobToBatchJob(
									batchJobname,
									job.getJobDescriptionDocumentAsString());

							job.setJobname(jobname);
							job.updateJobDirectory();

							EventBus.publish(BatchJobObject.this.batchJobname,
									new BatchJobEvent(BatchJobObject.this,
											"Creation of job "
											+ job.getJobname()
											+ " successful."));

							success = true;
							break;
						} catch (Exception e) {
							// e.printStackTrace();
							EventBus
							.publish(
									BatchJobObject.this.batchJobname,
									new BatchJobEvent(
											BatchJobObject.this,
											"Creation of job "
											+ job.getJobname()
											+ " failed or interrupted.\n\t("
											+ e
											.getLocalizedMessage()
											+ ")")
									+ "\n");

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
			myLogger.debug("Preparing and creation of jobs cancelled.");
			executor.shutdownNow();
			throw new InterruptedException("Interrupted while creating jobs...");
		}
		myLogger.debug("Finished creation of " + getNewlyAddedJobs().size()
				+ " jobs as part of batchjob: " + batchJobname);
		EventBus.publish(this.batchJobname, new BatchJobEvent(this,
				"Finished creation of " + getNewlyAddedJobs().size()
				+ " jobs as part of batchjob: " + batchJobname));

		if (failedSubmissions.size() > 0) {
			myLogger.error(failedSubmissions.size() + " submission failed...");
			EventBus.publish(this.batchJobname, new BatchJobEvent(this,
					"Not all jobs for batchjob " + batchJobname
					+ " created successfully. Aborting..."));
			throw new JobsException(failedSubmissions);
		}

		if (Thread.interrupted()) {
			throw new InterruptedException(
			"Interrupted after creating all jobs.");
		}

		refresh(false);

		if (optimize) {
			try {
				EventBus.publish(this.batchJobname, new BatchJobEvent(this,
						"Optimizing batchjob: " + batchJobname));
				try {
					serviceInterface.redistributeBatchJob(
							this.batchJobname);


					StatusObject status = new StatusObject(serviceInterface,
							BatchJobObject.this.batchJobname);

					try {
						status.waitForActionToFinish(4, false, true, "Redistribution: ");
					} catch (InterruptedException e) {
						e.printStackTrace();
						throw e;
					}

					EventBus.publish(this.batchJobname, new BatchJobEvent(this,	"Redistribution finished."));

					optimizationResult = serviceInterface.getJobProperty(this.batchJobname, Constants.BATCHJOB_OPTIMIZATION_RESULT);

					if (Thread.interrupted()) {
						throw new InterruptedException(
						"Interrupted after creating all jobs.");
					}

				} catch (JobPropertiesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(e);
				}

				for (JobObject job : getNewlyAddedJobs()) {
					job.updateJobDirectory();
				}


				EventBus
				.publish(
						this.batchJobname,
						new BatchJobEvent(
								this,
								"Optimizing of batchjob "
								+ batchJobname
								+ " finished.\nJob distribution:\n"
								+ getOptimizationResult()));
			} catch (NoSuchJobException e) {
				throw new RuntimeException(e);
			}
		}

		refresh(false);

		try {
			uploadInputFiles(uploadCommonFiles);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			EventBus.publish(this.batchJobname, new BatchJobEvent(this,
					"Uploading input files for batchjob: " + batchJobname
					+ " failed: " + e.getLocalizedMessage()));
			throw new BackendException("Could not upload input files...", e);
		}
		getNewlyAddedJobs().clear();
	}

	/**
	 * Refresh all jobs on the backend.
	 * 
	 * Waits for the refresh to finish.
	 */
	public void refresh() {
		refresh(true);
	}

	public void refresh(boolean wait) {
		getWrappedDtoBatchJob(true, wait);
	}

	private void refreshMultiPartJobStatus(boolean waitForRefreshToFinish) throws InterruptedException {

		String handle;
		try {
			handle = serviceInterface.refreshBatchJobStatus(batchJobname);
		} catch (NoSuchJobException e) {

			this.isRefreshing = false;
			pcs.firePropertyChange(REFRESHING, true, false);
			myLogger.error(e);
			return;
		}

		if (waitForRefreshToFinish) {


			if (Thread.interrupted()) {
				this.isRefreshing = false;
				pcs.firePropertyChange(REFRESHING, true, false);
				Thread.currentThread().interrupt();
				throw new InterruptedException("Batch job refresh interrupted.");
			}
			this.isRefreshing = true;
			pcs.firePropertyChange(REFRESHING, false, true);
			DtoActionStatus status = serviceInterface.getActionStatus(handle);

			if ( status == null ) {
				this.isRefreshing = false;
				pcs.firePropertyChange(REFRESHING, true, false);
				myLogger.error("Can't get status for handle: "+handle);
				return;
			}

			while ( !status.isFinished()) {

				try {
					Thread.sleep(ClientPropertiesManager
							.getJobStatusRecheckIntervall()*1000);
				} catch (InterruptedException e) {
					this.isRefreshing = false;
					pcs.firePropertyChange(REFRESHING, true, false);
					throw e;
				}



				status = serviceInterface.getActionStatus(handle);

				if ( status == null ) {
					this.isRefreshing = false;
					pcs.firePropertyChange(REFRESHING, true, false);
					myLogger.error("Can't get status for handle: "+handle);
					return;
				}

			}
			this.isRefreshing = false;
			pcs.firePropertyChange(REFRESHING, true, false);
		}

	}

	//	/**
	//	 * Restarts all jobs that failed using the provided
	//	 * {@link FailedJobRestarter}.
	//	 *
	//	 * @param restarter
	//	 *            the job restarter that contains the logic how to restart the
	//	 *            job
	//	 */
	//	public void restartFailedJobs(FailedJobRestarter restarter) {
	//
	//		if (restarter == null) {
	//			restarter = new FailedJobRestarter() {
	//
	//				public void restartJob(JobObject job)
	//				throws JobSubmissionException {
	//					try {
	//						job.restartJob();
	//					} catch (JobPropertiesException e) {
	//						throw new JobSubmissionException("Can't resubmit job: "
	//								+ e.getLocalizedMessage());
	//					}
	//				}
	//			};
	//		}
	//
	//		for (DtoJob dtoJob : getMultiPartJob(true).getFailedJobs().getAllJobs()) {
	//
	//			JobObject failedJob = null;
	//			try {
	//				failedJob = new JobObject(serviceInterface, dtoJob.jobname());
	//				EventBus.publish(this.batchJobname, new BatchJobEvent(this,
	//						"Restarting job " + failedJob.getJobname())
	//				+ "...");
	//				restarter.restartJob(failedJob);
	//			} catch (Exception e) {
	//				if (failedJob != null) {
	//					EventBus.publish(this.batchJobname, new BatchJobEvent(this,
	//							"Restarting of job " + failedJob.getJobname())
	//					+ " failed: " + e.getLocalizedMessage());
	//				} else {
	//					EventBus.publish(this.batchJobname, new BatchJobEvent(this,
	//							"Restarting failed: " + e.getLocalizedMessage()));
	//				}
	//				e.printStackTrace();
	//			}
	//		}
	//
	//	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Wrapper method for the 2 most commonly used restart methods: Restarting failed jobs and restarting jobs that are still in the queue (on locations where no job is started yet).
	 * 
	 * @param restartFailedJobs restart jobs that failed (those jobs are moved to locations where jobs have successfully finished -- if possible)
	 * @param resubmitWaitingJobs restart jobs that sit in queues at locations where no jobs have started yet. Those jobs are moved to locations where jobs are already running or have finished successfully.
	 * @param startUnsubmittedJobs whether to start jobs that are newly added (not submitted at all yet)
	 * @param waitForRestartToFinish whether to wait for the whole batchjob-restart to finish
	 * @return whether the restart was successful or not (i.e. an error occured)
	 * @throws InterruptedException if the creaton of newly added jobs is interrupted
	 * @throws BackendException if something fails on the backend when creating newly adding jobs
	 * @throws JobsException if a newly added job can't be created on the backend
	 */
	public boolean restart(boolean restartFailedJobs, boolean resubmitWaitingJobs, boolean startUnsubmittedJobs, boolean waitForRestartToFinish) throws JobsException, BackendException, InterruptedException {

		DefaultResubmitPolicy policy = new DefaultResubmitPolicy();

		policy.setProperty(DefaultResubmitPolicy.RESTART_FAILED_JOBS,  restartFailedJobs);
		policy.setProperty(DefaultResubmitPolicy.RESTART_WAITING_JOBS, resubmitWaitingJobs);
		policy.setProperty(DefaultResubmitPolicy.START_NEWLY_READY_JOBS, startUnsubmittedJobs);

		return restart(policy, waitForRestartToFinish);

	}


	/**
	 * Restarts the batchjob using the specified ResubmitPolicy
	 * @param policy
	 * @param waitForRestartToFinish
	 * @return
	 * @throws InterruptedException if the creaton of newly added jobs is interrupted
	 * @throws BackendException if something fails on the backend when creating newly adding jobs
	 * @throws JobsException if a newly added job can't be created on the backend
	 */
	public boolean restart(ResubmitPolicy policy, final boolean waitForRestartToFinish) throws JobsException, BackendException, InterruptedException {

		setResubmitting(true);

		prepareAndCreateJobs(false, false);

		try {
			serviceInterface.restartBatchJob(batchJobname,
					policy.getName(), policy.getProperties())
					.propertiesAsMap();
		} catch (NoSuchJobException e) {
			setResubmitting(false);
			return false;
		} catch (JobPropertiesException e) {
			setResubmitting(false);
			return false;
		}

		Thread waitThread = new Thread() {
			@Override
			public void run() {

				String handle = batchJobname;
				DtoActionStatus status = serviceInterface.getActionStatus(handle);
				while (!status.isFinished()) {

					try {
						Thread.sleep(ClientPropertiesManager
								.getJobStatusRecheckIntervall()*1000);
					} catch (InterruptedException e) {
						// doesn't happen
					}
					status = serviceInterface.getActionStatus(handle);
				}

				setResubmitting(false);
			}
		};

		waitThread.start();

		if ( waitForRestartToFinish ) {

			try {
				waitThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		getWrappedDtoBatchJob(true, false);

		return true;
	}

	/**
	 * @param jobnamesToRestart
	 * @param submissionLocationsToUse
	 * @param waitForRestartToFinish
	 * @return
	 * @throws InterruptedException if the creaton of newly added jobs is interrupted
	 * @throws BackendException if something fails on the backend when creating newly adding jobs
	 * @throws JobsException if a newly added job can't be created on the backend
	 */
	public boolean restart(Set<String> jobnamesToRestart, Set<String> submissionLocationsToUse, boolean waitForRestartToFinish) throws JobsException, BackendException, InterruptedException {

		ResubmitPolicy policy = new SpecificJobsResubmitPolicy(jobnamesToRestart, submissionLocationsToUse);

		return restart(policy, waitForRestartToFinish);
	}

	/**
	 * Returns all running jobs.
	 * 
	 * @return all running jobs
	 */
	public SortedSet<DtoJob> runningJobs() {
		return getWrappedDtoBatchJob(false).runningJobs();
	}

	/**
	 * In this method you can specify how many input files are uploaded at the
	 * same time. This can increase job submission time quite considerably.
	 * Default is 1.
	 * 
	 * @param threads
	 *            the number of threads
	 */
	public void setConcurrentInputFileUploadThreads(int threads) {
		this.concurrentFileUploadThreads = threads;
	}

	/**
	 * In this method you can specify how many jobs should be created at the
	 * same time.
	 * 
	 * Normally you don't need that. But you may experience a bit if you have a
	 * lot of jobs to create.
	 * 
	 * @param threads
	 *            the number of threads
	 */
	public void setConcurrentJobCreationThreads(int threads) {
		this.concurrentJobCreationThreads = threads;
	}

	/**
	 * Sets the default application for this multipart job.
	 * 
	 * This is used internally to use mds to calculate the job distribution.
	 * 
	 * @param defaultApplication
	 *            the default application
	 */
	private void setDefaultApplication(String defaultApplication) {
		this.defaultApplication = defaultApplication;

		try {
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.APPLICATIONNAME_KEY, defaultApplication);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}

		for (JobObject job : this.getJobs()) {
			job.setApplication(defaultApplication);
		}
	}

	/**
	 * In this method you can set the default number of cpus.
	 * 
	 * All cpu properties for all jobs that are added at this stage are
	 * overwritten.
	 * 
	 * It is used to calculate job distribution. You can have different numbers
	 * of cpus for each single job, but the metascheduling might become a tad
	 * unpredictable/sub-optimal.
	 * 
	 * @param defaultNoCpus
	 *            the default number of cups
	 */
	public void setDefaultNoCpus(int defaultNoCpus) {
		this.defaultNoCpus = defaultNoCpus;

		try {
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.NO_CPUS_KEY, new Integer(defaultNoCpus)
			.toString());
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}

		for (JobObject job : this.getJobs()) {
			job.setCpus(defaultNoCpus);
		}

	}

	/**
	 * Sets the default version for this multipart job.
	 * 
	 * @param defaultVersion
	 */
	private void setDefaultVersion(String defaultVersion) {
		this.defaultVersion = defaultVersion;

		try {
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.APPLICATIONVERSION_KEY, defaultVersion);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}

		for (JobObject job : this.getJobs()) {
			job.setApplicationVersion(defaultVersion);
		}

	}

	/**
	 * A convenience method to set the same walltime to all jobs.
	 * 
	 * @param walltimeInSeconds
	 *            the walltime.
	 */
	public void setDefaultWalltimeInSeconds(int walltimeInSeconds) {
		this.defaultWalltime = walltimeInSeconds;
		this.maxWalltimeInSecondsAcrossJobs = walltimeInSeconds;

		try {
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.WALLTIME_IN_MINUTES_KEY, new Integer(
							walltimeInSeconds / 60).toString());
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}

		for (JobObject job : this.getJobs()) {
			job.setWalltimeInSeconds(walltimeInSeconds);
		}
	}

	private void setIsBeingKilled(boolean isBeingKilled) {
		boolean old = this.isBeingKilled;
		this.isBeingKilled = isBeingKilled;
		pcs.firePropertyChange("isBeingKilled", old, isBeingKilled);
	}

	/**
	 * Sets the method to distribute the job.
	 * 
	 * Possible methods are: "percentage" (default) and "equal"
	 * 
	 * @param method
	 *            the method
	 */
	public void setJobDistributionMethod(String method) {

		try {
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.DISTRIBUTION_METHOD, method);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Sets a filter to only run jobs at sites that don't match.
	 * 
	 * You can either set use this method or the
	 * {@link #setLocationsToInclude(String[])} one. Only the last one set is
	 * used.
	 * 
	 * @param sites
	 *            a list of simple patterns that specify on which sites to
	 *            exclude to run jobs
	 */
	public void setLocationsToExclude(String[] locationPatterns) {
		if (locationPatterns == null) {
			locationPatterns = new String[] {};
		}

		this.submissionLocationsToExclude = locationPatterns;
		this.submissionLocationsToInclude = null;

		try {
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.LOCATIONS_TO_EXCLUDE_KEY, StringUtils.join(
							locationPatterns, ","));
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.LOCATIONS_TO_INCLUDE_KEY, null);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets a filter to only run jobs at the specified submissionlocations.
	 * 
	 * You can either set use this method or the
	 * {@link #setLocationsToExclude(String[])} one. Only the last one set is
	 * used.
	 * 
	 * @param locationPatterns
	 *            a list of simple pattern that specify on which submission
	 *            locations to run jobs
	 */
	public void setLocationsToInclude(String[] locationPatterns) {

		if (locationPatterns == null) {
			locationPatterns = new String[] {};
		}

		this.submissionLocationsToInclude = locationPatterns;
		this.submissionLocationsToExclude = null;

		try {
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.LOCATIONS_TO_INCLUDE_KEY, StringUtils.join(
							locationPatterns, ","));
			serviceInterface.addJobProperty(this.batchJobname,
					Constants.LOCATIONS_TO_EXCLUDE_KEY, null);
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
	}

	private void setResubmitting(boolean b) {

		if ( isResubmitting == b ) {
			return;
		}

		isResubmitting = b;

		pcs.firePropertyChange(RESUBMITTING, !b, b);

	}

	/**
	 * Tells the backend to submit this batchjob.
	 * 
	 * @throws JobSubmissionException
	 *             if the jobsubmission fails
	 * @throws NoSuchJobException
	 *             if no such job exists on the backend
	 * @throws InterruptedException
	 */
	public void submit() throws JobSubmissionException, NoSuchJobException,
	InterruptedException {

		submit(false);
	}

	public void submit(boolean waitForSubmissionToFinish)
	throws JobSubmissionException, NoSuchJobException,
	InterruptedException {

		EventBus.publish(this.batchJobname, new BatchJobEvent(this,
				"Submitting batchjob " + batchJobname + " to backend..."));

		if (Thread.interrupted()) {
			throw new InterruptedException("Interrupted before job submission.");
		}

		try {
			serviceInterface.submitJob(batchJobname);
		} catch (JobSubmissionException jse) {
			EventBus.publish(this.batchJobname, new BatchJobEvent(this,
					"Job submission for batchjob " + batchJobname + " failed: "
					+ jse.getLocalizedMessage()));
			throw jse;
		} catch (NoSuchJobException nsje) {
			EventBus.publish(this.batchJobname, new BatchJobEvent(this,
					"Job submitssion for batchjob " + batchJobname
					+ " failed: " + nsje.getLocalizedMessage()));
			throw nsje;
		}

		Thread waitThread = new Thread() {
			@Override
			public void run() {
				StatusObject status = new StatusObject(serviceInterface,
						BatchJobObject.this.batchJobname);

				try {
					status.waitForActionToFinish(4, false, true, "Submission status: ");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		waitThread.start();

		if (!waitForSubmissionToFinish) {
			EventBus
			.publish(
					this.batchJobname,
					new BatchJobEvent(
							this,
							"All jobs of batchjob "
							+ batchJobname
							+ " ready for submission. Continuing submission in background..."));
		} else {
			try {
				waitThread.join();
			} catch (InterruptedException e) {
				waitThread.interrupt();
			}

		}
		refresh(false);
	}

	/**
	 * Returns all successful jobs.
	 * 
	 * @return all successful jobs
	 */
	public SortedSet<DtoJob> successfulJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new TreeSet<DtoJob>();
		}
		return temp.successfulJobs();
	}

	@Override
	public String toString() {
		return getJobname();
	}

	/**
	 * Returns all unsubmitted jobs.
	 * 
	 * @return all unsubmitted jobs
	 */
	public SortedSet<DtoJob> unsubmittedJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new TreeSet<DtoJob>();
		}
		return temp.unsubmittedJobs();
	}

	private void uploadInputFiles(boolean uploadCommonFiles) throws InterruptedException,
	FileUploadException {

		final List<Exception> exceptions = Collections
		.synchronizedList(new LinkedList<Exception>());

		final ExecutorService executor = Executors
		.newFixedThreadPool(getConcurrentInputFileUploadThreads());

		final int all = inputFiles.keySet().size();

		EventBus.publish(getJobname(), new BatchJobEvent(BatchJobObject.this,
				"Uploading " + all + " input files ("
				+ getConcurrentInputFileUploadThreads()
				+ " concurrent upload threads.)"));

		List<Future<?>> tasks = new LinkedList<Future<?>>();

		// uploading single job input files

		//		for (final JobObject job : getJobs()) {
		for (final JobObject job : getNewlyAddedJobs()) {

			checkInterruptedStatus(executor, tasks);

			Future<?> f = executor.submit(new Thread() {
				@Override
				public void run() {
					try {
						try {
							job.stageFiles();
						} catch (InterruptedException e) {
							executor.shutdownNow();
						}
					} catch (FileTransferException e) {
						exceptions.add(e);
						executor.shutdownNow();
					}
				}
			});
			tasks.add(f);
		}

		int i = 0;

		if ( uploadCommonFiles ) {
			// uploading common job input files
			for (final String inputFile : inputFiles.keySet()) {

				checkInterruptedStatus(executor, tasks);

				i = i + 1;
				Thread thread = new BatchJobFileUploadThread(serviceInterface,
						this, inputFile, i, executor, exceptions);
				Future<?> f = executor.submit(thread);
				// so the gridftp servers don't get hit at exactly the same time, to
				// distribute the load
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					executor.shutdownNow();
					for (Future<?> f2 : tasks) {
						f2.cancel(true);
					}
					throw new InterruptedException(
					"Interrupted while uploading common input files.");
				}
			}
		}

		executor.shutdown();

		try {
			executor.awaitTermination(10 * 3600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			myLogger.debug("Interrupted....");
			executor.shutdownNow();
			for (Future<?> f : tasks) {
				f.cancel(true);
			}
			throw new InterruptedException(
			"Interrupted while waiting for file uploads to finish.");

		}

		if ((exceptions.size() > 0) && (exceptions.get(0) != null)) {
			throw new FileUploadException(exceptions.get(0));
		}

		EventBus.publish(this.batchJobname, new BatchJobEvent(this,
				"Uploading input files for batchjob: " + batchJobname
				+ " finished."));

	}

	/**
	 * Returns all waiting jobs.
	 * 
	 * @return all waiting jobs
	 */
	public SortedSet<DtoJob> waitingJobs() {
		DtoBatchJob temp = getWrappedDtoBatchJob(false);
		if ( temp == null ) {
			return new TreeSet<DtoJob>();
		}
		return temp.waitingJobs();
	}
}
