package grisu.backend.model.fs;

import grisu.backend.model.RemoteFileTransferObject;
import grisu.backend.model.User;
import grisu.backend.model.UserInfoManager;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.jcommons.interfaces.InfoManager;
import grith.jgrith.Credential;
import grith.jgrith.WrappedGssCredential;
import nz.org.nesi.goji.control.GlobusOnlineUserSession;

import org.ietf.jgss.GSSCredential;

public class GlobusOnlineFileTransferPlugin implements FileTransferPlugin {

	private final String go_user = "markus";
	private final InfoManager im;

	private final GlobusOnlineUserSession go;

	public GlobusOnlineFileTransferPlugin(User user) {

		im = new UserInfoManager(user);

		GSSCredential gssCred = user.getCredential().getCredential();
		Credential cred = new WrappedGssCredential(gssCred);
		go = new GlobusOnlineUserSession(go_user, cred, im);

	}

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException {

		GlobusOnlineFileTransferObject to = new GlobusOnlineFileTransferObject(
				go, source, target);
		return to;
	}
}
