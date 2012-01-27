package grisu.model.dto;

import grisu.control.ServiceInterface;
import grisu.model.FileManager;
import grisu.model.MountPoint;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "gridfile")
public class GridFile implements Comparable<GridFile>, Transferable {

	static final Logger myLogger = LoggerFactory.getLogger(GridFile.class
			.getName());

	public static final DataFlavor GRIDFILE_FLAVOR = new DataFlavor(
			GridFile.class, "Grid file");

	public static final DataFlavor[] DATA_FLAVORS = new DataFlavor[] {
		GRIDFILE_FLAVOR, DataFlavor.stringFlavor };

	public static final String ROOT = "FileRoot";

	public static final String FILETYPE_FILE = "file";
	public static final int FILETYPE_FILE_PRIORITY = Integer.MAX_VALUE;
	public static final String FILETYPE_FOLDER = "folder";
	public static final int FILETYPE_FOLDER_PRIORITY = 0;
	public static final String FILETYPE_ROOT = "root";
	public static final int FILETYPE_ROOT_PRIORITY = Integer.MIN_VALUE;
	public static final String FILETYPE_MOUNTPOINT = "mountpoint";
	public static final int FILETYPE_MOUNTPOINT_PRIORITY = Integer.MIN_VALUE + 3;
	public static final int FILETYPE_VIRTUAL_PRIORITY = Integer.MIN_VALUE + 2;

	private static final int FILETYPE_EXCEPTION_PRIORITY = Integer.MIN_VALUE + 1;

	// public static final String FILETYPE_GROUP = "group";
	// public static final int FILETYPE_GROUP_PRIORITY = Integer.MIN_VALUE + 2;

	public static Set<String> extractUrls(Set<GridFile> files) {

		final Set<String> temp = new HashSet<String>();
		for (final GridFile source : files) {
			temp.add(source.getUrl());
		}
		return temp;
	}

	public static List<String> getChildrenNames(GridFile file) {

		if (file == null) {
			return null;
		}

		final List<String> result = new LinkedList<String>();
		for (final GridFile f : file.getChildren()) {
			result.add(f.getName());
		}
		return result;
	}

