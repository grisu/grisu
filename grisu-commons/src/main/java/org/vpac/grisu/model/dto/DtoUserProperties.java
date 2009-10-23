package org.vpac.grisu.model.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import au.org.arcs.jcommons.constants.Constants;

/**
 * This one holds information about a job that was created (and maybe already submitted to the endpoint resource).
 * 
 * You can use this to query information like job-directory and status of the job. Have a look in the 
 * Constants class in the GlueInterface module of the Infosystems project
 * for values of keys of possible job properties.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="userproperties")
public class DtoUserProperties {
	
	public static DtoUserProperties createUserProperties(Map<String, String> userProperties) {
		
		DtoUserProperties result = new DtoUserProperties();
		
		List<DtoUserProperty> list = new LinkedList<DtoUserProperty>();
		for ( String key : userProperties.keySet() ) {
			DtoUserProperty temp = new DtoUserProperty();
			temp.setKey(key);
			temp.setValue(userProperties.get(key));
			list.add(temp);
		}
		result.setProperties(list);
		
		return result;
	}
	

	/**
	 * The list of user properties.
	 */
	private List<DtoUserProperty> properties = new LinkedList<DtoUserProperty>();
	


	@XmlElement(name="userproperty")
	public List<DtoUserProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<DtoUserProperty> properties) {
		this.properties = properties;
	}
	
	public Map<String, String> propertiesAsMap() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		for ( DtoUserProperty prop : getProperties() ) {
			map.put(prop.getKey(), prop.getValue());
		}
		
		return map;
	}
	
	public void addUserProperty(String key, String value) {
		properties.add(new DtoUserProperty(key, value));
	}
	
	public String readUserProperty(String key) {
		return propertiesAsMap().get(key);
	}

	
}
