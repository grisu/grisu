package org.vpac.grisu.model.info;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.UserEnvironmentManager;

/**
 * Implementation of {@link UserApplicationInformation}.
 * 
 * @author markus
 */
public class UserApplicationInformationImpl extends ApplicationInformationImpl
		implements UserApplicationInformation {

	private Set<String> cachedSubmissionLocationsForUser = null;
	private Set<String> cachedAllSitesForUser = null;
	private Set<String> cachedAllVersionsForUser = null;
	private UserEnvironmentManager userInfo = null;

	public UserApplicationInformationImpl(
			final ServiceInterface serviceInterface,
			final UserEnvironmentManager userInfo, final String application) {
		super(serviceInterface, application);
		this.userInfo = userInfo;
	}

	public final Set<String> getAllAvailableSitesForUser() {

		if (cachedAllSitesForUser == null) {
			cachedAllSitesForUser = new TreeSet<String>();
			for (final String subLoc : getAllAvailableSubmissionLocationsForUser()) {
				cachedAllSitesForUser.add(getResourceInfo().getSite(subLoc));
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
