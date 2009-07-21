package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class DtoJobProperty {
	
	@XmlAttribute
	public String key;
	@XmlValue
	public String value;

}
