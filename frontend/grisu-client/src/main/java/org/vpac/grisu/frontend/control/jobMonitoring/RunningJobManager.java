package org.vpac.grisu.frontend.control.jobMonitoring;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventSubscriber;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.model.events.BatchJobKilledEvent;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;

import au.org.arcs.jcommons.constants.Constants;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class RunningJobManager {

	private class UpdateTimerTask extends TimerTask implements EventSubscriber {

		public void onEvent(Object event) {

			if ( event instanceof BatchJobKilledEvent ) {
				BatchJobKilledEvent e = (BatchJobKilledEvent)event;

				updateBatchJobList(e.getApplication());
			}


		}

		@Override
		public void run() {

			try {

				// update single jobs
				for (String application : cachedSingleJobsPerApplication.keySet()) {
					updateJobList(application);
				}

				for ( JobObject job : getAllCurrentlyWatchedSingleJobs() ) {
					myLogger.debug("Refreshing job: " + job.getJobname());
					job.getStatus(true);
				}

				for (String application : cachedBatchJobsPerApplication.keySet()) {
					updateBatchJobList(application);
				}

				for (BatchJobObject bj : getAllCurrentlyWatchedBatchJobs()) {
					if (!bj.isFinished(false) && !bj.isRefreshing() && !bj.isBeingKilled()) {
						myLogger.debug("Refreshing job: " + bj.getJobname());
						bj.refresh(true);
					}
				}

				if ( ! stop ) {
					updateTimer.schedule(new UpdateTimerTask(),
							UPDATE_TIME_IN_SECONDS * 1000);
				} else {
					updateTimer.cancel();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}

	static final Logger myLogger = Logger.getLogger(RunningJobManager.class
			.getName());

	public static void updateJobList(ServiceInterface si,
			EventList<JobObject> jobObjectList, DtoJobs newJobs) {

		Set<JobObject> toRemove = new HashSet<JobObject>();
		Set<DtoJob> newJobsCopy = new HashSet<DtoJob>(newJobs.getAllJobs());

		for (JobObject jo : jobObjectList) {
			boolean inList = false;

			for (DtoJob job : newJobs.getAllJobs()) {
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

		for (DtoJob newJob : newJobsCopy) {
			try {
				JobObject jo = new JobObject(si, newJob);
				jobObjectList.add(jo);
			} catch (NoSuchJobException e) {
				// probably not that important
				e.printStackTrace();
			}
		}

	}

	private final int UPDATE_TIME_IN_SECONDS = 300;

	private static Map<ServiceInterface, RunningJobManager> cachedRegistries = new HashMap<ServiceInterface, RunningJobManager>();
	public static RunningJobManager getDefault(final ServiceInterface si) {

		if (si == null) {
			throw new RuntimeException(
			"ServiceInterface not initialized yet. Can't get default registry...");
		}

		synchronized (si) {
			if (cachedRegistries.get(si) == null) {
				RunningJobManager m = new RunningJobManager(si);
				cachedRegistries.put(si, m);
			}
		}

		return cachedRegistries.get(si);
	}

	private final UserEnvironmentManager em;

	private final ServiceInterface si;

	private final Map<String, JobObject> cachedAllSingleJobs = Collections
	.synchronizedMap(new HashMap<String, JobObject>());

	private final Map<String, EventList<JobObject>> cachedSingleJobsPerApplication = Collections
	.synchronizedMap(new HashMap<String, EventList<JobObject>>());

	private final Map<String, BatchJobObject> cachedAllBatchJobs = Collections
	.synchronizedMap(new HashMap<String, BatchJobObject>());

	private final Map<String, EventList<BatchJobObject>> cachedBatchJobsPerApplication = Collections
	.synchronizedMap(new HashMap<String, EventList<BatchJobObject>>());;

	//	private final boolean checkForNewApplicationsForSingleJobs = false;

	private final Timer updateTimer = new Timer();

	private boolean stop = false;

	public RunningJobManager(ServiceInterface si) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
		.getUserEnvironmentManager();

		startAutoRefresh();
	}

	public synchronized BatchJobObject createBatchJob(String jobname, String submissionFqan, String defaultApplication, String defaultVersion) throws BatchJobException {

		BatchJobObject batchJob = new BatchJobObject(si, jobname, submissionFqan, defaultApplication, defaultVersion);
		cachedAllBatchJobs.put(jobname, batchJob);
		cachedBatchJobsPerApplication.get(defaultApplication).add(batchJob);
		return batchJob;

	}

	private synchronized Collection<BatchJobObject> getAllCurrentlyWatchedBatchJobs() {

		return cachedAllBatchJobs.values();

	}

	private synchronized Collection<JobObject> getAllCurrentlyWatchedSingleJobs() {

		return cachedAllSingleJobs.values();
	}

	public BatchJobObject getBatchJob(String jobname) throws NoSuchJobException {

		synchronized (jobname) {

			if (cachedAllBatchJobs.get(jobname) == null) {

				try {
					BatchJobObject temp = new BatchJobObject(si, jobname, false);
					cachedAllBatchJobs.put(jobname, temp);
				} catch (BatchJobException e) {
					throw new RuntimeException(e);
				}

			}
			return cachedAllBatchJobs.get(jobname);

		}
	}

	public synchronized EventList<BatchJobObject> getBatchJobs(
			String application) {

		if ( StringUtils.isBlank(application) ) {
			application = Constants.ALLJOBS_KEY;
		}

		if (cachedBatchJobsPerApplication.get(application) == null) {

			EventList<BatchJobObject> temp = new BasicEventList<BatchJobObject>();

			for (String jobname : em
					.getCurrentBatchJobnames(application, false)) {
				try {
					temp.add(getBatchJob(jobname));
				} catch (NoSuchJobException e) {
					throw new RuntimeException(e);
				}
			}

			cachedBatchJobsPerApplication.put(application, temp);

		}
		return cachedBatchJobsPerApplication.get(application);
	}

	public JobObject getJob(String jobname, boolean refreshOnBackend) throws NoSuchJobException {

		synchronized (jobname) {

			if (cachedAllSingleJobs.get(jobname) == null) {

				try {
					JobObject temp = new JobObject(si, jobname, refreshOnBackend);
					cachedAllSingleJobs.put(jobname, temp);
				} catch (RuntimeException e) {
					myLogger.error(e);
					return null;
				}

			}

		}

		return cachedAllSingleJobs.get(jobname);
	}

	public synchronized EventList<JobObject> getJobs(String application) {

		if ( StringUtils.isBlank(application) ) {
			application = Constants.ALLJOBS_KEY;
		}

		if (cachedSingleJobsPerApplication.get(application) == null) {

			EventList<JobObject> temp = new BasicEventList<JobObject>();

			for (String jobname : em.getCurrentJobnames(application, false)) {

				try {
					JobObject j= getJob(jobname, false);
					if ( j != null ) {
						temp.add(j);
					}
				} catch (NoSuchJobException e) {
					throw new RuntimeException(e);
				}
			}

			cachedSingleJobsPerApplication.put(application, temp);

		}
		return cachedSingleJobsPerApplication.get(application);

	}

	public final ServiceInterface getServiceInterface() {
		return this.si;
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

		if ( StringUtils.isBlank(application) ) {
			application = Constants.ALLJOBS_KEY;
		}

		EventList<BatchJobObject> list = getBatchJobs(application);

		SortedSet<String> jobnames = em.getCurrentBatchJobnames(application,
				true);
		SortedSet<String> jobnamesNew = new TreeSet<String>(jobnames);

		for (BatchJobObject bj : list) {
			jobnamesNew.remove(bj.getJobname());
		}
		for (String name : jobnamesNew) {
			try {
				list.add(getBatchJob(name));
			} catch (NoSuchJobException e) {
				throw new RuntimeException(e);
			}
		}

		Set<BatchJobObject> toRemove = new HashSet<BatchJobObject>();
		for (BatchJobObject bj : list) {
			if (!jobnames.contains(bj.getJobname())) {
				toRemove.add(bj);
			}
		}
		list.removeAll(toRemove);
		for ( BatchJobObject bj : toRemove ) {
			cachedAllBatchJobs.remove(bj.getJobname());
		}

	}

	public synchronized void updateJobList(String application) {

		if ( StringUtils.isBlank(application) ) {
			application = Constants.ALLJOBS_KEY;
		}

		EventList<JobObject> list = getJobs(application);

		SortedSet<String> jobnames = em.getCurrentJobnames(application,
				true);
		SortedSet<String> jobnamesNew = new TreeSet<String>(jobnames);

		for (JobObject j : list) {
			jobnamesNew.remove(j.getJobname());
		}
		for (String name : jobnamesNew) {
			try {
				list.add(getJob(name, false));
			} catch (NoSuchJobException e) {
				throw new RuntimeException(e);
			}
		}

		Set<JobObject> toRemove = new HashSet<JobObject>();
		for (JobObject j : list) {
			if (!jobnames.contains(j.getJobname())) {
				toRemove.add(j);
			}
		}
		list.removeAll(toRemove);
		for ( JobObject j : toRemove ) {
			cachedAllSingleJobs.remove(j.getJobname());
		}

	}

}
