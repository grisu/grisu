package org.vpac.grisu.backend.model.fs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.vfs.FileObject;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.backend.model.RemoteFileTransferObject;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoActionStatus;
import org.vpac.grisu.model.dto.GridFile;

public class FileSystemManager {

	private final Map<String, FileTransferPlugin> filetransferPlugins = new HashMap<String, FileTransferPlugin>();
	private final Map<String, FileSystemInfoPlugin> fileSystemInfoPlugins = new HashMap<String, FileSystemInfoPlugin>();

	private final CommonsVfsFileSystemInfoAndTransferPlugin commonsVfsInfo;
	private final VirtualFileSystemInfoPlugin virtualFsInfo;
	private final VirtualFsTransferPlugin virtualFsTransfer;

	public FileSystemManager(User user) {

		commonsVfsInfo = new CommonsVfsFileSystemInfoAndTransferPlugin(user);
		virtualFsInfo = new VirtualFileSystemInfoPlugin(user);
		virtualFsTransfer = new VirtualFsTransferPlugin(user);
		fileSystemInfoPlugins.put("gsiftp", commonsVfsInfo);
		fileSystemInfoPlugins.put("ram", commonsVfsInfo);
		fileSystemInfoPlugins.put("tmp", commonsVfsInfo);
		fileSystemInfoPlugins.put("grid", virtualFsInfo);

		filetransferPlugins.put("gsiftp-gsiftp", commonsVfsInfo);
		filetransferPlugins.put("grid-gsiftp", virtualFsTransfer);
		filetransferPlugins.put("gsiftp-grid", virtualFsTransfer);

	}

	public FileObject aquireFile(String urlOrPath, String fqan)
			throws RemoteFileSystemException {

		String url = cleanPath(urlOrPath);

		return getFileSystemInfoPlugin(url).aquireFile(url, fqan);

	}

	private String cleanPath(String path) {
		if (path.startsWith("/")) {
			path = ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + ":/" + path;
		}
		return path;
	}

	public void closeFileSystems() {
		commonsVfsInfo.closeFileSystems();
	}

	public RemoteFileTransferObject copy(String source, String target,
			boolean overwrite) throws RemoteFileSystemException {

		String protSource = StringUtils.split(source, ':')[0];
		String protTarget = StringUtils.split(target, ':')[0];

		FileTransferPlugin pl = getFileTransferPlugin(protSource + "-"
				+ protTarget);

		return pl.copySingleFile(source, target, overwrite);
	}

	public boolean createFolder(String url) throws RemoteFileSystemException {

		return getFileSystemInfoPlugin(url).createFolder(url);

	}

	public void deleteFile(final String file) throws RemoteFileSystemException {
		getFileSystemInfoPlugin(file).deleteFile(file);
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

		String protocol = StringUtils.split(url, ':')[0];

		FileSystemInfoPlugin p = fileSystemInfoPlugins.get(protocol);

		if (p == null) {
			throw new RuntimeException("Protocol " + protocol
					+ " not supported.");
		}

		return p;

	}

	private FileTransferPlugin getFileTransferPlugin(String key) {

		FileTransferPlugin pl = filetransferPlugins.get(key);

		if (pl == null) {
			throw new NotImplementedException(
					"Filetransferplugin for not implemented: " + key);
		}

		return pl;
	}

	public GridFile getFolderListing(String pathOrUrl, int recursiveLevels)
			throws RemoteFileSystemException {

		pathOrUrl = cleanPath(pathOrUrl);
		return getFileSystemInfoPlugin(pathOrUrl).getFolderListing(pathOrUrl,
				recursiveLevels);

	}

	public InputStream getInputStream(String file)
			throws RemoteFileSystemException {
		return getFileSystemInfoPlugin(file).getInputStream(file);
	}

	public OutputStream getOutputStream(String file)
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
			final ProxyCredential cred, final boolean useHomeDirectory,
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
		for (String parent : parents) {
			String protNew = FileManager.getProtocol(parent);
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
