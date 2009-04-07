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

import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.fs.model.MountPoint;

public class UserInformationImpl implements UserInformation {
	
	private final ServiceInterface serviceInterface;
	
	private ResourceInformation resourceInfo = GrisuRegistry.getDefault().getResourceInformation();
	
	private String[] cachedFqans = null;
	private Set<String> cachedAllSubmissionLocations = null;
	private Set<String> cachedAllSites = null;
	private Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerSubmissionLocation = new TreeMap<String, Set<MountPoint>>();
	private Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerFqan = new TreeMap<String, Set<MountPoint>>();
	private MountPoint[] cachedMountPoints = null;


	
	public UserInformationImpl(ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public String[] getAllAvailableFqans() {
		
		if ( cachedFqans == null ) {
			this.cachedFqans = serviceInterface.getFqans();
		}
		return cachedFqans;
	}

	public Set<String> getAllAvailableSubmissionLocations() {

		if ( cachedAllSubmissionLocations == null ) {
			cachedAllSubmissionLocations = new HashSet<String>();
			cachedAllSites = new TreeSet<String>();
			for ( String fqan : getAllAvailableFqans() ) {
				cachedAllSubmissionLocations.addAll(Arrays.asList(resourceInfo.getAllAvailableSubmissionLocations(fqan)));
			}

		}
		return cachedAllSubmissionLocations;
	}

	public Set<String> getAllAvailableSites() {
		
		if ( cachedAllSites == null ) {
			Set<String> temp = new TreeSet<String>();
			for ( String subLoc : getAllAvailableSubmissionLocations() ) {
				temp.add(resourceInfo.getSite(subLoc));
			}
		}
		return cachedAllSites;
	}
	
	public MountPoint getRecommendedMountPoint(String submissionLocation, String fqan) {
		
		Set<MountPoint> temp = getMountPointsForSubmissionLocationAndFqan(submissionLocation, fqan);
		
		return temp.iterator().next();
		
	}

	public synchronized Set<MountPoint> getMountPointsForSubmissionLocation(
			String submissionLocation) {

		if (alreadyQueriedMountPointsPerSubmissionLocation
				.get(submissionLocation) == null) {

//			String[] urls = serviceInterface
//					.getStagingFileSystemForSubmissionLocation(submissionLocation);
			List<String> urls = resourceInfo.getStagingFilesystemsForSubmissionLocation(submissionLocation);

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

	public Set<MountPoint> getMountPointsForSubmissionLocationAndFqan(
			String submissionLocation, String fqan) {

//		String[] urls = serviceInterface
//				.getStagingFileSystemForSubmissionLocation(submissionLocation);
		List<String> urls = resourceInfo.getStagingFilesystemsForSubmissionLocation(submissionLocation);

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
	public synchronized MountPoint[] getMountPoints() {
		if (cachedMountPoints == null) {
			cachedMountPoints = serviceInterface.df();
		}
		return cachedMountPoints;
	}
	
	/**
	 * Gets all the mountpoints for this particular VO
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the mountpoints
	 */
	public Set<MountPoint> getMountPoints(String fqan) {

		if (fqan == null) {
			fqan = JobConstants.NON_VO_FQAN;
		}

		synchronized (fqan) {

			if (alreadyQueriedMountPointsPerFqan.get(fqan) == null) {

				Set<MountPoint> mps = new HashSet<MountPoint>();
				for (MountPoint mp : getMountPoints()) {
					if (mp.getFqan() == null
							|| mp.getFqan().equals(JobConstants.NON_VO_FQAN)) {
						if (fqan == null
								|| fqan.equals(JobConstants.NON_VO_FQAN)) {
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
	
}
