package org.vpac.grisu.model.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoApplicationDetails {
	
	public static DtoApplicationDetails createDetails(String appName, Map<String, String> details) {
		
		DtoApplicationDetails result = new DtoApplicationDetails();
		result.setApplicationName(appName);
		List<DtoApplicationDetail> list = new LinkedList<DtoApplicationDetail>();
		for ( String key : details.keySet() ) {
			DtoApplicationDetail temp = new DtoApplicationDetail();
			temp.key = key;
			temp.value = details.get(key);
			list.add(temp);
		}
		result.setDetails(list);
		
		return result;
	}
	
	
	@XmlElement(name="detail")
	private List<DtoApplicationDetail> details = new LinkedList<DtoApplicationDetail>();
	
	@XmlAttribute
	private String applicationName;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public List<DtoApplicationDetail> getDetails() {
		return details;
	}

	public void setDetails(List<DtoApplicationDetail> details) {
		this.details = details;
	}
	
	
	public Map<String, String> getDetailsAsMap() {
		
		Map<String, String> map = new HashMap<String, String>();
		for ( DtoApplicationDetail detail : getDetails() ) {
			map.put(detail.key, detail.value);
		}
		return map;
		
	}
	
	
	

}
