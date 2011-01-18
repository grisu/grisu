package org.vpac.grisu.backend.model.fs;

import java.util.Set;

import javax.activation.DataHandler;

import org.apache.commons.vfs.FileObject;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoActionStatus;
import org.vpac.grisu.model.dto.GridFile;

public interface FileSystemInfoPlugin {

	public FileObject aquireFile(String url, String fqan)
			throws RemoteFileSystemException;

	public void createFolder(String url) throws RemoteFileSystemException;

	public void deleteFile(final String file) throws RemoteFileSystemException;

	public GridFile getFolderListing(String url, int recursiveLevels)
			throws RemoteFileSystemException;

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
