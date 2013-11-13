package grisu.model;

import com.google.common.base.Strings;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.control.exceptions.StatusException;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.utils.FqanHelpers;
import grisu.model.dto.DtoBatchJob;
import grisu.model.dto.DtoJob;
import grisu.model.dto.GridFile;
import grisu.model.files.FileSystemItem;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.ResourceInformation;
import grisu.model.info.dto.*;
import grisu.model.info.dto.Queue;
import grisu.model.status.StatusObject;
import grisu.settings.ClientPropertiesManager;
import net.sf.ehcache.util.NamedThreadFactory;
import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.TreeModel;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * The implemenation of {@link UserEnvironmentManager}.
 *
 * @author markus
 *
 */
public class UserEnvironmentManagerImpl implements UserEnvironmentManager,
EventSubscriber<FqanEvent> {

	static final Logger myLogger = LoggerFactory
			.getLogger(UserEnvironmentManagerImpl.class);

	private final ServiceInterface serviceInterface;

	private final ResourceInformation resourceInfo;
	private FileManager fm;

    private String[] cachedFqans = null;
    private String[] cachedJobFqans = null;
	private Application[] cachedApplications = null;
	private String[] cachedFqansUsable = null;
	private String[] cachedUniqueGroupnames = null;
	private String[] cachedUniqueGroupnamesUsable = null;
	private final Map<String, Set<String>> cachedFqansPerApplication = new HashMap<String, Set<String>>();
	private Set<Queue> cachedAllSubmissionLocations = null;
	private SortedSet<String> cachedAllSites = null;
	private final Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerSubmissionLocation = new TreeMap<String, Set<MountPoint>>();
    private final Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerFqan = new TreeMap<String, Set<MountPoint>>();
    private final Map<String, Set<Queue>> alreadyQueriedQueuesPerFqan = new TreeMap<String, Set<Queue>>();
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
	private SortedSet<String> allJobnames = null;

	private TreeModel groupFileTreemodel = null;

	private final Map<String, DtoBatchJob> cachedBatchJobs = new TreeMap<String, DtoBatchJob>();
	private final Map<String, SortedSet<String>> cachedBatchJobnamesPerApplication = new HashMap<String, SortedSet<String>>();
	private final Map<String, SortedSet<String>> cachedJobnamesPerApplication = new HashMap<String, SortedSet<String>>();
	private final Map<String, SortedSet<DtoBatchJob>> cachedBatchJobsPerApplication = new HashMap<String, SortedSet<DtoBatchJob>>();

	private String currentFqan;

	private boolean internalFqanEvent = true;

	private boolean internalFqanChange = false;

	public UserEnvironmentManagerImpl(final ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
		this.resourceInfo = GrisuRegistryManager.getDefault(serviceInterface)
				.getResourceInformation();

		EventBus.subscribe(FqanEvent.class, this);

	}

	public void addFqanListener(final FqanListener listener) {

		throw new RuntimeException(
				"Adding of fqan listener not implemented yet.");

	}

	public synchronized String calculateUniqueJobname(String name) {

		String temp = name;
		int i = 1;

		while (getReallyAllJobnames(true).contains(temp)) {
			temp = name + "_" + Strings.padStart(Integer.toString(i), 3, '0');
			i = i + 1;
		}

		return temp;
	}

	public synchronized Application[] getAllAvailableApplications() {

		if (cachedApplications == null) {

			cachedApplications = serviceInterface
					.getAllAvailableApplications(DtoStringList
							.fromStringArray(getAllAvailableFqans(true)));
		}

		return cachedApplications;
	}

	// public synchronized Map<String, Set<String>> getAllAvailableExecutables()
	// {
	//
	// final Map<String, Set<String>> allExes = Collections
	// .synchronizedMap(new TreeMap<String, Set<String>>());
	//
	// final ThreadFactory tf = new NamedThreadFactory(
	// "infoGetAllAvailableExecutables");
	// final ExecutorService executor = Executors.newFixedThreadPool(50, tf);
	//
	// for (final String application : getAllAvailableApplications()) {
	// allExes.put(application,
	// Collections.synchronizedSet(new TreeSet<String>()));
	// }
	//
	// for (final String application : getAllAvailableApplications()) {
	//
	// final ApplicationInformation ai = GrisuRegistryManager.getDefault(
	// serviceInterface).getApplicationInformation(application);
	//
	// final Set<String> sublocs = ai.getAvailableAllSubmissionLocations();
	//
	// final Set<String> sublocsUser = getAllAvailableSubmissionLocations();
	//
	// for (final String subLoc : Sets.intersection(sublocs, sublocsUser)) {
	//
	// final Set<String> versions = ai.getAvailableVersions(subLoc);
	//
	// for (final String version : versions) {
	//
	// final Thread t = new Thread() {
	// @Override
	// public void run() {
	// final String[] exes = ai.getExecutables(subLoc,
	// version);
	// X.p("Exes for: " + application + " " + subLoc + " "
	// + version);
	// allExes.get(application)
	// .addAll(Arrays.asList(exes));
	// }
	// };
	// executor.execute(t);
	// }
	// }
	//
	// }
	//
	// executor.shutdown();
	//
	// try {
	// executor.awaitTermination(120, TimeUnit.SECONDS);
	// } catch (final InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// return allExes;
	// }

	public final String[] getAllAvailableFqans() {
		return getAllAvailableFqans(false);
	}

	public synchronized final String[] getAllAvailableFqans(
			boolean excludeUnusableFqans) {

		if (!excludeUnusableFqans) {

			if (cachedFqans == null) {
				DtoStringList temp = serviceInterface.getFqans();
				this.cachedFqans = temp.asArray();
			}

			return cachedFqans;
		} else {
			if (cachedFqansUsable == null) {
				final List<String> result = new ArrayList<String>();
				for (final String fqan : getAllAvailableFqans(false)) {
					if (getMountPoints(fqan).size() > 0) {
						result.add(fqan);
					}
				}
				cachedFqansUsable = result.toArray(new String[result.size()]);
			}
			return cachedFqansUsable;
		}
	}

    public synchronized final String[] getAllAvailableJobFqans() {
        if ( cachedJobFqans == null ) {
            final List<String> result = new ArrayList<String>();
				for (final String fqan : getAllAvailableFqans(true)) {
                    if (resourceInfo.getAllAvailableSubmissionLocations(fqan).length > 0) {
						result.add(fqan);
					}
				}
				cachedJobFqans = result.toArray(new String[result.size()]);
        }
        return cachedJobFqans;
    }

	public synchronized Set<String> getAllAvailableFqansForApplication(
			String application) {

		if (cachedFqansPerApplication.get(application) == null) {

			final ApplicationInformation ai = GrisuRegistryManager.getDefault(
					serviceInterface).getApplicationInformation(application);
			final Set<String> result = new TreeSet<String>();

			for (final String vo : getAllAvailableFqans()) {

				final Set<Queue> temp = ai
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

			for (final MountPoint mp : getMountPoints()) {
				if (StringUtils.isBlank(mp.getSite())) {
					cachedAllSites.add("Unknown");
					myLogger.error("No site specified for mountpoint: "
							+ mp.getAlias() + ", " + mp.getRootUrl());
				} else {
					cachedAllSites.add(mp.getSite());
				}
			}

		}
		return cachedAllSites;
	}

	public synchronized final Set<Queue> getAllAvailableSubmissionLocations() {

		if (cachedAllSubmissionLocations == null) {
			cachedAllSubmissionLocations = Collections
					.synchronizedSet(new TreeSet<Queue>());
			cachedAllSites = new TreeSet<String>();

			if ((getAllAvailableFqans() == null)
					|| (getAllAvailableFqans().length == 0)) {
				return cachedAllSubmissionLocations;
			}

			final ThreadFactory tf = new NamedThreadFactory(
					"infoGetAllAvailableSubLocs");
			final ExecutorService executor = Executors
					.newFixedThreadPool(
							getAllAvailableFqans().length, tf);
			for (final String fqan : getAllAvailableFqans()) {

				final Thread t = new Thread() {
					@Override
					public void run() {
						myLogger.debug(
								"Adding all sublocs to cache for fqan (current size: {}): {}",
								cachedAllSubmissionLocations.size(),
								fqan);
						cachedAllSubmissionLocations
						.addAll(Arrays.asList(resourceInfo
								.getAllAvailableSubmissionLocations(fqan)));
					}
				};
				executor.execute(t);
			}
			executor.shutdown();

			try {
				executor.awaitTermination(60, TimeUnit.SECONDS);
			} catch (final InterruptedException e) {
				myLogger.error(e.getLocalizedMessage(), e);
			}

		}
		return cachedAllSubmissionLocations;
	}

	public String[] getAllAvailableUniqueGroupnames(
			boolean excludeGroupsWithNoQuota) {

		if (!excludeGroupsWithNoQuota) {
			if (cachedUniqueGroupnames == null) {
				cachedUniqueGroupnames = new String[getAllAvailableFqans().length];
				for (int i = 0; i < cachedUniqueGroupnames.length; i++) {
					cachedUniqueGroupnames[i] = getUniqueGroupname(getAllAvailableFqans()[i]);
				}
			}
			return cachedUniqueGroupnames;
		} else {
			if (cachedUniqueGroupnamesUsable == null) {
				cachedUniqueGroupnamesUsable = new String[getAllAvailableFqans(true).length];
				for (int i = 0; i < cachedUniqueGroupnamesUsable.length; i++) {
					cachedUniqueGroupnamesUsable[i] = getUniqueGroupname(getAllAvailableFqans(true)[i]);
				}
			}
			return cachedUniqueGroupnamesUsable;
		}
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

			for (final String name : getCurrentBatchJobnames(application, true)) {

				DtoBatchJob bj = null;
				try {
					bj = getBatchJob(name, refresh);
				} catch (final NoSuchJobException e) {
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
			cachedBookmarks = DtoProperties.asMap(serviceInterface.getBookmarks());
		}

		return cachedBookmarks;
	}

	public synchronized List<FileSystemItem> getBookmarksFilesystems() {

		if (cachedBookmarkFilesystemList == null) {
			cachedBookmarkFilesystemList = new LinkedList<FileSystemItem>();
			for (final String bookmark : getBookmarks().keySet()) {
				final String url = getBookmarks().get(bookmark);
				try {
					cachedBookmarkFilesystemList.add(new FileSystemItem(
							bookmark, FileSystemItem.Type.BOOKMARK,
							getFileManager().createGridFile(url)));
				} catch (RemoteFileSystemException e) {
					cachedBookmarkFilesystemList.add(new FileSystemItem(
							bookmark, FileSystemItem.Type.BOOKMARK,
							new GridFile(url, false, e)));
				}

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

	public final synchronized String getCurrentFqan() {

		if (StringUtils.isBlank(currentFqan)) {

			currentFqan = ClientPropertiesManager.getLastUsedFqan();
			if (StringUtils.isBlank(currentFqan)) {
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
		if ((cachedJobList == null) || refreshJobStatus) {
			cachedJobList = serviceInterface.getActiveJobs(null,
					refreshJobStatus).getAllJobs();
		}
		return cachedJobList;
	}

	private FileManager getFileManager() {
		if (this.fm == null) {
			this.fm = GrisuRegistryManager.getDefault(serviceInterface)
					.getFileManager();
		}
		return this.fm;
	}

	public FileSystemItem getFileSystemForUrl(String url) {

		if (StringUtils.isBlank(url)) {
			return null;
		}

		final MountPoint mp = getMountPointForUrl(url);

		if (mp != null) {
			final String site = mp.getSite();

			for (final FileSystemItem item : getFileSystems()) {

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

		for (final FileSystemItem item : getFileSystems()) {

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

	public String getFullFqan(String uniqueGroupname) {
		return FqanHelpers.getFullFqan(getAllAvailableFqans(), uniqueGroupname);
	}

	// public TreeModel getGroupTreeFileModel(GlazedFile root) {
	//
	// if (groupFileTreemodel == null) {
	// groupFileTreemodel = new UserspaceFileTreeModel(serviceInterface,
	// root);
	// }
	// return groupFileTreemodel;
	// }

	public synchronized List<FileSystemItem> getLocalFileSystems() {

		if (cachedLocalFilesystemList == null) {
			cachedLocalFilesystemList = new LinkedList<FileSystemItem>();

			final File userHome = new File(System.getProperty("user.home"));
			cachedLocalFilesystemList.add(new FileSystemItem(
					userHome.getName(), FileSystemItem.Type.LOCAL,
					new GridFile(userHome)));

			for (final File file : File.listRoots()) {
				cachedLocalFilesystemList.add(new FileSystemItem(
						file.getName(), FileSystemItem.Type.LOCAL,
						new GridFile(file)));
			}
		}
		return cachedLocalFilesystemList;
	}

	public MountPoint getMountPointForAlias(String alias) {

		for (final MountPoint mp : getMountPoints()) {
			if (mp.getAlias().equals(alias)) {
				return mp;
			}
		}

		return null;
	}

	public final MountPoint getMountPointForUrl(final String url) {

		for (final MountPoint mp : getMountPoints()) {
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
			cachedMountPoints = serviceInterface.df().getMountpoints()
					.toArray(new MountPoint[] {});
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

				final Set<MountPoint> mps = new HashSet<MountPoint>();
				for (final MountPoint mp : getMountPoints()) {
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

				final SortedSet<MountPoint> mps = new TreeSet<MountPoint>();
				for (final MountPoint mp : getMountPoints()) {
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
			final List<String> urls = resourceInfo
					.getStagingFilesystemsForSubmissionLocation(submissionLocation);

			final Set<MountPoint> result = new TreeSet<MountPoint>();
			for (final String url : urls) {

				try {
					final URI uri = new URI(url);
					final String host = uri.getHost();
					final String protocol = uri.getScheme();

					for (final MountPoint mp : getMountPoints()) {

						if ((mp.getRootUrl().indexOf(host) != -1)
								&& (mp.getRootUrl().indexOf(protocol) != -1)) {
							result.add(mp);
						}

					}
				} catch (final URISyntaxException e) {
					myLogger.error(e.getLocalizedMessage(), e);
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
		final List<String> urls = resourceInfo
				.getStagingFilesystemsForSubmissionLocation(submissionLocation);

		final Set<MountPoint> result = new TreeSet<MountPoint>();

		for (final String url : urls) {

			try {
				final URI uri = new URI(url);
				final String host = uri.getHost();
				final String protocol = uri.getScheme();

				for (final MountPoint mp : getMountPoints(fqan)) {

					if ((mp.getRootUrl().indexOf(host) != -1)
							&& (mp.getRootUrl().indexOf(protocol) != -1)) {
						result.add(mp);
					}

				}
			} catch (final URISyntaxException e) {
				myLogger.error(e.getLocalizedMessage(), e);
				return null;
			}
		}

		return result;

	}

	public Set<MountPoint> getNonVolatileMountPoints() {

		final Set<MountPoint> result = new TreeSet<MountPoint>();

		for (final MountPoint m : getMountPoints()) {
			if (m.isVolatileFileSystem()) {
				continue;
			}
			result.add(m);
		}

		return result;
	}

	public synchronized String getProperty(String key) {

		if (StringUtils.isBlank(cachedUserProperties.get(key))) {
			final String temp = serviceInterface.getUserProperty(key);
			if (StringUtils.isBlank(temp)) {
				return null;
			} else {
				cachedUserProperties.put(key, temp);
			}
		}
		return cachedUserProperties.get(key);

	}

	public synchronized SortedSet<String> getReallyAllJobnames(boolean refresh) {

		if (allJobnames == null) {
			allJobnames = new TreeSet<String>(serviceInterface.getAllJobnames(
					Constants.ALLJOBS_INCL_BATCH_KEY).getStringList());
			allJobnames.addAll(serviceInterface.getAllBatchJobnames(null)
					.getStringList());
		} else if (refresh) {
			allJobnames.clear();
			allJobnames.addAll(serviceInterface.getAllJobnames(
					Constants.ALLJOBS_INCL_BATCH_KEY).getStringList());
			allJobnames.addAll(serviceInterface.getAllBatchJobnames(null)
					.getStringList());

		}
		return allJobnames;

	}

	public final MountPoint getRecommendedMountPoint(
			final String submissionLocation, final String fqan) {

		final Set<MountPoint> temp = getMountPointsForSubmissionLocationAndFqan(
				submissionLocation, fqan);

		return temp.iterator().next();

	}

	public synchronized List<FileSystemItem> getRemoteSites() {

		if (cachedRemoteFilesystemList == null) {
			cachedRemoteFilesystemList = new LinkedList<FileSystemItem>();
			for (final String site : getAllAvailableSites()) {
				cachedRemoteFilesystemList.add(new FileSystemItem(site,
						FileSystemItem.Type.REMOTE, new GridFile(site)));
			}
		}
		return cachedRemoteFilesystemList;
	}

	public String getUniqueGroupname(String fqan) {
		return FqanHelpers.getUniqueGroupname(getAllAvailableFqans(), fqan);
	}

	public boolean isMountPointAlias(String string) {

		for (final MountPoint mp : getMountPoints()) {
			if (mp.getAlias().equals(string)) {
				return true;
			}
		}

		return false;
	}

	public boolean isMountPointRoot(String rootUrl) {

		for (final MountPoint mp : getMountPoints()) {
			if (mp.getRootUrl().equals(rootUrl)) {
				return true;
			}
		}

		return false;

	}

	public void onEvent(FqanEvent arg0) {

		if (FqanEvent.DEFAULT_FQAN_CHANGED == arg0.getEvent_type()) {
			if (internalFqanChange) {
				return;
			} else {
				internalFqanEvent = false;
				setCurrentFqan(arg0.getFqan());
				internalFqanEvent = true;
			}
		}

		// if ( arg0.getEvent_type() == FqanEvent.DEFAULT_FQAN_CHANGED ) {
		// setCurrentFqan(arg0.getFqan());
		// }

	}

	public void removeFqanListener(final FqanListener listener) {

		throw new RuntimeException(
				"Removal of fqan listener not implemented yet.");
	}

	public synchronized FileSystemItem setBookmark(String alias, String url) {

		serviceInterface.addBookmark(alias, url);

		if (StringUtils.isBlank(url)) {
			final FileSystemItem temp = new FileSystemItem(alias,
					FileSystemItem.Type.BOOKMARK, null);
			getBookmarks().remove(alias);
			getBookmarksFilesystems().remove(temp);
			getFileSystems().remove(temp);
			return temp;
		} else {
			FileSystemItem temp;
			try {
				temp = new FileSystemItem(alias,
						FileSystemItem.Type.BOOKMARK, getFileManager()
						.createGridFile(url));
			} catch (RemoteFileSystemException e) {
				temp = new FileSystemItem(alias, FileSystemItem.Type.BOOKMARK,
						new GridFile(alias, true, e));
			}

			getBookmarks().put(alias, url);
			getBookmarksFilesystems().add(temp);
			getFileSystems().add(temp);
			return temp;
		}

	}

	public final void setCurrentFqan(final String currentFqan) {

		if (StringUtils.isNotBlank(currentFqan)) {
			this.currentFqan = currentFqan;
			ClientPropertiesManager.setLastUsedFqan(this.currentFqan);
			if (internalFqanEvent) {
				internalFqanChange = true;
				EventBus.publish(new FqanEvent(this,
						FqanEvent.DEFAULT_FQAN_CHANGED, currentFqan));
				internalFqanChange = false;
			}
		}
	}

	public synchronized void setProperty(String key, String value) {

		serviceInterface.setUserProperty(key, value);
		cachedUserProperties.put(key, value);

	}

	public StatusObject waitForActionToFinish(String handle)
			throws InterruptedException, StatusException {

		final StatusObject status = new StatusObject(serviceInterface, handle);

		status.waitForActionToFinish(
				ClientPropertiesManager.getDefaultActionStatusRecheckInterval(),
				false);

		return status;
	}

}
