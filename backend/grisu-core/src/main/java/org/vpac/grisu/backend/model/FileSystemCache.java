package org.vpac.grisu.backend.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.vpac.grisu.model.MountPoint;

import uk.ac.dl.escience.vfs.util.VFSUtil;

public class FileSystemCache {

	private Map<MountPoint, FileSystem> cachedFilesystems = new HashMap<MountPoint, FileSystem>();
	private DefaultFileSystemManager fsm = null;

	public FileSystemCache() {
		try {
			fsm = VFSUtil.createNewFsManager(false, false, true, true, true,
					true, true, null);
		} catch (final FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	public void addFileSystem(MountPoint mp, FileSystem fs) {
		cachedFilesystems.put(mp, fs);
	}

	public void close() {
		cachedFilesystems = null;

		fsm.close();
	}

	public FileSystem getFileSystem(MountPoint mp) {
		return cachedFilesystems.get(mp);
	}

	public DefaultFileSystemManager getFileSystemManager() {
		return fsm;
	}

	public Map<MountPoint, FileSystem> getFileSystems() {
		return cachedFilesystems;
	}

}
