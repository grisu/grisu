package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper that contains information about a remote file.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "file")
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

	@XmlElement(name = "lastModified")
	public long getLastModified() {
		return lastModified;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlAttribute(name = "url")
	public String getRootUrl() {
		return rootUrl;
	}

	@XmlElement(name = "size")
	public long getSize() {
		return size;
	}

	@XmlAttribute(name = "isFolder")
	public boolean isFolder() {
		return false;
	}

	public void setFolder(boolean dummy) {

	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
