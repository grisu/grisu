package org.vpac.grisu.control.serviceInterfaces;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.backend.model.job.Job;
import org.vpac.grisu.backend.model.job.BatchJob;
import org.vpac.grisu.control.exceptions.JobPropertiesException;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class JobDistributor {
	
	static final Logger myLogger = Logger
	.getLogger(JobDistributor.class.getName());
	
	public String distributeJobs(Set<Job> alljobs, SortedSet<GridResource> allAvailableResources, String[] sitesToInclude, String[] sitesToExclude) {
		
		Map<String, Integer> submissionLocations = new TreeMap<String, Integer>();

		Long allWalltime = 0L;
		for ( Job job : alljobs ) {
			allWalltime = allWalltime + Long.parseLong(job.getJobProperty(Constants.WALLTIME_IN_MINUTES_KEY));
		}

		Map<GridResource, Long> resourcesToUse = new TreeMap<GridResource, Long>();
		List<Integer> ranks = new LinkedList<Integer>();
		Long allRanks = 0L;
		for (GridResource resource : allAvailableResources) {

			if (sitesToInclude != null && sitesToInclude.length > 0) {

				for (String site : sitesToInclude) {
					if (resource.getSiteName().toLowerCase().contains(
							site.toLowerCase())) {
						resourcesToUse.put(resource, new Long(0L));
						ranks.add(resource.getRank());
						allRanks = allRanks + resource.getRank();
						break;
					}
				}

			} else if (sitesToExclude != null && sitesToExclude.length > 0) {

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
		
		for (Job job : alljobs) {

			GridResource subLocResource = null;
			long oldWalltimeSummary = 0L;

			for (int i = lastIndex; i < resourceArray.length * 2; i++) {
				int indexToUse = i;
				if (i >= resourceArray.length) {
					indexToUse = indexToUse - resourceArray.length;
				}

				GridResource resource = resourceArray[indexToUse];

				long rankPercentage = (resource.getRank() * 100) / (allRanks);
				long wallTimePercentage = ((Long.parseLong(job.getJobProperty(Constants.WALLTIME_IN_MINUTES_KEY)) + resourcesToUse
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

			job.addJobProperty(Constants.SUBMISSIONLOCATION_KEY, subLoc);
//			needs to be done on the backend
//			try {
//				processJobDescription(job);
//			} catch (JobPropertiesException e) {
//				e.printStackTrace();
//				throw new RuntimeException(e);
//			}
//			jobdao.saveOrUpdate(job);
			resourcesToUse.put(subLocResource, oldWalltimeSummary + Long.parseLong(job.getJobProperty(Constants.WALLTIME_IN_MINUTES_KEY)));
		}

		StringBuffer message = new StringBuffer(
				"Filled submissionlocations for "+alljobs.size()+" jobs: " + "\n");
		message.append("Submitted jobs to:\t\t\tAmount\n");
		for (String sl : submissionLocations.keySet()) {
			message
					.append(sl + "\t\t\t\t" + submissionLocations.get(sl)
							+ "\n");
		}
		myLogger.debug(message.toString());
		
		return message.toString();
		
	}

}
