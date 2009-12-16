package org.vpac.grisu.model.dto;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper that contains information about one remote folder.
 * 
 * It has the absolute url to this folder, the basename and two lists of
 * children folders and children files.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "folder")
public class DtoFolder implements DtoRemoteObject {

	public static DtoFolder listLocalFolder(File folder,
			boolean includeParentInFileListing) {

		DtoFolder result = new DtoFolder();
		result.setRootUrl(folder.toURI().toString());
		result.setFolder(true);
		result.setName(folder.getName());

		if (includeParentInFileListing) {
			DtoFolder childFolder = new DtoFolder();
			childFolder.setFolder(true);
			childFolder.setName(folder.getName());
			childFolder.setRootUrl(folder.toURI().toString());
		}

		for (File child : folder.listFiles()) {

			if (child.isDirectory()) {

				DtoFolder childFolder = new DtoFolder();
				childFolder.setFolder(true);
				childFolder.setName(child.getName());
				childFolder.setRootUrl(child.toURI().toString());

				result.getChildrenFolders().add(childFolder);

			} else if (child.isFile()) {

				DtoFile childFile = new DtoFile();
				childFile.setFolder(false);
				childFile.setName(child.getName());
				childFile.setRootUrl(child.toURI().toString());
				try {
					childFile.setLastModified(child.lastModified());
					childFile.setSize(child.length());
				} catch (Exception e) {
					// no idea why I need to catch this. Without it it seems to
					// stall...
					e.printStackTrace();
				}

				result.getChildrenFiles().add(childFile);

			} else {
				System.out.println("Can't determine type of file: "
						+ child.getPath());
				// throw new
				// RuntimeException("Can't determine type of file: "+child.getPath());
			}

		}

		return result;
	}

	/**
	 * The absolute url to this folder.
	 */
	private String rootUrl;
	/**
	 * The basename of this folder.
	 */
	private String name;
	/**
	 * A list of children folders of this folder.
	 */
	private List<DtoFolder> childrenFolders = new LinkedList<DtoFolder>();
	/**
	 * A list of children files of this folder.
	 */
	private List<DtoFile> childrenFiles = new LinkedList<DtoFile>();

	public void addChildFile(DtoFile child) {
		childrenFiles.add(child);
	}

	public void addChildFolder(DtoFolder child) {
		childrenFolders.add(child);
	}

	@XmlElement(name = "file")
	public List<DtoFile> getChildrenFiles() {
		return childrenFiles;
	}

	@XmlElement(name = "folder")
	public List<DtoFolder> getChildrenFolders() {
		return childrenFolders;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlAttribute(name = "url")
	public String getRootUrl() {
		return rootUrl;
	}

	@XmlAttribute(name = "isFolder")
	public boolean isFolder() {
		return true;
	}

	public List<DtoRemoteObject> listAllChildren() {

		List<DtoRemoteObject> result = new LinkedList<DtoRemoteObject>();

		for (DtoFolder folder : getChildrenFolders()) {
			result.add(folder);
		}
		for (DtoFile file : getChildrenFiles()) {
			result.add(file);
		}
		return result;
	}

	public List<String> listOfAllFilesUnderThisFolder() {

		List<String> result = new LinkedList<String>();

		for (DtoFolder childFolder : getChildrenFolders()) {
			result.addAll(childFolder.listOfAllFilesUnderThisFolder());
		}
		for (DtoFile childFile : getChildrenFiles()) {
			result.add(childFile.getRootUrl());
		}
		return result;
	}

	public void setChildrenFiles(List<DtoFile> childrenFiles) {
		this.childrenFiles = childrenFiles;
	}

	public void setChildrenFolders(List<DtoFolder> childrenFolders) {
		this.childrenFolders = childrenFolders;
	}

	public void setFolder(boolean dummy) {
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRootUrl(String url) {
		this.rootUrl = url;
	}

}
