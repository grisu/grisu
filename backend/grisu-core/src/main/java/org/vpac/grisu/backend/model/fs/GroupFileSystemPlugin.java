package org.vpac.grisu.backend.model.fs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.GridFile;

public class GroupFileSystemPlugin implements VirtualFileSystemPlugin {

	static final Logger myLogger = Logger.getLogger(GroupFileSystemPlugin.class
			.getName());

	public static final String IDENTIFIER = "groups";

	private final static String BASE = (ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME
			+ "://" + IDENTIFIER);

	private final User user;

	public GroupFileSystemPlugin(User user) {
		this.user = user;
	}

	public GridFile createGridFile(final String path, int recursiveLevels)
			throws RemoteFileSystemException {

		// Thread.dumpStack();
		if (recursiveLevels > 1) {
			throw new RuntimeException(
					"Recursion levels greater than 1 not supported yet");
		}

		String rightPart = path.substring(BASE.length());
		if (rightPart.contains("//")) {
			GridFile result = null;

			// that means we everything before the // is the fqan and everything
			// after is the path

			int i = rightPart.indexOf("//");
			String fqanT = rightPart.substring(0, i);
			String restPath = rightPart.substring(i + 2);
			if (!fqanT.startsWith("/")) {
				fqanT = "/" + fqanT;
			}

			Set<String> urls = resolveUrls(restPath, fqanT);

			if (urls.size() == 0) {
				// TODO not sure what to do
				throw new RuntimeException("No real url found for virtual url.");
			} else if (urls.size() == 1) {
				result = new GridFile(urls.iterator().next(), -1L);
				result.setIsVirtual(false);
				result.setPath(path);
			} else {
				result = new GridFile(path, -1L);
				result.setIsVirtual(true);
				result.setPath(path);
			}

			if (recursiveLevels == 0) {
				return result;
			}

			Set<GridFile> childs = listGroup(fqanT, restPath);

			for (GridFile file : childs) {
				result.addChildren(file.getChildren());
			}

			Map<String, Set<String>> temp = findDirectChildFqans(fqanT);
			Set<String> childFqans = temp.keySet();
			for (String fqan : childFqans) {

				Set<MountPoint> mps = user.getMountPoints(fqan);
				if (mps.size() == 1) {
					GridFile file = new GridFile(mps.iterator().next());
					file.setName(FileManager.getFilename(fqan));
					file.setPath((path + "/" + file.getName()).replace("///",
							"/").replace("//", "/")
							+ "//");
					file.setIsVirtual(true);
					file.addSites(temp.get(fqan));
					result.addChild(file);
				} else {
					GridFile file = new GridFile((BASE + fqan).replace("///",
							"/").replace("//", "/")
							+ "//", fqan);
					file.setPath((path + "/" + file.getName()).replace("///",
							"/").replace("//", "/")
							+ "//");
					for (MountPoint mp : mps) {
						// X.p("Add" + mp.getRootUrl());
						file.addUrl(mp.getRootUrl(),
								GridFile.FILETYPE_MOUNTPOINT_PRIORITY);
					}
					file.setIsVirtual(true);
					file.addSites(temp.get(fqan));
					result.addChild(file);
				}
			}

			return result;

		}

		int index = BASE.length();
		String importantUrlPart = path.substring(index);
		String[] tokens = StringUtils.split(importantUrlPart, '/');

		GridFile result = null;

		if (tokens.length == 0) {
			// means root of the groupfilesystem

			result = new GridFile(path, -1L);
			result.setIsVirtual(true);
			result.setPath(path);

			if (recursiveLevels == 0) {
				return result;
			}

			for (String vo : user.getFqans().values()) {
				GridFile f = new GridFile(BASE + "/" + vo, -1L);
				f.setIsVirtual(true);
				f.setPath(path + "/" + vo);

				for (MountPoint mp : user.getMountPoints("/" + vo)) {
					f.addSite(mp.getSite());
					f.addUrl(mp.getRootUrl(),
							GridFile.FILETYPE_MOUNTPOINT_PRIORITY);
				}

				result.addChild(f);
			}

		} else if (tokens.length == 1) {
			// means is root of VO so we need to list potential files on all
			// sites that support this vo
			// and also all child vos
			String parentfqan = "/" + tokens[0];
			Set<String> urls = resolveUrls(path, parentfqan);

			if (urls.size() == 1) {
				result = new GridFile(urls.iterator().next(), -1L);
				result.setIsVirtual(false);
				result.setPath(path);
			} else {
				result = new GridFile(path, -1L);
				result.setIsVirtual(true);
				result.setPath(path);
				for (String u : urls) {
					result.addUrl(u, GridFile.FILETYPE_MOUNTPOINT_PRIORITY);
				}
			}

			if (recursiveLevels == 0) {
				return result;
			}

			Map<String, Set<String>> temp = findDirectChildFqans(parentfqan);
			Set<String> childFqans = temp.keySet();

			for (String fqan : childFqans) {

				Set<MountPoint> mps = user.getMountPoints(fqan);
				if (mps.size() == 1) {
					GridFile file = new GridFile(mps.iterator().next());
					file.setName(FileManager.getFilename(fqan));
					file.setPath(path + "/" + file.getName());
					file.setIsVirtual(true);
					file.addSites(temp.get(fqan));
					result.addChild(file);
				} else {
					GridFile file = new GridFile(BASE + fqan, fqan);
					file.setPath(path + "/" + file.getName());
					file.setIsVirtual(true);
					file.addSites(temp.get(fqan));
					for (MountPoint mp : mps) {
						file.addUrl(mp.getRootUrl(),
								GridFile.FILETYPE_MOUNTPOINT_PRIORITY);
					}
					file.addFqan(fqan);
					result.addChild(file);

				}
			}

			Set<GridFile> files = listGroup("/" + tokens[0], "");
			for (GridFile file : files) {
				result.addChildren(file.getChildren());
			}

		} else {

			String currentUrl = BASE;
			String potentialFqan = "";

			Set<String> parentUrls = new HashSet<String>();
			Set<GridFile> children = new TreeSet<GridFile>();

			for (String token : tokens) {

				currentUrl = currentUrl + "/" + token;
				potentialFqan = potentialFqan + "/" + token;

				if (!user.getFqans().keySet().contains(potentialFqan)) {
					continue;
				}

				String rest = path.substring(currentUrl.length());

				Set<String> urls = resolveUrls(rest, potentialFqan);
				parentUrls.addAll(urls);

				if (recursiveLevels == 1) {

					Set<GridFile> files = listGroup(potentialFqan, rest);
					for (GridFile file : files) {
						children.addAll(file.getChildren());
					}

				}

			}

			if (recursiveLevels == 1) {

				Map<String, Set<String>> temp = findDirectChildFqans(potentialFqan);
				Set<String> childFqans = temp.keySet();
				for (String fqan : childFqans) {
					Set<MountPoint> mps = user.getMountPoints(fqan);
					if (mps.size() == 0) {
						continue;
					}
					if (mps.size() == 1) {
						GridFile file = new GridFile(mps.iterator().next());
						file.setName(FileManager.getFilename(fqan));
						file.setPath(path + "/" + file.getName());
						file.setIsVirtual(true);
						file.addSites(temp.get(fqan));
						children.add(file);
					} else {
						GridFile file = new GridFile(BASE + fqan, fqan);
						file.setPath(path + "/" + file.getName());
						file.setIsVirtual(true);
						file.addSites(temp.get(fqan));
						children.add(file);
					}
				}
			}

			if (parentUrls.size() == 1) {
				result = new GridFile(parentUrls.iterator().next(), -1L);
				result.setIsVirtual(false);
				result.setPath(path);
			} else {
				result = new GridFile(path, -1L);
				result.setIsVirtual(true);
				result.setPath(path);
				for (String u : parentUrls) {
					result.addUrl(u, GridFile.FILETYPE_FOLDER_PRIORITY);
				}
			}

			result.addChildren(children);
		}

		return result;

	}

