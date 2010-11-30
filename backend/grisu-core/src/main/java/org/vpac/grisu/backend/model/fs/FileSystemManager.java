package org.vpac.grisu.backend.model.fs;

import java.util.HashMap;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.dto.GridFile;

public class FileSystemManager {

	private final Map<String, FileTransferPlugin> filetransferPlugins = new HashMap<String, FileTransferPlugin>();
	private final Map<String, FileSystemInfoPlugin> fileSystemInfoPlugins = new HashMap<String, FileSystemInfoPlugin>();

	private final CommonsVfsFileSystemInfoPlugin commonsVfsInfo;

	public FileSystemManager(User user) {

		commonsVfsInfo = new CommonsVfsFileSystemInfoPlugin(user);
		fileSystemInfoPlugins.put("gsiftp", commonsVfsInfo);
		fileSystemInfoPlugins.put("ram", commonsVfsInfo);
		fileSystemInfoPlugins.put("tmp", commonsVfsInfo);
		filetransferPlugins.put("gsiftp-gsiftp", commonsVfsInfo);
		fileSystemInfoPlugins
				.put("grid", new VirtualFileSystemInfoPlugin(user));

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

	private FileSystemInfoPlugin getFileSystemInfoPlugin(String url) {

		String protocol = StringUtils.split(url, ':')[0];

		return fileSystemInfoPlugins.get(protocol);

	}

	public GridFile getFolderListing(String pathOrUrl)
			throws InvalidPathException, RemoteFileSystemException {

		pathOrUrl = cleanPath(pathOrUrl);
		return getFileSystemInfoPlugin(pathOrUrl).getFolderListing(pathOrUrl);

	}

}
