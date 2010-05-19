package org.vpac.grisu.model.files;

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
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.model.dto.DtoFolder;
import org.vpac.grisu.model.dto.DtoRemoteObject;

public class GlazedFile implements Comparable<GlazedFile>, Transferable {

	public enum Type implements Comparable<Type> {

		FILETYPE_ROOT, FILETYPE_SITE, FILETYPE_MOUNTPOINT, FILETYPE_FOLDER, FILETYPE_FILE

	}

	public static final DataFlavor GLAZED_FILE_FLAVOR = new DataFlavor(
			GlazedFile.class, "Grisu file");

	public static final DataFlavor[] DATA_FLAVORS = new DataFlavor[] {
			GLAZED_FILE_FLAVOR, DataFlavor.stringFlavor };

	public static final String LOCAL_FILESYSTEM = "Local";

	public static final String ROOT = "FileRoot";

	public static String calculateSizeString(Long size) {

		String sizeString;

		if (size.equals(-1L)) {
			sizeString = "";
		} else {

			if (size > 1024 * 1024) {
				sizeString = size / (1024 * 1024) + "MB";
			} else if (size > 1024) {
				sizeString = size / 1024 + " KB";
			} else {
				sizeString = size + " B";
			}
		}

		return sizeString;

	}

	public static String calculateTimeStampString(Long timestamp) {

		if (timestamp.equals(-1L)) {
			return "";
		} else {
			Date date = new Date(timestamp);
			return date.toString();
		}

	}

	public static Set<String> extractUrls(Set<GlazedFile> files) {

		Set<String> temp = new HashSet<String>();
		for (GlazedFile source : files) {
			temp.add(source.getUrl());
		}
		return temp;
	}

	private Type type;

	private final DtoFile file;
	private final DtoFolder folder;

	private final String name;

	private final String url;
	private final long size;
	private final long lastModified;
	private final ServiceInterface si;

	private boolean parentMarker = false;

	public GlazedFile() {
		this.type = Type.FILETYPE_ROOT;
		this.file = null;
		this.folder = null;
		this.url = ROOT;
		this.size = -1L;
		this.lastModified = -1L;
		this.name = ROOT;
		this.si = null;
	}

	public GlazedFile(DtoRemoteObject obj) {

		if (obj.isFolder()) {
			folder = (DtoFolder) obj;
			file = null;
			type = Type.FILETYPE_FOLDER;
			size = -1L;
			lastModified = -1L;
		} else {
			folder = null;
			file = (DtoFile) obj;
			type = Type.FILETYPE_FILE;
			size = file.getSize();
			lastModified = file.getLastModified();
		}

		url = obj.getRootUrl();
		if (StringUtils.isNotBlank(obj.getName())) {
			name = obj.getName();
		} else {
			name = "/";
		}
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

		this.file = null;
		this.folder = null;
		this.url = localFile.toURI().toString();

		this.lastModified = localFile.lastModified();
		this.name = localFile.getName();
		this.si = null;

	}

	public GlazedFile(MountPoint mp) {
		this.type = Type.FILETYPE_MOUNTPOINT;
		this.file = null;
		this.folder = null;
		this.url = mp.getRootUrl();
		this.size = -1L;
		this.lastModified = -1L;
		this.name = mp.getAlias();
		this.si = null;
	}

	public GlazedFile(String sitename) {

		this.type = Type.FILETYPE_SITE;
		this.file = null;
		this.folder = null;
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
		this.folder = null;
		this.file = null;
		this.size = -1L;
		this.lastModified = -1L;
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
		this.folder = null;
		this.file = null;
		this.size = -1L;
		this.lastModified = -1L;
	}

	public int compareTo(GlazedFile o) {

		return this.getName().compareTo(o.getName());

	}

	@Override
	public boolean equals(Object other) {

		if (other instanceof GlazedFile) {
			String url = ((GlazedFile) other).getUrl();
			return this.getUrl().equals(url);
		}
		return false;

	}

	public long getLastModified() {

		if (si != null) {
			try {
				long result = si.lastModified(url);
			} catch (RemoteFileSystemException e) {
				return -1L;
			}
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

		if (si != null) {
			try {
				long result = si.getFileSize(url);
			} catch (RemoteFileSystemException e) {
				return -1L;
			}
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
				File file = new File(getUrl());
				if (file.isDirectory()) {
					type = Type.FILETYPE_FOLDER;
				} else if (file.exists()) {
					type = Type.FILETYPE_FILE;
				}
			} else {

				if (si != null) {
					if (isFolder()) {
						type = Type.FILETYPE_FOLDER;
					} else {
						try {
							if (si.fileExists(url)) {
								type = Type.FILETYPE_FILE;
							}
						} catch (RemoteFileSystemException e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
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

		if (si != null) {
			try {
				return si.isFolder(this.url);
			} catch (RemoteFileSystemException e) {
				e.printStackTrace();
				return false;
			}
		} else {

			if (Type.FILETYPE_FILE.equals(type)) {
				return false;
			} else {
				return true;
			}
		}
	}

	public boolean isMarkedAsParent() {
		return this.parentMarker;
	}

	public void setParent() {
		this.parentMarker = true;
	}

	@Override
	public String toString() {
		return getName();
	}

}
