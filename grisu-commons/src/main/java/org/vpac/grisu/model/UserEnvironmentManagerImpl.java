package org.vpac.grisu.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.info.ResourceInformation;

import au.org.arcs.mds.Constants;

/**
 * The implemenation of {@link UserEnvironmentManager}.
 * 
 * @author markus
 * 
 */
public class UserEnvironmentManagerImpl implements UserEnvironmentManager {

	private final ServiceInterface serviceInterface;

	private ResourceInformation resourceInfo;

	private String[] cachedFqans = null;
	private Set<String> cachedAllSubmissionLocations = null;
	private Set<String> cachedAllSites = null;
	private Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerSubmissionLocation = new TreeMap<String, Set<MountPoint>>();
	private Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerFqan = new TreeMap<String, Set<MountPoint>>();
	private MountPoint[] cachedMountPoints = null;

	private String currentFqan;

	public UserEnvironmentManagerImpl(final ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
		resourceInfo = GrisuRegistry.getDefault(serviceInterface)
				.getResourceInformation();
	}

	public final String[] getAllAvailableFqans() {

		if (cachedFqans == null) {
			this.cachedFqans = serviceInterface.getFqans();
		}
		return cachedFqans;
	}

	public final Set<String> getAllAvailableSubmissionLocations() {

		if (cachedAllSubmissionLocations == null) {
			cachedAllSubmissionLocations = new HashSet<String>();
			cachedAllSites = new TreeSet<String>();
			for (String fqan : getAllAvailableFqans()) {
				cachedAllSubmissionLocations.addAll(Arrays.asList(resourceInfo
						.getAllAvailableSubmissionLocations(fqan)));
			}

		}
		return cachedAllSubmissionLocations;
	}

	public final Set<String> getAllAvailableSites() {

		if (cachedAllSites == null) {
			Set<String> temp = new TreeSet<String>();
			for (String subLoc : getAllAvailableSubmissionLocations()) {
				temp.add(resourceInfo.getSite(subLoc));
			}
		}
		return cachedAllSites;
	}

	public final MountPoint getRecommendedMountPoint(final String submissionLocation,
			final String fqan) {

		Set<MountPoint> temp = getMountPointsForSubmissionLocationAndFqan(
				submissionLocation, fqan);

		return temp.iterator().next();

	}

	public final synchronized Set<MountPoint> getMountPointsForSubmissionLocation(
			final String submissionLocation) {

		if (alreadyQueriedMountPointsPerSubmissionLocation
				.get(submissionLocation) == null) {

			// String[] urls = serviceInterface
			// .getStagingFileSystemForSubmissionLocation(submissionLocation);
			List<String> urls = resourceInfo
					.getStagingFilesystemsForSubmissionLocation(submissionLocation);

			Set<MountPoint> result = new TreeSet<MountPoint>();
			for (String url : urls) {

				try {
					URI uri = new URI(url);
					String host = uri.getHost();
					String protocol = uri.getScheme();

					for (MountPoint mp : getMountPoints()) {

						if (mp.getRootUrl().indexOf(host) != -1
								&& mp.getRootUrl().indexOf(protocol) != -1) {
							result.add(mp);
						}

					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}

			alreadyQueriedMountPointsPerSubmissionLocation.put(
					submissionLocation, result);
		}
		return alreadyQueriedMountPointsPerSubmissionLocation
				.get(submissionLocation);
	}

	public final Set<MountPoint> getMountPointsForSubmissionLocationAndFqan(
			final String submissionLocation, final String fqan) {

		// String[] urls = serviceInterface
		// .getStagingFileSystemForSubmissionLocation(submissionLocation);
		List<String> urls = resourceInfo
				.getStagingFilesystemsForSubmissionLocation(submissionLocation);

		Set<MountPoint> result = new TreeSet<MountPoint>();

		for (String url : urls) {

			try {
				URI uri = new URI(url);
				String host = uri.getHost();
				String protocol = uri.getScheme();

				for (MountPoint mp : getMountPoints(fqan)) {

					if (mp.getRootUrl().indexOf(host) != -1
							&& mp.getRootUrl().indexOf(protocol) != -1) {
						result.add(mp);
					}

				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		return result;

	}

	/**
	 * Get all the users' mountpoints.
	 * 
	 * @return all mountpoints
	 */
	public final synchronized MountPoint[] getMountPoints() {
		if (cachedMountPoints == null) {
			cachedMountPoints = serviceInterface.df();
		}
		return cachedMountPoints;
	}

	/**
	 * Gets all the mountpoints for this particular VO.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the mountpoints
	 */
	public final Set<MountPoint> getMountPoints(String fqan) {

		if (fqan == null) {
			fqan = Constants.NON_VO_FQAN;
		}

		synchronized (fqan) {

			if (alreadyQueriedMountPointsPerFqan.get(fqan) == null) {

				Set<MountPoint> mps = new HashSet<MountPoint>();
				for (MountPoint mp : getMountPoints()) {
					if (mp.getFqan() == null
							|| mp.getFqan().equals(Constants.NON_VO_FQAN)) {
						if (fqan == null
								|| fqan.equals(Constants.NON_VO_FQAN)) {
							mps.add(mp);
							continue;
						} else {
							continue;
						}
					} else {
						if (mp.getFqan().equals(fqan)) {
							mps.add(mp);
							continue;
						}
					}
				}
				alreadyQueriedMountPointsPerFqan.put(fqan, mps);
			}
			return alreadyQueriedMountPointsPerFqan.get(fqan);
		}
	}

	public final MountPoint getMountPointForUrl(final String url) {

		for (MountPoint mp : getMountPoints()) {
			if (mp.isResponsibleForAbsoluteFile(url)) {
				return mp;
			}
		}

		return null;
	}

	public void addFqanListener(final FqanListener listener) {
		// TODO Auto-generated method stub

	}

	public final String getCurrentFqan() {
		return currentFqan;
	}

	public void removeFqanListener(final FqanListener listener) {
		// TODO Auto-generated method stub

	}

	public final void setCurrentFqan(final String currentFqan) {
		this.currentFqan = currentFqan;
	}

}
