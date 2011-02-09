package grisu.model.dto;

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

		final DtoApplicationInfo appInfo = new DtoApplicationInfo();
		appInfo.setName(name);

		final List<DtoVersionInfo> versionList = new LinkedList<DtoVersionInfo>();
		for (final String version : appversionmap.keySet()) {
			final DtoVersionInfo versionInfo = new DtoVersionInfo();
			versionInfo.setName(version);
			final List<DtoSubmissionLocationInfo> subLocs = new LinkedList<DtoSubmissionLocationInfo>();
			for (final String subLoc : appversionmap.get(version).split(",")) {
				final DtoSubmissionLocationInfo temp = new DtoSubmissionLocationInfo();
				temp.setSubmissionLocation(subLoc);
				subLocs.add(temp);
			}
			final DtoSubmissionLocations subLocsObj = new DtoSubmissionLocations();
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
		final Map<String, String> result = new TreeMap<String, String>();

		for (final DtoVersionInfo version : getAllVersions()) {
			final String versionString = version.getName();
			final List<DtoSubmissionLocationInfo> subLocs = version
					.getAllSubmissionLocations().getAllSubmissionLocations();
			final StringBuffer subLocNames = new StringBuffer();
			for (final DtoSubmissionLocationInfo subLoc : subLocs) {
				subLocNames.append(subLoc.getSubmissionLocation() + ",");
			}
			final String subLocString = subLocNames.substring(0,
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
