package grisu.backend.model.fs;

import grisu.control.exceptions.RemoteFileSystemException;
import grisu.model.MountPoint;
import grisu.model.dto.DtoActionStatus;
import grisu.model.dto.GridFile;
import grith.jgrith.cred.Cred;

import java.util.Set;

import javax.activation.DataHandler;

public interface FileSystemInfoPlugin {

	public boolean createFolder(String url) throws RemoteFileSystemException;

	public void deleteFile(final String file) throws RemoteFileSystemException;

	public DataHandler download(final String filename)
			throws RemoteFileSystemException;

	public boolean fileExists(final String file)
			throws RemoteFileSystemException;

	public long getFileSize(final String file) throws RemoteFileSystemException;

	public GridFile getFolderListing(String url, int recursiveLevels)
			throws RemoteFileSystemException;

	public GrisuInputStream getInputStream(String file)
			throws RemoteFileSystemException;

	public GrisuOutputStream getOutputStream(String file)
			throws RemoteFileSystemException;

	public boolean isFolder(final String file) throws RemoteFileSystemException;

	public long lastModified(final String url) throws RemoteFileSystemException;

	public MountPoint mountFileSystem(String uri, String mountPointName,
			Cred cred, boolean useHomeDirectory, String site)
					throws RemoteFileSystemException;

	public String resolveFileSystemHomeDirectory(String filesystemRoot,
			String fqan) throws RemoteFileSystemException;

	public String upload(DataHandler source, String filename)
			throws RemoteFileSystemException;

	public void uploadFileToMultipleLocations(Set<String> parents,
			final DataHandler source, final String targetFilename,
			DtoActionStatus optionalstatus) throws RemoteFileSystemException;

}
