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
import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;
import org.vpac.grisu.X;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoFileObject;

public class VirtualFileSystemPluginImpl implements FileSystemInfoPlugin,
		VirtualFileSystemPlugin {

	static final Logger myLogger = Logger
			.getLogger(VirtualFileSystemPluginImpl.class.getName());

	public static final String GROUP_IDENTIFIER = "groups";

	private final User user;

	public VirtualFileSystemPluginImpl(User user) {
		this.user = user;
	}

	public FileObject aquireFile(String url, String fqan)
			throws RemoteFileSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	private DtoFileObject assembleFileObject(String url,
			Map<MountPoint, DtoFileObject> lsMap) {

		DtoFileObject result = new DtoFileObject(url, -1L);
		result.setIsVirtual(true);

		for (MountPoint mp : lsMap.keySet()) {
			DtoFileObject child = lsMap.get(mp);
			result.addUrl(child.getUrl(), 0);
			result.addChildren(child.getChildren());
		}

		return result;
	}

	public DtoFileObject createDtoFileObject(final String path,
			int recursiveLevels) throws InvalidPathException {

		if (recursiveLevels != 1) {
			throw new RuntimeException(
					"Recursion levels other than 1 not supported yet");
		}

		String[] tokens = StringUtils.split(path, '/');
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

		final String restUrl = StringUtils.join(tokens, "/", 3, tokens.length);

		final Map<MountPoint, DtoFileObject> lsMap = Collections
				.synchronizedMap(new HashMap<MountPoint, DtoFileObject>());

		final ExecutorService pool = Executors.newFixedThreadPool(10);

		for (final MountPoint mp : mps) {
			Thread t = new Thread() {
				@Override
				public void run() {

					String urlToLs = mp.getRootUrl() + "/" + restUrl;
					try {
						DtoFileObject result = user.getFolderListing(urlToLs);
						myLogger.debug("retrieved results from: "
								+ mp.getAlias());
						result.setPath(path);
						for (DtoFileObject c : result.getChildren()) {
							if (path.endsWith("/")) {
								c.setPath(path + c.getName());
							} else {
								c.setPath(path + "/" + c.getName());
							}
						}
						lsMap.put(mp, result);
					} catch (Exception e) {
						X.p(e.getLocalizedMessage());
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

	public DtoFileObject getFolderListing(String url)
			throws RemoteFileSystemException {

		try {
			return createDtoFileObject(url, 1);
		} catch (InvalidPathException e) {
			throw new RemoteFileSystemException(e);
		}

	}

	public String getRootIdentifier() {
		return GROUP_IDENTIFIER;
	}

}
