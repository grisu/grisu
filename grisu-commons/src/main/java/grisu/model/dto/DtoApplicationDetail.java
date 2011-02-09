package grisu.model.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Just a wrapper that contains a key/value pair to represent one application
 * detail.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "applicationdetail")
public class DtoApplicationDetail {

	/**
	 * The name of the application detail. E.g. "executable"
	 */
	private String key;
	/**
	 * The value for this application details. E.g. "java"
	 */
	private String value;

	@XmlAttribute
	public String getKey() {
		return key;
	}

	@XmlValue
	public String getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
