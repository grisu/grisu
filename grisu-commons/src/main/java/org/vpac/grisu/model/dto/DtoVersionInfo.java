package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This one contains a list of submission locations (that are available for a certain VO) of a specific version of an application.
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="version")
public class DtoVersionInfo {
	
	public static DtoVersionInfo createVersionInfo(String versionname, String[] submissionLocations) {
		
		DtoVersionInfo result = new DtoVersionInfo();
		
		DtoSubmissionLocations subLocs = DtoSubmissionLocations.createSubmissionLocationsInfo(submissionLocations);
		
		result.setAllSubmissionLocations(subLocs);
		
		return result;
		
	}
	
	/**
	 * The version name.
	 */
	private String name;
	
	/**
	 * The list of available submission locations.
	 */
	private DtoSubmissionLocations allSubmissionLocations;

	@XmlElement(name="submissionlocation")
	public DtoSubmissionLocations getAllSubmissionLocations() {
		return allSubmissionLocations;
	}

	public void setAllSubmissionLocations(
			DtoSubmissionLocations allSubmissionLocations) {
		this.allSubmissionLocations = allSubmissionLocations;
	}

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
