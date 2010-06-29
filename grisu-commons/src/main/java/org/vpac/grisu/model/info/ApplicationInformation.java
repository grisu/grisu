package org.vpac.grisu.model.info;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;

/**
 * Interface which describes the stuff that needs to be known from an
 * application grid-wide in order to be able to submit a job for the application
 * it represents.
 * 
 * @author Markus Binsteiner
 * 
 */
public interface ApplicationInformation {

	/**
	 * Calculates all versions for this application that are availabe for this
	 * fqan, regardless of submissionLocation.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return all available versions
	 */
	Set<String> getAllAvailableVersionsForFqan(String fqan);

	/**
	 * Calculates all submissionlocations for this kind of job.
	 * 
	 * Basically, this returns the same info as
	 * {@link #getAllAvailableVersionsForFqan(String)}, but it returns fully
	 * populated GridResources as a SortedList. It also takes into account
	 * jobproperties. As a result, it might take a bit longer to get a result
	 * from this method.
	 * 
	 * If the current thread is interrupted this will return null
	 * 
	 * @param additionalJobProperties
	 *            the jobProperties
	 * @param fqan
	 *            the fqan to submit the job
	 * @return a sorted list of the best resources to submit this job to.
	 */
	SortedSet<GridResource> getAllSubmissionLocationsAsGridResources(
			Map<JobSubmissionProperty, String> additionalJobProperties,
			String fqan);

	/**
	 * Retrieves a map of all available details (executables, modules, ...)
	 * Grisu knows about this version of the application on this
	 * submissionLocation.
	 * 
	 * @param subLoc
	 *            the submissionLocation
	 * @param version
	 *            the version
	 * @return the details
	 */
	Map<String, String> getApplicationDetails(String subLoc, String version);

	/**
	 * The name of the application this object is all about.
	 * 
	 * @return the name of the application
	 */
	String getApplicationName();

	/**
	 * Returns a set of all available submissionLocations for this application
	 * grid-wide.
	 * 
	 * @return the submissionLocations
	 */
	Set<String> getAvailableAllSubmissionLocations();

	/**
	 * Calculates all the submissionlocations that are available to the user for
	 * the provided fqan and application. It returns all submissionlocations,
	 * regardless of the version of the application.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the submissionlocations
	 */
	Set<String> getAvailableSubmissionLocationsForFqan(String fqan);

	/**
	 * Returns a set of all available submissionLocations for one version of
	 * this application grid-wide.
	 * 
	 * @param version
	 *            the version in question
	 * @return the submissionLocations
	 */
	Set<String> getAvailableSubmissionLocationsForVersion(String version);

	/**
	 * This calculates all the submissionlocations that are available to the
	 * user on the grid for this fqan and application and version.
	 * 
	 * @param version
	 *            the version
	 * @param fqan
	 *            the fqan
	 * @return the submissionLocations
	 */
	Set<String> getAvailableSubmissionLocationsForVersionAndFqan(
			String version, String fqan);

	/**
	 * Calculates all available versions of the application on this
	 * submissionlocation.
	 * 
	 * @param subLoc
	 *            the submissionLocation
	 * @return a set of all available versions
	 */
	Set<String> getAvailableVersions(String subLoc);

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
	SortedSet<GridResource> getBestSubmissionLocations(
			Map<JobSubmissionProperty, String> additionalJobProperties,
			String fqan);

	/**
	 * The available executables for this application on this
	 * submissionlocation.
	 * 
	 * @param subLoc
	 *            the submissionlocation
	 * @param version
	 *            the version of the application
	 * @return a list of all executables
	 */
	String[] getExecutables(String subLoc, String version);

}
