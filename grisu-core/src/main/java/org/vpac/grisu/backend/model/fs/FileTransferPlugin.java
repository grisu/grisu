package org.vpac.grisu.backend.model.fs;

import org.vpac.grisu.backend.model.RemoteFileTransferObject;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

public interface FileTransferPlugin {

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException;

}
