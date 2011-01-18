package org.vpac.grisu.backend.model.fs;

import java.util.Map;
import java.util.TreeMap;

import javax.activation.DataHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.GridFile;

public class VirtualFileSystemInfoPlugin implements FileSystemInfoPlugin {

	static final Logger myLogger = Logger
			.getLogger(VirtualFileSystemInfoPlugin.class.getName());

	private final Map<String, VirtualFileSystemPlugin> plugins = new TreeMap<String, VirtualFileSystemPlugin>();

	public VirtualFileSystemInfoPlugin(User user) {
		// plugins.put(AllGroupsFileSystemPlugin.IDENTIFIER,
		// new AllGroupsFileSystemPlugin(user));
		plugins.put(GroupFileSystemPlugin.IDENTIFIER,
				new GroupFileSystemPlugin(user));
	}

	public FileObject aquireFile(String url, String fqan)
			throws RemoteFileSystemException {
		throw new RuntimeException("Not implemented yet.");
	}

	// public RemoteFileTransferObject copySingleFile(String source,
	// String target, boolean overwrite) throws RemoteFileSystemException {
	//
	// try {
	// X.p("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	// X.p("Source: " + source);
	// X.p("Target: " + target);
	// if (StringUtils.startsWith(source, "gridftp")) {
	//
	// } else if (StringUtils.startsWith(source, "grid")) {
	//
	// } else {
	// throw new RuntimeException("Protocol not supported for file: "
	// + source);
	// }
	// // GridFile sourceFile = getPlugin(source).createGridFile(source,
	// // 0);
	// GridFile targetFile = getPlugin(target).createGridFile(target, 0);
	//
	// // X.p("Source: " + sourceFile.getName());
	// X.p("Target: " + targetFile.getName());
	// X.p("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	//
	// throw new RuntimeException("Not implemented...");
	// } catch (InvalidPathException e) {
	// throw new RemoteFileSystemException(e);
	// }
	// }

	public void createFolder(String url) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
				"Folder creation not supported for virtual file system.");
	}

	public GridFile getFolderListing(String url, int recursiveLevels)
			throws RemoteFileSystemException {

		if (url.equals(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://")) {
			GridFile root = new GridFile(url, -1L);
			root.setIsVirtual(true);
			root.setPath(url);
			if (recursiveLevels == 0) {
				return root;
			}
			for (String key : plugins.keySet()) {
				GridFile vfs = new GridFile(
						ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://"
								+ key, -1L);
				vfs.setIsVirtual(true);
				vfs.setPath(url + vfs.getName());
				vfs.setName(StringUtils.capitalize(key));
				root.addChild(vfs);
			}
			return root;

		}
		try {
			return getPlugin(url).createGridFile(url, recursiveLevels);
		} catch (InvalidPathException e) {
			throw new RemoteFileSystemException(e);
		}
	}

	private VirtualFileSystemPlugin getPlugin(String url) {
		String plugin = StringUtils.split(url, "/")[1];
		return plugins.get(plugin.toLowerCase());
	}

	public MountPoint mountFileSystem(String uri, String mountPointName,
			ProxyCredential cred, boolean useHomeDirectory, String site)
			throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
				"Mounting not supported for virtual file system.");
	}

	public String resolveFileSystemHomeDirectory(String filesystemRoot,
			String fqan) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
				"Resolving file system home not supported for virtual file system.");
	}

	public String upload(DataHandler source, String filename)
			throws RemoteFileSystemException {

		throw new RemoteFileSystemException(
				"File upload not supported for virtual file system.");
	}
}
