package grisu.backend.model.fs;

import grisu.backend.model.User;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.control.serviceInterfaces.AbstractServiceInterface;
import grisu.grin.model.resources.Directory;
import grisu.grin.model.resources.Site;
import grisu.model.FileManager;
import grisu.model.MountPoint;
import grisu.model.dto.DtoProperty;
import grisu.model.dto.GridFile;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	static final Logger myLogger = LoggerFactory
			.getLogger(SiteFileSystemPlugin.class.getName());

	public static final String IDENTIFIER = "sites";
	private final static String BASE = (ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME
			+ "://" + IDENTIFIER);

	private final User user;

	public SiteFileSystemPlugin(User user) {
		this.user = user;
	}

	public GridFile createGridFile(final String path, int recursiveLevels)
			throws InvalidPathException {

		if (recursiveLevels > 1) {
			throw new RuntimeException(
					"Recursion levels other than 1 not supported yet");
		}

		final int index = BASE.length();
		final String importantUrlPart = path.substring(index);
		final String[] tokens = StringUtils.split(importantUrlPart, '/');

		if (tokens.length == 0) {
			// means root of virtual filesystem, list sites...
			GridFile result = null;
			result = new GridFile(path, -1L);
			result.setVirtual(true);
			result.setPath(path);

			if (recursiveLevels == 0) {
				return result;
			}

			for (final GridFile s : getSites()) {
				result.addChild(s);
				result.addSites(s.getSites());
				result.addFqans(s.getFqans());
			}

			return result;

		} else if (tokens.length == 1) {

			GridFile result = null;
			result = new GridFile(path, -1L);
			result.setVirtual(true);
			result.setPath(path);

			if (recursiveLevels == 0) {
				return result;
			}

			for (final GridFile mp : getHosts(tokens[0])) {
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

		final Set<GridFile> result = new TreeSet<GridFile>();
		final Map<String, Set<MountPoint>> hosts = new TreeMap<String, Set<MountPoint>>();

		for (final MountPoint mp : user.getAllMountPoints()) {

			if (mp.getSite().equals(site)) {
				final String host = FileManager.getHost(mp.getRootUrl());
				if (hosts.get(host) == null) {
					final Set<MountPoint> mps = new TreeSet<MountPoint>();
					mps.add(mp);
					hosts.put(host, mps);
				} else {
					final Set<MountPoint> mps = hosts.get(host);
					mps.add(mp);
				}
			}
		}

		for (final String host : hosts.keySet()) {
			final String path = BASE + "/" + site + "/" + host;
			final GridFile file = new GridFile(path, -1L);
			file.setVirtual(true);
			file.addSite(site);
			for (final MountPoint mp : hosts.get(host)) {
				file.addFqan(mp.getFqan());
			}
			result.add(file);
		}

		return result;

	}

	private GridFile getPath(String[] pathTokens) {

		final String site = pathTokens[0];
		final String host = pathTokens[1];

		final String path = StringUtils.join(pathTokens, "/", 2,
				pathTokens.length);

		final String requestedFileWithoutProtocol = host + "/" + path;

		final Set<MountPoint> mountPointsFound = new TreeSet<MountPoint>();

		for (final MountPoint mp : user.getAllMountPoints()) {
			if (!mp.getSite().equals(site) || !mp.getRootUrl().contains(host)) {
				continue;
			}

			if (mp.getRootUrl().contains(requestedFileWithoutProtocol)) {
				mountPointsFound.add(mp);
				continue;
			}

		}

		if (mountPointsFound.size() > 0) {
			final String gridPath = BASE + "/" + site + "/" + host + "/" + path;
			// will always be directory since it's below mountpoint level
			final GridFile result = new GridFile(gridPath, -1L);
			// is virtual because can't be accessed directly
			result.setVirtual(true);
			result.setPath(gridPath);
			result.addSite(site);
			final Map<String, Set<MountPoint>> childs = new TreeMap<String, Set<MountPoint>>();
			for (final MountPoint mp : mountPointsFound) {
				result.addSite(mp.getSite());
				result.addFqan(mp.getFqan());

				String mpRestPath = FileManager.removeTrailingSlash(mp
						.getRootUrl());
				mpRestPath = mpRestPath.substring(mpRestPath
						.indexOf(requestedFileWithoutProtocol)
						+ requestedFileWithoutProtocol.length());
				mpRestPath = StringUtils.removeStart(mpRestPath, "/");

				if (StringUtils.isBlank(mpRestPath)) {

					return listFiles(BASE + "/" + site + "/" + host + "/"
							+ path, mp.getRootUrl());
				}

				String child = null;
				final int index = mpRestPath.indexOf("/");
				if (index < 0) {
					child = mpRestPath;
				} else {
					child = mpRestPath.substring(0, index);
				}

				if (childs.get(child) == null) {
					final Set<MountPoint> tempMPs = new TreeSet<MountPoint>();
					tempMPs.add(mp);
					childs.put(child, tempMPs);
				} else {
					final Set<MountPoint> tempMPs = childs.get(child);
					tempMPs.add(mp);
				}
			}

			for (final String child : childs.keySet()) {
				final GridFile childFile = new GridFile(gridPath + "/" + child);
				childFile.setVirtual(true);
				childFile.setPath(gridPath + "/" + child);
				for (final MountPoint mp : childs.get(child)) {
					childFile.addFqan(mp.getFqan());
					childFile.addSite(mp.getSite());
					result.addFqan(mp.getFqan());
				}
				result.addChild(childFile);
			}

			return result;
		} else {

			final String gridPath = BASE + "/" + site + "/" + host + "/" + path;
			// will always be directory since it's below mountpoint level
			final GridFile result = new GridFile(gridPath, -1L);
			// is virtual because can't be accessed directly
			result.setVirtual(true);
			result.setPath(gridPath);
			result.addSite(site);

			final Map<String, Set<MountPoint>> mountPointsResponsible = new TreeMap<String, Set<MountPoint>>();
			for (final MountPoint mp : user.getAllMountPoints()) {
				if (!mp.getSite().equals(site)
						|| !mp.getRootUrl().contains(host)) {
					continue;
				}

				final String mpPath = mp.getRootUrl().substring(
						mp.getRootUrl().indexOf(host));
				// X.p("PATH: " + mpPath);

				if (requestedFileWithoutProtocol.contains(mpPath)) {

					final String restPath = requestedFileWithoutProtocol
							.substring(mpPath.length());

					final String url = FileManager.removeTrailingSlash(mp
							.getRootUrl()) + "/" + restPath;
					if (mountPointsResponsible.get(url) == null) {
						final Set<MountPoint> temp = new TreeSet<MountPoint>();
						temp.add(mp);
						mountPointsResponsible.put(url, temp);
					} else {
						final Set<MountPoint> temp = mountPointsResponsible
								.get(url);
						temp.add(mp);
					}
				}
			}

			if (mountPointsResponsible.size() == 0) {
				return result;
			}

			for (final String url : mountPointsResponsible.keySet()) {

				final GridFile child = listFiles(gridPath, url);
				for (final MountPoint mp : mountPointsResponsible.get(url)) {
					child.addFqan(mp.getFqan());
				}
				result.addChildren(child.getChildren());
				result.addFqans(child.getFqans());
				final Map<String, String> urls = DtoProperty
						.mapFromDtoPropertiesList(child.getUrls());
				for (final String u : urls.keySet()) {
					result.addUrl(url, Integer.parseInt(urls.get(u)));
				}
				result.setUrl(child.getUrl());

				for (final GridFile c : result.getChildren()) {
					c.addFqans(child.getFqans());
					c.setVirtual(false);
				}
			}

			return result;

		}
	}

	private Set<GridFile> getSites() {

		final Map<String, Set<String>> sites = new TreeMap<String, Set<String>>();
		for (final String vo : user.getFqans().keySet()) {
			final Collection<Directory> dataLocations = AbstractServiceInterface.informationManager
					.getDataLocationsForVO(vo);

			for (final Directory dir : dataLocations) {
				final Site site = dir.getSite();
				if (!sites.keySet().contains(site.getName())) {
					final Set<String> tempVOs = new TreeSet<String>();
					tempVOs.add(vo);
					sites.put(site.getName(), tempVOs);
				} else {
					final Set<String> tempVos = sites.get(site.getName());
					tempVos.add(vo);
				}

			}
		}

		final Set<GridFile> result = new TreeSet<GridFile>();
		for (final String site : sites.keySet()) {
			final String path = BASE + "/" + site;
			final GridFile s = new GridFile(path, -1L);
			s.setVirtual(true);
			s.setPath(path);
			s.addSite(site);
			for (final String vo : sites.get(site)) {
				s.addFqan(vo);
			}
			result.add(s);
		}

		return result;

	}

	private GridFile listFiles(String path, String url) {

		try {
			final GridFile f = user.ls(url, 1);
			f.setPath(path);
			f.setVirtual(false);
			for (final GridFile c : f.getChildren()) {
				c.setPath(path + "/" + c.getName());
				c.setVirtual(false);
			}

			return f;

		} catch (final RemoteFileSystemException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return new GridFile(url, false, e);
		}

	}

}
