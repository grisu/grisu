package grisu.backend.model;

import grisu.backend.hibernate.BatchJobDAO;
import grisu.backend.hibernate.JobDAO;
import grisu.backend.hibernate.UserDAO;
import grisu.backend.model.fs.UserFileManager;
import grisu.backend.model.job.Job;
import grisu.backend.model.job.JobSubmitter;
import grisu.backend.model.job.UserBatchJobManager;
import grisu.backend.model.job.UserJobManager;
import grisu.backend.model.job.gt4.GT4Submitter;
import grisu.backend.model.job.gt5.GT5Submitter;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoValidCredentialException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.control.serviceInterfaces.AbstractServiceInterface;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;
import grisu.model.MountPoint;
import grisu.model.dto.DtoActionStatus;
import grisu.model.dto.GridFile;
import grisu.model.info.dto.Directory;
import grisu.model.info.dto.VO;
import grisu.model.job.JobSubmissionObjectImpl;
import grisu.settings.ServerPropertiesManager;
import grisu.utils.MountPointHelpers;
import grith.jgrith.cred.AbstractCred;
import grith.jgrith.cred.Cred;
import grith.jgrith.utils.FqanHelpers;
import grith.jgrith.vomsProxy.VomsException;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.ehcache.util.NamedThreadFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
//import grith.jgrith.credential.Credential;

/**
 * The User class holds all the relevant data a job could want to know from the
 * user it is running under. This class belongs to the three central classes of
 * grisu (the other two are {@link ServiceInterface} and {@link Job}.
 * 
 * At the moment it holds filesystem information which can be used to stage
 * files from the desktop. Also it has got information about vo memberships of
 * the user.
 * 
 * @author Markus Binsteiner
 * 
 */
@Entity
@Table(name = "users")
public class User {

	private final static boolean ENABLE_FILESYSTEM_CACHE = ServerPropertiesManager
			.useFileSystemCache();

	public final boolean checkFileSystemsBeforeUse = false;
	public static final int DEFAULT_JOB_SUBMISSION_RETRIES = 5;

	public static UserDAO userdao = new UserDAO();
	protected static final JobDAO jobdao = new JobDAO();
	// this needs to be static because otherwise the session be lost and the
	// action status can't be found anymore by the client
	private static final Map<String, Map<String, DtoActionStatus>> actionStatuses = new HashMap<String, Map<String, DtoActionStatus>>();

	private static final String NOT_ACCESSIBLE = "Not accessible";

	private static final String ACCESSIBLE = "Accessible";

	public static User createUser(Cred cred,
			AbstractServiceInterface si) {

		// make sure there is a valid credential
		if ((cred == null) || !cred.isValid()) {
			throw new NoValidCredentialException(
					"No valid credential exists in this session");
		}

		// myLogger.debug("CREATING USER SESSION: " + cred.getDn());

		// if ( getCredential())

		Date time1 = new Date();

		User user;
		// try to look up user in the database
		user = userdao.findUserByDN(cred.getDN());
		Date time2 = new Date();

		myLogger.debug("Login benchmark - db lookup: "
				+ new Long((time2.getTime() - time1.getTime())).toString()
				+ " ms");

		if (user == null) {
			user = new User(cred);
			time1 = new Date();
			myLogger.debug("Login benchmark - constructor: "
					+ new Long((time1.getTime() - time2.getTime())).toString()
					+ " ms");

			userdao.saveOrUpdate(user);
		} else {
			user.setCredential(cred);
			time1 = new Date();
			myLogger.debug("Login benchmark - setting credential: "
					+ new Long((time1.getTime() - time2.getTime())).toString()
					+ " ms");

		}


		try {
			user.setAutoMountedMountPoints(user.df_auto_mds());
			time2 = new Date();
			myLogger.debug("Login benchmark - mountpoints: "
					+ new Long((time2.getTime() - time1.getTime())).toString()
					+ " ms");
		} catch (final Exception e) {
			throw new RuntimeException(
					"Can't aquire filesystems for user. Possibly because of misconfigured grisu backend",
					e);
		}

		// final User temp = user;

		// caching users archived jobs since those take a while to load...
		// new Thread() {
		// @Override
		// public void run() {
		// // temp.getDefaultArchiveLocation();
		// // temp.getArchivedJobs(null);
		// }
		// }.start();

		return user;

	}

	public static String get_vo_dn_path(final String dn) {
		return dn.replace("=", "_").replace(",", "_").replace(" ", "_");
	}


	protected final BatchJobDAO batchJobDao = new BatchJobDAO();

	private static Logger myLogger = LoggerFactory.getLogger(User.class
			.getName());

	private final UserJobManager jobSubmissionmanager = null;

	private Long id = null;

	// the (default) credential to contact gridftp file shares
	// private ProxyCredential cred = null;

	private Cred credential;

	private UserJobManager jobmanager;
	// the (default) credentials dn
	private String dn = null;

	// the mountpoints of a user
	private Set<MountPoint> mountPoints = new HashSet<MountPoint>();

	private Map<String, String> mountPointCache = Collections
			.synchronizedMap(new HashMap<String, String>());

	private final Map<String, ImmutableSet<MountPoint>> mountPointsPerFqanCache = new TreeMap<String, ImmutableSet<MountPoint>>();
	private Set<MountPoint> mountPointsAutoMounted = new HashSet<MountPoint>();

	private Set<MountPoint> allMountPoints = null;

	// credentials are chache so we don't have to contact myproxy/voms anytime
	// we want to make a transaction
	//	private Map<String, ProxyCredential> cachedCredentials = new HashMap<String, ProxyCredential>();

	// All fqans of the user
	private Map<String, VO> fqans = null;
	private Set<String> cachedUniqueGroupnames = null;
	private Map<String, String> userProperties = new HashMap<String, String>();

	private Map<String, String> bookmarks = new HashMap<String, String>();

