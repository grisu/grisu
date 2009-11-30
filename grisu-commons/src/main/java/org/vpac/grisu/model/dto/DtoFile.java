package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper that contains information about a remote file.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="file")
public class DtoFile implements DtoRemoteObject {
	
	/**
	 * The absolute url to this remote file.
	 */
	private String rootUrl;
	/**
	 * The basename of this file.
	 */
	private String name;
	
	/**
	 * The size of this file in bytes. 
	 */
	private long size;
	/**
	 * The last-modified timestamp of this file.
	 */
	private long lastModified;
	
	@XmlAttribute(name="url")
	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	@XmlElement(name="size")
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@XmlElement(name="lastModified")
	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="isFolder")
	public boolean isFolder() {
		return false;
	}
	
	public void setFolder(boolean dummy) {
		
	}

}
