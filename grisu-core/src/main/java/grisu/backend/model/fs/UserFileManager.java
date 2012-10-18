package grisu.backend.model.fs;

import grisu.backend.model.RemoteFileTransferObject;
import grisu.backend.model.User;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.control.exceptions.StatusException;
import grisu.model.FileManager;
import grisu.model.MountPoint;
import grisu.model.dto.DtoActionStatus;
import grisu.model.dto.GridFile;
import grisu.model.info.dto.DtoStringList;
import grisu.model.status.StatusObject;
import grith.jgrith.cred.Cred;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFileManager {

	private static Logger myLogger = LoggerFactory
			.getLogger(UserFileManager.class.getName());

	private final Map<String, FileTransferPlugin> filetransferPlugins = new HashMap<String, FileTransferPlugin>();
	private final Map<String, FileSystemInfoPlugin> fileSystemInfoPlugins = new HashMap<String, FileSystemInfoPlugin>();

	private final CommonsVfsFileSystemInfoAndTransferPlugin commonsVfsInfo;
	private final VirtualFileSystemInfoPlugin virtualFsInfo;
	private final VirtualFsTransferPlugin virtualFsTransfer;

	// private final GlobusOnlineFileTransferPlugin goFileTransfer;

	private final User user;

	public UserFileManager(User user) {

		this.user = user;
		commonsVfsInfo = new CommonsVfsFileSystemInfoAndTransferPlugin(user);
		virtualFsInfo = new VirtualFileSystemInfoPlugin(user);
		virtualFsTransfer = new VirtualFsTransferPlugin(user, virtualFsInfo);
		// goFileTransfer = new GlobusOnlineFileTransferPlugin(user);
		fileSystemInfoPlugins.put("gsiftp", commonsVfsInfo);
		fileSystemInfoPlugins.put("ram", commonsVfsInfo);
		fileSystemInfoPlugins.put("tmp", commonsVfsInfo);
		fileSystemInfoPlugins.put("grid", virtualFsInfo);

		filetransferPlugins.put("gsiftp-gsiftp", commonsVfsInfo);
		// filetransferPlugins.put("gsiftp-gsiftp", goFileTransfer);
		filetransferPlugins.put("grid-gsiftp", virtualFsTransfer);
		filetransferPlugins.put("gsiftp-grid", virtualFsTransfer);
		filetransferPlugins.put("grid-grid", virtualFsTransfer);

	}

	// public FileObject aquireFile(String urlOrPath, String fqan)
	// throws RemoteFileSystemException {
	//
	// String url = cleanPath(urlOrPath);
	//
	// return getFileSystemInfoPlugin(url).aquireFile(url, fqan);
	//
	// }

	private String cleanPath(String path) {
		if (path.startsWith("/")) {
			path = ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + ":/" + path;
		}
		return path;
	}

	public RemoteFileTransferObject copy(String source, String target,
			boolean overwrite) throws RemoteFileSystemException {

		final String protSource = StringUtils.split(source, ':')[0];
		final String protTarget = StringUtils.split(target, ':')[0];

		final FileTransferPlugin pl = getFileTransferPlugin(protSource + "-"
				+ protTarget);

		return pl.copySingleFile(source, target, overwrite);
	}

	public RemoteFileTransferObject cpSingleFile(final String source,
			final String target, final boolean overwrite,
			final boolean startFileTransfer,
			final boolean waitForFileTransferToFinish)
					throws RemoteFileSystemException {

		final RemoteFileTransferObject fileTransfer = copy(source, target,
				overwrite);

		if (startFileTransfer) {
			fileTransfer.startTransfer(waitForFileTransferToFinish);
		}

		return fileTransfer;
	}

	public boolean createFolder(String url) throws RemoteFileSystemException {

		return getFileSystemInfoPlugin(url).createFolder(url);

	}

	public String deleteFile(final String file)
			throws RemoteFileSystemException {

		final String handle = "delete_" + file + "_" + new Date().getTime();
		final DtoActionStatus status = new DtoActionStatus(handle, 2);
		user.getActionStatuses().put(handle, status);

		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					status.addElement("Starting to delete file " + file);
					getFileSystemInfoPlugin(file).deleteFile(file);
					status.addElement("Finished deletion.");
					status.setFinished(true);
					status.setFailed(false);
				} catch (final Exception e) {
					status.setFinished(true);
					status.setFailed(true);
					status.addElement("Deletion failed: "
							+ e.getLocalizedMessage());
					status.setErrorCause(e.getLocalizedMessage());
				}
			}
		};
		t.setName(handle);
		t.start();

		return handle;

	}

	public String deleteFile(final String file, boolean wait)
			throws RemoteFileSystemException {

		String handle = deleteFile(file);

		if (wait) {

			DtoActionStatus as = user.getActionStatuses().get(handle);


			StatusObject so = new StatusObject(as);

			try {
				so.waitForActionToFinish(2, false, -1);
			} catch (StatusException e) {
				throw new RemoteFileSystemException(e);
			}

			if (as.isFailed()) {
				throw new RemoteFileSystemException("Error deleting file: "
						+ as.getErrorCause());
			}

		}

		return handle;

	}

	public String deleteFiles(final DtoStringList files) {

		// TODO implement that as background task

		if ((files == null) || (files.asArray().length == 0)) {
			return null;
		}

		final String handle = "Deleting_" + files.getStringList().size()
				+ "_files_" + new Date().getTime();

		final DtoActionStatus status = new DtoActionStatus(handle,
				files.asArray().length * 2);
		user.getActionStatuses().put(handle, status);

		final Thread t = new Thread() {
			@Override
			public void run() {

				for (final String file : files.getStringList()) {
					try {
						status.addElement("Deleting file " + file + "...");
						deleteFile(file, true);
						status.addElement("Success.");
					} catch (final Exception e) {
						status.addElement("Failed: " + e.getLocalizedMessage());
						status.setFailed(true);
						status.setErrorCause(e.getLocalizedMessage());
						myLogger.error("Could not delete file: " + file);
						// filesNotDeleted.add(file);
					}
					status.setFinished(true);
				}

			}
		};
		t.setName(handle);
		t.start();

		return handle;

	}

	public DataHandler download(String filename)
			throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(filename).download(filename);
	}

	public boolean fileExists(String file) throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(file).fileExists(file);
	}

	public long getFileSize(final String file) throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(file).getFileSize(file);
	}

	private FileSystemInfoPlugin getFileSystemInfoPlugin(String url) {

		final String protocol = StringUtils.split(url, ':')[0];

		final FileSystemInfoPlugin p = fileSystemInfoPlugins.get(protocol);

		if (p == null) {
			throw new RuntimeException("Protocol " + protocol
					+ " not supported.");
		}

		return p;

	}

	private FileTransferPlugin getFileTransferPlugin(String key) {

		final FileTransferPlugin pl = filetransferPlugins.get(key);

		if (pl == null) {
			throw new NotImplementedException(
					"Filetransferplugin for not implemented: " + key);
		}

		return pl;
	}

	public GridFile getFolderListing(String pathOrUrl, int recursiveLevels)
			throws RemoteFileSystemException {

		pathOrUrl = cleanPath(pathOrUrl);

		myLogger.debug(user.getDn() + ": Listing folder (" + recursiveLevels
				+ " levels): " + pathOrUrl);

		final GridFile result = getFileSystemInfoPlugin(pathOrUrl)
				.getFolderListing(pathOrUrl, recursiveLevels);

		myLogger.debug(user.getDn() + ": Listed: "
				+ GridFile.getChildrenNames(result));

		return result;

	}

	public GrisuInputStream getInputStream(String file)
			throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(file).getInputStream(file);
	}

	public GrisuOutputStream getOutputStream(String file)
			throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(file).getOutputStream(file);
	}

	public boolean isFolder(final String file) throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(file).isFolder(file);
	}

	public long lastModified(final String url) throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(url).lastModified(url);
	}

	public MountPoint mountFileSystem(String uri, final String mountPointName,
			final Cred cred, final boolean useHomeDirectory,
			final String site) throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(uri).mountFileSystem(uri,
				mountPointName, cred, useHomeDirectory, site);
	}

	public String resolveFileSystemHomeDirectory(String filesystemRoot,
			String fqan) throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(filesystemRoot)
				.resolveFileSystemHomeDirectory(filesystemRoot, fqan);
	}

	public String upload(final DataHandler source, final String filename)
			throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(filename).upload(source, filename);
	}

	public void uploadFileToMultipleLocations(Set<String> parents,
			final DataHandler source, final String targetFilename,
			DtoActionStatus status) throws RemoteFileSystemException {

		String prot = null;
		for (final String parent : parents) {
			final String protNew = FileManager.getProtocol(parent);
			if ((prot != null) && !prot.equals(protNew)) {
				throw new RemoteFileSystemException(
						"Multiple remote protocols not supported (yet).");
			}
			prot = protNew;
		}

		getFileSystemInfoPlugin(prot).uploadFileToMultipleLocations(parents,
				source, targetFilename, status);
	}
}
