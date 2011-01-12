package org.vpac.grisu.backend.model.fs;

import org.vpac.grisu.backend.model.RemoteFileTransferObject;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.dto.GridFile;

public class VirtualFsTransferPlugin implements FileTransferPlugin {

	private final User user;

	public VirtualFsTransferPlugin(User user) {
		this.user = user;
	}

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException {

		GridFile sourceFile = user.getGridFile(source);
		GridFile targetFile = user.getGridFile(target);

		if (sourceFile.getUrls().size() > 1) {
			throw new RemoteFileSystemException("Source file not unique: "
					+ sourceFile.getUrl());
		}

		if (targetFile.getUrls().size() > 1) {
			throw new RemoteFileSystemException("Target url not unique: "
					+ targetFile.getUrl());
		}

		return user.getFileSystemManager().copy(sourceFile.getUrl(),
				targetFile.getUrl(), overwrite);
	}

}
