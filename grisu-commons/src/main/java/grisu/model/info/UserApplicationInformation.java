package grisu.model.info;

import grisu.model.UserEnvironmentManager;

import java.util.Set;


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
	 * Calculates all the sites where this application is available to the user.
	 * 
	 * @return all sites
	 */
	Set<String> getAllAvailableSitesForUser();

	/**
	 * Calculates all the submissionlocations for this applications that are
	 * available to the user regardless of version and fqan used to submit a
	 * job.
	 * 
	 * @return all submissionlocations
	 */
	Set<String> getAllAvailableSubmissionLocationsForUser();

	/**
	 * Calculates all versions for this application that are availabe for the
	 * user, regardless of submissionLocation.
	 * 
	 * @return all available versions
	 */
	Set<String> getAllAvailableVersionsForUser();

}