package grisu.frontend.control.jobMonitoring;

import ca.odell.glazedlists.EventList;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.GrisuJob;
import org.bushe.swing.event.EventSubscriber;

import java.util.Collection;
import java.util.List;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 13/09/13
 * Time: 5:37 PM
 */
public interface RunningJobManager extends EventSubscriber {



    Long MIN_WAIT_TIME_SEC = 5L;

    void createJob(GrisuJob job)
            throws JobPropertiesException;

    void createJob(GrisuJob job, String fqan)
                    throws JobPropertiesException;

    EventList<GrisuJob> getAllJobs();

    GrisuJob getJob(String jobname, boolean refreshOnBackend)
                            throws NoSuchJobException;

    EventList<GrisuJob> getJobs(String application);

    ServiceInterface getServiceInterface();

    void onEvent(Object event);

    void stopUpdate();

    void updateJobnameList(String app, boolean force);

    String killJobsByName(Collection<String> jobs, boolean clean);

    String killJobs(List<GrisuJob> jobs, boolean clean);
}
