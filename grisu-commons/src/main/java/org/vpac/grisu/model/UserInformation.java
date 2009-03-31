package org.vpac.grisu.model;

import java.util.Set;

/**
 * Wrapps information about the user and the available resources to him grid-wide.
 * 
 * @author Markus Binsteiner
 *
 */
public interface UserInformation {
	
	/**
	 * All of the users fqans.
	 * @return the fqans
	 */
	public String[] getAllAvailableFqans();

	/**
	 * All the submissionLocations the user has got access to with all his fqans.
	 * @return all submissionLocations
	 */
	public Set<String> getAllAvailableSubmissionLocations();
	
	/**
	 * All the submissionLocations the user has got access to with this fqan.
	 * @param fqan the fqan
	 * @return all submissionLocations
	 */
	public String[] getAllAvailableSubmissionLocations(String fqan);
	
}
