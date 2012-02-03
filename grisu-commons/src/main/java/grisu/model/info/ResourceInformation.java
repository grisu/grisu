package grisu.model.info;

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
	Set<String> getAllApplications();

	/**
	 * A list of all applications that are available for a particular vo.
	 * 
	 * @param fqan
	 *            the vo
	 * @return the applications
	 */
	Set<String> getAllApplicationsForFqans(Set<String> fqan);

	/**
	 * Returns a list of all sites a user that is member of the specified fqan
	 * can access.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the list of sites
	 */
	Set<String> getAllAvailableSites(String fqan);

	/**
	 * All the submissionLocations the user has got access to with this fqan.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return all submissionLocations
	 */
	String[] getAllAvailableSubmissionLocations(String fqan);

	/**
	 * Returns a list of all available submission locations, regardless of VO.
	 * 
	 * @return all submission locations
	 */
	String[] getAllSubmissionLocations();


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
