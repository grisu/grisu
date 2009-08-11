package org.vpac.grisu.frontend.info.clientsidemds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.info.UserApplicationInformation;
import org.vpac.grisu.settings.Environment;

import au.org.arcs.grid.grisu.matchmaker.MatchMakerImpl;
import au.org.arcs.grid.sched.MatchMaker;
import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.interfaces.InformationManager;

public class ClientSideUserApplicationInformation extends
		ClientSideApplicationInformation implements UserApplicationInformation {
	
	private Set<String> cachedSubmissionLocationsForUser = null;
	private Set<String> cachedAllSitesForUser = null;
	private Set<String> cachedAllVersionsForUser = null;
	private final UserEnvironmentManager userInfo;
	private final MatchMaker matchMaker;
	
	public ClientSideUserApplicationInformation(GrisuRegistry registry, String applicationName, InformationManager infoManager) {
		super(registry, applicationName, infoManager);
		this.userInfo = registry.getUserEnvironmentManager();
		this.matchMaker = new MatchMakerImpl(Environment.getGrisuDirectory().toString());
		
	}
	
	public final Set<String> getAllAvailableSubmissionLocationsForUser() {

		if (cachedSubmissionLocationsForUser == null) {
			cachedSubmissionLocationsForUser = new HashSet<String>();
			for (String fqan : userInfo.getAllAvailableFqans()) {
				cachedSubmissionLocationsForUser
						.addAll(getAvailableSubmissionLocationsForFqan(fqan));
			}
		}
		return cachedSubmissionLocationsForUser;
	}

	public final Set<String> getAllAvailableSitesForUser() {

		if (cachedAllSitesForUser == null) {
			cachedAllSitesForUser = new TreeSet<String>();
			for (String subLoc : getAllAvailableSubmissionLocationsForUser()) {
				cachedAllSitesForUser.add(registry.getResourceInformation().getSite(subLoc));
			}
		}
		return cachedAllSitesForUser;
	}

	public final Set<String> getAllAvailableVersionsForUser() {

		if (cachedAllVersionsForUser == null) {
			cachedAllVersionsForUser = new TreeSet<String>();
			for (String fqan : userInfo.getAllAvailableFqans()) {
				cachedAllVersionsForUser
						.addAll(getAllAvailableVersionsForFqan(fqan));
			}
		}
		return cachedAllVersionsForUser;
	}

	public final SortedSet<GridResource> getBestSubmissionLocations(
			final Map<JobSubmissionProperty, String> additionalJobProperties,
			final String fqan) {

		Map<JobSubmissionProperty, String> basicJobProperties = new HashMap<JobSubmissionProperty, String>();
		basicJobProperties.put(JobSubmissionProperty.APPLICATIONNAME,
				getApplicationName());

		basicJobProperties.putAll(additionalJobProperties);

		return new TreeSet<GridResource>(matchMaker.findMatchingResources(basicJobProperties, fqan));
	}

}
