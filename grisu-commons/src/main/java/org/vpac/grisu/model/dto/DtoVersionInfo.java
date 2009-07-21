package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="version")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoVersionInfo {
	
	public static DtoVersionInfo createVersionInfo(String versionname, String[] submissionLocations) {
		
		DtoVersionInfo result = new DtoVersionInfo();
		
		DtoSubmissionLocations subLocs = DtoSubmissionLocations.createSubmissionLocationsInfo(submissionLocations);
		
		result.setAllSubmissionLocations(subLocs);
		
		return result;
		
	}
	
	@XmlAttribute(name="name")
	private String name;
	
	@XmlElement(name="submissionlocation")
	private DtoSubmissionLocations allSubmissionLocations;

	public DtoSubmissionLocations getAllSubmissionLocations() {
		return allSubmissionLocations;
	}

	public void setAllSubmissionLocations(
			DtoSubmissionLocations allSubmissionLocations) {
		this.allSubmissionLocations = allSubmissionLocations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
