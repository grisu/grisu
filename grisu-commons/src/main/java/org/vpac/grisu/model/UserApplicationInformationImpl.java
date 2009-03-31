package org.vpac.grisu.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.vpac.grisu.control.ServiceInterface;


public class UserApplicationInformationImpl extends ApplicationInformationImpl
		implements UserApplicationInformation {

	private Set<String> cachedSubmissionLocationsForUser = null;
	private Map<String, Set<String>> cachedSubmissionLocationsForUserPerFqan = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedSubmissionLocationsForUserPerVersionAndFqan = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> cachedVersionsForUserPerFqan = new HashMap<String, Set<String>>();
	private Set<String> cachedAllVersionsForUser = null;
	private UserInformation userInfo = null;
	
	public UserApplicationInformationImpl(ServiceInterface serviceInterface, UserInformation userInfo, String application) {
		super(serviceInterface, application);
		this.userInfo = userInfo;
	}
	
	public Set<String> getAllAvailableSubmissionLocationsForUser() {
		
		if ( cachedSubmissionLocationsForUser == null ) {
			cachedSubmissionLocationsForUser = new HashSet<String>();
			for ( String fqan : userInfo.getAllAvailableFqans() ) {
				cachedSubmissionLocationsForUser.addAll(getAvailableSubmissionLocationsForUser(fqan));
			}
		}
		return cachedSubmissionLocationsForUser;
	}

	public Set<String> getAvailableSubmissionLocationsForUser(String fqan) {

		if ( cachedSubmissionLocationsForUserPerFqan.get(fqan) == null ) {
			Set<String> temp = new HashSet<String>();
			for ( String subLoc : userInfo.getAllAvailableSubmissionLocations(fqan)) {
				if ( getAllSubmissionLocations().contains(subLoc) ) {
					temp.add(subLoc);
				}
			}
			cachedSubmissionLocationsForUserPerFqan.put(fqan, temp);
		}
		return cachedSubmissionLocationsForUserPerFqan.get(fqan);
	}

	public Set<String> getAvailableSubmissionLocationsPerVersionForUser(String version, String fqan) {

		String KEY = version + "_" + fqan;
		
		if ( cachedSubmissionLocationsForUserPerVersionAndFqan.get(KEY) == null ) {
			Set<String> temp = new HashSet<String>();
			for ( String subLoc : userInfo.getAllAvailableSubmissionLocations(fqan) ) {
				if ( getAllSubmissionLocations().contains(subLoc) ) {
					temp.add(subLoc);
				}
			}
			cachedSubmissionLocationsForUserPerVersionAndFqan.put(KEY, temp);
		}
		return cachedSubmissionLocationsForUserPerVersionAndFqan.get(KEY);
	}

	public Set<String> getAllAvailableVersionsForUser() {
		
		if ( cachedAllVersionsForUser == null ) {
			cachedAllVersionsForUser = new TreeSet<String>();
			for ( String fqan : userInfo.getAllAvailableFqans() ) {
				cachedAllVersionsForUser.addAll(getAllAvailableVersionsForUser(fqan));
			}
		}
		return cachedAllVersionsForUser;
	}
	
	public Set<String> getAllAvailableVersionsForUser(String fqan) {

		if ( cachedVersionsForUserPerFqan.get(fqan) == null ) {
			Set<String> result = new TreeSet<String>();
			for ( String subLoc : getAvailableSubmissionLocationsForUser(fqan) ) {
				List<String> temp = Arrays.asList(serviceInterface.getVersionsOfApplicationOnSubmissionLocation(getApplicationName(), subLoc));
				result.addAll(temp);
			}
			cachedVersionsForUserPerFqan.put(fqan, result);
		}
		return cachedVersionsForUserPerFqan.get(fqan);
	}


}