	private Map<String, String> archiveLocations = null;
	private Map<String, JobSubmissionObjectImpl> jobTemplates = new HashMap<String, JobSubmissionObjectImpl>();
	private UserFileManager fsm;

	// private final InformationManager infoManager;

	// private final MatchMaker matchmaker;

	private UserBatchJobManager batchjobmanager = null;

	// for hibernate
	public User() {
		// this.infoManager = AbstractServiceInterface.informationManager;
		// this.matchmaker = AbstractServiceInterface.matchmaker;
	}

	/**
	 * Constructs a user using and associates a (default) credential with it.
	 * 
	 * @param cred
	 *            the credential
	 * @throws FileSystemException
	 *             if the users default filesystems can't be mounted
	 */
	private User(final Cred cred) {
		this.dn = cred.getDN();
		this.credential = cred;
		// this.infoManager = AbstractServiceInterface.informationManager;
		// this.matchmaker = AbstractServiceInterface.matchmaker;

	}


	/**
	 * Constructs a User object not using an associated credential.
	 * 
	 * @param dn
	 *            the dn of the user
	 */
	public User(final String dn) {
		this.dn = dn;
		// this.infoManager = AbstractServiceInterface.informationManager;
		// this.matchmaker = AbstractServiceInterface.matchmaker;
	}

	public void addArchiveLocation(String alias, String value) {

		getArchiveLocations().put(alias, value);
		userdao.saveOrUpdate(this);

	}

	public void addBookmark(String alias, String url) {
		this.bookmarks.put(alias, url);
		userdao.saveOrUpdate(this);
	}



	/**
	 * Not used yet.
	 * 
	 * @param vo
	 */
	public void addFqan(final String fqan, final VO vo) {
		fqans.put(fqan, vo);
	}

	public void addProperty(String key, String value) {

		getUserProperties().put(key, value);
		userdao.saveOrUpdate(this);

	}

	public void cleanCache() {
		// TODO disconnect filesystems somehow?
		// cachedFilesystemConnections = new HashMap<MountPoint, FileSystem>();
		// // does this affect existing filesystem connection
		// for ( ProxyCredential proxy : cachedCredentials.values() ) {
		// proxy.destroy();
		// }
		//		cachedCredentials = new HashMap<String, ProxyCredential>();
	}

	public void clearMountPointCache(String keypattern) {
		if (StringUtils.isBlank(keypattern)) {
			this.mountPointCache = Collections
					.synchronizedMap(new HashMap<String, String>());
		}
		userdao.saveOrUpdate(this);
	}

