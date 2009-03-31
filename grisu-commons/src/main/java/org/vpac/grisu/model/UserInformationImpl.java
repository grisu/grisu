package org.vpac.grisu.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vpac.grisu.control.ServiceInterface;

public class UserInformationImpl implements UserInformation {
	
	private final ServiceInterface serviceInterface;
	
	private String[] cachedFqans = null;
	private Set<String> cachedAllSubmissionLocations = null;
	private Map<String, String[]> cachedAllSubmissionLocationsPerFqan = new HashMap<String, String[]>();
	
	public UserInformationImpl(ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public String[] getAllAvailableFqans() {
		
		if ( cachedFqans == null ) {
			this.cachedFqans = serviceInterface.getFqans();
		}
		return cachedFqans;
	}

	public Set<String> getAllAvailableSubmissionLocations() {

		if ( cachedAllSubmissionLocations == null ) {
			cachedAllSubmissionLocations = new HashSet<String>();
			for ( String fqan : getAllAvailableFqans() ) {
				cachedAllSubmissionLocations.addAll(Arrays.asList(getAllAvailableSubmissionLocations(fqan)));
			}

		}
		return cachedAllSubmissionLocations;
	}

	public String[] getAllAvailableSubmissionLocations(String fqan) {

		if ( cachedAllSubmissionLocationsPerFqan.get(fqan) == null ) {
			String[] temp = serviceInterface.getAllSubmissionLocations(fqan);
			cachedAllSubmissionLocationsPerFqan.put(fqan, temp);
		}
		return cachedAllSubmissionLocationsPerFqan.get(fqan);
	}

}
