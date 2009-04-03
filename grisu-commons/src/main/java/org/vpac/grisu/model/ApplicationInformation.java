package org.vpac.grisu.model;

import java.util.Map;
import java.util.Set;


/**
 * Interface which describes the stuff that needs to be known from an application grid-wide
 * in order to be able to submit a job for the application it represents.
 * 
 * @author Markus Binsteiner
 *
 */
public interface ApplicationInformation {
	
	
	/**
	 * The name of the application this object is all about
	 * @return the name of the application
	 */
	public String getApplicationName();
	
	/**
	 * The available executables for this application on this submissionlocation
	 * @param subLoc the submissionlocation
	 * @param version the version of the application
	 * @return a list of all executables
	 */
	public String[] getExecutables(String subLoc, String version);
	
	/**
	 * Calculates all available versions of the application on this submissionlocation.
	 * @param subLoc the submissionLocation
	 * @return a set of all available versions
	 */
	public Set<String> getAvailableVersions(String subLoc);
	
	/**
	 * Retrieves a map of all available details (executables, modules, ...) Grisu knows about this version of the
	 * application on this submissionLocation
	 * @param subLoc the submissionLocation
	 * @param version the version 
	 * @return the details
	 */
	public Map<String, String> getApplicationDetails(String subLoc, String version);
	
	/**
	 * Returns a set of all available submissionLocations for this application grid-wide.
	 * @return the submissionLocations
	 */
	public Set<String> getAvailableAllSubmissionLocations();
	
	/**
	 * Returns a set of all available submissionLocations for one version of this 
	 * application grid-wide.
	 * @param version the version in question
	 * @return the submissionLocations
	 */
	public Set<String> getAvailableSubmissionLocationsForVersion(String version);
	
	/**
	 * Calculates all the submissionlocations that are available to the user
	 * for the provided fqan and application. It returns all submissionlocations, regardless
	 * of the version of the application.
	 * 
	 * @param fqan the fqan
	 * @return the submissionlocations
	 */
	public Set<String> getAvailableSubmissionLocationsForFqan(String fqan);
		
	/**
	 * This calculates all the submissionlocations that are available to the user
	 * on the grid for this fqan and application and version.
	 * 
	 * @param version the version
	 * @param fqan the fqan
	 * @return the submissionLocations
	 */
	public Set<String> getAvailableSubmissionLocationsForVersionAndFqan(String version, String fqan);
	
	/**
	 * Calculates all versions for this application that are availabe for this fqan, regardless of submissionLocation. 
	 * @param fqan the fqan
	 * @return all available versions
	 */
	public Set<String> getAllAvailableVersionsForFqan(String fqan);
	
}
