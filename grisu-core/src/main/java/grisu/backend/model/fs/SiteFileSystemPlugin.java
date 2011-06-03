package grisu.backend.model.fs;

import grisu.backend.model.User;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.control.serviceInterfaces.AbstractServiceInterface;
import grisu.model.FileManager;
import grisu.model.MountPoint;
import grisu.model.dto.GridFile;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * A plugin to list archived and running jobs in a tree-like structure.
 * 
 * The base url for this plugin is grid://jobs . The next token is either
 * "active" or "archived" and then comes the name of the job followed by this
 * job directories content.
 * 
 * @author Markus Binsteiner
 * 
 */
public class SiteFileSystemPlugin implements VirtualFileSystemPlugin {

	static final Logger myLogger = Logger.getLogger(SiteFileSystemPlugin.class
			.getName());

	public static final String IDENTIFIER = "sites";
	private final static String BASE = (ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME
			+ "://" + IDENTIFIER);

	private final User user;

	public SiteFileSystemPlugin(User user) {
		this.user = user;
	}

	public GridFile createGridFile(final String path, int recursiveLevels)
	throws InvalidPathException {

		if (recursiveLevels != 1) {
			throw new RuntimeException(
			"Recursion levels other than 1 not supported yet");
		}

		int index = BASE.length();
		String importantUrlPart = path.substring(index);
		String[] tokens = StringUtils.split(importantUrlPart, '/');

		if (tokens.length == 0) {
			// means root of virtual filesystem, list sites...
			GridFile result = null;
			result = new GridFile(path, -1L);
			result.setIsVirtual(true);
			result.setPath(path);

			if (recursiveLevels == 0) {
				return result;
			}

			for (GridFile s : getSites()) {
				result.addChild(s);
				result.addSites(s.getSites());
				result.addFqans(s.getFqans());
			}

			return result;

		} else if (tokens.length == 1) {

			GridFile result = null;
			result = new GridFile(path, -1L);
			result.setIsVirtual(true);
			result.setPath(path);

			if (recursiveLevels == 0) {
				return result;
			}

			for (GridFile mp : getHosts(tokens[0])) {
				result.addChild(mp);
				result.addSites(mp.getSites());
				result.addFqans(mp.getFqans());
			}

			return result;

		} else {

			return getPath(tokens);

		}

		// return null;

	}

	private Set<GridFile> getHosts(String site) {

		Set<GridFile> result = new TreeSet<GridFile>();
		Map<String, Set<MountPoint>> hosts = new TreeMap<String, Set<MountPoint>>();

		for (MountPoint mp : user.getAllMountPoints()) {

			if (mp.getSite().equals(site)) {
				String host = FileManager.getHost(mp.getRootUrl());
				if (hosts.get(host) == null) {
					Set<MountPoint> mps = new TreeSet<MountPoint>();
					mps.add(mp);
					hosts.put(host, mps);
				} else {
					Set<MountPoint> mps = hosts.get(host);
					mps.add(mp);
				}
			}
		}

		for (String host : hosts.keySet()) {
			String path = BASE + "/" + site + "/" + host;
			GridFile file = new GridFile(path, -1L);
			file.setIsVirtual(true);
			file.addSite(site);
			for (MountPoint mp : hosts.get(host)) {
				file.addFqan(mp.getFqan());
			}
			result.add(file);
		}

		return result;

	}

