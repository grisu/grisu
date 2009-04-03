package org.vpac.grisu.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;

public class ApplicationInformationImpl implements ApplicationInformation {
	
	protected final ServiceInterface serviceInterface;
	private final String application; 
	
	protected final ResourceInformation resourceInfo = GrisuRegistry.getDefault().getResourceInformation();
	
	private Map<String, Map<String, String>> cachedApplicationDetails = new HashMap<String, Map<String, String>>();
	private Set<String> cachedAllSubmissionLocations = null;
	private Map<String, Set<String>> cachedVersionsPerSubmissionLocations = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedSubmissionLocationsPerVersion = new HashMap<String, Set<String>>();
//	private Map<String, Set<String>> cachedVersionsForSubmissionLocation = new HashMap<String, Set<String>>();
	
	private Map<String, Set<String>> cachedSubmissionLocationsForUserPerFqan = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedSubmissionLocationsForUserPerVersionAndFqan = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedVersionsForUserPerFqan = new HashMap<String, Set<String>>();

	
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
			cachedVersionsPerSubmissionLocations.put(KEY, new HashSet<String>(temp));
		} 
		return cachedVersionsPerSubmissionLocations.get(KEY);
		
	}


	public String[] getExecutables(String subLoc, String version) {

		return getApplicationDetails(subLoc, version).get(JobConstants.MDS_EXECUTABLES_KEY).split(",");
		
	}


	public Set<String> getAvailableAllSubmissionLocations() {
		
		if ( cachedAllSubmissionLocations == null ) {
			cachedAllSubmissionLocations = new HashSet(Arrays.asList(serviceInterface.getSubmissionLocationsForApplication(application))); 
		}
		return cachedAllSubmissionLocations;

	}

	
	public Set<String> getAvailableSubmissionLocationsForVersion(String version) {
		
		if ( cachedSubmissionLocationsPerVersion.get(version) == null ) {
			List<String> temp = Arrays.asList(serviceInterface.getSubmissionLocationsForApplication(application, version));
			cachedSubmissionLocationsPerVersion.put(version, new HashSet(temp));
		}
		return cachedSubmissionLocationsPerVersion.get(version);
	}
	
	public Set<String> getAllAvailableVersionsForFqan(String fqan) {

		if ( cachedVersionsForUserPerFqan.get(fqan) == null ) {
			Set<String> result = new TreeSet<String>();
			for ( String subLoc : getAvailableSubmissionLocationsForFqan(fqan) ) {
				List<String> temp = Arrays.asList(serviceInterface.getVersionsOfApplicationOnSubmissionLocation(getApplicationName(), subLoc));
				result.addAll(temp);
			}
			cachedVersionsForUserPerFqan.put(fqan, result);
		}
		return cachedVersionsForUserPerFqan.get(fqan);
	}
	
	public Set<String> getAvailableSubmissionLocationsForFqan(String fqan) {

		if ( cachedSubmissionLocationsForUserPerFqan.get(fqan) == null ) {
			Set<String> temp = new HashSet<String>();
			for ( String subLoc : resourceInfo.getAllAvailableSubmissionLocations(fqan)) {
				if ( getAvailableAllSubmissionLocations().contains(subLoc) ) {
					temp.add(subLoc);
				}
			}
			cachedSubmissionLocationsForUserPerFqan.put(fqan, temp);
		}
		return cachedSubmissionLocationsForUserPerFqan.get(fqan);
	}

	public Set<String> getAvailableSubmissionLocationsForVersionAndFqan(String version, String fqan) {

		String KEY = version + "_" + fqan;
		
		if ( cachedSubmissionLocationsForUserPerVersionAndFqan.get(KEY) == null ) {
			Set<String> temp = new HashSet<String>();
			for ( String subLoc : resourceInfo.getAllAvailableSubmissionLocations(fqan) ) {
				if ( getAvailableSubmissionLocationsForVersion(version).contains(subLoc) ) {
					temp.add(subLoc);
				}
			}
			cachedSubmissionLocationsForUserPerVersionAndFqan.put(KEY, temp);
		}
		return cachedSubmissionLocationsForUserPerVersionAndFqan.get(KEY);
	}
	
}
