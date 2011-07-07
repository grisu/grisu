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

		if (sourceFile.getUrls().size() > 1) {

			// for (DtoProperty u : sourceFile.getUrls()) {
			// X.p(u.getKey() + " = " + u.getValue());
			// }

			throw new RemoteFileSystemException("Source file not unique: "
					+ sourceFile.getUrl());
		}

		// GridFile targetFile = null;
		//
		// try {
		// targetFile = user.getFileSystemManager()
		// .getFolderListing(target, 0);
		// if (targetFile.getUrls().size() > 1) {
		// throw new RemoteFileSystemException("Target url not unique: "
		// + targetFile.getUrl());
		// }
		// } catch (Exception e) {
		// // good
		// }


		return user.getFileSystemManager().copy(sourceFile.getUrl(), target,
				overwrite);
	}

}
