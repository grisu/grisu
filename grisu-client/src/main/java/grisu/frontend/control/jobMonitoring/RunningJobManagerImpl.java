package grisu.frontend.control.jobMonitoring;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.google.common.collect.Sets;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.events.JobCleanedEvent;
import grisu.frontend.model.events.NewJobEvent;
import grisu.frontend.model.job.GrisuJob;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.info.dto.DtoStringList;
import grisu.model.status.StatusObject;
import grisu.settings.ClientPropertiesManager;
import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunningJobManagerImpl implements RunningJobManager {

    static final Logger myLogger = LoggerFactory
            .getLogger(RunningJobManagerImpl.class.getName());
    private static Map<ServiceInterface, RunningJobManager> cachedRegistries = new HashMap<ServiceInterface, RunningJobManager>();
    private final UserEnvironmentManager em;
    private final FileManager fm;
    private final ServiceInterface si;
    private final Map<String, GrisuJob> cachedAllSingleJobs = Collections
            .synchronizedMap(new HashMap<String, GrisuJob>());
    private final Map<String, EventList<GrisuJob>> cachedSingleJobsPerApplication = Collections
            .synchronizedMap(new HashMap<String, EventList<GrisuJob>>());
    private final Map<String, Long> jobListAccessTime = Collections.synchronizedMap(new HashMap<String, Long>());
    private final Timer updateTimer = new Timer();
    private int updateTimeInSeconds = ClientPropertiesManager.getDefaultJobStatusRecheckInterval();
    private boolean stop = false;

    private final ExecutorService jobUpdateExecutor;

    private final Set<GrisuJob> jobsToUpdate = Collections.synchronizedSet(new LinkedHashSet<GrisuJob>());

    public RunningJobManagerImpl(ServiceInterface si) {
        this.si = si;

        jobUpdateExecutor = Executors.newFixedThreadPool(15);

        this.em = GrisuRegistryManager.getDefault(si)
                .getUserEnvironmentManager();
        this.fm = GrisuRegistryManager.getDefault(si).getFileManager();

        EventBus.subscribe(NewJobEvent.class, this);
        EventBus.subscribe(JobCleanedEvent.class, this);

        startAutoRefresh();
    }


    public static RunningJobManager getDefault(final ServiceInterface si) {

        if (si == null) {
            throw new RuntimeException(
                    "ServiceInterface not initialized yet. Can't get default registry...");
        }

        synchronized (si) {
            if (cachedRegistries.get(si) == null) {
                final RunningJobManager m = new RunningJobManagerImpl(si);
                cachedRegistries.put(si, m);
            }
        }

        return cachedRegistries.get(si);
    }

    public void setUpdateTimeInSeconds(int updateTimeInSeconds) {
        this.updateTimeInSeconds = updateTimeInSeconds;
    }

    public synchronized void createJob(GrisuJob job)
            throws JobPropertiesException {

        createJob(job, null);
    }

    public synchronized void createJob(final GrisuJob job, String fqan)
            throws JobPropertiesException {

        if (StringUtils.isBlank(fqan)) {
            job.createJob();
        } else {
            job.createJob(fqan);
        }

        cachedAllSingleJobs.put(job.getJobname(), job);
//		getJobs(job.getApplication()).add(job);

        new Thread() {
            @Override
            public void run() {
                updateJobnameList(job.getApplication(), true);

            }
        }.start();
        // if (watchingAllSingleJobs) {
        // getJobs(Constants.ALLJOBS_KEY).add(job);
        // }
    }

    private synchronized Collection<GrisuJob> getAllCurrentlyWatchedSingleJobs() {

        return cachedAllSingleJobs.values();
    }

    public EventList<GrisuJob> getAllJobs() {
        return getJobs(Constants.ALLJOBS_KEY);
    }

    public GrisuJob getJob(String jobname, boolean refreshOnBackend)
            throws NoSuchJobException {

        synchronized (jobname.intern()) {

            if (cachedAllSingleJobs.get(jobname) == null) {

                try {
                    final GrisuJob temp = new GrisuJob(si, jobname,
                            refreshOnBackend);
                    cachedAllSingleJobs.put(jobname, temp);
                } catch (final RuntimeException e) {
                    myLogger.debug(e.getLocalizedMessage());
                    return null;
                }

            }

        }

        return cachedAllSingleJobs.get(jobname);
    }

    public synchronized EventList<GrisuJob> getJobs(String application) {

        if (StringUtils.isBlank(application)) {
            application = Constants.ALLJOBS_KEY;
        }

        if (cachedSingleJobsPerApplication.get(application.toLowerCase()) == null) {

            final EventList<GrisuJob> temp = new BasicEventList<GrisuJob>();

            // we can load this in the background, since it's an eventlist,
            // can't we?
            final String tempApp = application;
            Thread t = new Thread() {
                @Override
                public void run() {

                    for (final String jobname : em.getCurrentJobnames(tempApp,
                            false)) {

                        try {
                            final GrisuJob j = getJob(jobname, false);
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
            };
            jobUpdateExecutor.execute(t);

            cachedSingleJobsPerApplication.put(application.toLowerCase(), temp);

        }
        return cachedSingleJobsPerApplication.get(application.toLowerCase());

    }

    public final ServiceInterface getServiceInterface() {
        return this.si;
    }

    public void onEvent(final Object event) {

        if (event instanceof NewJobEvent) {
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

    public void updateJobnameList(String app, boolean force) {

        if (StringUtils.isBlank(app)) {
            app = Constants.ALLJOBS_KEY;
        }

        final String application = app.toLowerCase();

        synchronized (application.intern()) {

            Long lastAccess = jobListAccessTime.get(application);

            Long now = new Date().getTime();

            if (lastAccess != null && now - lastAccess < MIN_WAIT_TIME_SEC && !force) {
                return;
            } else {
                jobListAccessTime.put(application, new Date().getTime());
            }

            final EventList<GrisuJob> list = getJobs(application);

            final SortedSet<String> jobnames = em.getCurrentJobnames(
                    application, true);
            final SortedSet<String> jobnamesNew = new TreeSet<String>(jobnames);

            for (final GrisuJob j : list) {
                final String jobname = j.getJobname();
                jobnamesNew.remove(jobname);
            }
            for (final String name : jobnamesNew) {

                if (StringUtils.isBlank(name)) {
                    continue;
                }

//                Thread t = new Thread() {
//                    public void run() {
                        try {
                            final GrisuJob temp = getJob(name, false);
                            if (temp == null) {
                                //return;
                                continue;
                            }
                            if (!list.contains(temp)) {
                                list.getReadWriteLock().writeLock().lock();
                                list.add(temp);
                                list.getReadWriteLock().writeLock().unlock();
                            }
                        } catch (final NoSuchJobException e) {
                            //throw new RuntimeException(e);
                            myLogger.debug("Couldn't find job '" + name + "': " + e.getLocalizedMessage());
                        }
//                    }
//                };
//                jobUpdateExecutor.execute(t);

            }

            final Set<GrisuJob> toRemove = new HashSet<GrisuJob>();
            for (final GrisuJob j : list) {
                final String jobname = j.getJobname();
                if (!jobnames.contains(jobname)) {
                    toRemove.add(j);
                }
            }

            list.removeAll(toRemove);
            for (final GrisuJob j : toRemove) {
                cachedAllSingleJobs.remove(j.getJobname());
            }

            final Set<GrisuJob> tempList = new HashSet<GrisuJob>(
                    getAllCurrentlyWatchedSingleJobs());
            for (final GrisuJob job : tempList) {
                if ( jobsToUpdate.contains(job)) {
                    myLogger.debug("Jobupdatelist already contains job {}. Skipping.", job.getJobname());
                    continue;
                }

                jobsToUpdate.add(job);
                // do the rest in the background
                final Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            myLogger.debug("Refreshing job: " + job.getJobname());
                            job.getStatus(true);
                        } finally {
                            jobsToUpdate.remove(job);
                        }
                    }

                };
                jobUpdateExecutor.execute(t);
            }

            jobListAccessTime.put(application, new Date().getTime());


        }

    }

    public String killJobsByName(Collection<String> jobs, boolean clean) {
        final String handle = si.killJobs(
                DtoStringList.fromStringColletion(jobs), clean);

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

        return handle;
    }

    public String killJobs(List<GrisuJob> jobs, boolean clean) {

        if (jobs == null || jobs.size() == 0) {
            return null;
        }

        if (clean) {
            for (GrisuJob job : jobs) {
                job.setBeingCleaned(true);
            }
        }

        Set<String> list = Sets.newTreeSet();
        for (GrisuJob job : jobs) {
            list.add(job.getJobname());
        }

        return killJobsByName(list, clean);

    }

    private class UpdateTimerTask extends TimerTask {

        @Override
        public void run() {

            try {

                // update single jobs
                for (final String application : cachedSingleJobsPerApplication
                        .keySet()) {
                    updateJobnameList(application, false);
                }

            } catch (final Exception e) {
                myLogger.error(e.getLocalizedMessage(), e);
            } finally {

                if (!stop) {
                    updateTimer.schedule(new UpdateTimerTask(),
                            updateTimeInSeconds * 1000);
                } else {
                    updateTimer.cancel();
                }

            }
        }

    }

}
