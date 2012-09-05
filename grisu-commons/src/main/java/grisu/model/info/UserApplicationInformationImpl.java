package grisu.model.info;

import grisu.control.ServiceInterface;
import grisu.model.UserEnvironmentManager;
import grisu.model.info.dto.Queue;
import grisu.model.info.dto.Site;
import grisu.model.info.dto.Version;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of {@link UserApplicationInformation}.
 * 
 * @author markus
 */
public class UserApplicationInformationImpl extends ApplicationInformationImpl
implements UserApplicationInformation {

	private Set<Queue> cachedSubmissionLocationsForUser = null;
	private Set<Site> cachedAllSitesForUser = null;
	private Set<Version> cachedAllVersionsForUser = null;
	private UserEnvironmentManager userInfo = null;

	public UserApplicationInformationImpl(
			final ServiceInterface serviceInterface,
			final UserEnvironmentManager userInfo, final String application) {
		super(serviceInterface, application);
		this.userInfo = userInfo;
	}

	public synchronized final Set<Site> getAllAvailableSitesForUser() {

		if (cachedAllSitesForUser == null) {
			cachedAllSitesForUser = new TreeSet<Site>();
			for (final Queue subLoc : getAllAvailableSubmissionLocationsForUser()) {
				cachedAllSitesForUser.add(subLoc.getGateway().getSite());
			}
		}
		return cachedAllSitesForUser;
	}

	public synchronized final Set<Queue> getAllAvailableSubmissionLocationsForUser() {

		if (cachedSubmissionLocationsForUser == null) {
			cachedSubmissionLocationsForUser = new HashSet<Queue>();
			for (final String fqan : userInfo.getAllAvailableFqans()) {
				cachedSubmissionLocationsForUser
				.addAll(getAvailableSubmissionLocationsForFqan(fqan));
			}
		}
		return cachedSubmissionLocationsForUser;
	}

	public synchronized final Set<Version> getAllAvailableVersionsForUser() {

		if (cachedAllVersionsForUser == null) {
			cachedAllVersionsForUser = new TreeSet<Version>();
			for (final String fqan : userInfo.getAllAvailableFqans()) {
				cachedAllVersionsForUser
				.addAll(getAllAvailableVersionsForFqan(fqan));
			}
		}
		return cachedAllVersionsForUser;
	}

}
