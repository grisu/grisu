package org.vpac.grisu.js.model;

import java.util.Map;


/**
 * Just a wrapper object
 * 
 * @author Markus Binsteiner
 *
 */
public interface InformationObject {
	
	public final static int DEFAULT_VERSION_MODE = 0;
	public final static int ANY_VERSION_MODE = 1;
	public final static int EXACT_VERSION_MODE = 2;
	
	public String getApplicationName();
	
	public String[] getExecutables(String subLoc, String version);
	
	public String[] getAvailableVersions(String subLoc);
	
	public Map<String, String> getApplicationDetails(String subLoc, String version);
	
	public String[] getAllSubmissionLocations();
	
	public String[] getSubmissionLocationsForVersion(String version);
	
}
