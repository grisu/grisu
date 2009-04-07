package org.vpac.grisu.model;

import java.util.Set;

import org.vpac.grisu.fs.model.MountPoint;

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
	 * Gets all the mountpoints for this particular VO
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the mountpoints
	 */
	public Set<MountPoint> getMountPoints(String fqan);
	
	/**
	 * Get all the users' mountpoints.
	 * 
	 * @return all mountpoints
	 */
	public MountPoint[] getMountPoints();
	
	public Set<MountPoint> getMountPointsForSubmissionLocationAndFqan(
			String submissionLocation, String fqan);
	
	public Set<MountPoint> getMountPointsForSubmissionLocation(
			String submissionLocation);
	
	public Set<String> getAllAvailableSites();
	
	
	public MountPoint getRecommendedMountPoint(String submissionLocation, String fqan);
	
}
