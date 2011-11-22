package grisu.backend.model.fs;

import grisu.backend.model.RemoteFileTransferObject;
import grisu.backend.model.User;
import grisu.backend.model.UserInfoManager;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.jcommons.interfaces.InfoManager;
import nz.org.nesi.goji.control.GlobusOnlineUserSession;

public class GlobusOnlineFileTransferPlugin implements FileTransferPlugin {

	private final String go_user = "markus";
	private final InfoManager im;

	private final GlobusOnlineUserSession go;

	public GlobusOnlineFileTransferPlugin(User user) {

		im = new UserInfoManager(user);
		go = new GlobusOnlineUserSession(go_user, im);

	}

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException {

		GlobusOnlineFileTransferObject to = new GlobusOnlineFileTransferObject(
				go, source, target);
		return to;
	}
}
