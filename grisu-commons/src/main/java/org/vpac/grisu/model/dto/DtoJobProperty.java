package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * A wrapper class that holds a job property key and value.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="jobproperty")
public class DtoJobProperty {
	
	/**
	 * The key of the job property (see a list of possible values in the Constants class in the Infosystems
	 * GlueInterface module.
	 */
	@XmlAttribute
	public String key;
	/**
	 * The value for this job property.
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
