package org.vpac.grisu.client.model;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.fs.model.InformationObject;

public class ApplicationInformationObject implements InformationObject {
	
	private final ServiceInterface serviceInterface;
	private final String application; 
	
	private Map<String, Map<String, String>> cachedApplicationDetails = new HashMap<String, Map<String, String>>();
	private String[] cachedAllSubmissionLocations = null;
	private Map<String, String[]> cachedVersionsPerSubmissionLocations = new HashMap<String, String[]>();
	private Map<String, String[]> cachedSubmissionLocationsPerVersion = new HashMap<String, String[]>();
	
	public ApplicationInformationObject(ServiceInterface serviceInterface, String application) {
		this.serviceInterface = serviceInterface;
		this.application = application;
	}
	

	public Map<String, String> getApplicationDetails(String subLoc, String version) {
		String KEY = version+"_"+subLoc;
		
		if ( cachedApplicationDetails.get(KEY) == null ) {
			Map<String, String> details =  serviceInterface.getApplicationDetails(application, version, subLoc);
			cachedApplicationDetails.put(KEY, details);
		}
		return cachedApplicationDetails.get(KEY);
	}


	public String getApplicationName() {
		return application;
	}


	public String[] getAvailableVersions(String subLoc) {
		
		String KEY = subLoc;
	
		if ( cachedVersionsPerSubmissionLocations.get(KEY) == null ) {
			String[] temp = serviceInterface.getVersionsOfApplicationOnSubmissionLocation(application, subLoc);
			cachedVersionsPerSubmissionLocations.put(KEY, temp);
		} 
		return cachedVersionsPerSubmissionLocations.get(KEY);
		
	}


	public String[] getExecutables(String subLoc, String version) {

		return getApplicationDetails(subLoc, version).get(JobConstants.MDS_EXECUTABLES_KEY).split(",");
		
	}


	public String[] getAllSubmissionLocations() {
		
		if ( cachedAllSubmissionLocations == null ) {
			cachedAllSubmissionLocations = serviceInterface.getSubmissionLocationsForApplication(application); 
		}
		return cachedAllSubmissionLocations;

	}

	
	public String[] getSubmissionLocationsForVersion(String version) {
		
		if ( cachedSubmissionLocationsPerVersion.get(version) == null ) {
			String[] temp = serviceInterface.getSubmissionLocationsForApplication(application, version);
			cachedSubmissionLocationsPerVersion.put(version, temp);
		}
		return cachedSubmissionLocationsPerVersion.get(version);
	}

}
