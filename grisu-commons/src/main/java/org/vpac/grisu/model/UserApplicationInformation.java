package org.vpac.grisu.model;

import java.util.Set;


/**
 * This class contains information from {@link ApplicationInformation} and {@link UserInformation} 
 * objects in order to be able to give information about which submissionlocations/versions are 
 * available for the user (and not on the whole of the grid, like the {@link ApplicationInformation} 
 * object would).
 * 
 * @author Markus Binsteiner
 *
 */
public interface UserApplicationInformation extends ApplicationInformation {

	/**
	 * Calculates all the submissionlocations that are available to the user
	 * for the provided fqan and application. It returns all submissionlocations, regardless
	 * of the version of the application.
	 * 
	 * @param fqan the fqan
	 * @return the submissionlocations
	 */
	public Set<String> getAvailableSubmissionLocationsForUser(String fqan);
	
	/**
	 * This calculates all the submissionlocations that are available to the user
	 * on the grid for this fqan and application and version.
	 * 
	 * @param version the version
	 * @param fqan the fqan
	 * @return the submissionLocations
	 */
	public Set<String> getAvailableSubmissionLocationsPerVersionForUser(String version, String fqan);
	
	/**
	 * Calculates all the submissionlocations for this applications that are available to the user
	 * regardless of version and fqan used to submit a job.
	 * 
	 * @return all submissionlocations
	 */
	public Set<String> getAllAvailableSubmissionLocationsForUser();
	
	/**
	 * Calculates all versions for this application that are availabe for the user, regardless of submissionLocation.
	 * @return all available versions
	 */
	public Set<String> getAllAvailableVersionsForUser();
	
	/**
	 * Calculates all versions for this application that are availabe for this fqan, regardless of submissionLocation. 
	 * @param fqan the fqan
	 * @return all available versions
	 */
	public Set<String> getAllAvailableVersionsForUser(String fqan);

}
