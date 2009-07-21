package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoFile implements DtoRemoteObject {
	
	@XmlAttribute(name="url")
	private String rootUrl;
	@XmlAttribute(name="name")
	private String name;
	
	@XmlElement(name="size")
	private long size;
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
