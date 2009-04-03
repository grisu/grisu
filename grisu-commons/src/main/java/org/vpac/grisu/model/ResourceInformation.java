package org.vpac.grisu.model;

import java.util.Set;

public interface ResourceInformation {
	
	public String[] getAllSubmissionLocations();
	
	public String getSite(String urlOrSubmissionLocation);
	
	/**
	 * All the submissionLocations the user has got access to with this fqan.
	 * @param fqan the fqan
	 * @return all submissionLocations
	 */
	public String[] getAllAvailableSubmissionLocations(String fqan);
	
	public Set<String> getAllAvailableSites(String fqan);
	
	
	/**
	 * Convenience method to get a list of sites out of a set of submissionlocations
	 * @param submissionLocations the submissionLocations
	 * @return the sites
	 */
	public Set<String> distillSitesFromSubmissionLocations(Set<String> submissionLocations);
	
	/**
	 * Convenience method to filter all submissionlocations out of a list of submissionlocations that are located at the specified site
	 * @param site the site
	 * @param submissionlocations all submissionlocations
	 * @return the submissionlocations that are located on the site
	 */
	public Set<String> filterSubmissionLocationsForSite(String site, Set<String> submissionlocations);

}
