package org.vpac.grisu.backend.model.fs;

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
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.GridFile;

public class AllGroupsFileSystemPlugin implements VirtualFileSystemPlugin {

	static final Logger myLogger = Logger
			.getLogger(AllGroupsFileSystemPlugin.class.getName());

	public static final String IDENTIFIER = "allgroups";

	private final User user;

	public AllGroupsFileSystemPlugin(User user) {
		this.user = user;
	}

	private GridFile assembleFileObject(String path, Map<String, GridFile> lsMap) {

		GridFile result = new GridFile(path, -1L);
		result.setIsVirtual(true);

		for (String url : lsMap.keySet()) {
			result.addUrl(url, 0);

			GridFile child = lsMap.get(url);

			if (!child.isFolder()) {
				boolean alreadyInChildren = false;
				for (GridFile resultChild : result.getChildren()) {
					if (resultChild.getName().equals(child.getName())) {
						resultChild.addUrl(child.getUrl(), 0);
						resultChild.setUrl(child.getPath());
						resultChild.setIsVirtual(true);
						resultChild.setLastModified(-1L);
						resultChild.setSize(-1L);
						resultChild.addSites(child.getSites());
						result.addSites(child.getSites());
						alreadyInChildren = true;
						break;
					}
				}
				if (!alreadyInChildren) {
					result.addChild(child);
					result.addSites(child.getSites());
				}
				continue;
			} else {

				// for a folder, we need to do a bit more.
				// first, we add the parent url
				// priority we can sort out later if necessary
				result.addUrl(FileManager.calculateParentUrl(child.getUrl()), 0);

				// now we add the children
				// but me need to make sure that it's not already in there

				for (GridFile c : child.getChildren()) {
					boolean alreadyInChildren = false;
					for (GridFile resultChild : result.getChildren()) {
						if (resultChild.getName().equals(c.getName())) {
							resultChild.addUrl(c.getUrl(), 0);
							resultChild.setUrl(c.getPath());
							resultChild.setIsVirtual(true);
							resultChild.addSites(child.getSites());
							resultChild.setLastModified(-1L);
							result.addSites(child.getSites());
							alreadyInChildren = true;
							break;
						}
					}
					if (!alreadyInChildren) {
						result.addChild(c);
						result.addSites(child.getSites());
					}
				}
			}

		}

		return result;
	}

	public GridFile createGridFile(final String path, int recursiveLevels)
			throws InvalidPathException {

		if (recursiveLevels != 1) {
			throw new RuntimeException(
					"Recursion levels other than 1 not supported yet");
		}

		String[] tokens = StringUtils.split(path, '/');

		if (tokens.length == 2) {
			// only display groups

			GridFile result = new GridFile(
					ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://"
							+ IDENTIFIER, -1L);

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
					GridFile child = new GridFile("grid://" + IDENTIFIER + "/"
							+ group, -1L);
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
							GridFile result = user.getFolderListing(urlToLs, 1);
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
				return assembleFileObject(path, lsMap);

			}
		}

	}
}