	private MountPoint createMountPoint(final Directory dir, final String fqan,
			Executor executor) throws Exception {

		final String server = dir.getFilesystem().getUrl();
		final String path = dir.getPath();

		String url = null;

		final int startProperties = path.indexOf("[");
		final int endProperties = path.indexOf("]");

		if ((startProperties >= 0) && (endProperties < 0)) {
			myLogger.error("Path: " + path + " for host " + server
					+ " has incorrect syntax. Ignoring...");
			return null;
		}

		String alias = null;

		String propString = null;
		try {
			propString = path.substring(startProperties + 1, endProperties);
		} catch (final Exception e) {
			// that's ok
			// myLogger.debug("No extra properties for path: " + path);
		}

		final Map<String, String> properties = new HashMap<String, String>();
		boolean userDnPath = dir.isShared();
		if (StringUtils.isNotBlank(propString)) {

			final String[] parts = propString.split(";");
			for (final String part : parts) {
				if (part.indexOf("=") <= 0) {
					// myLogger.error("Invalid path spec: " + path
					// + ".  No \"=\" found. Ignoring this mountpoint...");
					throw new Exception("Invalid path spec: " + path
							+ ".  No \"=\" found. Ignoring this mountpoint...");
				}
				final String key = part.substring(0, part.indexOf("="));
				if (StringUtils.isBlank(key)) {
					// myLogger.error("Invalid path spec: " + path
					// + ".  No key found. Ignoring this mountpoint...");
					throw new Exception("Invalid path spec: " + path
							+ ".  No key found. Ignoring this mountpoint...");
				}
				String value = null;
				try {
					value = part.substring(part.indexOf("=") + 1);
					if (StringUtils.isBlank(value)) {
						// myLogger.error("Invalid path spec: "
						// + path
						// + ".  No key found. Ignoring this mountpoint...");
						throw new Exception(
								"Invalid path spec: "
										+ path
										+ ".  No key found. Ignoring this mountpoint...");
					}
				} catch (final Exception e) {
					// myLogger.error("Invalid path spec: " + path
					// + ".  No key found. Ignoring this mountpoint...");
					throw new Exception("Invalid path spec: " + path
							+ ".  No key found. Ignoring this mountpoint...");
				}

				properties.put(key, value);

			}
			alias = properties.get(MountPoint.ALIAS_KEY);

			try {
				userDnPath = Boolean.parseBoolean(properties
						.get(MountPoint.USER_SUBDIR_KEY));
			} catch (final Exception e) {
				// that's ok
				myLogger.debug("Could not find or parse"
						+ MountPoint.USER_SUBDIR_KEY
						+ " key. Using user subdirs..");
				userDnPath = true;
			}

		}

		String tempPath = null;
		if (startProperties < 0) {
			tempPath = path.substring(0, path.length());
		} else {
			tempPath = path.substring(0, startProperties);
		}

		boolean isHomeDir = false;

		properties.put(MountPoint.PATH_KEY, tempPath);

		if (tempPath.startsWith("/~/")) {
			try {
				url = getFileSystemHomeDirectory(server,
						fqan);

				String additionalUrl = null;
				try {
					additionalUrl = tempPath
							.substring(1, tempPath.length() - 3);
				} catch (final Exception e) {
					additionalUrl = "";
				}

				url = url + additionalUrl;

				isHomeDir = true;

			} catch (final Exception e) {
				// myLogger.error(e.getLocalizedMessage(), e);
				throw e;
			}

		} else if (path.contains("${GLOBUS_SCRATCH_DIR")) {
			try {
				url = getFileSystemHomeDirectory(server,
						fqan) + "/.globus/scratch";
				userDnPath = false;
			} catch (final Exception e) {
				// myLogger.error(e.getLocalizedMessage(), e);
				throw e;
			}
		} else {

			// url = server.replace(":2811", "") + path + "/"
			// + User.get_vo_dn_path(getCred().getDn());
			url = server + tempPath;

		}

		if (StringUtils.isBlank(url)) {
			// myLogger.error("Url is blank for " + server + " and " + path);
			throw new Exception("Url is blank for " + server + " and " + path);
		}

		// add dn dir if necessary

		if (userDnPath) {
			url = url + "/" + User.get_vo_dn_path(getCredential().getDN());

			// try to connect to filesystem in background and store in database
			// if not successful, so next time won't be tried again...
			if (executor != null) {

				final String urlTemp = url;
				final Thread t = new Thread() {
					@Override
					public void run() {
						final String key = urlTemp + fqan;
						try {
							// try to create the dir if it doesn't exist

							// checking whether subfolder exists
							if (StringUtils.isNotBlank(getMountPointCache()
									.get(key)) && !NOT_ACCESSIBLE.equals(key)) {
								// exists apparently, don't need to create
								// folder...
								return;
							}

							if (NOT_ACCESSIBLE.equals(key)) {
								myLogger.debug(getDn()
										+ ": FS cache indicates that url "
										+ urlTemp
										+ " / "
										+ fqan
										+ "is not accessible. Clear FS cache if you think that has changed.");
								return;
							}
							// myLogger.debug("Did not find "
							// + urlTemp
							// + "in cache, trying to access/create folder...");
							final boolean exists = getFileManager()
									.fileExists(urlTemp);
							if (!exists) {
								myLogger.debug("Mountpoint does not exist. Trying to create non-exitent folder: "
										+ urlTemp);
								getFileManager().createFolder(urlTemp);
								// } else {
								// myLogger.debug("MountPoint " + urlTemp
								// + " exists.");
							}

							getMountPointCache().put(key, ACCESSIBLE);

						} catch (final Exception e) {
							myLogger.error("Could not create folder: "
									+ urlTemp, e);

							if (ENABLE_FILESYSTEM_CACHE) {
								getMountPointCache().put(key, NOT_ACCESSIBLE);
							}

						} finally {
							if (ENABLE_FILESYSTEM_CACHE) {
								try {
									userdao.saveOrUpdate(User.this);
								} catch (final Exception e) {
									myLogger.debug("Could not save filesystem state for fs "
											+ urlTemp
											+ ": "
											+ e.getLocalizedMessage());
								}
							}
						}
					}
				};

				executor.execute(t);
			}

		}

		MountPoint mp = null;

		if (StringUtils.isBlank(alias)) {
			alias = MountPointHelpers.calculateMountPointName(server, fqan);
		}
		mp = new MountPoint(getDn(), fqan, url, alias,
				dir.getSite().toString(), true, isHomeDir);

		for (final String key : properties.keySet()) {
			mp.addProperty(key, properties.get(key));
		}

		final boolean isVolatile = dir.isVolatileDirectory();
		mp.setVolatileFileSystem(isVolatile);

		return mp;

		// + "." + fqan + "." + path);
		// + "." + fqan);
		// cachedGridFtpHomeDirs.put(keyMP, mp);
		//
		// return cachedGridFtpHomeDirs.get(keyMP);
	}

	/**
	 * Gets all mountpoints for this fqan.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the mountpoints
	 */
	public Set<MountPoint> df(String fqan) {

		final Set<MountPoint> result = new HashSet<MountPoint>();
		for (final MountPoint mp : getAllMountPoints()) {
			if (StringUtils.isNotBlank(mp.getFqan())
					&& mp.getFqan().equals(fqan)) {
				result.add(mp);
			}
		}
		return result;
	}