	private GridFile getPath(String[] pathTokens) {

		String site = pathTokens[0];
		String host = pathTokens[1];

		String path = StringUtils.join(pathTokens, "/", 2, pathTokens.length);

		String requestedFileWithoutProtocol = host + "/" + path;

		Set<MountPoint> mountPointsFound = new TreeSet<MountPoint>();

		for (MountPoint mp : user.getAllMountPoints()) {
			if (!mp.getSite().equals(site) || !mp.getRootUrl().contains(host)) {
				continue;
			}

			if (mp.getRootUrl().contains(requestedFileWithoutProtocol)) {
				mountPointsFound.add(mp);
				continue;
			}

		}

		if (mountPointsFound.size() > 0) {
			String gridPath = BASE + "/" + site + "/" + host + "/" + path;
			// will always be directory since it's below mountpoint level
			GridFile result = new GridFile(gridPath, -1L);
			// is virtual because can't be accessed directly
			result.setIsVirtual(true);
			result.setPath(gridPath);
			result.addSite(site);
			Map<String, Set<MountPoint>> childs = new TreeMap<String, Set<MountPoint>>();
			for (MountPoint mp : mountPointsFound) {
				result.addSite(mp.getSite());
				result.addFqan(mp.getFqan());

				String mpRestPath = FileManager.removeTrailingSlash(mp
						.getRootUrl());
				mpRestPath = mpRestPath.substring(
						mpRestPath
						.indexOf(requestedFileWithoutProtocol)
						+ requestedFileWithoutProtocol.length());
				mpRestPath = StringUtils.removeStart(mpRestPath, "/");

				if (StringUtils.isBlank(mpRestPath)) {

					return listFiles(BASE + "/" + site + "/" + host + "/"
							+ path,
							mp.getRootUrl());
				}

				String child = null;
				int index = mpRestPath.indexOf("/");
				if (index < 0) {
					child = mpRestPath;
				} else {
					child = mpRestPath.substring(0, index);
				}

				if (childs.get(child) == null) {
					Set<MountPoint> tempMPs = new TreeSet<MountPoint>();
					tempMPs.add(mp);
					childs.put(child, tempMPs);
				} else {
					Set<MountPoint> tempMPs = childs.get(child);
					tempMPs.add(mp);
				}
			}

			for (String child : childs.keySet()) {
				GridFile childFile = new GridFile(gridPath + "/" + child);
				childFile.setIsVirtual(true);
				childFile.setPath(gridPath + "/" + child);
				for (MountPoint mp : childs.get(child)) {
					childFile.addFqan(mp.getFqan());
					childFile.addSite(mp.getSite());
					result.addFqan(mp.getFqan());
				}
				result.addChild(childFile);
			}

			return result;
		} else {

			String gridPath = BASE + "/" + site + "/" + host + "/" + path;
			// will always be directory since it's below mountpoint level
			GridFile result = new GridFile(gridPath, -1L);
			// is virtual because can't be accessed directly
			result.setIsVirtual(true);
			result.setPath(gridPath);
			result.addSite(site);

			Map<String, Set<MountPoint>> mountPointsResponsible = new TreeMap<String, Set<MountPoint>>();
			for (MountPoint mp : user.getAllMountPoints()) {
				if (!mp.getSite().equals(site)
						|| !mp.getRootUrl().contains(host)) {
					continue;
				}

				String mpPath = mp.getRootUrl().substring(
						mp.getRootUrl().indexOf(host));
				// X.p("PATH: " + mpPath);

				if (requestedFileWithoutProtocol.contains(mpPath)) {

					String restPath = requestedFileWithoutProtocol
					.substring(mpPath.length());

					String url = FileManager.removeTrailingSlash(mp
							.getRootUrl()) + "/" + restPath;
					if (mountPointsResponsible.get(url) == null) {
						Set<MountPoint> temp = new TreeSet<MountPoint>();
						temp.add(mp);
						mountPointsResponsible.put(url, temp);
					} else {
						Set<MountPoint> temp = mountPointsResponsible.get(url);
						temp.add(mp);
					}
				}
			}

			if (mountPointsResponsible.size() == 0) {
				return result;
			}

			for (String url : mountPointsResponsible.keySet()) {

				GridFile child = listFiles(gridPath, url);
				for (MountPoint mp : mountPointsResponsible.get(url)) {
					child.addFqan(mp.getFqan());
				}
				result.addChildren(child.getChildren());
				result.addFqans(child.getFqans());

				for (GridFile c : result.getChildren()) {
					c.addFqans(child.getFqans());
					c.setIsVirtual(false);
				}
			}

			return result;

		}
	}

	private Set<GridFile> getSites() {

		Map<String, Set<String>> sites = new TreeMap<String, Set<String>>();
		for (String vo : user.getFqans().keySet()) {
			Map<String, String[]> dataLocations = AbstractServiceInterface.informationManager
			.getDataLocationsForVO(vo);
			for (String host : dataLocations.keySet()) {
				String site = AbstractServiceInterface.informationManager
				.getSiteForHostOrUrl(host);
				if (!sites.keySet().contains(site)) {
					Set<String> tempVOs = new TreeSet<String>();
					tempVOs.add(vo);
					sites.put(site, tempVOs);
				} else {
					Set<String> tempVos = sites.get(site);
					tempVos.add(vo);
				}

			}
		}

		Set<GridFile> result = new TreeSet<GridFile>();
		for (String site : sites.keySet()) {
			String path = BASE + "/" + site;
			GridFile s = new GridFile(path, -1L);
			s.setIsVirtual(true);
			s.setPath(path);
			s.addSite(site);
			for (String vo : sites.get(site)) {
				s.addFqan(vo);
			}
			result.add(s);
		}

		return result;

	}

	private GridFile listFiles(String path, String url) {

		try {
			GridFile f = user.ls(url, 1);
			f.setPath(path);
			f.setIsVirtual(false);
			for (GridFile c : f.getChildren()) {
				c.setPath(path + "/" + c.getName());
				c.setIsVirtual(false);
			}

			return f;

		} catch (RemoteFileSystemException e) {
			myLogger.error(e);
			return new GridFile(url, false, e);
		}

	}



}
