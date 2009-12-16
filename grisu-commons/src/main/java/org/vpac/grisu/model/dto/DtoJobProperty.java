package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * A wrapper class that holds a job property key and value.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "jobproperty")
public class DtoJobProperty {

	public static List<DtoJobProperty> dtoJobPropertiesFromMap(
			Map<String, String> map) {

		List<DtoJobProperty> result = new LinkedList<DtoJobProperty>();

		for (String key : map.keySet()) {
			result.add(new DtoJobProperty(key, map.get(key)));
		}

		return result;

	}

	/**
	 * The key of the job property (see a list of possible values in the
	 * Constants class in the Infosystems GlueInterface module.
	 */
	private String key;
	/**
	 * The value for this job property.
	 */
	private String value;

	public DtoJobProperty() {
	}

	public DtoJobProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

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
