package org.vpac.grisu.control.serviceInterfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.vpac.grisu.backend.model.job.Job;

import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class SubmitPolicy {
	
	private boolean submitToFailedLocations = false;
	private boolean submitToRunningLocations = true;
	private boolean submitToDoneLocations = true;
	private boolean submitToWaitingLocations = false;
	private boolean submitToAllLocations = true;
	
	private boolean resubmitRunningJobs = false;
	private boolean resubmitFailedJobs = true;
	private boolean resubmitDoneJobs = false;
	private boolean resubmitWaitingJobs = true;
	private boolean submitReadyJobs = true;
	
	private SortedSet<GridResource> calculatedResources;
	private Set<Job> calculatedJobs;
	
	private final Set<Job> allJobs;
	private final SortedSet<GridResource> allResources;
	private final BatchJobStatus bjs;
	
	private final Map<String, GridResource> cacheGridResourceMap = new HashMap<String, GridResource>();
	
	public SubmitPolicy(Set<Job> allJobs, SortedSet<GridResource> allResources) {
		this.allJobs = allJobs;
		this.allResources = allResources;
		
		for ( GridResource gr : allResources ) {
			cacheGridResourceMap.put(SubmissionLocationHelpers.createSubmissionLocationString(gr), gr);
		}

		bjs = new BatchJobStatus(getAllJobs());

	}
	
	public boolean isSubmitToAllLocations() {
		return submitToAllLocations;
	}
	
	public void setSubmitToAllLocations(boolean all) {
		this.submitToAllLocations = all;
	}
	
	public boolean isSubmitToFailedLocations() {
		return submitToFailedLocations;
	}
	public void setSubmitToFailedLocations(boolean submitToFailedLocations) {
		this.submitToFailedLocations = submitToFailedLocations;
	}
	public boolean isSubmitToRunningLocations() {
		return submitToRunningLocations;
	}
	public void setSubmitToRunningLocations(boolean submitToRunningLocations) {
		this.submitToRunningLocations = submitToRunningLocations;
	}
	public boolean isSubmitToDoneLocations() {
		return submitToDoneLocations;
	}
	public void setSubmitToDoneLocations(boolean submitToDoneLocations) {
		this.submitToDoneLocations = submitToDoneLocations;
	}
	public boolean isSubmitToWaitingLocations() {
		return submitToWaitingLocations;
	}
	public void setSubmitToWaitingLocations(boolean submitToWaitingLocations) {
		this.submitToWaitingLocations = submitToWaitingLocations;
	}
	public boolean isResubmitRunningJobs() {
		return resubmitRunningJobs;
	}
	public void setResubmitRunningJobs(boolean resubmitRunningJobs) {
		this.resubmitRunningJobs = resubmitRunningJobs;
	}
	public boolean isResubmitFailedJobs() {
		return resubmitFailedJobs;
	}
	public void setResubmitFailedJobs(boolean resubmitFailedJobs) {
		this.resubmitFailedJobs = resubmitFailedJobs;
	}
	public boolean isResubmitDoneJobs() {
		return resubmitDoneJobs;
	}
	public void setResubmitDoneJobs(boolean resubmitDoneJobs) {
		this.resubmitDoneJobs = resubmitDoneJobs;
	}
	public boolean isResubmitWaitingJobs() {
		return resubmitWaitingJobs;
	}
	public void setResubmitWaitingJobs(boolean resubmitWaitingJobs) {
		this.resubmitWaitingJobs = resubmitWaitingJobs;
	}
	public boolean isSubmitReadyJobs() {
		return submitReadyJobs;
	}
	public void setSubmitReadyJobs(boolean submitReadyJobs) {
		this.submitReadyJobs = submitReadyJobs;
	}
	public Set<Job> getAllJobs() {
		return allJobs;
	}
	public SortedSet<GridResource> getAllResources() {
		return allResources;
	}
	
	public void recalculate() {
		
		calculatedJobs = null;
		calculatedResources = null;
	
	}
	
	public SortedSet<GridResource> getCalculatedGridResources() {
		if ( calculatedResources == null ) {

			calculatedResources = new TreeSet<GridResource>();
			
			if ( submitToAllLocations ) {
				
				calculatedResources = allResources;
				
			} else {

			if ( submitToDoneLocations ) {
				for ( String subLoc : bjs.getDoneSubLocs() ) {
					GridResource temp = cacheGridResourceMap.get(subLoc);

					if ( temp != null ) {
						calculatedResources.add(temp);
					}
				}
			}
			if ( submitToFailedLocations ) {
				for ( String subLoc : bjs.getFailedSubLocs() ) {
					GridResource temp = cacheGridResourceMap.get(subLoc);

					if ( temp != null ) {
						calculatedResources.add(temp);
					}
				}
			}
			if ( submitToRunningLocations ) {
				for ( String subLoc : bjs.getRunningSubLocs() ) {
					GridResource temp = cacheGridResourceMap.get(subLoc);

					if ( temp != null ) {
						calculatedResources.add(temp);
					}
				}
			}
			if ( submitToWaitingLocations ) {
				for ( String subLoc : bjs.getRunningSubLocs() ) {
					GridResource temp = cacheGridResourceMap.get(subLoc);

					if ( temp != null ) {
						calculatedResources.add(temp);
					}
				}
			}
			}
			
		}
		return calculatedResources;
	}
	public Set<Job> getCalculatedJobs() {
		
		if ( calculatedJobs == null ) {

			calculatedJobs = new HashSet<Job>();
			if ( resubmitDoneJobs ) {
				for ( Job job : bjs.getDoneJobs() ) {
					calculatedJobs.add(job);
				}
			}
			if ( resubmitFailedJobs ) {
				for ( Job job : bjs.getFailedJobs() ) {
					calculatedJobs.add(job);
				}
			}
			if ( resubmitRunningJobs ) {
				for ( Job job : bjs.getRunningJobs() ) {
					calculatedJobs.add(job);
				}
			}
			if ( resubmitWaitingJobs ) {
				for ( Job job : bjs.getWaitingJobs() ) {
					calculatedJobs.add(job);
				}
			}
			if ( submitReadyJobs ) {
				for ( Job job : bjs.getReadyJobs() ) {
					calculatedJobs.add(job);
				}
			}
			
		}
		return calculatedJobs;
	}

}
