package org.vpac.grisu.backend.model.fs;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.X;
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

		X.p("Creating grid file for: " + path);

		if (recursiveLevels != 1) {
			throw new RuntimeException(
					"Recursion levels other than 1 not supported yet");
		}

		X.p("Base: " + BASE);

		GridFile result = new GridFile(path, -1L);
		result.setIsVirtual(true);
		result.setPath(path);

		String rightPart = path.substring(BASE.length());
		if (rightPart.contains("//")) {
			// that means we everything before the // is the fqan and everything
			// after is the path

			int i = rightPart.indexOf("//");
			String fqanT = rightPart.substring(0, i);
			String restPath = rightPart.substring(i + 2);
			X.p("rightPart: " + rightPart);
			X.p("Fqan: " + fqanT);
			X.p("path: " + restPath);

			Set<GridFile> childs = listGroup(fqanT, restPath);
			for (GridFile file : childs) {
				result.addChildren(file.getChildren());
			}

			Set<String> childFqans = findDirectChildFqans(fqanT);
			for (String fqan : childFqans) {
				Set<MountPoint> mps = user.getMountPoints(fqan);
				if (mps.size() == 1) {
					GridFile file = new GridFile(mps.iterator().next());
					file.setName(FileManager.getFilename(fqan));
					file.setPath((path + "/" + file.getName()).replace("///",
							"/").replace("//", "/")
							+ "//");
					result.addChild(file);
				} else {
					GridFile file = new GridFile((BASE + fqan).replace("///",
							"/").replace("//", "/")
							+ "//", fqan);
					file.setPath((path + "/" + file.getName()).replace("///",
							"/").replace("//", "/")
							+ "//");
					result.addChild(file);
				}
			}

			return result;

		}

		int index = BASE.length();
		String importantUrlPart = path.substring(index);
		X.p("Part: " + importantUrlPart);
		String[] tokens = StringUtils.split(importantUrlPart, '/');

		if (tokens.length == 0) {

			for (String vo : user.getFqans().values()) {
				GridFile f = new GridFile(BASE + "/" + vo, -1L);
				f.setIsVirtual(true);
				f.setPath(path + "/" + vo);
				result.addChild(f);
			}

		} else if (tokens.length == 1) {

			Set<String> childFqans = findDirectChildFqans("/" + tokens[0]);

			for (String fqan : childFqans) {

				Set<MountPoint> mps = user.getMountPoints(fqan);
				if (mps.size() == 1) {
					GridFile file = new GridFile(mps.iterator().next());
					file.setName(FileManager.getFilename(fqan));
					file.setPath(path + "/" + file.getName());
					result.addChild(file);
				} else {
					GridFile file = new GridFile(BASE + fqan, fqan);
					file.setPath(path + "/" + file.getName());
					result.addChild(file);
				}
			}

		} else {

			String currentUrl = BASE;
			String potentialFqan = "";

			for (int i = 0; i < tokens.length; i++) {
				X.p("----------------------------");
				X.p("TOKEN " + i + ": " + tokens[i]);
				currentUrl = currentUrl + "/" + tokens[i];
				potentialFqan = potentialFqan + "/" + tokens[i];

				if (!user.getFqans().keySet().contains(potentialFqan)) {
					X.p("not a Fqan: " + potentialFqan);
					continue;
				}

				X.p("PotentialFqan: " + potentialFqan);
				X.p("Current url: " + currentUrl);
				String rest = path.substring(currentUrl.length());
				X.p("Rest of path: " + rest);

				Set<GridFile> files = listGroup(potentialFqan, rest);
				for (GridFile file : files) {
					result.addChildren(file.getChildren());
				}

			}

			Set<String> childFqans = findDirectChildFqans(potentialFqan);
			for (String fqan : childFqans) {
				Set<MountPoint> mps = user.getMountPoints(fqan);
				if (mps.size() == 1) {
					GridFile file = new GridFile(mps.iterator().next());
					file.setName(FileManager.getFilename(fqan));
					file.setPath(path + "/" + file.getName());
					result.addChild(file);
				} else {
					GridFile file = new GridFile(BASE + fqan, fqan);
					file.setPath(path + "/" + file.getName());
					result.addChild(file);
				}
			}

		}

		return result;

	}

	private Set<String> findDirectChildFqans(String parentFqan) {

		String[] tokens = parentFqan.substring(1).split("/");

		X.p(StringUtils.join(tokens, " --- "));

		Set<String> result = new TreeSet<String>();

		for (String fqan : user.getFqans().keySet()) {

			X.p("Fqan: " + fqan);

			String[] fqanTokens = fqan.substring(1).split("/");
			int fqanTokenLength = fqanTokens.length;

			if ((fqanTokenLength == tokens.length + 1)
					&& fqanTokens[tokens.length - 1]
							.equals(tokens[tokens.length - 1])) {
				X.p("HIT: " + fqan);
				result.add(fqan);
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

			X.p("MOUNTPOINT: " + mp.getRootUrl());

			final String urlToQuery = mp.getRootUrl() + "/" + path;
			X.p("URL to query: " + urlToQuery);

			Thread t = new Thread() {
				@Override
				public void run() {

					try {
						GridFile file = user.getFileSystemManager()
								.getFolderListing(urlToQuery);
						file.addSite(mp.getSite());
						X.p("Added file: " + file.getUrl());
						result.add(file);
					} catch (InvalidPathException e) {
						GridFile f = new GridFile(urlToQuery, true, e);
						result.add(f);
					} catch (RemoteFileSystemException rfse) {
						String msg = rfse.getLocalizedMessage();
						if (!msg.contains("not a folder")) {
							GridFile f = new GridFile(urlToQuery, true, rfse);
							result.add(f);
						} else {
							X.p("not folder");
						}
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

		X.p("FILES: " + StringUtils.join(result, " -- "));

		return result;

	}
}
