package org.vpac.grisu.frontend.info.clientsidemds;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.settings.Environment;

import au.org.arcs.grid.grisu.matchmaker.MatchMakerImpl;
import au.org.arcs.grid.sched.MatchMaker;
import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.interfaces.InformationManager;

public class ClientSideApplicationInformation implements ApplicationInformation {

	private final InformationManager infoManager;
	private final String applicationName;
	protected final GrisuRegistry registry;
	private final MatchMaker matchMaker;

	public ClientSideApplicationInformation(GrisuRegistry registry,
			String applicationName, InformationManager infoManager) {
		this.registry = registry;
		this.infoManager = infoManager;
		this.applicationName = applicationName;
		this.matchMaker = new MatchMakerImpl(Environment.getGrisuClientDirectory()
				.toString());
	}

	public Set<String> getAllAvailableVersionsForFqan(String fqan) {
		return new TreeSet<String>(Arrays.asList(infoManager
				.getAllVersionsOfApplicationOnGridForVO(applicationName, fqan)));
	}

	public SortedSet<GridResource> getAllSubmissionLocationsAsGridResources(
			Map<JobSubmissionProperty, String> additionalJobProperties,
			String fqan) {

		Map<JobSubmissionProperty, String> basicJobProperties = new HashMap<JobSubmissionProperty, String>();
		basicJobProperties.put(JobSubmissionProperty.APPLICATIONNAME,
				getApplicationName());

		basicJobProperties.putAll(additionalJobProperties);

		return new TreeSet<GridResource>(matchMaker.findAllResources(
				basicJobProperties, fqan));
	}

	public Map<String, String> getApplicationDetails(String subLoc,
			String version) {
		return infoManager.getApplicationDetails(applicationName, version,
				registry.getResourceInformation().getSite(subLoc));
	}

	public String getApplicationName() {
		return applicationName;
	}

	public Set<String> getAvailableAllSubmissionLocations() {
		return new TreeSet<String>(Arrays.asList(infoManager
				.getAllSubmissionLocationsForApplication(applicationName)));
	}

	public Set<String> getAvailableSubmissionLocationsForFqan(String fqan) {
		return new TreeSet<String>(Arrays.asList(infoManager
				.getAllSubmissionLocationsForVO(fqan)));
	}

	public Set<String> getAvailableSubmissionLocationsForVersion(String version) {
		return new TreeSet<String>(Arrays.asList(infoManager
				.getAllSubmissionLocations(applicationName, version)));
	}

	public Set<String> getAvailableSubmissionLocationsForVersionAndFqan(
			String version, String fqan) {

		Set<String> temp = new TreeSet<String>();
		for (String subLoc : registry.getResourceInformation()
				.getAllAvailableSubmissionLocations(fqan)) {
			if (getAvailableSubmissionLocationsForVersion(version).contains(
					subLoc)) {
				temp.add(subLoc);
			}
		}
		return temp;
	}

	public Set<String> getAvailableVersions(String subLoc) {
		return new TreeSet<String>(Arrays.asList(infoManager
				.getVersionsOfApplicationOnSubmissionLocation(applicationName,
						subLoc)));
	}

	public final SortedSet<GridResource> getBestSubmissionLocations(
			final Map<JobSubmissionProperty, String> additionalJobProperties,
			final String fqan) {

		Map<JobSubmissionProperty, String> basicJobProperties = new HashMap<JobSubmissionProperty, String>();
		basicJobProperties.put(JobSubmissionProperty.APPLICATIONNAME,
				getApplicationName());

		basicJobProperties.putAll(additionalJobProperties);

		return new TreeSet<GridResource>(matchMaker.findAvailableResources(
				basicJobProperties, fqan));
	}

	public String[] getExecutables(String subLoc, String version) {

		return getApplicationDetails(subLoc, version).get(
				Constants.MDS_EXECUTABLES_KEY).split(",");
	}

}
