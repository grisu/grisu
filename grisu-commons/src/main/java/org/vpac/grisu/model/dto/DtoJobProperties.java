package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoJobProperties {
	
	public static DtoJobProperties createJobProperties(Map<String, String> jobProperties) {
		
		DtoJobProperties result = new DtoJobProperties();
		
		List<DtoJobProperty> list = new LinkedList<DtoJobProperty>();
		for ( String key : jobProperties.keySet() ) {
			DtoJobProperty temp = new DtoJobProperty();
			temp.key = key;
			temp.value = jobProperties.get(key);
			list.add(temp);
		}
		
		result.setProperties(list);
		return result;
	}
	
	@XmlElement(name="jobproperty")
	private List<DtoJobProperty> properties = new LinkedList<DtoJobProperty>();

	public List<DtoJobProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<DtoJobProperty> properties) {
		this.properties = properties;
	}
	

}
