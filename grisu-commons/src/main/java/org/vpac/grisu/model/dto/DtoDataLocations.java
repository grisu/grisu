package org.vpac.grisu.model.dto;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoDataLocations {
	
	public static DtoDataLocations createDataLocations(String fqan, Map<String, String[]> dataLocationsMap) {
		
		DtoDataLocations result = new DtoDataLocations();
		
		List<DtoDataLocation> list = new LinkedList<DtoDataLocation>();
		
		for ( String key : dataLocationsMap.keySet() ) {
			DtoDataLocation temp = new DtoDataLocation();
			temp.rooturl = key;
			temp.fqan = fqan;
			temp.paths = Arrays.asList(dataLocationsMap.get(key));
			list.add(temp);
		}
		
		result.setDataLocations(list);
		
		return result;
	}
	
	@XmlElement(name="datalocation")
	private List<DtoDataLocation> dataLocations = new LinkedList<DtoDataLocation>();

	public List<DtoDataLocation> getDataLocations() {
		return dataLocations;
	}

	public void setDataLocations(List<DtoDataLocation> dataLocations) {
		this.dataLocations = dataLocations;
	}
	
	

}
