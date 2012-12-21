package grisu.frontend.control.jobMonitoring;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.model.events.BatchJobKilledEvent;
import grisu.frontend.model.events.JobCleanedEvent;
import grisu.frontend.model.events.NewBatchJobEvent;
import grisu.frontend.model.events.NewJobEvent;
import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.model.job.JobObject;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.DtoJob;
import grisu.model.dto.DtoJobs;
import grisu.model.dto.GridFile;
import grisu.model.info.dto.DtoStringList;
import grisu.model.status.StatusObject;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.google.common.collect.Sets;

public class RunningJobManager implements EventSubscriber {

	private class UpdateTimerTask extends TimerTask {

		@Override
		public void run() {

			try {

				// update single jobs
				for (final String application : cachedSingleJobsPerApplication
						.keySet()) {
					updateJobnameList(application, false);
				}

				for (final String application : cachedBatchJobsPerApplication
						.keySet()) {
					updateBatchJobList(application);
				}

				final List<BatchJobObject> tempListB = new LinkedList<BatchJobObject>(
						getAllCurrentlyWatchedBatchJobs());
				for (final BatchJobObject bj : tempListB) {
					if (!bj.isFinished(false) && !bj.isRefreshing()
							&& !bj.isBeingKilled()) {
						myLogger.debug("Refreshing job: " + bj.getJobname());
						bj.refresh(true);
					}
				}

				if (!stop) {
					updateTimer.schedule(new UpdateTimerTask(),
							UPDATE_TIME_IN_SECONDS * 1000);
				} else {
					updateTimer.cancel();
				}
			} catch (final Exception e) {
				myLogger.error(e.getLocalizedMessage(), e);
			}
		}

	}

	static final Logger myLogger = LoggerFactory
			.getLogger(RunningJobManager.class.getName());

	public static void updateJobList(ServiceInterface si,
			EventList<JobObject> jobObjectList, DtoJobs newJobs) {

		final Set<JobObject> toRemove = new HashSet<JobObject>();
		final Set<DtoJob> newJobsCopy = new HashSet<DtoJob>(
				newJobs.getAllJobs());

		for (final JobObject jo : jobObjectList) {
			boolean inList = false;

			for (final DtoJob job : newJobs.getAllJobs()) {
				if (jo.getJobname().equals(job.jobname())) {
					inList = true;
					jo.updateWithDtoJob(job);
					newJobsCopy.remove(job);
					break;
				}
			}
			if (!inList) {
				toRemove.add(jo);
			}
		}

		jobObjectList.removeAll(toRemove);

		for (final DtoJob newJob : newJobsCopy) {
			try {
				final JobObject jo = new JobObject(si, newJob);
				jobObjectList.add(jo);
			} catch (final NoSuchJobException e) {
				myLogger.error(e.getLocalizedMessage(), e);
			}
		}

	}

	private final int UPDATE_TIME_IN_SECONDS = 360;

	private static Map<ServiceInterface, RunningJobManager> cachedRegistries = new HashMap<ServiceInterface, RunningJobManager>();

	public static RunningJobManager getDefault(final ServiceInterface si) {

		if (si == null) {
			throw new RuntimeException(
					"ServiceInterface not initialized yet. Can't get default registry...");
		}

		synchronized (si) {
			if (cachedRegistries.get(si) == null) {
				final RunningJobManager m = new RunningJobManager(si);
				cachedRegistries.put(si, m);
			}
		}

		return cachedRegistries.get(si);
	}
	
	public static final Long MIN_WAIT_TIME_SEC = 5L;

	private final UserEnvironmentManager em;
	private final FileManager fm;
	private final ServiceInterface si;

	private boolean watchingAllSingleJobs = false;
	private boolean watchingAllBatchJobs = false;

	private final Map<String, JobObject> cachedAllSingleJobs = Collections
			.synchronizedMap(new HashMap<String, JobObject>());

	private final Map<String, EventList<JobObject>> cachedSingleJobsPerApplication = Collections
			.synchronizedMap(new HashMap<String, EventList<JobObject>>());

	private final Map<String, BatchJobObject> cachedAllBatchJobs = Collections
			.synchronizedMap(new HashMap<String, BatchJobObject>());

	private final Map<String, EventList<BatchJobObject>> cachedBatchJobsPerApplication = Collections
			.synchronizedMap(new HashMap<String, EventList<BatchJobObject>>());

	private final Map<String, Long> jobListAccessTime = Collections.synchronizedMap(new HashMap<String, Long>());
	
