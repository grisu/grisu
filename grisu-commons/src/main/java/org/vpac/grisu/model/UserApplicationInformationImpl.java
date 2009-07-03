package org.vpac.grisu.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.js.model.JobSubmissionProperty;


public class UserApplicationInformationImpl extends ApplicationInformationImpl
		implements UserApplicationInformation {

	private Set<String> cachedSubmissionLocationsForUser = null;
	private Set<String> cachedAllSitesForUser = null;
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
				cachedSubmissionLocationsForUser.addAll(getAvailableSubmissionLocationsForFqan(fqan));
			}
		}
		return cachedSubmissionLocationsForUser;
	}
	
	public Set<String> getAllAvailableSitesForUser() {
		
		if ( cachedAllSitesForUser == null ) {
			cachedAllSitesForUser = new TreeSet<String>();
			for ( String subLoc : getAllAvailableSubmissionLocationsForUser() ) {
				cachedAllSitesForUser.add(resourceInfo.getSite(subLoc));
			}
		}
		return cachedAllSitesForUser;
	}



	public Set<String> getAllAvailableVersionsForUser() {
		
		if ( cachedAllVersionsForUser == null ) {
			cachedAllVersionsForUser = new TreeSet<String>();
			for ( String fqan : userInfo.getAllAvailableFqans() ) {
				cachedAllVersionsForUser.addAll(getAllAvailableVersionsForFqan(fqan));
			}
		}
		return cachedAllVersionsForUser;
	}

	public List<GridResource> getBestSubmissionLocations(
			Map<JobSubmissionProperty, String> additionalJobProperties, String fqan) {

		Map<JobSubmissionProperty, String> basicJobProperties = new HashMap<JobSubmissionProperty, String>();
		basicJobProperties.put(JobSubmissionProperty.APPLICATIONNAME, getApplicationName());
		
		basicJobProperties.putAll(additionalJobProperties);
		
		Map<String, String> converterMap = new HashMap<String, String>();
		for ( JobSubmissionProperty key : basicJobProperties.keySet() ) {
			converterMap.put(key.toString(), basicJobProperties.get(key));
		}
		
		return serviceInterface.findMatchingSubmissionLocations(converterMap, fqan);
	}
	





}
