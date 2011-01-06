package org.vpac.grisu.backend.model.fs;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.log4j.Logger;
import org.vpac.grisu.backend.model.RemoteFileTransferObject;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.GridFile;
import org.vpac.security.light.vomsProxy.VomsException;

public class CommonsVfsFileSystemInfoPlugin implements FileSystemInfoPlugin,
		FileTransferPlugin {

	private static Logger myLogger = Logger
			.getLogger(CommonsVfsFileSystemInfoPlugin.class.getName());

	private final User user;

	private final ThreadLocalCommonsVfsManager threadLocalFsManager;

	public CommonsVfsFileSystemInfoPlugin(User user) {
		this.user = user;
		threadLocalFsManager = new ThreadLocalCommonsVfsManager(this.user);

	}

	/**
	 * Resolves the provided filename into a FileObject. If the filename starts
	 * with "/" a file on one of the "mounted" filesystems is looked up. Else it
	 * has to start with the name of a (supported) protocol (like: gsiftp:///).
	 * 
	 * @param urlOrPath
	 *            the filename
	 * @param cred
	 *            the credential to access the filesystem on which the file
	 *            resides
	 * @return the FileObject
	 * @throws RemoteFileSystemException
	 *             if there is a problem resolving the file
	 * @throws VomsException
	 *             if the (possible) required voms credential could not be
	 *             created
	 */
	public FileObject aquireFile(String url, final String fqan)
			throws RemoteFileSystemException {

		if (Thread.interrupted()) {
			Thread.currentThread().interrupt();
			throw new RemoteFileSystemException("Accessing file interrupted.");
		}

		if (url.startsWith("tmp:") || url.startsWith("ram:")) {
			try {
				return threadLocalFsManager.getFsManager().resolveFile(url);
			} catch (final FileSystemException e) {
				throw new RemoteFileSystemException(
						"Could not access file on local temp filesystem: "
								+ e.getLocalizedMessage());
			}
		}

		FileObject fileObject = null;
		try {
			FileSystem root = null;

			// root = this.createFilesystem(mp.getRootUrl(), mp.getFqan());
			root = threadLocalFsManager.getFileSystem(url, fqan);

			final String fileUri = root.getRootName().getURI();

			try {
				final URI uri = new URI(url);
				url = uri.toString();
			} catch (final URISyntaxException e) {
				myLogger.error(e);
				throw new RemoteFileSystemException(
						"Could not get uri for file " + url);
			}

			final String tempUriString = url.replace(":2811", "").substring(
					fileUri.length());
			fileObject = root.resolveFile(tempUriString);
			// fileObject = root.resolveFile(file_to_aquire);

		} catch (final FileSystemException e) {
			throw new RemoteFileSystemException("Could not access file: " + url
					+ ": " + e.getMessage());
		}

		return fileObject;

	}

	public void closeFileSystems() {
		threadLocalFsManager.remove();
	}

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException {

		final FileObject source_file;
		final FileObject target_file;

		source_file = aquireFile(source, null);
		target_file = aquireFile(target, null);

		// String targetFileString;
		// try {
		// targetFileString = target_file.getURL().toString();
		// } catch (final FileSystemException e1) {
		// myLogger.error("Could not retrieve targetfile url: "
		// + e1.getLocalizedMessage());
		// throw new RemoteFileSystemException(
		// "Could not retrive targetfile url: "
		// + e1.getLocalizedMessage());
		// }

		final RemoteFileTransferObject fileTransfer = new RemoteFileTransferObject(
				source_file, target_file, overwrite);

		myLogger.info("Creating fileTransfer object for source: "
				+ source_file.getName() + " and target: "
				+ target_file.toString());

		return fileTransfer;

	}

	public GridFile getFolderListing(String url)
			throws RemoteFileSystemException {

		final FileObject fo = aquireFile(url, null);

		try {

			if (!FileType.FOLDER.equals(fo.getType())) {
				throw new RemoteFileSystemException("Url: " + url
						+ " not a folder.");
			}
			long lastModified = fo.getContent().getLastModifiedTime();

			MountPoint mp = user.getResponsibleMountpointForAbsoluteFile(url);
			final GridFile folder = new GridFile(url, lastModified);

			folder.addSite(mp.getSite());
			folder.addFqan(mp.getFqan());
			// TODO the getChildren command seems to throw exceptions without
			// reason
			// every now and the
			// probably a bug in commons-vfs-grid. Until this is resolved, I
			// always
			// try 2 times...
			FileObject[] children = null;
			try {
				children = fo.getChildren();
			} catch (final Exception e) {
				e.printStackTrace();
				myLogger.error("Couldn't get children of :"
						+ fo.getName().toString() + ". Trying one more time...");
				children = fo.getChildren();
			}

			for (final FileObject child : children) {
				if (FileType.FOLDER.equals(child.getType())) {
					GridFile childfolder = new GridFile(child.getURL()
							.toString());
					// GridFile childfolder = child.getURL().get
					// try {
					// childfolder = new GridFile(child.getURL().toURI()
					// .toASCIIString(), child.getContent()
					// .getLastModifiedTime());
					// } catch (URISyntaxException e) {
					// e.printStackTrace();
					// throw new RemoteFileSystemException(e);
					// }
					childfolder.addFqan(mp.getFqan());
					childfolder.addSite(mp.getSite());
					folder.addChild(childfolder);
				} else if (FileType.FILE.equals(child.getType())) {
					final GridFile childFile = new GridFile(child.getURL()
							.toString(), child.getContent().getSize(), child
							.getContent().getLastModifiedTime());
					childFile.addFqan(mp.getFqan());
					childFile.addSite(mp.getSite());
					folder.addChild(childFile);
				}
			}
			return folder;
		} catch (FileSystemException fse) {
			throw new RemoteFileSystemException(fse);
		}

	}
}
