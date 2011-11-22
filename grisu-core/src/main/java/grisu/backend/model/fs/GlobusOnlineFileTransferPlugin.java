package grisu.backend.model.fs;

import grisu.backend.model.RemoteFileTransferObject;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.control.info.SqlInfoManager;
import grisu.jcommons.interfaces.InfoManager;
import nz.org.nesi.goji.control.GlobusOnlineUserSession;

public class GlobusOnlineFileTransferPlugin implements FileTransferPlugin {

	private final String go_user = "markus";
	private final InfoManager im = new SqlInfoManager();

	private final GlobusOnlineUserSession go;

	public GlobusOnlineFileTransferPlugin() {

		go = new GlobusOnlineUserSession(go_user, im);

	}

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException {

		GlobusOnlineFileTransferObject to = new GlobusOnlineFileTransferObject(
				go, source, target);
		return to;
	}
}
