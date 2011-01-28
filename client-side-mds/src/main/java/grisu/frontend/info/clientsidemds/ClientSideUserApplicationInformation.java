package grisu.frontend.info.clientsidemds;

import grisu.model.GrisuRegistry;
import grisu.model.UserEnvironmentManager;
import grisu.model.info.UserApplicationInformation;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


import au.org.arcs.jcommons.interfaces.InformationManager;
import au.org.arcs.jcommons.interfaces.MatchMaker;

public class ClientSideUserApplicationInformation extends
		ClientSideApplicationInformation implements UserApplicationInformation {

	private Set<String> cachedSubmissionLocationsForUser = null;
	private Set<String> cachedAllSitesForUser = null;
	private Set<String> cachedAllVersionsForUser = null;
	private final UserEnvironmentManager userInfo;

	public ClientSideUserApplicationInformation(GrisuRegistry registry,
			String applicationName, InformationManager infoManager,
			MatchMaker matchMaker) {
		super(registry, applicationName, infoManager, matchMaker);
		this.userInfo = registry.getUserEnvironmentManager();
	}

	public final Set<String> getAllAvailableSitesForUser() {

		if (cachedAllSitesForUser == null) {
			cachedAllSitesForUser = new TreeSet<String>();
			for (final String subLoc : getAllAvailableSubmissionLocationsForUser()) {
				cachedAllSitesForUser.add(registry.getResourceInformation()
						.getSite(subLoc));
			}
		}
		return cachedAllSitesForUser;
	}

	public final Set<String> getAllAvailableSubmissionLocationsForUser() {

		if (cachedSubmissionLocationsForUser == null) {
			cachedSubmissionLocationsForUser = new HashSet<String>();
			for (final String fqan : userInfo.getAllAvailableFqans()) {
				cachedSubmissionLocationsForUser
						.addAll(getAvailableSubmissionLocationsForFqan(fqan));
			}
		}
		return cachedSubmissionLocationsForUser;
	}

	public final Set<String> getAllAvailableVersionsForUser() {

		if (cachedAllVersionsForUser == null) {
			cachedAllVersionsForUser = new TreeSet<String>();
			for (final String fqan : userInfo.getAllAvailableFqans()) {
				cachedAllVersionsForUser
						.addAll(getAllAvailableVersionsForFqan(fqan));
			}
		}
		return cachedAllVersionsForUser;
	}

}
