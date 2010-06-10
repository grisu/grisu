package org.vpac.grisu.model;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.StatusException;
import org.vpac.grisu.model.dto.DtoBatchJob;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.model.files.GlazedFile;
import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.model.info.ResourceInformation;
import org.vpac.grisu.model.status.StatusObject;
import org.vpac.grisu.settings.ClientPropertiesManager;

import au.org.arcs.jcommons.constants.Constants;

/**
 * The implemenation of {@link UserEnvironmentManager}.
 * 
 * @author markus
 * 
 */
public class UserEnvironmentManagerImpl implements UserEnvironmentManager {

	static final Logger myLogger = Logger
			.getLogger(UserEnvironmentManagerImpl.class.getName());

	private final ServiceInterface serviceInterface;

	private final ResourceInformation resourceInfo;
	private final FileManager fm;

	private String[] cachedFqans = null;
	private final Map<String, Set<String>> cachedFqansPerApplication = new HashMap<String, Set<String>>();
	private Set<String> cachedAllSubmissionLocations = null;
	private SortedSet<String> cachedAllSites = null;
	private final Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerSubmissionLocation = new TreeMap<String, Set<MountPoint>>();
	private final Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerFqan = new TreeMap<String, Set<MountPoint>>();
	private MountPoint[] cachedMountPoints = null;
	private final Map<String, SortedSet<MountPoint>> alreadyQueriedMountPointsPerSite = new TreeMap<String, SortedSet<MountPoint>>();

	private final Map<String, String> cachedUserProperties = null;
	private Map<String, String> cachedBookmarks = null;

	private List<FileSystemItem> cachedLocalFilesystemList = null;
	private List<FileSystemItem> cachedBookmarkFilesystemList = null;
	private List<FileSystemItem> cachedRemoteFilesystemList = null;
	private List<FileSystemItem> cachedAllFileSystems = null;

	private SortedSet<String> cachedAllUsedApplicationsSingle = null;
	private SortedSet<String> cachedAllUsedApplicationsBatch = null;

	private SortedSet<DtoJob> cachedJobList = null;

	private SortedSet<String> cachedJobNames = null;
	private SortedSet<String> cachedBatchJobNames = null;

	private final Map<String, DtoBatchJob> cachedBatchJobs = new TreeMap<String, DtoBatchJob>();
	private final Map<String, SortedSet<String>> cachedBatchJobnamesPerApplication = new HashMap<String, SortedSet<String>>();
	private final Map<String, SortedSet<String>> cachedJobnamesPerApplication = new HashMap<String, SortedSet<String>>();
	private final Map<String, SortedSet<DtoBatchJob>> cachedBatchJobsPerApplication = new HashMap<String, SortedSet<DtoBatchJob>>();

	private String currentFqan;

	public UserEnvironmentManagerImpl(final ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
		this.resourceInfo = GrisuRegistryManager.getDefault(serviceInterface)
				.getResourceInformation();
		this.fm = GrisuRegistryManager.getDefault(serviceInterface)
				.getFileManager();
	}

	public void addFqanListener(final FqanListener listener) {
		// TODO Auto-generated method stub

	}

	public String calculateUniqueJobname(String name) {

		String temp = name;
		int i = 1;

		while (getCurrentJobnames(false).contains(temp)
				|| getCurrentBatchJobnames(false).contains(temp)) {
			temp = name + "_" + i;
			i = i + 1;
		}

		return temp;
	}

	public synchronized final String[] getAllAvailableFqans() {

		if (cachedFqans == null) {
			this.cachedFqans = serviceInterface.getFqans().asArray();
		}
		return cachedFqans;
	}

	public Set<String> getAllAvailableFqansForApplication(String application) {

		if (cachedFqansPerApplication.get(application) == null) {

			ApplicationInformation ai = GrisuRegistryManager.getDefault(
					serviceInterface).getApplicationInformation(application);
			Set<String> result = new TreeSet<String>();

			for (String vo : getAllAvailableFqans()) {

				Set<String> temp = ai
						.getAvailableSubmissionLocationsForFqan(vo);
				if (temp.size() > 0) {
					result.add(vo);
				}

			}

			cachedFqansPerApplication.put(application, result);
		}
		return cachedFqansPerApplication.get(application);
	}