	private final Timer updateTimer = new Timer();;

	// private final boolean checkForNewApplicationsForSingleJobs = false;

	private boolean stop = false;

	public RunningJobManager(ServiceInterface si) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();

		EventBus.subscribe(NewJobEvent.class, this);
		EventBus.subscribe(JobCleanedEvent.class, this);
		EventBus.subscribe(BatchJobKilledEvent.class, this);
		EventBus.subscribe(NewBatchJobEvent.class, this);

		startAutoRefresh();
	}

	public synchronized BatchJobObject createBatchJob(String jobname,
			String submissionFqan, String defaultApplication,
			String defaultVersion) throws BatchJobException {

		final BatchJobObject batchJob = new BatchJobObject(si, jobname,
				submissionFqan, defaultApplication, defaultVersion);
		cachedAllBatchJobs.put(jobname, batchJob);
		// EventList<BatchJobObject> temp = cachedBatchJobsPerApplication
		// .get(defaultApplication);
		getBatchJobs(defaultApplication).add(batchJob);
		if (watchingAllBatchJobs) {
			getBatchJobs(Constants.ALLJOBS_KEY).add(batchJob);
		}
		return batchJob;

	}

	public synchronized void createJob(JobObject job)
			throws JobPropertiesException {

		createJob(job, null);
	}

	public synchronized void createJob(JobObject job, String fqan)
			throws JobPropertiesException {

		if (StringUtils.isBlank(fqan)) {
			job.createJob();
		} else {
			job.createJob(fqan);
		}

		cachedAllSingleJobs.put(job.getJobname(), job);
		getJobs(job.getApplication()).add(job);
		// if (watchingAllSingleJobs) {
		// getJobs(Constants.ALLJOBS_KEY).add(job);
		// }
	}

	public EventList<BatchJobObject> getAllBatchJobs() {
		return getBatchJobs(Constants.ALLJOBS_KEY);
	}

	private synchronized Collection<BatchJobObject> getAllCurrentlyWatchedBatchJobs() {

		return cachedAllBatchJobs.values();

	}

	private synchronized Collection<JobObject> getAllCurrentlyWatchedSingleJobs() {

		return cachedAllSingleJobs.values();
	}

	public EventList<JobObject> getAllJobs() {
		return getJobs(Constants.ALLJOBS_KEY);
	}

	public BatchJobObject getBatchJob(String jobname) throws NoSuchJobException {

		synchronized (jobname) {

			if (cachedAllBatchJobs.get(jobname) == null) {

				try {
					final BatchJobObject temp = new BatchJobObject(si, jobname,
							false);
					cachedAllBatchJobs.put(jobname, temp);
				} catch (final BatchJobException e) {
					throw new RuntimeException(e);
				}

			}
			return cachedAllBatchJobs.get(jobname);

		}
	}

	public synchronized EventList<BatchJobObject> getBatchJobs(
			String application) {

		if (StringUtils.isBlank(application)) {
			application = Constants.ALLJOBS_KEY;
		}

		if (Constants.ALLJOBS_KEY.equals(application)) {
			watchingAllBatchJobs = true;
		}

		if (cachedBatchJobsPerApplication.get(application) == null) {

			final EventList<BatchJobObject> temp = new BasicEventList<BatchJobObject>();
			final String tempApp = application;
			Thread t = new Thread() {
				@Override
				public void run() {

					for (final String jobname : em.getCurrentBatchJobnames(
							tempApp, false)) {
						try {
							final BatchJobObject j = getBatchJob(jobname);
							if (!temp.contains(j)) {
								temp.getReadWriteLock().writeLock().lock();
								temp.add(j);
							}
						} catch (final NoSuchJobException e) {
							throw new RuntimeException(e);
						} finally {
							temp.getReadWriteLock().writeLock().unlock();
						}
					}
				}
			};

			t.start();

			cachedBatchJobsPerApplication.put(application, temp);

		}
		return cachedBatchJobsPerApplication.get(application);
	}

	public List<GridFile> getFinishedOutputFilesForBatchJob(
			BatchJobObject batchJob, String[] patterns)
			throws RemoteFileSystemException {

		final List<GridFile> files = new LinkedList<GridFile>();

		final List<String> fileurls = batchJob.getListOfOutputFiles(true,
				patterns);

		for (final String url : fileurls) {
			files.add(fm.createGridFile(url));
		}

		return files;

	}

	public JobObject getJob(String jobname, boolean refreshOnBackend)
			throws NoSuchJobException {

		synchronized (jobname) {

			if (cachedAllSingleJobs.get(jobname) == null) {

				try {
					final JobObject temp = new JobObject(si, jobname,
							refreshOnBackend);
					cachedAllSingleJobs.put(jobname, temp);
				} catch (final RuntimeException e) {
					myLogger.error(e.getLocalizedMessage(), e);
					return null;
				}

			}

		}

		return cachedAllSingleJobs.get(jobname);
	}

	public synchronized EventList<JobObject> getJobs(String application) {

		if (StringUtils.isBlank(application)) {
			application = Constants.ALLJOBS_KEY;
		}

		if (Constants.ALLJOBS_KEY.equals(application)) {
			watchingAllSingleJobs = true;
		}

		if (cachedSingleJobsPerApplication.get(application) == null) {

			final EventList<JobObject> temp = new BasicEventList<JobObject>();

			// we can load this in the background, since it's an eventlist,
			// can't we?
			final String tempApp = application;
			new Thread() {
				@Override
				public void run() {

					for (final String jobname : em.getCurrentJobnames(tempApp,
							false)) {

						try {
							final JobObject j = getJob(jobname, false);
							temp.getReadWriteLock().writeLock().lock();
							if (j != null) {
								if (!temp.contains(j)) {
									temp.add(j);
								}
							}
						} catch (final NoSuchJobException e) {
							throw new RuntimeException(e);
						} finally {
							temp.getReadWriteLock().writeLock().unlock();
						}
					}
				}
			}.start();

			cachedSingleJobsPerApplication.put(application, temp);

		}
		return cachedSingleJobsPerApplication.get(application);

	}

	public final ServiceInterface getServiceInterface() {
		return this.si;
	}

	public void onEvent(final Object event) {

		if (event instanceof NewBatchJobEvent) {
			final NewBatchJobEvent ev = (NewBatchJobEvent) event;
			GrisuRegistryManager.getDefault(si).getUserEnvironmentManager()
					.getCurrentBatchJobnames(true);

			new Thread() {
				@Override
				public void run() {

					updateBatchJobList(ev.getBatchJob().getApplication());
				}
			}.start();

		} else if (event instanceof BatchJobKilledEvent) {
			final BatchJobKilledEvent e = (BatchJobKilledEvent) event;
			new Thread() {
				@Override
				public void run() {

					updateBatchJobList(e.getApplication());
				}
			}.start();
		} else if (event instanceof NewJobEvent) {
			final NewJobEvent ev = (NewJobEvent) event;

			GrisuRegistryManager.getDefault(si).getUserEnvironmentManager()
					.getCurrentJobnames(true);
			new Thread() {
				@Override
				public void run() {

					updateJobnameList(ev.getJob().getApplication(), true);
				}
			}.start();

		} else if (event instanceof JobCleanedEvent) {
			final JobCleanedEvent ev = (JobCleanedEvent) event;
			new Thread() {
				@Override
				public void run() {

					updateJobnameList(ev.getJob().getApplication(), true);
				}
			}.start();
		}
	}

	private void startAutoRefresh() {

		updateTimer.schedule(new UpdateTimerTask(), 0);

	}

	public void stopUpdate() {

		this.stop = true;
		updateTimer.cancel();
	}

	/**
	 * Updates the list of jobnames for this application.
	 * 
	 * This doesn't update the batchjobs itself.
	 * 
	 * @param application
	 *            the application
	 */
	public synchronized void updateBatchJobList(String application) {

		if (StringUtils.isBlank(application)) {
			application = Constants.ALLJOBS_KEY;
		}

		application = application.toLowerCase();

		final EventList<BatchJobObject> list = getBatchJobs(application);

		final SortedSet<String> jobnames = em.getCurrentBatchJobnames(
				application, true);
		final SortedSet<String> jobnamesNew = new TreeSet<String>(jobnames);

		for (final BatchJobObject bj : list) {
			jobnamesNew.remove(bj.getJobname());
		}
		for (final String name : jobnamesNew) {
			try {
				if (StringUtils.isBlank(name)) {
					continue;
				}
				final BatchJobObject temp = getBatchJob(name);
				if (temp == null) {
					continue;
				}
				if (watchingAllBatchJobs) {
					if (!getAllBatchJobs().contains(temp)) {
						getAllBatchJobs().add(temp);
					}
				}

				if (!list.contains(temp)) {
					list.getReadWriteLock().writeLock().lock();
					list.add(temp);
					list.getReadWriteLock().writeLock().unlock();
				}
			} catch (final NoSuchJobException e) {
				throw new RuntimeException(e);
			}
		}

		final Set<BatchJobObject> toRemove = new HashSet<BatchJobObject>();
		for (final BatchJobObject bj : list) {
			if (!jobnames.contains(bj.getJobname())) {
				toRemove.add(bj);
			}
		}

		if (watchingAllBatchJobs) {
			getAllBatchJobs().getReadWriteLock().writeLock().lock();
			getAllBatchJobs().removeAll(toRemove);
			getAllBatchJobs().getReadWriteLock().writeLock().unlock();
		}

		list.removeAll(toRemove);
		for (final BatchJobObject bj : toRemove) {
			cachedAllBatchJobs.remove(bj.getJobname());
		}

	}

	public Thread updateJobnameList(String app, boolean force) {

		if (StringUtils.isBlank(app)) {
			app = Constants.ALLJOBS_KEY;
		}
		
		final String application = app.toLowerCase();
		
		synchronized (application.intern()) {

			Long lastAccess = jobListAccessTime.get(application);
			
			Long now = new Date().getTime();
			
			if ( lastAccess != null && now-lastAccess < MIN_WAIT_TIME_SEC  && ! force) {
				return null;
			} else {
				jobListAccessTime.put(application, new Date().getTime());
			}

			final EventList<JobObject> list = getJobs(application);

			final SortedSet<String> jobnames = em.getCurrentJobnames(
					application, true);
			final SortedSet<String> jobnamesNew = new TreeSet<String>(jobnames);

			for (final JobObject j : list) {
				final String jobname = j.getJobname();
				jobnamesNew.remove(jobname);
			}
			for (final String name : jobnamesNew) {
				try {
					if (StringUtils.isBlank(name)) {
						continue;
					}
					final JobObject temp = getJob(name, false);
					if (temp == null) {
						continue;
					}
					if (watchingAllSingleJobs) {
						if (!getAllJobs().contains(temp)) {
							getAllJobs().getReadWriteLock().writeLock().lock();
							getAllJobs().add(temp);
							getAllJobs().getReadWriteLock().writeLock()
									.unlock();
						}
					}
					if (!list.contains(temp)) {
						list.getReadWriteLock().writeLock().lock();
						list.add(temp);
						list.getReadWriteLock().writeLock().unlock();
					}
				} catch (final NoSuchJobException e) {
					throw new RuntimeException(e);
				}
			}

			final Set<JobObject> toRemove = new HashSet<JobObject>();
			for (final JobObject j : list) {
				final String jobname = j.getJobname();
				if (!jobnames.contains(jobname)) {
					toRemove.add(j);
				}
			}

			if (watchingAllSingleJobs) {
				getAllJobs().getReadWriteLock().writeLock().lock();
				getAllJobs().removeAll(toRemove);
				getAllJobs().getReadWriteLock().writeLock().unlock();
			}

			list.removeAll(toRemove);
			for (final JobObject j : toRemove) {
				cachedAllSingleJobs.remove(j.getJobname());
			}
			
			// do the rest in the background
			final Thread t = new Thread() {
				@Override
				public void run() {

					final Set<JobObject> tempList = new HashSet<JobObject>(
							getAllCurrentlyWatchedSingleJobs());
					for (final JobObject job : tempList) {
						myLogger.debug("Refreshing job: " + job.getJobname());
						job.getStatus(true);
					}
					jobListAccessTime.put(application, new Date().getTime());

				}
			};
			t.start();
			return t;
		}
	}

	public void killJobs(List<JobObject> jobs, boolean clean) {

		if (jobs == null || jobs.size() == 0) {
			return;
		}

		if (clean) {
			for (JobObject job : jobs) {
				job.setBeingCleaned(true);
			}
		}

		Set<String> list = Sets.newTreeSet();
		for (JobObject job : jobs) {
			list.add(job.getJobname());
		}

		final String handle = si.killJobs(
				DtoStringList.fromStringColletion(list), clean);

		Thread t = new Thread() {
			public void run() {

				try {
					StatusObject so = new StatusObject(si, handle);
					while (!so.isFinished()) {
						updateJobnameList(null, false);
						Thread.sleep(2000);
					}
				} catch (Exception e) {
					myLogger.debug(
							"Error when waiting for multiple job cleaning to finish: "
									+ e.getLocalizedMessage(), e);
				} finally {
					updateJobnameList(null, true);
				}
			}
		};
		t.setName("multi job clean wait");
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();

	}

}
