package org.vpac.grisu.backend.model.fs;

import java.util.HashMap;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.vfs.FileObject;
import org.vpac.grisu.backend.model.RemoteFileTransferObject;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
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

	private FileSystemInfoPlugin getFileSystemInfoPlugin(String url) {

		String protocol = StringUtils.split(url, ':')[0];

		return fileSystemInfoPlugins.get(protocol);

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
			throws InvalidPathException, RemoteFileSystemException {

		pathOrUrl = cleanPath(pathOrUrl);
		return getFileSystemInfoPlugin(pathOrUrl).getFolderListing(pathOrUrl,
				recursiveLevels);

	}

}
