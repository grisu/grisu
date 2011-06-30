package grisu.backend.model.fs;

import grisu.backend.model.RemoteFileTransferObject;
import grisu.backend.model.User;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.model.dto.GridFile;


public class VirtualFsTransferPlugin implements FileTransferPlugin {

	private final User user;

	public VirtualFsTransferPlugin(User user) {
		this.user = user;
	}

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException {

		GridFile sourceFile;
		sourceFile = user.getFileSystemManager().getFolderListing(source, 0);

		GridFile targetFile;

		targetFile = user.getFileSystemManager().getFolderListing(source, 0);

		if (sourceFile.getUrls().size() > 1) {

			// for (DtoProperty u : sourceFile.getUrls()) {
			// X.p(u.getKey() + " = " + u.getValue());
			// }

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
