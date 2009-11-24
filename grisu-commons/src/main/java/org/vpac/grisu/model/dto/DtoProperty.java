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
@XmlRootElement(name="userproperty")
public class DtoProperty {
	
	public static List<DtoProperty> dtoUserPropertiesFromMap(Map<String, String> map) {
		
		List<DtoProperty> result = new LinkedList<DtoProperty>();
		
		for ( String key : map.keySet() ) {
			result.add(new DtoProperty(key, map.get(key)));
		}
		
		return result;
		
	}
	
	/**
	 * The key of the job property (see a list of possible values in the Constants class in the Infosystems
	 * GlueInterface module.
	 */
	private String key;
	/**
	 * The value for this job property.
	 */
	private String value;
	
	public DtoProperty() {
	}
	
	public DtoProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	@XmlAttribute
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@XmlValue
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
