package grisu.backend.model.fs;

import grisu.backend.model.RemoteFileTransferObject;
import grisu.control.exceptions.RemoteFileSystemException;


public interface FileTransferPlugin {

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException;

}
