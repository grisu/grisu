package grisu.model.info;

import grisu.model.info.dto.Application;
import grisu.model.info.dto.Queue;
import grisu.model.info.dto.Site;

import java.util.List;
import java.util.Set;

/**
 * Classes that implement this interface provide general information about the
 * grid.
 * 
 * @author markus
 * 
 */
public interface ResourceInformation {

	/**
	 * Convenience method to get a list of sites out of a set of
	 * submissionlocations.
	 * 
	 * @param submissionLocations
	 *            the submissionLocations
	 * @return the sites
	 */
	Set<String> distillSitesFromSubmissionLocations(
			Set<String> submissionLocations);

	/**
	 * Convenience method to filter all submissionlocations out of a list of
	 * submissionlocations that are located at the specified site.
	 * 
	 * @param site
	 *            the site
	 * @param submissionlocations
	 *            all submissionlocations
	 * @return the submissionlocations that are located on the site
	 */
	Set<String> filterSubmissionLocationsForSite(String site,
			Set<String> submissionlocations);

	/**
	 * A list of all applications that are available grid-wide.
	 * 
	 * @return all application packages
	 */
	Application[] getAllApplications();

	/**
	 * A list of all applications that are available for a particular vo.
	 * 
	 * @param fqan
	 *            the vo
	 * @return the applications
	 */
	Application[] getAllApplicationsForFqans(Set<String> fqan);

	/**
	 * Returns a list of all sites a user that is member of the specified fqan
	 * can access.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the list of sites
	 */
	Set<Site> getAllAvailableSites(String fqan);

	/**
	 * All the submissionLocations the user has got access to with this fqan.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return all submissionLocations
	 */
	Queue[] getAllAvailableSubmissionLocations(String fqan);

	/**
	 * Returns a list of all available submission locations, regardless of VO.
	 * 
	 * @return all submission locations
	 */
	Queue[] getAllSubmissionLocations();


	/**
	 * Calculates the best staging filesystem for this submissionLocation.
	 * 
	 * @param subLoc
	 *            the submissionLocation
	 * @return the staging filesystem
	 */
	String getRecommendedStagingFileSystemForSubmissionLocation(String subLoc);

	/**
	 * Returns the name of the site the specified submission location or ulr
	 * belongs to.
	 * 
	 * @param urlOrSubmissionLocation
	 *            the submission location or url
	 * @return the name of the site
	 */
	String getSite(String urlOrSubmissionLocation);

	/**
	 * A list of all available staging filesystems for this submissionlocation.
	 * In order of relevance.
	 * 
	 * @param subLoc
	 *            the submissionLocation
	 * @return the staging filesystems
	 */
	List<String> getStagingFilesystemsForSubmissionLocation(String subLoc);

}
