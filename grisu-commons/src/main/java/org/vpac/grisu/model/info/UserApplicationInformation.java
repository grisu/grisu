package org.vpac.grisu.model.info;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.job.JobSubmissionProperty;

/**
 * This class contains information from {@link ApplicationInformation} and
 * {@link UserEnvironmentManager} objects in order to be able to give
 * information about which submissionlocations/versions are available for the
 * user (and not on the whole of the grid, like the
 * {@link ApplicationInformation} object would). It basically just adds a few
 * methods to display a subset of submissionlocations/versions that take into
 * account the vos a user is in. Probably not needed very often...
 * 
 * @author Markus Binsteiner
 * 
 */
public interface UserApplicationInformation extends ApplicationInformation {

	/**
	 * Calculates all the submissionlocations for this applications that are
	 * available to the user regardless of version and fqan used to submit a
	 * job.
	 * 
	 * @return all submissionlocations
	 */
	public Set<String> getAllAvailableSubmissionLocationsForUser();

	/**
	 * Calculates all the sites where this application is available to the user.
	 * 
	 * @return all sites
	 */
	public Set<String> getAllAvailableSitesForUser();

	/**
	 * Calculates all versions for this application that are availabe for the
	 * user, regardless of submissionLocation.
	 * 
	 * @return all available versions
	 */
	public Set<String> getAllAvailableVersionsForUser();

	/**
	 * Calculates the best {@link GridResource}s to submit this job to.
	 * 
	 * @param additionalJobProperties
	 *            additional job properties (e.g. walltime). Have a look at the
	 *            MatchMaker interface for supported keys.
	 * @param fqan
	 *            the fqan to submit the job with
	 * @return a sorted list of the best resources to submit this job to.
	 */
	public List<GridResource> getBestSubmissionLocations(
			Map<JobSubmissionProperty, String> additionalJobProperties,
			String fqan);

}
