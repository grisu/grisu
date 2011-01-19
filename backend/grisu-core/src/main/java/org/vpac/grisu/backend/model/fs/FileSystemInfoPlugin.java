package org.vpac.grisu.backend.model.fs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.activation.DataHandler;

import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoActionStatus;
import org.vpac.grisu.model.dto.GridFile;

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

	public InputStream getInputStream(String file)
			throws RemoteFileSystemException;

	public OutputStream getOutputStream(String file)
			throws RemoteFileSystemException;

	public boolean isFolder(final String file) throws RemoteFileSystemException;

	public long lastModified(final String url) throws RemoteFileSystemException;

	public MountPoint mountFileSystem(String uri, String mountPointName,
			ProxyCredential cred, boolean useHomeDirectory, String site)
			throws RemoteFileSystemException;

	public String resolveFileSystemHomeDirectory(String filesystemRoot,
			String fqan) throws RemoteFileSystemException;

	public String upload(DataHandler source, String filename)
			throws RemoteFileSystemException;

	public void uploadFileToMultipleLocations(Set<String> parents,
			final DataHandler source, final String targetFilename,
			DtoActionStatus optionalstatus) throws RemoteFileSystemException;

}
