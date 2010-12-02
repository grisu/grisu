package org.vpac.grisu.backend.model.fs;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.gridftp.cogjglobus.GridFtpFileSystemConfigBuilder;
import org.apache.log4j.Logger;
import org.vpac.grisu.backend.model.FileSystemCache;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.model.MountPoint;

// to get one filesystemmanager per thread
public class ThreadLocalCommonsVfsManager extends ThreadLocal {

	private static Logger myLogger = Logger
			.getLogger(ThreadLocalCommonsVfsManager.class.getName());

	private final User user;

	public ThreadLocalCommonsVfsManager(User user) {
		this.user = user;
	}

	private FileSystem createFileSystem(String rootUrl,
			ProxyCredential credToUse) {

		final FileSystemOptions opts = new FileSystemOptions();

		if (rootUrl.startsWith("gsiftp")) {
			myLogger.debug("Url \"" + rootUrl
					+ "\" is gsiftp url, using gridftpfilesystembuilder...");

			final GridFtpFileSystemConfigBuilder builder = GridFtpFileSystemConfigBuilder
					.getInstance();
			builder.setGSSCredential(opts, credToUse.getGssCredential());
			// builder.setUserDirIsRoot(opts, true);
		}

		FileObject fileRoot;
		try {
			fileRoot = getFsManager().resolveFile(rootUrl, opts);
		} catch (final FileSystemException e) {
			myLogger.error("Can't connect to filesystem: " + rootUrl
					+ " using VO: " + credToUse.getFqan());
			throw new RuntimeException("Can't connect to filesystem " + rootUrl
					+ ": " + e.getLocalizedMessage(), e);
		}

		FileSystem fileBase = null;
		fileBase = fileRoot.getFileSystem();

		return fileBase;

	}

	@Override
	public void finalize() {

		// just in case, I know it's not 100% that this will be called
		remove();

	}

	public FileSystem getFileSystem(final String rootUrl, String fqan)
			throws FileSystemException {

		if (Thread.interrupted()) {
			Thread.currentThread().interrupt();
			remove();
			return null;
		}

		synchronized (rootUrl) {
			ProxyCredential credToUse = null;

			MountPoint temp = null;
			try {
				temp = user.getResponsibleMountpointForAbsoluteFile(rootUrl);
			} catch (final IllegalStateException e) {
				myLogger.info(e);
			}
			if ((fqan == null) && (temp != null) && (temp.getFqan() != null)) {
				fqan = temp.getFqan();
			}
			// get the right credential for this mountpoint
			if (fqan != null) {

				credToUse = user.getCred(fqan);

			} else {
				credToUse = user.getCred();
			}

			FileSystem fileBase = null;

			if (temp == null) {
				// means we have to figure out how to connect to this. I.e.
				// which fqan to use...
				// throw new FileSystemException(
				// "Could not find mountpoint for url " + rootUrl);

				// creating a filesystem...
				myLogger.info("Creating filesystem without mountpoint...");
				return createFileSystem(rootUrl, credToUse);

			} else {
				// great, we can re-use this filesystem
				if (((FileSystemCache) get()).getFileSystem(temp) == null) {

					fileBase = createFileSystem(temp.getRootUrl(), credToUse);

					if (temp != null) {
						((FileSystemCache) get()).addFileSystem(temp, fileBase);
					}
				} else {
					fileBase = ((FileSystemCache) get()).getFileSystem(temp);
				}
			}

			if (Thread.interrupted()) {
				remove();
				Thread.currentThread().interrupt();
				return null;
			}

			return fileBase;
		}

	}

	public DefaultFileSystemManager getFsManager() {

		if (Thread.interrupted()) {
			remove();
			Thread.currentThread().interrupt();
			return null;
		}

		return ((FileSystemCache) super.get()).getFileSystemManager();
	}

	@Override
	public Object initialValue() {

		if (Thread.interrupted()) {
			Thread.currentThread().interrupt();
			return null;
		}
		myLogger.debug("Creating new FS Manager.");
		final FileSystemCache cache = new FileSystemCache();
		myLogger.debug("Creating fsm for thread "
				+ Thread.currentThread().getName()
				+ ". cachedFileSystems size: " + cache.getFileSystems().size());
		return cache;

	}

	@Override
	public void remove() {
		myLogger.debug("Removing fsm for thread "
				+ Thread.currentThread().getName());

		// I can do that in it's own thread, right? Within a ThreadLocal object?
		new Thread() {
			@Override
			public void run() {
				try {
					((FileSystemCache) get()).close();
				} catch (Exception e) {
					myLogger.debug(e);
				}
			}
		}.start();
		super.remove();
	}
}
