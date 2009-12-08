package org.vpac.grisu.model;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.model.files.GlazedFile;
import org.vpac.grisu.model.info.ResourceInformation;

import au.org.arcs.jcommons.constants.Constants;

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
	private SortedSet<String> cachedAllSites = null;
	private Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerSubmissionLocation = new TreeMap<String, Set<MountPoint>>();
	private Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerFqan = new TreeMap<String, Set<MountPoint>>();
	private MountPoint[] cachedMountPoints = null;
	private Map<String, SortedSet<MountPoint>> alreadyQueriedMountPointsPerSite = new TreeMap<String, SortedSet<MountPoint>>();

	private Map<String, String> cachedUserProperties = null;
	private Map<String, String> cachedBookmarks = null;
	
	private List<FileSystemItem> cachedLocalFilesystemList = null;
	private List<FileSystemItem> cachedBookmarkFilesystemList = null;
	private List<FileSystemItem> cachedRemoteFilesystemList = null;
	private List<FileSystemItem> cachedAllFileSystems = null;
	
	private String currentFqan;

	public UserEnvironmentManagerImpl(final ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
		resourceInfo = GrisuRegistryManager.getDefault(serviceInterface)
				.getResourceInformation();
	}

	public synchronized final String[] getAllAvailableFqans() {

		if (cachedFqans == null) {
			this.cachedFqans = serviceInterface.getFqans().asArray();
		}
		return cachedFqans;
	}

	public synchronized final Set<String> getAllAvailableSubmissionLocations() {

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

	public synchronized final SortedSet<String> getAllAvailableSites() {

		if (cachedAllSites == null) {
			cachedAllSites = new TreeSet<String>();
			for (String subLoc : getAllAvailableSubmissionLocations()) {
				cachedAllSites.add(resourceInfo.getSite(subLoc));
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

	public synchronized final Set<MountPoint> getMountPointsForSubmissionLocationAndFqan(
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
			cachedMountPoints = serviceInterface.df().getMountpoints().toArray(new MountPoint[]{});
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

	public SortedSet<MountPoint> getMountPointsForSite(String site) {

		if (site == null) {
			throw new IllegalArgumentException("Site can't be null.");
		}

		synchronized (site) {

			if (alreadyQueriedMountPointsPerSite.get(site) == null) {

				SortedSet<MountPoint> mps = new TreeSet<MountPoint>();
				for (MountPoint mp : getMountPoints()) {
					if (mp.getSite().equals(site)) {
						mps.add(mp);
						continue;
					}
				}
				alreadyQueriedMountPointsPerSite.put(site, mps);
			}
			return alreadyQueriedMountPointsPerSite.get(site);
		}
		
	}

	public boolean isMountPointAlias(String string) {

		for ( MountPoint mp : getMountPoints() ) {
			if ( mp.getAlias().equals(string) ) {
				return true;
			}
		}
		
		return false;
	}

	public MountPoint getMountPointForAlias(String alias) {

		for ( MountPoint mp : getMountPoints() ) {
			if ( mp.getAlias().equals(alias) ) {
				return mp;
			}
		}
		
		return null;
	}

	public boolean isMountPointRoot(String rootUrl) {

		for ( MountPoint mp : getMountPoints() ) {
			if ( mp.getRootUrl().equals(rootUrl) ) {
				return true;
			}
		}
		
		return false;
		
	}

	public synchronized String getProperty(String key) {

		if ( StringUtils.isBlank(cachedUserProperties.get(key)) ) {
			String temp = serviceInterface.getUserProperty(key);
			if ( StringUtils.isBlank(temp) ) {
				return null;
			} else {
				cachedUserProperties.put(key, temp);
			}
		}
		return cachedUserProperties.get(key);
		
	}

	public synchronized void setProperty(String key, String value) {

		serviceInterface.setUserProperty(key, value);
		cachedUserProperties.put(key, value);
		
	}

	public synchronized Map<String, String> getBookmarks() {

		if ( cachedBookmarks == null ) {
			cachedBookmarks = serviceInterface.getBookmarks().propertiesAsMap();
		}
		
		return cachedBookmarks;
	}

	public synchronized void setBookmark(String alias, String url) {

		serviceInterface.setBookmark(alias, url);
		
		if ( StringUtils.isBlank(url) ) {
			getBookmarks().remove(alias);
			getBookmarksFilesystems().remove(new FileSystemItem(alias, FileSystemItem.Type.BOOKMARK, null));
		} else {
			getBookmarks().put(alias, url);
			getBookmarksFilesystems().add(new FileSystemItem(alias, FileSystemItem.Type.BOOKMARK, 
					createGlazedFileFromUrl(url)));
		}
		
		
		
	}
	
	public synchronized List<FileSystemItem> getLocalFileSystems() {

		if ( cachedLocalFilesystemList == null ) {
			cachedLocalFilesystemList = new LinkedList<FileSystemItem>();
			
			File userHome = new File(System.getProperty("user.home"));
			cachedLocalFilesystemList.add(new FileSystemItem(userHome.getName(), FileSystemItem.Type.LOCAL, 
					new GlazedFile(userHome)));
			
			for ( File file : File.listRoots() ) {
				cachedLocalFilesystemList.add(new FileSystemItem(file.getName(),
						FileSystemItem.Type.LOCAL, new GlazedFile(file)));
			}
		}
		return cachedLocalFilesystemList;
	}

	public synchronized List<FileSystemItem> getFileSystems() {

		if ( cachedAllFileSystems == null ) {
		
			cachedAllFileSystems = new LinkedList<FileSystemItem>();
	
			cachedAllFileSystems.addAll(getLocalFileSystems());
			cachedAllFileSystems.addAll(getBookmarksFilesystems());
			cachedAllFileSystems.addAll(getRemoteSites());
		} 
		return cachedAllFileSystems;
	}
	
	public GlazedFile createGlazedFileFromUrl(String url) {
		
		if ( FileManager.isLocal(url) ) {
			try {
				File file = new File(new URI(url));
				return new GlazedFile(file);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		} else {
			return new GlazedFile(url, serviceInterface);
		}
		
	}
	

	public synchronized List<FileSystemItem> getBookmarksFilesystems() {

		if ( cachedBookmarkFilesystemList == null ) {
			cachedBookmarkFilesystemList = new LinkedList<FileSystemItem>();
			for ( String bookmark : getBookmarks().keySet() ) {
				String url = getBookmarks().get(bookmark);
				cachedBookmarkFilesystemList.add(new FileSystemItem(bookmark, 
							FileSystemItem.Type.BOOKMARK, createGlazedFileFromUrl(url)));
			}
		}
		return cachedBookmarkFilesystemList;
	}

	public synchronized List<FileSystemItem> getRemoteSites() {

		if ( cachedRemoteFilesystemList == null ) {
			cachedRemoteFilesystemList = new LinkedList<FileSystemItem>();
			for ( String site : getAllAvailableSites() ) {
				cachedRemoteFilesystemList.add(new FileSystemItem(site, FileSystemItem.Type.REMOTE, new GlazedFile(site)));
			}
		}
		return cachedRemoteFilesystemList;
	}

	public FileSystemItem getFileSystemForUrl(String url) {

		for ( FileSystemItem item : getFileSystems() ) {
			
			if ( !item.isDummy() && url.startsWith(item.getRootFile().getUrl()) ) {
				return item;
			}
		}
		return null;
	}
	
	

}
