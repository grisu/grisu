package grisu.control.serviceInterfaces;

import grisu.backend.model.job.Job;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.JsdlHelpers;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class PercentageJobDistributor implements JobDistributor {

	static final Logger myLogger = Logger
			.getLogger(PercentageJobDistributor.class.getName());

	public Map<String, Integer> distributeJobs(Set<Job> allJobs,
			SortedSet<GridResource> allAvailableResources) {

		final Map<String, Integer> submissionLocations = new TreeMap<String, Integer>();

		Long allWalltime = 0L;
		for (final Job job : allJobs) {
			allWalltime = allWalltime
					+ Long.parseLong(job
							.getJobProperty(Constants.WALLTIME_IN_MINUTES_KEY));
		}

		final Map<GridResource, Long> resourcesToUse = new TreeMap<GridResource, Long>();
		final List<Integer> ranks = new LinkedList<Integer>();
		Long allRanks = 0L;
		for (final GridResource resource : allAvailableResources) {

			resourcesToUse.put(resource, new Long(0L));
			ranks.add(resource.getRank());
			allRanks = allRanks + resource.getRank();
		}

		myLogger.debug("Rank summary: " + allRanks);
		myLogger.debug("Walltime summary: " + allWalltime);

		final GridResource[] resourceArray = resourcesToUse.keySet().toArray(
				new GridResource[] {});
		int lastIndex = 0;

		for (final Job job : allJobs) {

			GridResource subLocResource = null;
			long oldWalltimeSummary = 0L;

			for (int i = lastIndex; i < resourceArray.length * 2; i++) {
				int indexToUse = i;
				if (i >= resourceArray.length) {
					indexToUse = indexToUse - resourceArray.length;
				}

				final GridResource resource = resourceArray[indexToUse];

				final long rankPercentage = (resource.getRank() * 100)
						/ (allRanks);
				final long wallTimePercentage = ((Long.parseLong(job
						.getJobProperty(Constants.WALLTIME_IN_MINUTES_KEY)) + resourcesToUse
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

			final String subLoc = SubmissionLocationHelpers
					.createSubmissionLocationString(subLocResource);
			Integer currentCount = submissionLocations.get(subLoc);
			if (currentCount == null) {
				currentCount = 0;
			}
			submissionLocations.put(subLoc, currentCount + 1);

			job.addJobProperty(Constants.SUBMISSIONLOCATION_KEY, subLoc);

			JsdlHelpers.setCandidateHosts(job.getJobDescription(),
					new String[] { subLoc });

			// needs to be done on the backend
			// try {
			// processJobDescription(job);
			// } catch (JobPropertiesException e) {
			// e.printStackTrace();
			// throw new RuntimeException(e);
			// }
			// jobdao.saveOrUpdate(job);
			resourcesToUse
					.put(subLocResource,
							oldWalltimeSummary
									+ Long.parseLong(job
											.getJobProperty(Constants.WALLTIME_IN_MINUTES_KEY)));
		}

		return submissionLocations;

	}

}