	private Map<String, Set<String>> findDirectChildFqans(String parentFqan) {

		String[] tokens = parentFqan.substring(1).split("/");

		Map<String, Set<String>> result = new TreeMap<String, Set<String>>();

		for (String fqan : user.getFqans().keySet()) {

			if (!fqan.startsWith(parentFqan)) {
				continue;
			}

			String[] fqanTokens = fqan.substring(1).split("/");
			int fqanTokenLength = fqanTokens.length;

			if ((fqanTokenLength == tokens.length + 1)
					&& fqanTokens[tokens.length - 1]
							.equals(tokens[tokens.length - 1])) {

				Set<String> sites = new TreeSet<String>();
				for (MountPoint mp : user.getMountPoints(fqan)) {
					sites.add(mp.getSite());
				}

				result.put(fqan, sites);
			}
		}
		return result;
	}

	private Set<GridFile> listGroup(String fqan, String path)
			throws RemoteFileSystemException {

		Set<MountPoint> mps = user.getMountPoints(fqan);

		final Set<GridFile> result = Collections
				.synchronizedSet(new TreeSet<GridFile>());

		final ExecutorService pool = Executors.newFixedThreadPool(20);

		for (final MountPoint mp : mps) {

			final String urlToQuery = mp.getRootUrl() + "/" + path;

			Thread t = new Thread() {
				@Override
				public void run() {

					try {
						GridFile file = user.getFileSystemManager()
								.getFolderListing(urlToQuery, 1);
						file.addSite(mp.getSite());
						result.add(file);
					} catch (RemoteFileSystemException rfse) {
						String msg = rfse.getLocalizedMessage();
						if (!msg.contains("not a folder")) {
							GridFile f = new GridFile(urlToQuery, true, rfse);
							result.add(f);
						}
					} catch (Exception ex) {
						GridFile f = new GridFile(urlToQuery, true, ex);
						result.add(f);
					}
				}
			};

			pool.execute(t);
		}

		pool.shutdown();

		try {
			pool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RemoteFileSystemException(e);
		}

		return result;

	}

	private Set<String> resolveUrls(String path, String fqan) {

		Set<MountPoint> mps = user.getMountPoints(fqan);
		Set<String> urls = new HashSet<String>();

		for (final MountPoint mp : mps) {
			final String urlToQuery = mp.getRootUrl() + "/" + path;
			urls.add(urlToQuery);

		}

		return urls;
	}
}
