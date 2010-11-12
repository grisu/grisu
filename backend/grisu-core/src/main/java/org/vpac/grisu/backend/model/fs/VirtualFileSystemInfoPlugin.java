package org.vpac.grisu.backend.model.fs;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.dto.GridFile;

public class VirtualFileSystemInfoPlugin implements FileSystemInfoPlugin {

	static final Logger myLogger = Logger
			.getLogger(VirtualFileSystemInfoPlugin.class.getName());

	private final Map<String, VirtualFileSystemPlugin> plugins = new HashMap<String, VirtualFileSystemPlugin>();

	public VirtualFileSystemInfoPlugin(User user) {
		plugins.put(GroupFileSystemPlugin.IDENTIFIER,
				new GroupFileSystemPlugin(user));
	}

	public FileObject aquireFile(String url, String fqan)
			throws RemoteFileSystemException {
		throw new RuntimeException("Not implemented yet.");
	}

	public GridFile getFolderListing(String url)
			throws RemoteFileSystemException {

		try {
			return getPlugin(url).createDtoFileObject(url, 1);
		} catch (InvalidPathException e) {
			throw new RemoteFileSystemException(e);
		}
	}

	private VirtualFileSystemPlugin getPlugin(String url) {
		String plugin = StringUtils.split(url, "/")[1];
		return plugins.get(plugin);
	}

}
