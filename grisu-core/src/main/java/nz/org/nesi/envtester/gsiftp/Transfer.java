package nz.org.nesi.envtester.gsiftp;

import grisu.backend.utils.DummyMarkerImpl;
import grisu.jcommons.interfaces.GrinformationManagerDozer;
import grisu.jcommons.interfaces.InformationManager;
import grisu.settings.ServerPropertiesManager;
import grith.jgrith.cred.AbstractCred;
import grith.jgrith.cred.Cred;
import grith.jgrith.voms.VOManagement.VOManager;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.gridftp.cogjglobus.GridFtpFileSystemConfigBuilder;
import org.globus.ftp.MarkerListener;

import uk.ac.dl.escience.vfs.util.VFSUtil;

public class Transfer {

	private static DefaultFileSystemManager fsm;

	public static DefaultFileSystemManager getFsm() {
		if (fsm == null) {
			try {
				fsm = VFSUtil.createNewFsManager(false, false, true, true,
						true, true, true, System.getProperty("java.io.tmpdir"));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return fsm;
	}

	private final static MarkerListener dummyMarker = new DummyMarkerImpl();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		InformationManager im = new GrinformationManagerDozer(
				"/data/src/config/nesi-grid-info/nesi_info.groovy");
		AbstractCred.DEFAULT_VO_MANAGER = new VOManager(im);

		Cred c = AbstractCred.getExistingOrCliCredential();

		Cred nz = c.getGroupCredential("/nz/nesi");

		FileObject localSource = resolveLocalFile("/home/markus/tmp/hello");

		FileObject source = resolveFile(nz,
				"gsiftp://pan.nesi.org.nz/home/mbin029/autos.r");

		FileObject target = resolveFile(nz,
				"gsiftp://ng2sge.canterbury.ac.nz/home/users/bestgrid/grid013/");

		// copy(source, target);
		copy(localSource, target);

		System.exit(0);

	}

	public static FileObject resolveLocalFile(String path) {
		try {
			return getFsm().resolveFile("file:" + path);
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	public static FileObject resolveFile(Cred credToUse, String url)
			throws FileSystemException {

		final FileSystemOptions opts = new FileSystemOptions();
		final GridFtpFileSystemConfigBuilder builder = GridFtpFileSystemConfigBuilder
				.getInstance();
		builder.setGSSCredential(opts, credToUse.getGSSCredential());
		builder.setTimeout(opts,
				ServerPropertiesManager.getFileSystemConnectTimeout());

		FileObject fileRoot = null;
		fileRoot = getFsm().resolveFile(url, opts);

		if ( url.contains("~") ) {
			FileSystem fs = fileRoot.getFileSystem();
			String home = (String)fs.getAttribute("HOME_DIRECTORY");
			String newUrl = url.replace("~", home);

			fileRoot = getFsm().resolveFile(newUrl, opts);
		}

		return fileRoot;

	}

	public static void copy(FileObject source, FileObject target) {

		try {
			VFSUtil.copy(source, target, dummyMarker, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
