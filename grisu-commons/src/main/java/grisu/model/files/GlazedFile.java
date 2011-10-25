package grisu.model.files;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.MountPoint;
import grisu.model.dto.GridFile;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlazedFile implements Comparable<GlazedFile>, Transferable {

	public enum Type implements Comparable<Type> {

		FILETYPE_ROOT,
		FILETYPE_SITE,
		FILETYPE_MOUNTPOINT,
		FILETYPE_FOLDER,
		FILETYPE_FILE
	}

	static final Logger myLogger = LoggerFactory.getLogger(GlazedFile.class);

	public static final DataFlavor GLAZED_FILE_FLAVOR = new DataFlavor(
			GlazedFile.class, "Grisu file");

	public static final DataFlavor[] DATA_FLAVORS = new DataFlavor[] {
			GLAZED_FILE_FLAVOR, DataFlavor.stringFlavor };

	public static final String LOCAL_FILESYSTEM = "Local";

	public static String calculateTimeStampString(Long timestamp) {

		if (timestamp.equals(-1L)) {
			return "";
		} else {
			final Date date = new Date(timestamp);
			return date.toString();
		}

	}

	public static Set<String> extractUrls(Set<GlazedFile> files) {

		final Set<String> temp = new HashSet<String>();
		for (final GlazedFile source : files) {
			temp.add(source.getUrl());
		}
		return temp;
	}

	private Type type;

	// private final DtoFile file;

	private final String name;

	private final String url;
	private long size = -2L;
	private long lastModified;
	private final ServiceInterface si;

	private boolean parentMarker = false;

	public GlazedFile() {
		this.type = Type.FILETYPE_ROOT;
		this.url = GridFile.ROOT;
		this.size = -1L;
		this.lastModified = -1L;
		this.name = GridFile.ROOT;
		this.si = null;
	}

	public GlazedFile(File localFile) {

		if (localFile.isDirectory()) {
			this.type = Type.FILETYPE_FOLDER;
			this.size = -1L;
		} else {
			this.type = Type.FILETYPE_FILE;
			this.size = localFile.length();
		}

		this.url = localFile.toURI().toString();

		this.lastModified = localFile.lastModified();
		this.name = localFile.getName();
		this.si = null;

	}

	public GlazedFile(GridFile obj) {
		if (obj.isFolder()) {
			type = Type.FILETYPE_FOLDER;
			size = -1L;
			lastModified = obj.getLastModified();
		} else {
			type = Type.FILETYPE_FILE;
			size = obj.getSize();
			lastModified = obj.getLastModified();
		}

		url = obj.getUrl();
		if (StringUtils.isNotBlank(obj.getName())) {
			name = obj.getName();
		} else {
			name = "/";
		}
		this.si = null;
	}

	public GlazedFile(MountPoint mp) {
		this.type = Type.FILETYPE_MOUNTPOINT;
		this.url = mp.getRootUrl();
		this.size = -1L;
		this.lastModified = -1L;
		this.name = mp.getAlias();
		this.si = null;
	}

	public GlazedFile(String sitename) {

		this.type = Type.FILETYPE_SITE;
		this.url = sitename;
		this.size = -1L;
		this.lastModified = -1L;
		this.name = sitename;
		this.si = null;

	}

	public GlazedFile(String url, ServiceInterface si) {

		this.url = url;
		if (StringUtils.isNotBlank(FileManager.getFilename(url))) {
			this.name = FileManager.getFilename(url);
		} else {
			this.name = "/";
		}

		this.si = si;
		this.type = null;
		this.size = -2L;
		this.lastModified = -2L;
	}

	/**
	 * Use this constructor if you know the type of the file. That saves
	 * potentially having to connect the backend to find out the type.
	 * 
	 * @param url
	 * @param si
	 * @param type
	 */
	public GlazedFile(String url, ServiceInterface si, Type type) {
		this.url = url;
		if (StringUtils.isNotBlank(FileManager.getFilename(url))) {
			this.name = FileManager.getFilename(url);
		} else {
			this.name = "/";
		}

		this.si = si;
		this.type = type;
		this.size = -1L;
		this.lastModified = -1L;
	}

	public int compareTo(GlazedFile o) {

		return this.getName().compareTo(o.getName());

	}

	@Override
	public boolean equals(Object other) {

		if (other instanceof GlazedFile) {
			final String url = ((GlazedFile) other).getUrl();
			return this.getUrl().equals(url);
		}
		return false;

	}

	public long getLastModified() {

		if (lastModified > -2L) {
			return lastModified;
		}

		if (si == null) {
			lastModified = -2L;
			return lastModified;
		}

		try {
			lastModified = si.lastModified(url);
		} catch (final RemoteFileSystemException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			lastModified = -1L;
		}

		return lastModified;
	}

	public String getName() {
		return name;
	}

	public String getNameWithoutExtension() {
		return FilenameUtils.getBaseName(getName());
	}

	public long getSize() {

		if (size > -2L) {
			return size;
		}

		if (si == null) {
			size = -2L;
			return size;
		}
		if (Type.FILETYPE_FILE.equals(getType())) {

			if (FileManager.isLocal(getUrl())) {
				GrisuRegistryManager.getDefault(si).getFileManager();
				final File temp = FileManager.getFileFromUriOrPath(getUrl());
				size = temp.length();
			} else {

				try {
					size = si.getFileSize(getUrl());
				} catch (final RemoteFileSystemException e) {
					myLogger.error(e.getLocalizedMessage(), e);
					size = -1;
				}
			}

		} else {
			size = -1L;
		}
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

	public Type getType() {

		if (type == null) {

			if (FileManager.isLocal(getUrl())) {
				final File file = new File(getUrl());
				if (file.isDirectory()) {
					type = Type.FILETYPE_FOLDER;
				} else if (file.exists()) {
					type = Type.FILETYPE_FILE;
				}
			} else {

				if (si != null) {

					try {
						if (si.isFolder(url)) {
							type = Type.FILETYPE_FOLDER;
						} else {
							try {
								if (si.fileExists(url)) {
									type = Type.FILETYPE_FILE;
								}
							} catch (final RemoteFileSystemException e) {
								throw new RuntimeException(e);
							}
						}
					} catch (final RemoteFileSystemException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		return type;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {

		return 23 * getUrl().hashCode();

	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {

		if (Arrays.binarySearch(getTransferDataFlavors(), flavor) >= 0) {
			return true;
		} else {
			return false;
		}

	}

	public boolean isFolder() {

		if (Type.FILETYPE_FILE.equals(getType())) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isMarkedAsParent() {
		return this.parentMarker;
	}

	public void refresh() {
		this.size = -2L;
		this.lastModified = -2L;
	}

	public void setParent() {
		this.parentMarker = true;
	}

	@Override
	public String toString() {
		return getName();
	}

}
