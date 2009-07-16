package org.vpac.grisu.model.info;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistry;

import au.org.arcs.mds.Constants;

/**
 * Default implementation for {@link ApplicationInformation}.
 * 
 * @author Markus Binsteiner
  */
public class ApplicationInformationImpl implements ApplicationInformation {

	private final ServiceInterface serviceInterface;
	private final String application;

	private final ResourceInformation resourceInfo;

	public final ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	public final ResourceInformation getResourceInfo() {
		return resourceInfo;
	}

	private Map<String, Map<String, String>> cachedApplicationDetails = new HashMap<String, Map<String, String>>();
	private Set<String> cachedAllSubmissionLocations = null;
	private Map<String, Set<String>> cachedVersionsPerSubmissionLocations = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedSubmissionLocationsPerVersion = new HashMap<String, Set<String>>();
	// private Map<String, Set<String>> cachedVersionsForSubmissionLocation =
	// new HashMap<String, Set<String>>();

	private Map<String, Set<String>> cachedSubmissionLocationsForUserPerFqan = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedSubmissionLocationsForUserPerVersionAndFqan = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedVersionsForUserPerFqan = new HashMap<String, Set<String>>();

	/**
	 * Default constructor for this class.
	 * 
	 * @param serviceInterface the serviceinterface
	 * @param app the name of the application
	 */
	public ApplicationInformationImpl(final ServiceInterface serviceInterface,
			final String app) {
		this.serviceInterface = serviceInterface;
		this.resourceInfo = GrisuRegistry.getDefault(serviceInterface)
				.getResourceInformation();
		this.application = app;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getApplicationDetails(java.lang.String, java.lang.String)
	 */
	public final Map<String, String> getApplicationDetails(final String subLoc,
			final String version) {
		final String KEY = version + "_" + subLoc;

		if (cachedApplicationDetails.get(KEY) == null) {
			Map<String, String> details = serviceInterface
					.getApplicationDetails(application, version, subLoc);
			cachedApplicationDetails.put(KEY, details);
		}
		return cachedApplicationDetails.get(KEY);
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getApplicationName()
	 */
	public final String getApplicationName() {
		return application;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getAvailableVersions(java.lang.String)
	 */
	public final Set<String> getAvailableVersions(final String subLoc) {

		final String KEY = subLoc;

		if (cachedVersionsPerSubmissionLocations.get(KEY) == null) {
			List<String> temp = Arrays.asList(serviceInterface
					.getVersionsOfApplicationOnSubmissionLocation(application,
							subLoc));
			cachedVersionsPerSubmissionLocations.put(KEY, new HashSet<String>(
					temp));
		}
		return cachedVersionsPerSubmissionLocations.get(KEY);

	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getExecutables(java.lang.String, java.lang.String)
	 */
	public final String[] getExecutables(final String subLoc, final String version) {

		return getApplicationDetails(subLoc, version).get(
				Constants.MDS_EXECUTABLES_KEY).split(",");

	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getAvailableAllSubmissionLocations()
	 */
	public final Set<String> getAvailableAllSubmissionLocations() {

		if (cachedAllSubmissionLocations == null) {
			cachedAllSubmissionLocations = new HashSet(Arrays
					.asList(serviceInterface
							.getSubmissionLocationsForApplication(application)));
		}
		return cachedAllSubmissionLocations;

	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getAvailableSubmissionLocationsForVersion(java.lang.String)
	 */
	public final Set<String> getAvailableSubmissionLocationsForVersion(final String version) {

		if (cachedSubmissionLocationsPerVersion.get(version) == null) {
			List<String> temp = Arrays
					.asList(serviceInterface
							.getSubmissionLocationsForApplication(application,
									version));
			cachedSubmissionLocationsPerVersion.put(version, new HashSet(temp));
		}
		return cachedSubmissionLocationsPerVersion.get(version);
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getAllAvailableVersionsForFqan(java.lang.String)
	 */
	public final Set<String> getAllAvailableVersionsForFqan(final String fqan) {

		if (cachedVersionsForUserPerFqan.get(fqan) == null) {
			Set<String> result = new TreeSet<String>();
			for (String subLoc : getAvailableSubmissionLocationsForFqan(fqan)) {
				List<String> temp = Arrays.asList(serviceInterface
						.getVersionsOfApplicationOnSubmissionLocation(
								getApplicationName(), subLoc));
				result.addAll(temp);
			}
			cachedVersionsForUserPerFqan.put(fqan, result);
		}
		return cachedVersionsForUserPerFqan.get(fqan);
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getAvailableSubmissionLocationsForFqan(java.lang.String)
	 */
	public final Set<String> getAvailableSubmissionLocationsForFqan(final String fqan) {

		if (cachedSubmissionLocationsForUserPerFqan.get(fqan) == null) {
			Set<String> temp = new HashSet<String>();
			for (String subLoc : resourceInfo
					.getAllAvailableSubmissionLocations(fqan)) {
				if (getAvailableAllSubmissionLocations().contains(subLoc)) {
					temp.add(subLoc);
				}
			}
			cachedSubmissionLocationsForUserPerFqan.put(fqan, temp);
		}
		return cachedSubmissionLocationsForUserPerFqan.get(fqan);
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.model.info.ApplicationInformation#getAvailableSubmissionLocationsForVersionAndFqan(java.lang.String, java.lang.String)
	 */
	public final Set<String> getAvailableSubmissionLocationsForVersionAndFqan(
			final String version, final String fqan) {

		final String KEY = version + "_" + fqan;

		if (cachedSubmissionLocationsForUserPerVersionAndFqan.get(KEY) == null) {
			Set<String> temp = new HashSet<String>();
			for (String subLoc : resourceInfo
					.getAllAvailableSubmissionLocations(fqan)) {
				if (getAvailableSubmissionLocationsForVersion(version)
						.contains(subLoc)) {
					temp.add(subLoc);
				}
			}
			cachedSubmissionLocationsForUserPerVersionAndFqan.put(KEY, temp);
		}
		return cachedSubmissionLocationsForUserPerVersionAndFqan.get(KEY);
	}

}
