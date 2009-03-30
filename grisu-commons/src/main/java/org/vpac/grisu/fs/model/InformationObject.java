package org.vpac.grisu.fs.model;

import java.util.Map;


/**
 * Just a wrapper object
 * 
 * @author Markus Binsteiner
 *
 */
public interface InformationObject {
	
	public String getApplicationName();
	
	public String[] getExecutables(String subLoc, String version);
	
	public String[] getAvailableVersions(String subLoc);
	
	public Map<String, String> getApplicationDetails(String subLoc, String version);
	
	public String[] getAllSubmissionLocations();
	
	public String[] getSubmissionLocationsForVersion(String version);
	
}
