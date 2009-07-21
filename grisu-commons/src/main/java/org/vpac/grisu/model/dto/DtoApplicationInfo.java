package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="application")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoApplicationInfo {
	
	public static DtoApplicationInfo createApplicationInfo(String name, Map<String, String> appversionmap) {
		
		DtoApplicationInfo appInfo = new DtoApplicationInfo();
		appInfo.setName(name);
		
		List<DtoVersionInfo> versionList = new LinkedList<DtoVersionInfo>();
		for (String version : appversionmap.keySet() ) {
			DtoVersionInfo versionInfo = new DtoVersionInfo();
			versionInfo.setName(version);
			List<DtoSubmissionLocationInfo> subLocs = new LinkedList<DtoSubmissionLocationInfo>();
			for ( String subLoc : appversionmap.get(version).split(",")) {
				DtoSubmissionLocationInfo temp = new DtoSubmissionLocationInfo();
				temp.setSubmissionLocation(subLoc);
				subLocs.add(temp);
			}
			DtoSubmissionLocations subLocsObj = new DtoSubmissionLocations();
			subLocsObj.setAllSubmissionLocations(subLocs);
			versionInfo.setAllSubmissionLocations(subLocsObj);
			versionList.add(versionInfo);
		}
		appInfo.setAllVersions(versionList);
		
		return appInfo;
	}
	
	@XmlAttribute(name="applicationName")
	private String name;
	
	@XmlElement(name="version")
	private List<DtoVersionInfo> allVersions = new LinkedList<DtoVersionInfo>();

	public List<DtoVersionInfo> getAllVersions() {
		return allVersions;
	}

	public void setAllVersions(List<DtoVersionInfo> allVersions) {
		this.allVersions = allVersions;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
