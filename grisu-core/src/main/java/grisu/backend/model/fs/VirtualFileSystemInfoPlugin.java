package grisu.backend.model.fs;

import grisu.backend.model.ProxyCredential;
import grisu.backend.model.User;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.model.MountPoint;
import grisu.model.dto.DtoActionStatus;
import grisu.model.dto.GridFile;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.activation.DataHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;

public class VirtualFileSystemInfoPlugin implements FileSystemInfoPlugin {

	static final Logger myLogger = Logger
	.getLogger(VirtualFileSystemInfoPlugin.class.getName());

	private final Map<String, VirtualFileSystemPlugin> plugins = new TreeMap<String, VirtualFileSystemPlugin>();

	public VirtualFileSystemInfoPlugin(User user) {
		plugins.put(GroupFileSystemPlugin.IDENTIFIER,
				new GroupFileSystemPlugin(user));
		plugins.put(JobsFileSystemPlugin.IDENTIFIER, new JobsFileSystemPlugin(
				user));
	}

	public FileObject aquireFile(String url, String fqan)
	throws RemoteFileSystemException {
		throw new RuntimeException("Not implemented yet.");
	}

	public boolean createFolder(String url) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"Folder creation not supported for virtual file system.");
	}

	public void deleteFile(String file) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"File deletion not supported for virtual file system.");

	}

	public DataHandler download(String filename)
	throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"File download not supported for virtual file system.");
	}

	public boolean fileExists(String file) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"File exists not supported for virtual file system.");
	}

	public long getFileSize(String file) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"Get filesize not supported for virtual file system.");
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

	public GrisuInputStream getInputStream(String file)
	throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"get input stream not supported for virtual file system.");

	}

	public GrisuOutputStream getOutputStream(String file)
	throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"Get outputstream not supported for virtual file system.");
	}

	private VirtualFileSystemPlugin getPlugin(String url) {
		String plugin = StringUtils.split(url, "/")[1];
		return plugins.get(plugin.toLowerCase());
	}

	public boolean isFolder(String file) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"Is folder not supported for virtual file system.");
	}

	public long lastModified(String url) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"Last modified not supported for virtual file system.");

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

	public void uploadFileToMultipleLocations(Set<String> parents,
			DataHandler source, String targetFilename,
			DtoActionStatus optionalstatus) throws RemoteFileSystemException {
		throw new RemoteFileSystemException(
		"Multi file upload not supported for virtual file system.");

	}
}
