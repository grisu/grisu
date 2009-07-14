package org.vpac.grisu.model.info;

import java.util.List;
import java.util.Set;

/**
 * Classes that implement this interface provide general information about the grid.
 * 
 * @author markus
 *
 */
public interface ResourceInformation {
	
	/**
	 * Returns a list of all available submission locations, regardless of VO.
	 * 
	 * @return all submission locations
	 */
	public String[] getAllSubmissionLocations();
	
	/**
	 * Returns the name of the site the specified submission location or ulr belongs to.
	 * 
	 * @param urlOrSubmissionLocation the submission location or url
	 * @return the name of the site
	 */
	public String getSite(String urlOrSubmissionLocation);
	
	/**
	 * All the submissionLocations the user has got access to with this fqan.
	 * @param fqan the fqan
	 * @return all submissionLocations
	 */
	public String[] getAllAvailableSubmissionLocations(String fqan);
	
	/**
	 * Returns a list of all sites a user that is member of the specified fqan can access.
	 * 
	 * @param fqan the fqan
	 * @return the list of sites
	 */
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

	/**
	 * A list of all available staging filesystems for this submissionlocation. In order of relevance.
	 * @param subLoc the submissionLocation
	 * @return the staging filesystems
	 */
	public List<String> getStagingFilesystemsForSubmissionLocation(String subLoc);
	
	/**
	 * Calculates the best staging filesystem for this submissionLocation
	 * @param subLoc the submissionLocation
	 * @return the staging filesystem
	 */
	public String getRecommendedStagingFileSystemForSubmissionLocation(String subLoc);

	
}
