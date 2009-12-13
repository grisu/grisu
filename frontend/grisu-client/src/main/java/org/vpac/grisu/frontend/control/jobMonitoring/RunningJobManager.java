package org.vpac.grisu.frontend.control.jobMonitoring;

import java.util.LinkedList;
import java.util.List;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;

public class RunningJobManager {
	
	private final UserEnvironmentManager em;
	private final ServiceInterface si;
	
	private List<BatchJobObject> cachedAllBatchJobs = null;
	
	public RunningJobManager(ServiceInterface si) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();
	}
	
	
	public List<BatchJobObject> getAllBatchJobs(boolean refresh) {
	
		if ( cachedAllBatchJobs == null ) {
			cachedAllBatchJobs = new LinkedList<BatchJobObject>();
			for ( String jobname : em.getCurrentBatchJobnames() ) {
				try {
					BatchJobObject temp = new BatchJobObject(si, jobname, refresh);
					cachedAllBatchJobs.add(temp);
				} catch (BatchJobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchJobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if ( refresh ) { 
			
			for ( BatchJobObject bj : cachedAllBatchJobs ) {
				bj.refresh();
			}
			
		}
		return cachedAllBatchJobs;
	}
	
	

}
