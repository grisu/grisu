package grisu.backend.model;

import grisu.model.MountPoint;
import grisu.model.info.dto.Directory;
import grisu.model.info.dto.FileSystem;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class UserInfoManager {

	private final User user;

	private Map<String, Set<FileSystem>> filesystems = null;
	private final Map<String, Set<Directory>> dirs = Maps.newTreeMap();

	public UserInfoManager(User user) {
		this.user = user;
	}

	// public Set<Directory> getDirectoriesForVO(String vo) {
	//
	// if (dirs.get(vo) == null) {
	// synchronized (vo) {
	//
	// Set<MountPoint> mps = user.getMountPoints(vo);
	// Set<Directory> directories = Sets.newHashSet();
	// dirs.put(vo, directories);
	//
	// for ( MountPoint mp : mps ) {
	// Set<FileSystem> temp = getFileSystemsForVO(vo);
	// FileSystem fs = null;
	// for (FileSystem fsTemp : temp) {
	// String fsUrl = fsTemp.getUrl();
	// String mpUrl = mp.getRootUrl();
	// if (mpUrl.startsWith(fsUrl)) {
	// fs = fsTemp;
	// break;
	// }
	// }
	//
	// if (fs == null) {
	// throw new RuntimeException(
	// "No filesystem found for mountpoint "
	// + mp.getAlias());
	// }
	//
	// Directory d = new Directory(fs, mp.getPath(), vo, null);
	// directories.add(d);
	// }
	//
	// }
	// }
	// return dirs.get(vo);
	// }

	private Map<String, Set<FileSystem>> getFileSystems() {
		if (filesystems == null) {
			Set<MountPoint> mps = user.getAllMountPoints();
			filesystems = Maps.newTreeMap();
			for (MountPoint mp : mps) {
				String url = mp.getRootUrl();
				FileSystem fs = new FileSystem(url);
				String fqan = mp.getFqan();
				Set<FileSystem> temp = filesystems.get(fqan);
				if (temp == null) {
					temp = Sets.newHashSet();
					filesystems.put(fqan, temp);
				}
				temp.add(fs);
			}
		}
		return filesystems;
	}

	public Set<FileSystem> getFileSystemsForVO(String fqan) {
		return getFileSystems().get(fqan);
	}

}
