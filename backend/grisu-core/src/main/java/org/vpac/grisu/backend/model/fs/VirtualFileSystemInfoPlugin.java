package org.vpac.grisu.backend.model.fs;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
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

	public GridFile getFolderListing(String url)
			throws RemoteFileSystemException {

		if (url.equals(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://")) {
			GridFile root = new GridFile(url, -1L);
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
			return getPlugin(url).createGridFile(url, 1);
		} catch (InvalidPathException e) {
			throw new RemoteFileSystemException(e);
		}
	}

	private VirtualFileSystemPlugin getPlugin(String url) {
		String plugin = StringUtils.split(url, "/")[1];
		return plugins.get(plugin.toLowerCase());
	}

}
