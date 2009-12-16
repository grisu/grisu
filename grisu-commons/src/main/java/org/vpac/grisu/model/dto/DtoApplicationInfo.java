package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class that contains information about which submission locations are
 * available for a certain application, sorted by version.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "application")
public class DtoApplicationInfo {

	public static DtoApplicationInfo createApplicationInfo(String name,
			Map<String, String> appversionmap) {

		DtoApplicationInfo appInfo = new DtoApplicationInfo();
		appInfo.setName(name);

		List<DtoVersionInfo> versionList = new LinkedList<DtoVersionInfo>();
		for (String version : appversionmap.keySet()) {
			DtoVersionInfo versionInfo = new DtoVersionInfo();
			versionInfo.setName(version);
			List<DtoSubmissionLocationInfo> subLocs = new LinkedList<DtoSubmissionLocationInfo>();
			for (String subLoc : appversionmap.get(version).split(",")) {
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

	/**
	 * The name of the application.
	 */
	private String name;

	/**
	 * All the versions of this application grid-wide, for all VOs.
	 */
	private List<DtoVersionInfo> allVersions = new LinkedList<DtoVersionInfo>();

	@XmlElement(name = "version")
	public List<DtoVersionInfo> getAllVersions() {
		return allVersions;
	}

	@XmlAttribute(name = "applicationName")
	public String getName() {
		return name;
	}

	public Map<String, String> getSubmissionLocationsPerVersionMap() {
		Map<String, String> result = new TreeMap<String, String>();

		for (DtoVersionInfo version : getAllVersions()) {
			String versionString = version.getName();
			List<DtoSubmissionLocationInfo> subLocs = version
					.getAllSubmissionLocations().getAllSubmissionLocations();
			StringBuffer subLocNames = new StringBuffer();
			for (DtoSubmissionLocationInfo subLoc : subLocs) {
				subLocNames.append(subLoc.getSubmissionLocation() + ",");
			}
			String subLocString = subLocNames.substring(0,
					subLocNames.length() - 1).toString();
			result.put(versionString, subLocString);
		}

		return result;
	}

	public void setAllVersions(List<DtoVersionInfo> allVersions) {
		this.allVersions = allVersions;
	}

	public void setName(String name) {
		this.name = name;
	}

}
