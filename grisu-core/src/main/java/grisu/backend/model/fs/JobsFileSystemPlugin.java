package grisu.backend.model.fs;

import grisu.backend.model.User;
import grisu.control.ServiceInterface;
import grisu.model.MountPoint;
import grisu.model.dto.GridFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JobsFileSystemPlugin implements VirtualFileSystemPlugin {

	static final Logger myLogger = Logger.getLogger(JobsFileSystemPlugin.class
			.getName());

	public static final String IDENTIFIER = "jobs";

	private final User user;

	public JobsFileSystemPlugin(User user) {
		this.user = user;
	}

	public GridFile createGridFile(final String path, int recursiveLevels)
			throws InvalidPathException {

		if (recursiveLevels != 1) {
			throw new RuntimeException(
					"Recursion levels other than 1 not supported yet");
		}

		String[] tokens = StringUtils.split(path, '/');

		if (tokens.length == 2) {

			GridFile result = new GridFile(
					ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://jobs",
					-1L);
			result.setIsVirtual(true);

			for (String group : user.getAllAvailableUniqueGroupnames()) {

				String fqan = user.getFullFqan(group);
				result.addFqan(fqan);
				if (user.getMountPoints(fqan).size() == 0) {
					// DtoFileObject child = new DtoFileObject("grid://"
					// + IDENTIFIER + "/" + group, -1L);
					// child.addFqan(fqan);
					// result.addChild(child);
					continue;
				} else if (user.getMountPoints(fqan).size() == 1) {

					MountPoint mp = user.getMountPoints(fqan).iterator().next();
					GridFile child = null;
					// try {
					// child = new DtoFileObject(mp.getRootUrl(), user
					// .aquireFile(mp.getRootUrl(), fqan).getContent()
					// .getLastModifiedTime());
					// } catch (Exception e) {
					// myLogger.error(e);
					child = new GridFile(mp.getRootUrl(), -1L);
					child.setName(group);
					// }
					child.setIsVirtual(false);
					child.addFqan(fqan);
					child.addSite(mp.getSite());
					result.addChild(child);
					result.addSite(mp.getSite());
				} else {
					GridFile child = new GridFile(
							ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://"
									+ IDENTIFIER + "/" + group, -1L);
					child.setIsVirtual(true);
					child.addFqan(fqan);

					for (MountPoint mp : user.getMountPoints(fqan)) {
						child.addSite(mp.getSite());
						result.addSite(mp.getSite());
						child.addUrl(mp.getRootUrl(), 0);
					}
					result.addChild(child);
				}
			}

			return result;

		} else {

			String uniqueGroup = tokens[2];

			if (!user.getAllAvailableUniqueGroupnames().contains(uniqueGroup)) {
				throw new InvalidPathException("Group \"" + tokens[2]
						+ "\" not available.");
			}
			String fullFqan = user.getFullFqan(uniqueGroup);

			Set<MountPoint> mps = new HashSet<MountPoint>();
			for (MountPoint mp : user.getAllMountPoints()) {
				if (mp.getFqan().equals(fullFqan)) {
					mps.add(mp);
				}
			}

			final String restUrl = StringUtils.join(tokens, "/", 3,
					tokens.length);

			final Map<String, GridFile> lsMap = Collections
					.synchronizedMap(new HashMap<String, GridFile>());

			final ExecutorService pool = Executors.newFixedThreadPool(10);

			for (final MountPoint mp : mps) {
				Thread t = new Thread() {
					@Override
					public void run() {

						String urlToLs = mp.getRootUrl() + "/" + restUrl;
						try {
							GridFile result = user.getFileSystemManager()
									.getFolderListing(urlToLs, 1);
							myLogger.debug("retrieved results from: "
									+ mp.getAlias());
							result.setPath(path);
							for (GridFile c : result.getChildren()) {
								if (path.endsWith("/")) {
									c.setPath(path + c.getName());
								} else {
									c.setPath(path + "/" + c.getName());
								}
							}
							result.addSite(mp.getSite());
							result.addFqan(mp.getFqan());
							lsMap.put(urlToLs, result);
						} catch (Exception e) {
							myLogger.error(e);
						}
					}
				};
				pool.execute(t);
			}

			pool.shutdown();

			try {
				pool.awaitTermination(5, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			switch (lsMap.size()) {
			case 0:
				return null;

			case 1:
				return lsMap.values().iterator().next();
			default:
				// return assembleFileObject(path, lsMap);
				return null;

			}
		}

	}

}
