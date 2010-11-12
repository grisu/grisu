package org.vpac.grisu.backend.model.fs;

import org.apache.commons.vfs.FileObject;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.dto.GridFile;

public interface FileSystemInfoPlugin {

	public FileObject aquireFile(String url, String fqan)
			throws RemoteFileSystemException;

	public GridFile getFolderListing(String url)
			throws RemoteFileSystemException;

}
