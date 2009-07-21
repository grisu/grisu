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
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoFolder implements DtoRemoteObject {

	/**
	 * The absolute url to this folder.
	 */
	@XmlAttribute(name="url")
	private String rootUrl;
	/**
	 * The basename of this folder.
	 */
	@XmlAttribute(name="name")
	private String name;
	/**
	 * A list of children folders of this folder.
	 */
	@XmlElement(name="folder")
	private List<DtoFolder> childrenFolders = new LinkedList<DtoFolder>();
	/**
	 * A list of children files of this folder.
	 */
	@XmlElement(name="file")
	private List<DtoFile> childrenFiles = new LinkedList<DtoFile>();
	
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return rootUrl;
	}
	
	public List<DtoFile> getChildrenFiles() {
		return childrenFiles;
	}
	
	public List<DtoFolder> getChildrenFolder() {
		return childrenFolders;
	}
	
	public void setUrl(String url) {
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
	
	
}
