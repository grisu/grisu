package org.vpac.grisu.model.dto;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.MountPoint;

@XmlRootElement(name = "fileObject")
public class GridFile implements Comparable {

	public static final String ROOT = "FileRoot";

	public static final String FILETYPE_FILE = "file";
	public static final int FILETYPE_FILE_PRIORITY = Integer.MAX_VALUE;
	public static final String FILETYPE_FOLDER = "folder";
	public static final int FILETYPE_FOLDER_PRIORITY = Integer.MAX_VALUE - 1;
	public static final String FILETYPE_ROOT = "root";
	public static final int FILETYPE_ROOT_PRIORITY = Integer.MIN_VALUE;
	public static final String FILETYPE_MOUNTPOINT = "mountpoint";
	public static final int FILETYPE_MOUNTPOINT_PRIORITY = Integer.MIN_VALUE + 1;

	// public static final String FILETYPE_GROUP = "group";
	// public static final int FILETYPE_GROUP_PRIORITY = Integer.MIN_VALUE + 2;

	public static GridFile listLocalFolder(File folder,
			boolean includeParentInFileListing) {

		String url = folder.toURI().toString();
		final GridFile result = new GridFile(url,
				folder.lastModified());

		if (includeParentInFileListing) {
			final GridFile childFolder = new GridFile(folder.toURI()
					.toString(), folder.lastModified());
			result.addChild(childFolder);
		}

		for (final File child : folder.listFiles()) {

			if (child.isDirectory()) {

				final GridFile childFolder = new GridFile(child
						.toURI().toString(), child.lastModified());
				result.addChild(childFolder);

			} else if (child.isFile()) {

				final GridFile childFile = new GridFile(child.toURI()
						.toString(), child.length(), child.lastModified());

				result.addChild(childFile);

			} else {
				System.out.println("Can't determine type of file: "
						+ child.getPath());
				// throw new
				// RuntimeException("Can't determine type of file: "+child.getPath());
			}

		}

		return result;
	}

	private String type;

	private String name;

	private String mainUrl;
	private String optionalPath;
	private Set<String> sites = new TreeSet<String>();
	private Set<String> fqans = new TreeSet<String>();

	private List<DtoProperty> urls = new LinkedList<DtoProperty>();
	private long size = -2L;
	private long lastModified;
	private boolean isVirtual = false;
	private Set<GridFile> children = new TreeSet<GridFile>();

	public GridFile() {
		this(null, -1L, -1L, FILETYPE_ROOT);
		this.isVirtual = true;
	}

	public GridFile(MountPoint mp) {

		this.type = FILETYPE_MOUNTPOINT;
		this.mainUrl = mp.getRootUrl();
		addUrl(mainUrl, FILETYPE_MOUNTPOINT_PRIORITY);
		this.size = -1L;
		this.lastModified = -1L;
		this.name = mp.getAlias();
	}

	// public DtoFileObject(String group) {
	// this(group, -1L, -1L, FILETYPE_GROUP);
	// }

	public GridFile(String url, long lastModified) {
		this(url, -1L, lastModified, FILETYPE_FOLDER);
	}

	public GridFile(String url, long size, long lastModified) {
		this(url, size, lastModified, FILETYPE_FILE);
	}

	public GridFile(String url, long size, long lastModified, String type) {
		if (FILETYPE_FOLDER.equals(type)) {
			this.type = FILETYPE_FOLDER;
			this.mainUrl = url;
			addUrl(mainUrl, FILETYPE_FOLDER_PRIORITY);
			this.size = -1L;
			this.lastModified = lastModified;
			this.name = FileManager.getFilename(url);
		} else if (FILETYPE_FILE.equals(type)) {
			this.type = FILETYPE_FILE;
			this.mainUrl = url;
			addUrl(mainUrl, FILETYPE_FILE_PRIORITY);
			this.size = size;
			this.lastModified = lastModified;
			this.name = FileManager.getFilename(url);
		} else if (FILETYPE_MOUNTPOINT.equals(type)) {
			throw new RuntimeException(
					"Constructor not usable for mountpoints...");
		} else if (FILETYPE_ROOT.equals(type)) {
			this.type = FILETYPE_ROOT;
			this.mainUrl = ROOT;
			addUrl(mainUrl, Integer.MAX_VALUE);
			this.size = -1L;
			this.lastModified = -1L;
			this.name = ROOT;
		} else {
			throw new RuntimeException("Type: " + type + " not supported...");
		}
	}

