package org.vpac.grisu.model.dto;

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

}
