package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Just a wrapper that contains a key/value pair to represent one application detail. 
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="applicationdetail")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoApplicationDetail {
	
	/**
	 * The name of the application detail. E.g. "executable"
	 */
	@XmlAttribute
	public String key;
	/**
	 * The value for this application details. E.g. "java"
	 */
	@XmlValue
	public String value;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