	/**
	 * Calculates all mountpoints that are automatically mounted using mds. At
	 * the moment, the port part of the gridftp url share is ignored. Maybe I'll
	 * change that later.
	 * 
	 * @param sites
	 *            the sites that should be used
	 * @return all MountPoints
	 */
	protected Set<MountPoint> df_auto_mds() {

		final Set<MountPoint> mps = Collections
				.synchronizedSet(new TreeSet<MountPoint>());

		final Map<String, MountPoint> successfullMountPoints = Collections
				.synchronizedMap(new HashMap<String, MountPoint>());
		final Map<String, Exception> unsuccessfullMountPoints = Collections
				.synchronizedMap(new HashMap<String, Exception>());
		final Date start = new Date();

		myLogger.debug("Getting mds mountpoints for user: " + getDn());

		Date end;

		final boolean lookupMountPointsConcurrently = true;
		if (lookupMountPointsConcurrently) {

			// to check whether dn_subdirs are created already and create them
			// if
			// not (in background)
			final int df_p = ServerPropertiesManager
					.getConcurrentMountPointLookups();
			final NamedThreadFactory fscacheTF = new NamedThreadFactory(
					"backgroundFScache");

			final ExecutorService backgroundExecutorForFilesystemCache = Executors
					.newFixedThreadPool(2, fscacheTF);
			// final ExecutorService backgroundExecutorForFilesystemCache =
			// null;

			final NamedThreadFactory homedirlookup = new NamedThreadFactory(
					"homeDirLookup");
			final ExecutorService executor = Executors.newFixedThreadPool(df_p,
					homedirlookup);

			final Date intermediate = new Date();

			myLogger.debug("Login benchmark intermediate: All executors created: "
					+ (intermediate.getTime() - start.getTime()) + " ms");

			final Map<String, VO> vos = getFqans();

			myLogger.debug("Login benchmark intermediate : all Fqans retrieved: "
					+ (new Date().getTime() - start.getTime()) + " ms");

			for (final String fqan : vos.keySet()) {

				final Thread t = new Thread() {
					@Override
					public void run() {
						myLogger.debug("Getting datalocations for vo " + fqan
								+ "....");
						// final Date start = new Date();

						Collection<Directory> dirs = AbstractServiceInterface.informationManager
								.getDirectoriesForVO(fqan);

						// final Map<String, String[]> mpUrl =
						// AbstractServiceInterface.informationManager
						// .getDataLocationsForVO(fqan);
						myLogger.debug("Getting datalocations for vo " + fqan
								+ " finished.");

						for (final Directory dir : dirs) {

							String uniqueString = null;
							try {

								String server = dir.getHost();
								String path = dir.getPath();

								uniqueString = server + " - " + path
										+ " - " + fqan;

								// X.p("\t" + uniqueString
								// + ": creating....");

								successfullMountPoints.put(uniqueString,
										null);

								myLogger.debug("Creating mountpoint for: "
										+ server + " / " + path + " / "
										+ fqan + "....");

								final MountPoint mp = createMountPoint(
										dir,
										fqan,
										(ENABLE_FILESYSTEM_CACHE) ? backgroundExecutorForFilesystemCache
												: null);

								myLogger.debug("Creating mountpoint for: "
										+ server + " / " + path + " / "
										+ fqan + " finished.");

								successfullMountPoints.put(server + "_"
										+ path + "_" + fqan, mp);

								// X.p("\t" + server + "/" + path + "/" +
								// fqan
								// + ": created");

								if (mp != null) {
									mps.add(mp);
									successfullMountPoints.put(
											uniqueString, mp);
								} else {
									successfullMountPoints
									.remove(uniqueString);
									unsuccessfullMountPoints
									.put(uniqueString,
											new Exception(
													"MountPoint not created, unknown reason."));
								}
							} catch (final Exception e) {
								// X.p(server + "/" + "/" + fqan +
								// ": failed : "
								// + e.getLocalizedMessage());
								// e.printStackTrace();
								successfullMountPoints.remove(uniqueString);
								unsuccessfullMountPoints.put(uniqueString,
										e);
								myLogger.error("Can't use mountpoint "
										+ dir.getHost() + " / " + fqan + ": "
										+ e.getLocalizedMessage());
							}
						}
					}

				};
				executor.execute(t);
			}

			end = new Date();
			myLogger.debug("Login benchmark: All mountpoint lookup threads started: "
					+ (end.getTime() - start.getTime()) + " ms");

			executor.shutdown();

			// X.p("Waiting...");

			try {
				executor.awaitTermination(2, TimeUnit.MINUTES);
			} catch (final InterruptedException e) {
				myLogger.error(e.getLocalizedMessage(), e);
			}

			// X.p("Finished creating mountpoints...");
			if (backgroundExecutorForFilesystemCache != null) {
				backgroundExecutorForFilesystemCache.shutdown();
			}

			return mps;

		} else {
			for (final String fqan : getFqans().keySet()) {

				myLogger.debug("Time for fqan " + fqan + ": "
						+ (new Date().getTime() - start.getTime()) + " ms");

				myLogger.debug("Getting datalocations for vo " + fqan + "....");
				// final Date start = new Date();
				final Collection<Directory> dirs = AbstractServiceInterface.informationManager
						.getDirectoriesForVO(fqan);
				myLogger.debug("Getting datalocations for vo " + fqan
						+ " finished.");
				// final Date end = new Date();
				// myLogger.debug("Querying for data locations for all sites and+ "
				// + fqan + " took: " + (end.getTime() - start.getTime())
				// + " ms.");
				for (final Directory dir : dirs) {

					String uniqueString = null;
					try {

						String server = dir.getHost();
						String path = dir.getPath();
						uniqueString = server + " - " + path + " - " + fqan;

						// X.p("\t" + uniqueString
						// + ": creating....");

						successfullMountPoints.put(uniqueString, null);

						myLogger.debug("Creating mountpoint for: " + server
								+ " / " + path + " / " + fqan + "....");

						final MountPoint mp = createMountPoint(dir, fqan, null);

						myLogger.debug("Creating mountpoint for: " + server
								+ " / " + path + " / " + fqan
								+ " finished.");

						successfullMountPoints.put(server + "_" + path
								+ "_" + fqan, mp);

						// X.p("\t" + server + "/" + path + "/" + fqan
						// + ": created");

						if (mp != null) {
							mps.add(mp);
							successfullMountPoints.put(uniqueString, mp);
						} else {
							successfullMountPoints.remove(uniqueString);
							unsuccessfullMountPoints
							.put(uniqueString,
									new Exception(
											"MountPoint not created, unknown reason."));
						}
					} catch (final Exception e) {
						// X.p(server + "/" + "/" + fqan + ": failed : "
						// + e.getLocalizedMessage());
						// e.printStackTrace();
						successfullMountPoints.remove(uniqueString);
						unsuccessfullMountPoints.put(uniqueString, e);
						myLogger.error("Can't use mountpoint " + dir.getHost()
								+ " / " + fqan + ": "
								+ e.getLocalizedMessage());
					}
				}
			}
			end = new Date();
		}
		myLogger.debug("Login benchmark: All mountpoint lookup threads finished: "
				+ (new Date().getTime() - end.getTime()) + " ms");

		for (final String us : successfullMountPoints.keySet()) {
			if (successfullMountPoints.get(us) == null) {
				unsuccessfullMountPoints.put(us, new Exception(
						"MountPoint not created. Probably timed out."));
			}
		}
		for (final String us : unsuccessfullMountPoints.keySet()) {
			successfullMountPoints.remove(us);

			myLogger.error(getDn() + ": Can't connect to mountpoint " + us
					+ ":\n\t"
					+ unsuccessfullMountPoints.get(us).getLocalizedMessage());
		}

		return mps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof User)) {
			return false;
		}

		final User user = (User) other;

		if (!dn.equals(user.dn)) {
			return false;
		} else {
			return true;
		}
	}

	public GridFile fillFolder(GridFile folder, int recursionLevel)
			throws RemoteFileSystemException {

		GridFile tempFolder = null;

		try {
			tempFolder = getFileManager().getFolderListing(
					folder.getUrl(), 1);
		} catch (final Exception e) {
			// myLogger.error(e.getLocalizedMessage(), e);
			myLogger.error(
					"Error getting folder listing. I suspect this to be a bug in the commons-vfs-grid library. Sleeping for 1 seconds and then trying again...",
					e);
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e1) {
				myLogger.error(e.getLocalizedMessage(), e);
			}
			tempFolder = getFileManager().getFolderListing(
					folder.getUrl(), 1);

		}
		folder.setChildren(tempFolder.getChildren());

		if (recursionLevel > 0) {
			for (final GridFile childFolder : tempFolder.getChildren()) {
				if (childFolder.isFolder()) {
					folder.addChild(fillFolder(childFolder, recursionLevel - 1));
				}
			}

		}
		return folder;
	}



	@Transient
	public Map<String, DtoActionStatus> getActionStatuses() {

		synchronized (dn) {

			Map<String, DtoActionStatus> actionStatusesForUser = actionStatuses
					.get(dn);
			if (actionStatusesForUser == null) {
				actionStatusesForUser = new HashMap<String, DtoActionStatus>();
				actionStatuses.put(dn, actionStatusesForUser);
			}

			return actionStatusesForUser;

		}
	}

	@Transient
	public Set<String> getAllAvailableUniqueGroupnames() {

		if (cachedUniqueGroupnames == null) {

			cachedUniqueGroupnames = new TreeSet<String>();
			final Iterator<String> it = getFqans().keySet().iterator();

			while (it.hasNext()) {
				final String fqan = it.next();
				cachedUniqueGroupnames.add(getUniqueGroupname(fqan));
			}
		}
		return cachedUniqueGroupnames;
	}



	/**
	 * Returns all mountpoints (including automounted ones for this session.
	 * 
	 * @return all mountpoints for this session
	 */
	@Transient
	public Set<MountPoint> getAllMountPoints() {
		if (allMountPoints == null) {

			throw new IllegalStateException("Mountpoints not set yet.");
			// allMountPoints = new TreeSet<MountPoint>();
			// // first the automounted ones because the manually ones are more
			// // important
			// allMountPoints.addAll(mountPointsAutoMounted);
			// allMountPoints.addAll(getMountPoints());
		}
		return allMountPoints;
	}



	/**
	 * Gets a map of this users bookmarks.
	 * 
	 * @return the users' properties
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	public Map<String, String> getArchiveLocations() {
		if (archiveLocations == null) {
			archiveLocations = new TreeMap<String, String>();
		}

		return archiveLocations;
	}

	@Transient
	public UserBatchJobManager getBatchJobManager() {
		if ( batchjobmanager == null ) {
			batchjobmanager = new UserBatchJobManager(this);
		}
		return batchjobmanager;
	}

	/**
	 * Gets a map of this users bookmarks.
	 * 
	 * @return the users' properties
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	public Map<String, String> getBookmarks() {
		return bookmarks;
	}

	/**
	 * Returns the default credential of the user (if any).
	 * 
	 * @return the default credential or null if there is none
	 */
	@Transient
	public Cred getCredential() {
		return credential;
	}

	@Transient
	public Cred getCredential(String fqan) {

		Cred cred = getCredential().getGroupCredential(fqan);

		return cred;
	}

	@Transient
	public synchronized String getDefaultArchiveLocation() {

		String defArcLoc = null;

		// if user configured default, use that.
		defArcLoc = getUserProperties().get(
				Constants.DEFAULT_JOB_ARCHIVE_LOCATION);

		if (!StringUtils.isBlank(defArcLoc)) {
			myLogger.info("Using default archive location for user " + getDn()
					+ ": " + defArcLoc);

			return defArcLoc;
		}

		final String defFqan = ServerPropertiesManager
				.getDefaultFqanForArchivedJobDirectory();

		// using backend default fqan if configured
		if (StringUtils.isNotBlank(defFqan)) {
			final Set<MountPoint> mps = df(defFqan);
			for (final MountPoint mp : mps) {
				if (!mp.isVolatileFileSystem()) {
					defArcLoc = mp.getRootUrl()
							+ "/"
							+ ServerPropertiesManager
							.getArchivedJobsDirectoryName();
					addArchiveLocation(
							Constants.JOB_ARCHIVE_LOCATION_AUTO + mp.getAlias(),
							defArcLoc);
					// setUserProperty(Constants.DEFAULT_JOB_ARCHIVE_LOCATION,
					// defArcLoc);
					myLogger.debug("Using backend default archive location: "
							+ defArcLoc);
					return defArcLoc;
				}
			}
		}

		// to be removed once we switch to new backend
		Set<MountPoint> mps = df("/nz/nesi");
		for (final MountPoint mp : mps) {
			if (!mp.isVolatileFileSystem()) {

				defArcLoc = mp.getRootUrl()
						+ "/"
						+ ServerPropertiesManager
						.getArchivedJobsDirectoryName();
				addArchiveLocation(
						Constants.JOB_ARCHIVE_LOCATION_AUTO + mp.getAlias(),
						defArcLoc);
				return defArcLoc;

			}
		}

		if (mps.size() > 0) {
			final MountPoint mp = mps.iterator().next();
			defArcLoc = mp.getRootUrl() + "/"
					+ ServerPropertiesManager.getArchivedJobsDirectoryName();

			addArchiveLocation(
					Constants.JOB_ARCHIVE_LOCATION_AUTO + mp.getAlias(),
					defArcLoc);

		} else {
			mps = df("/ARCS/BeSTGRID/Drug_discovery/Local");
			if (mps.size() > 0) {
				final MountPoint mp = mps.iterator().next();
				defArcLoc = mp.getRootUrl()
						+ "/"
						+ ServerPropertiesManager
						.getArchivedJobsDirectoryName();

				addArchiveLocation(
						Constants.JOB_ARCHIVE_LOCATION_AUTO + mp.getAlias(),
						defArcLoc);
			} else {
				mps = df("/ARCS/BeSTGRID");
				if (mps.size() > 0) {
					final MountPoint mp = mps.iterator().next();
					defArcLoc = mp.getRootUrl()
							+ "/"
							+ ServerPropertiesManager
							.getArchivedJobsDirectoryName();

					addArchiveLocation(
							Constants.JOB_ARCHIVE_LOCATION_AUTO + mp.getAlias(),
							defArcLoc);

				} else {
					mps = getAllMountPoints();
					if (mps.size() == 0) {
						return null;
					}
					final MountPoint mp = mps.iterator().next();
					defArcLoc = mp.getRootUrl()
							+ "/"
							+ ServerPropertiesManager
							.getArchivedJobsDirectoryName();

					addArchiveLocation(
							Constants.JOB_ARCHIVE_LOCATION_AUTO + mp.getAlias(),
							defArcLoc);

				}
			}

		}

		userdao.saveOrUpdate(this);

		myLogger.debug("Using temporary default archive location for user "
				+ getDn() + ": " + defArcLoc);

		return defArcLoc;
	}

	/**
	 * Returns the users dn.
	 * 
	 * @return the dn
	 */
	@Column(nullable = false)
	public String getDn() {
		return dn;
	}

	@Transient
	public UserFileManager getFileManager() {
		if (fsm == null) {
			this.fsm = new UserFileManager(this);
		}
		return fsm;
	}

	// private List<FileReservation> getFileReservations() {
	// return fileReservations;
	// }
	//
	// private void setFileReservations(List<FileReservation> fileReservations)
	// {
	// this.fileReservations = fileReservations;
	// }
	//
	// private List<FileTransfer> getFileTransfers() {
	// return fileTransfers;
	// }
	//
	// private void setFileTransfers(List<FileTransfer> fileTransfers) {
	// this.fileTransfers = fileTransfers;
	// }
	@Transient
	public String getFileSystemHomeDirectory(String filesystemRoot, String fqan)
			throws FileSystemException {

		final String key = filesystemRoot + fqan;
		if (ENABLE_FILESYSTEM_CACHE
				&& StringUtils.isNotBlank(getMountPointCache().get(key))) {

			if (NOT_ACCESSIBLE.equals(getMountPointCache().get(key))) {

				throw new FileSystemException(
						"Cached entry indicates filesystem "
								+ filesystemRoot
								+ " is not accessible. Clear cache if you think that has changed.");
			} else {
				myLogger.debug(getDn() + ": found cached filesystem for "
						+ filesystemRoot + " / " + fqan);
				return getMountPointCache().get(key);
			}
		} else {
			try {

				String uri = null;

				uri = getFileManager().resolveFileSystemHomeDirectory(
						filesystemRoot, fqan);
				myLogger.debug("Found filesystem home dir for: "
						+ filesystemRoot + " / " + fqan + ": " + uri);

				if (ENABLE_FILESYSTEM_CACHE && StringUtils.isNotBlank(uri)) {
					myLogger.debug("Saving in fs cache...");
					getMountPointCache().put(key, uri);
					userdao.saveOrUpdate(this);
				}

				return uri;
			} catch (final Exception e) {

				if (ENABLE_FILESYSTEM_CACHE) {
					myLogger.error(getDn() + ": Can't access filesystem "
							+ filesystemRoot + " / " + fqan
							+ ", saving in fs cache...");
					getMountPointCache().put(key, NOT_ACCESSIBLE);
					userdao.saveOrUpdate(this);
				}

				throw new FileSystemException(e);
			}
		}
	}

	/**
	 * Getter for the users' fqans.
	 * 
	 * @return all fqans as map with the fqan as key and the vo as value
	 */
	@Transient
	public Map<String, VO> getFqans() {

		Map<String, VO> fqans =  Maps.newTreeMap();
		fqans.putAll(credential.getAvailableFqans());
		fqans.put(Constants.NON_VO_FQAN, VO.NON_VO);
		return fqans;
	}

	@Transient
	public String getFullFqan(String uniqueGroupname) {
		return FqanHelpers.getFullFqan(getFqans().keySet(), uniqueGroupname);
	}



	// public GridFile getFolderListing(final String url, int recursionLevel)
	// throws RemoteFileSystemException, FileSystemException {
	//
	// try {
	// return getFileSystemManager().getFolderListing(url, recursionLevel);
	// } catch (InvalidPathException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// }
	// }


	@Id
	@GeneratedValue
	private Long getId() {
		return id;
	}

	@Transient
	public UserJobManager getJobManager() {
		if (jobmanager == null) {
			final Map<String, JobSubmitter> submitters = new HashMap<String, JobSubmitter>();
			submitters.put("GT4", new GT4Submitter());
			submitters.put("GT5", new GT5Submitter());
			jobmanager = new UserJobManager(this, submitters);
		}
		return jobmanager;
	}

	@ElementCollection
	public Map<String, JobSubmissionObjectImpl> getJobTemplates() {
		return jobTemplates;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(length = 400)
	private Map<String, String> getMountPointCache() {
		return mountPointCache;
	}

	// for hibernate
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable
	private Set<MountPoint> getMountPoints() {
		return mountPoints;
	}

	@Transient
	public ImmutableSet<MountPoint> getMountPoints(String fqan) {
		if (fqan == null) {
			fqan = Constants.NON_VO_FQAN;
		}

		synchronized (fqan) {

			if (mountPointsPerFqanCache.get(fqan) == null) {

				final Set<MountPoint> mps = new HashSet<MountPoint>();
				for (final MountPoint mp : getAllMountPoints()) {
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
				mountPointsPerFqanCache.put(fqan, ImmutableSet.copyOf(mps));
			}
			return mountPointsPerFqanCache.get(fqan);
		}
	}

	/**
	 * Checks whether the filesystem of any of the users' mountpoints contains
	 * the specified file.
	 * 
	 * @param file
	 *            the file
	 * @return the mountpoint of null if no filesystem contains this file
	 */
	@Transient
	public MountPoint getResponsibleMountpointForAbsoluteFile(final String file) {

		final String new_file = null;
		// myLogger.debug("Finding mountpoint for file: " + file);

		for (final MountPoint mountpoint : getAllMountPoints()) {
			if (mountpoint.isResponsibleForAbsoluteFile(file)) {
				return mountpoint;
			}
		}

		if (file.contains("/~/")) {
			String host = FileManager.getHost(file);
			Set<MountPoint> mps = Sets.newTreeSet();
			for (final MountPoint mp : getAllMountPoints()) {
				if (mp.isHomeDir()) {
					String mpHost = FileManager.getHost(mp.getRootUrl());
					if (mpHost.equals(host)) {
						mps.add(mp);
					}
				}
			}

			// if unique result, we can return it...
			if (mps.size() == 1) {
				return mps.iterator().next();
			} else if (mps.size() > 1) {
				// check whether all of those have the same absolute url, if
				// that is the case, we can return either of those
				// mountpoints...
				Set<String> urls = Sets.newTreeSet();
				for (MountPoint mp : mps) {
					urls.add(mp.getRootUrl());
				}
				if (urls.size() == 1) {
					return mps.iterator().next();
				}
			}
		}

		return null;
	}

	/**
	 * Checks whether any of the users' mountpoints contain the specified file.
	 * 
	 * @param file
	 *            the file
	 * @return the mountpoint or null if the file is not on any of the
	 *         mountpoints
	 */
	@Transient
	public MountPoint getResponsibleMountpointForUserSpaceFile(final String file) {

		for (final MountPoint mountpoint : getAllMountPoints()) {
			if (mountpoint.isResponsibleForUserSpaceFile(file)) {
				return mountpoint;
			}
		}

		return null;
	}

	@Transient
	public String getUniqueGroupname(String fqan) {
		return FqanHelpers.getUniqueGroupname(getFqans().keySet(), fqan);
	}

	// /**
	// * This is needed because of the url home directory/absolute path info
	// that
	// * is contained in the user object.
	// *
	// * We can't use /~/ dirs directly, since they are not unique. E.g.
	// * gsiftp://ng2.auckland.ac.nz/~/ could point to 2 different directories
	// for
	// * two different vos.
	// *
	// * @return the info manager
	// */
	// @Transient
	// public InfoManager getUserInfoManager() {
	// if ( this.im == null ) {
	// im = new UserInfoManager(this);
	// }
	// return this.im;
	// }

	/**
	 * Gets a map of this users properties. These properties can be used to
	 * store anything you can think of. Usful for history and such.
	 * 
	 * @return the users' properties
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	public Map<String, String> getUserProperties() {
		return userProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 29 * dn.hashCode();
	}


	public void logout() {
		actionStatuses.remove(dn);
	}

	public GridFile ls(final String directory, int recursion_level)
			throws RemoteFileSystemException {

		try {

			if (recursion_level == 0) {
				final GridFile file = getFileManager().getFolderListing(
						directory, 0);
				return file;
			}

			final GridFile rootfolder = getFileManager()
					.getFolderListing(directory, 1);
			if (recursion_level == 1) {

				return rootfolder;
			} else if (recursion_level <= 0) {
				recursion_level = Integer.MAX_VALUE;
			}

			recursion_level = recursion_level - 1;

			fillFolder(rootfolder, recursion_level);
			return rootfolder;

		} catch (final Exception e) {
			e.printStackTrace();
			throw new RemoteFileSystemException("Could not list directory "
					+ directory + ": " + e.getLocalizedMessage());
		}
	}

	/**
	 * Mounts a filesystem using the default credential of a user.
	 * 
	 * @param root
	 *            the filesystem to mount (something like:
	 *            gsiftp://ngdata.vapc.org/home/san04/markus)
	 * @param name
	 *            the name of the mountpoint (something like: /remote or
	 *            /remote.vpac)
	 * @throws FileSystemException
	 *             if the filesystem could not be mounted
	 * @throws RemoteFileSystemException
	 * @throws VomsException
	 */
	public MountPoint mountFileSystem(final String root, final String name,
			final boolean useHomeDirectory, String site)
					throws RemoteFileSystemException {

		return mountFileSystem(root, name, getCredential(), useHomeDirectory,
				site);
	}

	/**
	 * Adds a filesystem to the mountpoints of this user. The mountpoint is just
	 * an alias to make the urls shorter and easier to read/remember. You only
	 * need to mount a filesystem once. After you persisted the user (with
	 * hibernate) the alias and rootUrl of the filesystem are persisted as well.
	 * 
	 * A mountpoint always has to be in the root directory (for example: /local
	 * or /remote -- never /remote/ng2.vpac.org )
	 * 
	 * @param uri
	 *            the filesystem to mount (something like:
	 *            gsiftp://ngdata.vapc.org/home/san04/markus)
	 * @param mountPointName
	 *            the name of the mountpoint (something like: /remote or
	 *            /remote.vpac)
	 * @param cred
	 *            the credential that is used to contact the filesystem (can be
	 *            null for local filesystems)
	 * @return the root FileObject of the newly mounted FileSystem
	 * @throws VomsException
	 * @throws FileSystemException
	 *             if the filesystem could not be mounted
	 */
	public MountPoint mountFileSystem(String uri, final String mountPointName,
			final Cred cred, final boolean useHomeDirectory,
			final String site) throws RemoteFileSystemException {

		final MountPoint new_mp = getFileManager().mountFileSystem(uri,
				mountPointName, cred, useHomeDirectory, site);

		if (!mountPoints.contains(new_mp)) {
			mountPoints.add(new_mp);
			getAllMountPoints().add(new_mp);
		}

		userdao.saveOrUpdate(this);

		return new_mp;

	}

	public MountPoint mountFileSystem(final String root, final String name,
			final String fqan, final boolean useHomeDirectory, final String site)
					throws RemoteFileSystemException {

		if ((fqan == null) || Constants.NON_VO_FQAN.equals(fqan)) {
			return mountFileSystem(root, name, useHomeDirectory, site);
		} else {

			final Cred vomsProxyCred = getCredential(fqan);

			return mountFileSystem(root, name, vomsProxyCred, useHomeDirectory,
					site);
		}
	}



	public void removeArchiveLocation(String alias) {

		getArchiveLocations().remove(alias);
		userdao.saveOrUpdate(this);

	}

	public void removeBookmark(String alias) {
		this.bookmarks.remove(alias);
		userdao.saveOrUpdate(this);
	}

	/**
	 * Not used yet.
	 * 
	 * @param vo
	 */
	public void removeFqan(final String fqan) {
		fqans.remove(fqan);
	}

	public void removeProperty(final String key) {
		userProperties.remove(key);
		userdao.saveOrUpdate(this);
	}

	public void resetMountPoints() {
		// allMountPoints = null;
	}

	// public void addProperty(String key, String value) {
	// List<String> list = userProperties.get(key);
	// if ( list == null ) {
	// list = new LinkedList<String>();
	// }
	// list.add(value);
	// }

	/**
	 * Translates an absolute file url into an "user-space" one.
	 * 
	 * @param file
	 *            an absolute file url
	 *            (gsiftp://ngdata.vpac.org/home/san04/markus/test.txt)
	 * @return the "user-space" file url (/ngdata.vpac.org/test.txt)
	 */
	public String returnUserSpaceUrl(final String file) {
		final MountPoint mp = getResponsibleMountpointForAbsoluteFile(file);
		return mp.replaceAbsoluteRootUrlWithMountPoint(file);
	}

	private void setArchiveLocations(final Map<String, String> al) {
		this.archiveLocations = al;
	}

	/**
	 * Set's additional mountpoints that the user did not explicitly mount
	 * manually.
	 * 
	 * @param amps
	 *            the mountpoints to add (for this session)
	 */
	public void setAutoMountedMountPoints(final Set<MountPoint> amps) {

		// allMountPoints = null;
		this.mountPointsAutoMounted = amps;

		allMountPoints = new TreeSet<MountPoint>();
		// first the automounted ones because the manually ones are more
		// important
		allMountPoints.addAll(mountPointsAutoMounted);

		allMountPoints.addAll(getMountPoints());

	}

	private void setBookmarks(final Map<String, String> bm) {
		this.bookmarks = bm;
	}

	/**
	 * Sets the default credential for the user. The default credential is used
	 * mostly as convenience.
	 * 
	 * @param cred
	 *            the credential to use as default
	 */
	public synchronized void setCredential(final Cred cred) {

		if (cred.equals(this.credential)) {
			myLogger.debug("Not setting new credential since it's the same...");
			return;
		}

		myLogger.debug(cred.getDN() + ": Setting new credential.");

		this.credential = cred;
	}

	/**
	 * For hibernate.
	 * 
	 * @param dn
	 *            the dn of the user
	 */
	private void setDn(final String dn) {
		this.dn = dn;
	}

	/**
	 * Setter for the users' fqans.
	 * 
	 * @param fqans
	 *            all fqans as map with the fqan as key and the vo as value
	 */
	private void setFqans(final Map<String, VO> fqans) {
		this.fqans = fqans;
	}

	private void setId(final Long id) {
		this.id = id;
	}

	public void setJobTemplates(
			final Map<String, JobSubmissionObjectImpl> jobTemplates) {
		this.jobTemplates = jobTemplates;
	}

	private void setMountPointCache(final Map<String, String> mountPoints) {
		this.mountPointCache = mountPoints;
	}

	// for hibernate
	private void setMountPoints(final Set<MountPoint> mountPoints) {
		this.mountPoints = mountPoints;
	}

	private void setUserProperties(final Map<String, String> userProperties) {
		this.userProperties = userProperties;
	}

	public void setUserProperty(String key, String value) {

		if (StringUtils.isBlank(key)) {
			return;
		}

		if (Constants.CLEAR_MOUNTPOINT_CACHE.equals(key)) {
			clearMountPointCache(value);
			return;
		} else if (Constants.JOB_ARCHIVE_LOCATION.equals(key)) {
			final String[] temp = value.split(";");
			final String alias = temp[0];
			String url = temp[0];
			if (temp.length == 2) {
				url = temp[1];
			}
			addArchiveLocation(alias, url);
			return;
		}

		addProperty(key, value);

	}

	/**
	 * Unmounts a filesystem.
	 * 
	 * @param mountPointName
	 *            the name of the mountpoint (/local or /remote or something)
	 * @throws FileSystemException
	 *             if the filesystem could not be unmounted or something else
	 *             went wrong
	 */
	public void unmountFileSystem(final String mountPointName) {

		for (final MountPoint mp : mountPoints) {
			if (mp.getAlias().equals(mountPointName)) {
				mountPoints.remove(mp);
				getAllMountPoints().remove(mp);
				return;
			}
		}
		userdao.saveOrUpdate(this);
	}


}
