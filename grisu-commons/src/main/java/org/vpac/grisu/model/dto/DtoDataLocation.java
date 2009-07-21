package org.vpac.grisu.model.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DtoDataLocation {
	
	@XmlAttribute
	public String rooturl;
	@XmlElement(name="path")
	public List<String> paths;
	@XmlElement
	public String fqan;
	

}
