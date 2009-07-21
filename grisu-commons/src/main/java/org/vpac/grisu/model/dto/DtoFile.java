package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoFile implements DtoRemoteObject {
	
	/**
	 * The absolute url to this remote file.
	 */
	@XmlAttribute(name="url")
	private String rootUrl;
	/**
	 * The basename of this file.
	 */
	@XmlAttribute(name="name")
	private String name;
	
	/**
	 * The size of this file in bytes. 
	 */
	@XmlElement(name="size")
	private long size;
	/**
	 * The last-modified timestamp of this file.
	 */
	@XmlElement(name="lastModified")
	private long lastModified;
	
	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getUrl() {
		return rootUrl;
	}
	
	public String getName() {
		return name;
	}
	
	public void setUrl(String url) {
		this.rootUrl = url;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
