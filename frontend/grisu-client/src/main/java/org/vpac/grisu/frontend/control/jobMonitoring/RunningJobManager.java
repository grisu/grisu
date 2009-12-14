package org.vpac.grisu.frontend.control.jobMonitoring;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;

public class RunningJobManager {
	
	private final UserEnvironmentManager em;
	private final ServiceInterface si;
	
	private Map<String, BatchJobObject> cachedAllBatchJobs = new HashMap<String, BatchJobObject>();
	
	public RunningJobManager(ServiceInterface si) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();
	}
	
	
	public BatchJobObject getBatchJob(String jobname) throws BatchJobException {
		
		if ( cachedAllBatchJobs.get(jobname) == null ) {
			
			try {
				BatchJobObject temp = new BatchJobObject(si, jobname, false);
				cachedAllBatchJobs.put(jobname, temp);
			} catch (NoSuchJobException e) {
				throw new RuntimeException(e);
			}
			
			
		}
		return cachedAllBatchJobs.get(jobname);
	}
	
	
//	public Map<String, BatchJobObject> getAllBatchJobs(boolean refresh) {
//	
//		if ( cachedAllBatchJobs == null ) {
//			cachedAllBatchJobs = new HashMap<String, BatchJobObject>();
//			for ( String jobname : em.getCurrentBatchJobnames() ) {
//				try {
//					BatchJobObject temp = new BatchJobObject(si, jobname, refresh);
//					cachedAllBatchJobs.put(jobname, temp);
//				} catch (BatchJobException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (NoSuchJobException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		} else if ( refresh ) { 
//			
//			for ( BatchJobObject bj : cachedAllBatchJobs.values() ) {
//				bj.refresh(false);
//			}
//			
//		}
//		return cachedAllBatchJobs;
//	}
	
	

}