	public static GridFile listLocal(File file_or_folder,
			boolean includeParentInFileListing) {

		if (!file_or_folder.isDirectory()) {
			final GridFile file = new GridFile(file_or_folder.toURI()
					.toString(), file_or_folder.length(),
					file_or_folder.lastModified());
			return file;
		}

		final String url = file_or_folder.toURI().toString();
		final GridFile result = new GridFile(url, file_or_folder.lastModified());

		if (includeParentInFileListing) {
			final GridFile childFolder = new GridFile(file_or_folder.toURI()
					.toString(), file_or_folder.lastModified());
			result.addChild(childFolder);
		}

		for (final File child : file_or_folder.listFiles()) {

			if (child.isDirectory()) {

				final GridFile childFolder = new GridFile(child.toURI()
						.toString(), child.lastModified());
				result.addChild(childFolder);

			} else if (child.isFile()) {

				final GridFile childFile = new GridFile(child.toURI()
						.toString(), child.length(), child.lastModified());

				result.addChild(childFile);

			} else {
				myLogger.error("Can't determine type of file: "
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
	private String comment;
	private final Set<String> sites = Collections
			.synchronizedSet(new TreeSet<String>());

	private final Set<String> fqans = Collections
			.synchronizedSet(new TreeSet<String>());
	private final List<DtoProperty> urls = Collections
			.synchronizedList(new LinkedList<DtoProperty>());
	private long size = -2L;
	private long lastModified;
	private Boolean isVirtual = false;

	private Boolean inaccessable = false;

	private Set<GridFile> children = Collections
			.synchronizedSet(new TreeSet<GridFile>());

	public GridFile() {
		// this(null, -1L, -1L, FILETYPE_ROOT);
		// this.isVirtual = true;
		// this.setName("Grid");
		// this.setUrl(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://");
	}

	public GridFile(File f) {
		this(f, ((f.isDirectory()) ? FILETYPE_FOLDER_PRIORITY
				: FILETYPE_FILE_PRIORITY));

	}

	public GridFile(File f, int priority) {

		if (f.isDirectory()) {
			this.type = FILETYPE_FOLDER;
			this.size = -1L;
		} else {
			this.type = FILETYPE_FILE;
			this.size = f.length();
		}
		this.isVirtual = false;
		if (StringUtils.isBlank(f.getName())) {
			this.name = "/";
		} else {
			this.name = f.getName();
		}
		this.mainUrl = f.toURI().toString();
		addUrl(this.mainUrl, priority);
		this.lastModified = f.lastModified();
		addSite("Local");
	}

	public GridFile(MountPoint mp) {

		this.type = FILETYPE_MOUNTPOINT;
		this.mainUrl = mp.getRootUrl();
		addUrl(mainUrl, FILETYPE_MOUNTPOINT_PRIORITY);
		this.size = -1L;
		this.lastModified = -1L;
		this.name = mp.getAlias();
		this.addSite(mp.getSite());
		this.addFqan(mp.getFqan());
		this.isVirtual = false;
	}

	// public DtoFileObject(String group) {
	// this(group, -1L, -1L, FILETYPE_GROUP);
	// }

	/**
	 * Creates a folder with no lastModified time
	 * 
	 * @param url
	 */
	public GridFile(String url) {
		this(url, -1L);
	}

	public GridFile(String url, boolean isFile, Exception e) {
		this.mainUrl = url;
		if (isFile) {
			this.type = FILETYPE_FILE;
		} else {
			this.type = FILETYPE_FOLDER;
		}
		addUrl(this.mainUrl, FILETYPE_EXCEPTION_PRIORITY);

		this.inaccessable = true;
		this.isVirtual = true;
		this.name = FileManager.getFilename(url) + " (Error)";
		this.lastModified = -1L;
		this.size = -1L;
		this.comment = e.getLocalizedMessage();
	}

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
			if ((ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://")
					.equals(url)) {
				this.name = "Remote files";
				this.isVirtual = true;
			} else {
				this.name = FileManager.getFilename(url);
				this.isVirtual = false;
			}

		} else if (FILETYPE_FILE.equals(type)) {
			this.type = FILETYPE_FILE;
			this.mainUrl = url;
			addUrl(mainUrl, FILETYPE_FILE_PRIORITY);
			this.size = size;
			this.lastModified = lastModified;
			this.name = FileManager.getFilename(url);
			this.isVirtual = false;
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
			this.isVirtual = true;
		} else {
			throw new RuntimeException("Type: " + type + " not supported...");
		}
	}

	public GridFile(String url, String fqan) {
		this.type = FILETYPE_FOLDER;
		this.isVirtual = true;
		this.mainUrl = url;
		addUrl(this.mainUrl, FILETYPE_VIRTUAL_PRIORITY);
		this.size = -1L;
		this.lastModified = -1L;
		this.addFqan(fqan);
		this.setPath(url);
		this.name = FileManager.getFilename(url);
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
		if (fqans != null) {
			this.fqans.addAll(fqans);
		}
	}

	public void addSite(String site) {
		if (site != null) {
			getSites().add(site);
		}
	}

	public void addSites(Set<String> sites) {
		if (sites != null) {
			getSites().addAll(sites);
		}
	}

	public void addUrl(String url, Integer priority) {
		final DtoProperty temp = new DtoProperty(url, priority.toString());
		urls.add(temp);
		if (urls.size() > 1) {
			this.isVirtual = true;
		}
	}

	public int compareTo(GridFile o) {

		int result = Integer.MIN_VALUE;

		final Integer thisPriority = Integer.parseInt(urls.get(0).getValue());
		final Integer otherPriority = Integer.parseInt(o.getUrls().get(0)
				.getValue());

		if (otherPriority.equals(thisPriority)) {
			result = getName().compareToIgnoreCase(o.getName());

			if (result == 0) {
				result = getUrl().compareTo(o.getUrl());
			}

		} else {
			result = thisPriority.compareTo(otherPriority);
		}

		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof GridFile) {
			final boolean eq = getUrl().equals(((GridFile) other).getUrl());
			return eq;
		}
		return false;
	}

	public GridFile getChild(String filename) {
		if (filename == null) {
			return null;
		}

		for (final GridFile f : getChildren()) {
			if (f.getName().equals(filename)) {
				return f;
			}
		}
		return null;
	}

	@XmlElement(name = "child")
	public Set<GridFile> getChildren() {
		return children;
	}

	@XmlElement(name = "comment")
	public String getComment() {
		return this.comment;
	}

	@XmlAttribute(name = "fqan")
	public Set<String> getFqans() {
		return this.fqans;
	}

	// for jaxb marshalling
	@XmlAttribute(name = "isInaccessable")
	public Boolean getInaccessable() {
		return this.inaccessable;
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

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (DataFlavor.stringFlavor.equals(flavor)) {
			return getName();
		}
		throw new UnsupportedFlavorException(flavor);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return DATA_FLAVORS;
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

	// for jaxb marshalling
	@XmlAttribute(name = "isVirtual")
	public Boolean getVirtual() {
		return isVirtual;
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (Arrays.binarySearch(getTransferDataFlavors(), flavor) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isFolder() {
		if (FILETYPE_FILE.equals(getType())) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isInaccessable() {
		return this.inaccessable;
	}

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
		if (children == null) {
			this.children.clear();
			return;
		}
		this.children = children;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	private void setFqans(Set<String> fqans) {
		this.fqans.clear();
		this.fqans.addAll(fqans);
	}

	public void setInaccessible(Boolean isInaccessible) {
		this.inaccessable = isInaccessible;
	}

	// public void setIsInaccessible(Boolean isInaccessible) {
	// this.inaccessable = isInaccessible;
	// }

	// public void setIsVirtual(boolean isVirtual) {
	// this.isVirtual = isVirtual;
	// }

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.optionalPath = path;
		if (StringUtils.isNotBlank(path)) {
			this.isVirtual = true;
		}
	}

	private void setSites(Set<String> sites) {
		this.sites.clear();
		this.sites.addAll(sites);
		if ((sites != null) && (sites.size() > 1)) {
			this.isVirtual = true;
		}
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
		this.urls.clear();
		this.urls.addAll(urls);
		if ((urls != null) && (urls.size() > 1)) {
			this.isVirtual = true;
		}
	}

	public void setVirtual(Boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	@Override
	public String toString() {
		return getName();
	}
}