	public void addChild(GridFile child) {
		children.add(child);
	}

	public void addChildren(Set<GridFile> children) {
		getChildren().addAll(children);
	}

	public void addFqan(String fqan) {
		this.fqans.add(fqan);
	}

	public void addFqans(Set<String> fqans) {
		this.fqans.addAll(fqans);
	}

	public void addSite(String site) {
		getSites().add(site);
	}

	public void addSites(Set<String> sites) {
		getSites().addAll(sites);
	}

	public void addUrl(String url, Integer priority) {
		DtoProperty temp = new DtoProperty(url, priority.toString());
		urls.add(temp);
	}

	public int compareTo(Object ot) {

		int result = Integer.MIN_VALUE;
		if (ot instanceof GridFile) {
			GridFile o = (GridFile) ot;

			Integer thisPriority = Integer.parseInt(urls.get(0).getValue());
			Integer otherPriority = Integer.parseInt(o.getUrls().get(0)
					.getValue());

			if (thisPriority.equals(otherPriority)) {
				result = getName().compareTo(o.getName());

				if (result == 0) {
					result = getUrl().compareTo(o.getUrl());
				}
			} else {
				result = thisPriority.compareTo(otherPriority);
			}
		} else {
			result = -1;
		}

		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof GridFile) {
			boolean eq = getUrl().equals(((GridFile) other).getUrl());
			return eq;
		}
		return false;
	}

	@XmlElement(name = "child")
	public Set<GridFile> getChildren() {
		return children;
	}

	@XmlAttribute(name = "fqan")
	public Set<String> getFqans() {
		return this.fqans;
	}

	@XmlAttribute(name = "lastModified")
	public long getLastModified() {
		return lastModified;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "path")
	public String getPath() {
		return optionalPath;
	}

	@XmlElement(name = "site")
	public Set<String> getSites() {
		return this.sites;
	}

	@XmlAttribute(name = "size")
	public long getSize() {
		return size;
	}

	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	@XmlElement(name = "url")
	public String getUrl() {
		return mainUrl;
	}

	@XmlElement(name = "urls")
	public List<DtoProperty> getUrls() {
		return urls;
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}

	public boolean isFolder() {
		if (FILETYPE_FILE.equals(getType())) {
			return false;
		} else {
			return true;
		}
	}

	@XmlAttribute(name = "isVirtual")
	public boolean isVirtual() {
		return isVirtual;
	}

	public List<String> listOfAllFilesUnderThisFolder() {

		final List<String> result = new LinkedList<String>();

		for (final GridFile child : getChildren()) {
			if (child.isFolder()) {
				result.addAll(child.listOfAllFilesUnderThisFolder());
			} else {
				result.add(child.getUrl());
			}
		}

		return result;
	}

	public void removeFqan(String fqan) {
		this.fqans.remove(fqan);
	}

	public void removeFqans(Set<String> fqans) {
		this.fqans.removeAll(fqans);
	}

	public void removeSite(String site) {
		getSites().remove(site);
	}

	public void removeSites(Set<String> sites) {
		getSites().removeAll(sites);
	}

	public void setChildren(Set<GridFile> children) {
		this.children = children;
	}

	private void setFqans(Set<String> fqans) {
		this.fqans = fqans;
	}

	public void setIsVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.optionalPath = path;
	}

	private void setSites(Set<String> sites) {
		this.sites = sites;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.mainUrl = url;
		addUrl(url, 0);
	}

	public void setUrls(List<DtoProperty> urls) {
		this.urls = urls;
	}
}