	public synchronized final SortedSet<String> getAllAvailableSites() {

		if (cachedAllSites == null) {
			cachedAllSites = new TreeSet<String>();

			for (MountPoint mp : getMountPoints()) {
				cachedAllSites.add(mp.getSite());
			}
			// for (String subLoc : getAllAvailableSubmissionLocations()) {
			// cachedAllSites.add(resourceInfo.getSite(subLoc));
			// }
		}
		return cachedAllSites;
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

	public DtoBatchJob getBatchJob(String jobname, boolean refresh)
			throws NoSuchJobException {

		DtoBatchJob result = null;

		if (!refresh) {
			result = cachedBatchJobs.get(jobname);
		}
		if ((result == null) || refresh) {
			result = serviceInterface.getBatchJob(jobname);
			cachedBatchJobs.put(jobname, result);
		}

		return result;
	}

	public SortedSet<DtoBatchJob> getBatchJobs(String application,
			boolean refresh) {

		if (StringUtils.isBlank(application)) {
			application = Constants.ALLJOBS_KEY;
		}

		SortedSet<DtoBatchJob> result = null;

		if (!refresh) {
			result = cachedBatchJobsPerApplication.get(application);
		}

		if ((result == null) || refresh) {

			result = new TreeSet<DtoBatchJob>();

			for (String name : getCurrentBatchJobnames(application, true)) {

				DtoBatchJob bj = null;
				try {
					bj = getBatchJob(name, refresh);
				} catch (NoSuchJobException e) {
					throw new RuntimeException(e);
				}

				result.add(bj);
			}
			cachedBatchJobsPerApplication.put(application, result);

		}

		return result;
	}

	public synchronized Map<String, String> getBookmarks() {

		if (cachedBookmarks == null) {
			cachedBookmarks = serviceInterface.getBookmarks().propertiesAsMap();
		}

		return cachedBookmarks;
	}

	public synchronized List<FileSystemItem> getBookmarksFilesystems() {

		if (cachedBookmarkFilesystemList == null) {
			cachedBookmarkFilesystemList = new LinkedList<FileSystemItem>();
			for (String bookmark : getBookmarks().keySet()) {
				String url = getBookmarks().get(bookmark);
				cachedBookmarkFilesystemList.add(new FileSystemItem(bookmark,
						FileSystemItem.Type.BOOKMARK, fm
								.createGlazedFileFromUrl(url)));
			}
		}
		return cachedBookmarkFilesystemList;
	}

	public SortedSet<String> getCurrentApplications(boolean refresh) {

		if (cachedAllUsedApplicationsSingle == null) {
			cachedAllUsedApplicationsSingle = new TreeSet<String>(
					serviceInterface.getUsedApplications().getStringList());
		} else if (refresh) {
			cachedAllUsedApplicationsSingle.clear();
			cachedAllUsedApplicationsSingle.addAll(serviceInterface
					.getUsedApplications().getStringList());
		}
		return cachedAllUsedApplicationsSingle;
	}

	public SortedSet<String> getCurrentApplicationsBatch(boolean refresh) {

		if (cachedAllUsedApplicationsBatch == null) {
			cachedAllUsedApplicationsBatch = new TreeSet<String>(
					serviceInterface.getUsedApplicationsBatch().getStringList());
		} else if (refresh) {
			cachedAllUsedApplicationsBatch.clear();
			cachedAllUsedApplicationsBatch.addAll(serviceInterface
					.getUsedApplicationsBatch().getStringList());
		}
		return cachedAllUsedApplicationsBatch;
	}

	public SortedSet<String> getCurrentBatchJobnames(boolean refresh) {

		if (cachedBatchJobNames == null) {
			cachedBatchJobNames = new TreeSet<String>(serviceInterface
					.getAllBatchJobnames(null).getStringList());
		} else if (refresh) {
			cachedBatchJobNames.clear();
			cachedBatchJobNames.addAll(serviceInterface.getAllBatchJobnames(
					null).getStringList());
		}
		return cachedBatchJobNames;
	}

	public SortedSet<String> getCurrentBatchJobnames(String application,
			boolean refreshBatchJobnames) {

		if (StringUtils.isBlank(application)) {
			application = Constants.ALLJOBS_KEY;
		}

		application = application.toLowerCase();

		SortedSet<String> result = null;

		if (!refreshBatchJobnames) {
			result = cachedBatchJobnamesPerApplication.get(application);
		}

		if ((result == null) || refreshBatchJobnames) {
			result = serviceInterface.getAllBatchJobnames(application)
					.asSortedSet();
			cachedBatchJobnamesPerApplication.put(application, result);
		}
		return result;
	}

	public final String getCurrentFqan() {

		if (StringUtils.isBlank(currentFqan)) {
			
				currentFqan = ClientPropertiesManager.getLastUsedFqan();
				if (StringUtils.isBlank(currentFqan) ) {
					currentFqan = getAllAvailableFqans()[0];
			}
		}

		return currentFqan;
	}

	public synchronized SortedSet<String> getCurrentJobnames(boolean refresh) {

		if (cachedJobNames == null) {
			cachedJobNames = new TreeSet<String>(serviceInterface
					.getAllJobnames(null).getStringList());
		} else if (refresh) {
			cachedJobNames.clear();
			cachedJobNames.addAll(serviceInterface.getAllJobnames(null)
					.getStringList());
		}
		return cachedJobNames;
	}

	public SortedSet<String> getCurrentJobnames(String application,
			boolean refresh) {

		if (StringUtils.isBlank(application)) {
			application = Constants.ALLJOBS_KEY;
		}

		application = application.toLowerCase();

		SortedSet<String> result = null;

		if (!refresh) {
			result = cachedJobnamesPerApplication.get(application);
		}

		if ((result == null) || refresh) {
			result = serviceInterface.getAllJobnames(application).asSortedSet();
			cachedJobnamesPerApplication.put(application, result);
		}
		return result;

	}

	public SortedSet<DtoJob> getCurrentJobs(boolean refreshJobStatus) {
		if (cachedJobList == null) {
			cachedJobList = serviceInterface.ps(null, refreshJobStatus)
					.getAllJobs();
		}
		return cachedJobList;
	}

	public FileSystemItem getFileSystemForUrl(String url) {

		if (StringUtils.isBlank(url)) {
			return null;
		}

		MountPoint mp = getMountPointForUrl(url);

		if (mp != null) {
			String site = mp.getSite();

			for (FileSystemItem item : getFileSystems()) {

				if (FileSystemItem.Type.BOOKMARK.equals(item.getType())) {
					continue;
				}

				if (!item.isDummy()) {
					myLogger.debug("Checking filesystem: "
							+ item.getRootFile().getUrl());
					myLogger.debug("Against site of found mountPoint: " + site);

					if (site.equals(item.getRootFile().getName())) {
						return item;
					}
				}
			}
		}

		for (FileSystemItem item : getFileSystems()) {

			// if (!FileSystemItem.Type.BOOKMARK.equals(item.getType())) {
			// continue;
			// }

			if (!item.isDummy() && url.startsWith(item.getRootFile().getUrl())) {
				return item;
			}
		}
		return null;
	}

	public synchronized List<FileSystemItem> getFileSystems() {

		if (cachedAllFileSystems == null) {

			cachedAllFileSystems = new LinkedList<FileSystemItem>();

			cachedAllFileSystems.addAll(getLocalFileSystems());
			cachedAllFileSystems.addAll(getBookmarksFilesystems());
			cachedAllFileSystems.addAll(getRemoteSites());
		}
		return cachedAllFileSystems;
	}

	public synchronized List<FileSystemItem> getLocalFileSystems() {

		if (cachedLocalFilesystemList == null) {
			cachedLocalFilesystemList = new LinkedList<FileSystemItem>();

			File userHome = new File(System.getProperty("user.home"));
			cachedLocalFilesystemList.add(new FileSystemItem(
					userHome.getName(), FileSystemItem.Type.LOCAL,
					new GlazedFile(userHome)));

			for (File file : File.listRoots()) {
				cachedLocalFilesystemList.add(new FileSystemItem(
						file.getName(), FileSystemItem.Type.LOCAL,
						new GlazedFile(file)));
			}
		}
		return cachedLocalFilesystemList;
	}

	public MountPoint getMountPointForAlias(String alias) {

		for (MountPoint mp : getMountPoints()) {
			if (mp.getAlias().equals(alias)) {
				return mp;
			}
		}

		return null;
	}

	public final MountPoint getMountPointForUrl(final String url) {

		for (MountPoint mp : getMountPoints()) {
			if (mp.isResponsibleForAbsoluteFile(url)) {
				return mp;
			}
		}

		return null;
	}

	/**
	 * Get all the users' mountpoints.
	 * 
	 * @return all mountpoints
	 */
	public final synchronized MountPoint[] getMountPoints() {
		if (cachedMountPoints == null) {
			cachedMountPoints = serviceInterface.df().getMountpoints().toArray(
					new MountPoint[] {});
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
					if ((mp.getFqan() == null)
							|| mp.getFqan().equals(Constants.NON_VO_FQAN)) {
						if ((fqan == null)
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

						if ((mp.getRootUrl().indexOf(host) != -1)
								&& (mp.getRootUrl().indexOf(protocol) != -1)) {
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

					if ((mp.getRootUrl().indexOf(host) != -1)
							&& (mp.getRootUrl().indexOf(protocol) != -1)) {
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

	public synchronized String getProperty(String key) {

		if (StringUtils.isBlank(cachedUserProperties.get(key))) {
			String temp = serviceInterface.getUserProperty(key);
			if (StringUtils.isBlank(temp)) {
				return null;
			} else {
				cachedUserProperties.put(key, temp);
			}
		}
		return cachedUserProperties.get(key);

	}

	public final MountPoint getRecommendedMountPoint(
			final String submissionLocation, final String fqan) {

		Set<MountPoint> temp = getMountPointsForSubmissionLocationAndFqan(
				submissionLocation, fqan);

		return temp.iterator().next();

	}

	public synchronized List<FileSystemItem> getRemoteSites() {

		if (cachedRemoteFilesystemList == null) {
			cachedRemoteFilesystemList = new LinkedList<FileSystemItem>();
			for (String site : getAllAvailableSites()) {
				cachedRemoteFilesystemList.add(new FileSystemItem(site,
						FileSystemItem.Type.REMOTE, new GlazedFile(site)));
			}
		}
		return cachedRemoteFilesystemList;
	}

	public boolean isMountPointAlias(String string) {

		for (MountPoint mp : getMountPoints()) {
			if (mp.getAlias().equals(string)) {
				return true;
			}
		}

		return false;
	}

	public boolean isMountPointRoot(String rootUrl) {

		for (MountPoint mp : getMountPoints()) {
			if (mp.getRootUrl().equals(rootUrl)) {
				return true;
			}
		}

		return false;

	}

	public void removeFqanListener(final FqanListener listener) {
		// TODO Auto-generated method stub

	}

	public synchronized FileSystemItem setBookmark(String alias, String url) {

		serviceInterface.setBookmark(alias, url);

		if (StringUtils.isBlank(url)) {
			FileSystemItem temp = new FileSystemItem(alias,
					FileSystemItem.Type.BOOKMARK, null);
			getBookmarks().remove(alias);
			getBookmarksFilesystems().remove(temp);
			getFileSystems().remove(temp);
			return temp;
		} else {
			FileSystemItem temp = new FileSystemItem(alias,
					FileSystemItem.Type.BOOKMARK, fm
							.createGlazedFileFromUrl(url));
			getBookmarks().put(alias, url);
			getBookmarksFilesystems().add(temp);
			getFileSystems().add(temp);
			return temp;
		}

	}

	public final void setCurrentFqan(final String currentFqan) {
		
		if ( StringUtils.isNotBlank(currentFqan) ) {
			this.currentFqan = currentFqan;
			ClientPropertiesManager.setLastUsedFqan(this.currentFqan);
		}
	}

	public synchronized void setProperty(String key, String value) {

		serviceInterface.setUserProperty(key, value);
		cachedUserProperties.put(key, value);

	}

	public StatusObject waitForActionToFinish(String handle)
			throws InterruptedException, StatusException {

		StatusObject status = new StatusObject(serviceInterface, handle);

		status.waitForActionToFinish(ClientPropertiesManager
				.getDefaultActionStatusRecheckInterval(), false, false);

		return status;
	}

}
