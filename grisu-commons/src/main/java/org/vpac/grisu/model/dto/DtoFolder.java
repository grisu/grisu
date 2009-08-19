package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper that contains information about one remote folder.
 * 
 * It has the absolute url to this folder, the basename and two lists of children folders and children files.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="folder")
public class DtoFolder implements DtoRemoteObject {

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
	
	
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}
	
	@XmlAttribute(name="url")
	public String getRootUrl() {
		return rootUrl;
	}
	
	@XmlElement(name="file")
	public List<DtoFile> getChildrenFiles() {
		return childrenFiles;
	}
	
	@XmlElement(name="folder")
	public List<DtoFolder> getChildrenFolders() {
		return childrenFolders;
	}
	
	public void setChildrenFolders(List<DtoFolder> childrenFolders) {
		this.childrenFolders = childrenFolders;
	}

	public void setChildrenFiles(List<DtoFile> childrenFiles) {
		this.childrenFiles = childrenFiles;
	}

	public void setRootUrl(String url) {
		this.rootUrl = url;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addChildFolder(DtoFolder child) {
		childrenFolders.add(child);
	}
	
	public void addChildFile(DtoFile child) {
		childrenFiles.add(child);
	}

	@XmlAttribute(name="isFolder")
	public boolean isFolder() {
		return true;
	}
	
	public void setFolder(boolean dummy) {
	}


	public List<DtoRemoteObject> listAllChildren() {
		
		List<DtoRemoteObject> result = new LinkedList<DtoRemoteObject>();
		
		for ( DtoFolder folder : getChildrenFolders() ) {
			result.add(folder);
		}
		for ( DtoFile file : getChildrenFiles() ) {
			result.add(file);
		}
		return result;
	}
	
	public List<String> listOfAllFilesUnderThisFolder() {
		
		List<String> result = new LinkedList<String>();
		
		for ( DtoFolder childFolder : getChildrenFolders() ) {
			result.addAll(childFolder.listOfAllFilesUnderThisFolder());
		}
		for ( DtoFile childFile : getChildrenFiles() ) {
			result.add(childFile.getRootUrl());
		}
		return result;
	}
	
	
}
