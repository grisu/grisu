package org.vpac.grisu.model.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper that contains information about a datalocation.
 * 
 * A datalocation contains a root url and a list of relative paths on top of this root url. Also the fqan 
 * that is used to access this datalocation.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="datalocation")
public class DtoDataLocation {
	
	/**
	 * The root url.
	 */
	private String rooturl;
	/**
	 * A list of (local) relative paths (like "/home/grid-admin") that are available for the vo that is 
	 * connected to this datalocation. 
	 */
	private List<String> paths;
	
	/**
	 * The fqan of the VO that is used to create a voms proxy to access this datalocation.
	 */
	private String fqan;

	@XmlAttribute(name="rooturl")
	public String getRooturl() {
		return rooturl;
	}
	public void setRooturl(String rooturl) {
		this.rooturl = rooturl;
	}
	@XmlElement(name="path")
	public List<String> getPaths() {
		return paths;
	}
	public void setPaths(List<String> paths) {
		this.paths = paths;
	}
	@XmlElement
	public String getFqan() {
		return fqan;
	}
	public void setFqan(String fqan) {
		this.fqan = fqan;
	}


}
