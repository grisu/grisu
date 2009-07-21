package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoFolder implements DtoRemoteObject {

	@XmlAttribute(name="url")
	private String rootUrl;
	@XmlAttribute(name="name")
	private String name;
	@XmlElement(name="folder")
	private List<DtoFolder> childrenFolders = new LinkedList<DtoFolder>();
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
