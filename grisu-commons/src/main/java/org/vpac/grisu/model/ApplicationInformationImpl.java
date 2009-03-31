package org.vpac.grisu.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;

public class ApplicationInformationImpl implements ApplicationInformation {
	
	protected final ServiceInterface serviceInterface;
	private final String application; 
	
	private Map<String, Map<String, String>> cachedApplicationDetails = new HashMap<String, Map<String, String>>();
	private Set<String> cachedAllSubmissionLocations = null;
	private Map<String, Set<String>> cachedVersionsPerSubmissionLocations = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedSubmissionLocationsPerVersion = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedVersionsForSubmissionLocation = new HashMap<String, Set<String>>();

	
	public ApplicationInformationImpl(ServiceInterface serviceInterface, String application) {
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


	public Set<String> getAvailableVersions(String subLoc) {
		
		String KEY = subLoc;
	
		if ( cachedVersionsPerSubmissionLocations.get(KEY) == null ) {
			List<String> temp = Arrays.asList(serviceInterface.getVersionsOfApplicationOnSubmissionLocation(application, subLoc));
			cachedVersionsPerSubmissionLocations.put(KEY, new HashSet(temp));
		} 
		return cachedVersionsPerSubmissionLocations.get(KEY);
		
	}


	public String[] getExecutables(String subLoc, String version) {

		return getApplicationDetails(subLoc, version).get(JobConstants.MDS_EXECUTABLES_KEY).split(",");
		
	}


	public Set<String> getAllSubmissionLocations() {
		
		if ( cachedAllSubmissionLocations == null ) {
			cachedAllSubmissionLocations = new HashSet(Arrays.asList(serviceInterface.getSubmissionLocationsForApplication(application))); 
		}
		return cachedAllSubmissionLocations;

	}

	
	public Set<String> getSubmissionLocationsForVersion(String version) {
		
		if ( cachedSubmissionLocationsPerVersion.get(version) == null ) {
			List<String> temp = Arrays.asList(serviceInterface.getSubmissionLocationsForApplication(application, version));
			cachedSubmissionLocationsPerVersion.put(version, new HashSet(temp));
		}
		return cachedSubmissionLocationsPerVersion.get(version);
	}
	
}
