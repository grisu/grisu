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
public class DtoFileObject implements Comparable {

	public static final String ROOT = "FileRoot";

	public static final String FILETYPE_FILE = "file";
	public static final int FILETYPE_FILE_PRIORITY = Integer.MIN_VALUE;
	public static final String FILETYPE_FOLDER = "folder";
	public static final int FILETYPE_FOLDER_PRIORITY = Integer.MIN_VALUE + 1;
	public static final String FILETYPE_ROOT = "root";
	public static final int FILETYPE_ROOT_PRIORITY = Integer.MAX_VALUE;
	public static final String FILETYPE_MOUNTPOINT = "mountpoint";
	public static final int FILETYPE_MOUNTPOINT_PRIORITY = Integer.MAX_VALUE - 1;
	public static final String FILETYPE_GROUP = "group";
	public static final int FILETYPE_GROUP_PRIORITY = Integer.MAX_VALUE - 2;

	public static DtoFileObject listLocalFolder(File folder,
			boolean includeParentInFileListing) {

		String url = folder.toURI().toString();
		final DtoFileObject result = new DtoFileObject(url,
				folder.lastModified());

		if (includeParentInFileListing) {
			final DtoFileObject childFolder = new DtoFileObject(folder.toURI()
					.toString());
			result.addChild(childFolder);
		}

		for (final File child : folder.listFiles()) {

			if (child.isDirectory()) {

				final DtoFileObject childFolder = new DtoFileObject(child
						.toURI().toString(), child.lastModified());
				result.addChild(childFolder);

			} else if (child.isFile()) {

				final DtoFileObject childFile = new DtoFileObject(child.toURI()
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

	private List<DtoProperty> urls = new LinkedList<DtoProperty>();
	private long size = -2L;
	private long lastModified;
	private Set<DtoFileObject> children = new TreeSet<DtoFileObject>();

	public DtoFileObject() {

		this.type = FILETYPE_ROOT;
		this.mainUrl = ROOT;
		addUrl(mainUrl, Integer.MAX_VALUE);
		this.size = -1L;
		this.lastModified = -1L;
		this.name = ROOT;
	}

	public DtoFileObject(MountPoint mp) {

		this.type = FILETYPE_MOUNTPOINT;
		this.mainUrl = mp.getRootUrl();
		addUrl(mainUrl, FILETYPE_MOUNTPOINT_PRIORITY);
		this.size = -1L;
		this.lastModified = -1L;
		this.name = mp.getAlias();
	}

	public DtoFileObject(String group) {
		this.type = FILETYPE_GROUP;
		this.mainUrl = "/groups/" + group;
		addUrl(mainUrl, FILETYPE_GROUP_PRIORITY);
		this.size = -1L;
		this.lastModified = -1L;
		this.name = group;
	}

	public DtoFileObject(String url, long lastModified) {
		this.type = FILETYPE_FOLDER;
		this.mainUrl = url;
		addUrl(mainUrl, FILETYPE_FOLDER_PRIORITY);
		this.size = -1L;
		this.lastModified = lastModified;
		this.name = FileManager.getFilename(url);
	}

	public DtoFileObject(String url, long size, long lastModified) {
		this.type = FILETYPE_FILE;
		this.mainUrl = url;
		addUrl(mainUrl, FILETYPE_FILE_PRIORITY);
		this.size = size;
		this.lastModified = lastModified;
		this.name = FileManager.getFilename(url);
	}

	public void addChild(DtoFileObject child) {
		children.add(child);
	}

	public void addChildren(Set<DtoFileObject> children) {
		getChildren().addAll(children);
	}

	public void addUrl(String url, Integer priority) {
		DtoProperty temp = new DtoProperty(url, priority.toString());
		urls.add(temp);
	}

	public int compareTo(Object ot) {

		int result = Integer.MIN_VALUE;
		if (ot instanceof DtoFileObject) {
			DtoFileObject o = (DtoFileObject) ot;

			Integer thisPriority = Integer.parseInt(urls.get(0).getValue());
			Integer otherPriority = Integer.parseInt(o.getUrls().get(0)
					.getValue());

			if (thisPriority.equals(otherPriority)) {
				result = getName().compareTo(o.getName());
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
		if (other instanceof DtoFileObject) {
			boolean eq = getMainUrl().equals(
					((DtoFileObject) other).getMainUrl());
			return eq;
		}
		return false;
	}

	@XmlElement(name = "child")
	public Set<DtoFileObject> getChildren() {
		return children;
	}

	@XmlElement(name = "lastModified")
	public long getLastModified() {
		return lastModified;
	}

	@XmlAttribute(name = "mainUrl")
	public String getMainUrl() {
		return mainUrl;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "size")
	public long getSize() {
		return size;
	}

	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	@XmlElement(name = "urls")
	public List<DtoProperty> getUrls() {
		return urls;
	}

	@Override
	public int hashCode() {
		return getMainUrl().hashCode();
	}

	public boolean isFolder() {
		if (FILETYPE_FILE.equals(getType())) {
			return false;
		} else {
			return true;
		}
	}

	public List<String> listOfAllFilesUnderThisFolder() {

		final List<String> result = new LinkedList<String>();

		for (final DtoFileObject child : getChildren()) {
			if (child.isFolder()) {
				result.addAll(child.listOfAllFilesUnderThisFolder());
			} else {
				result.add(child.getMainUrl());
			}
		}

		return result;
	}

	public void setChildren(Set<DtoFileObject> children) {
		this.children = children;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	private void setMainUrl(String url) {
		this.mainUrl = url;
		addUrl(url, 0);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrls(List<DtoProperty> urls) {
		this.urls = urls;
	}
}
