package org.vpac.grisu.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.ServiceInterface;

public class UserInformationImpl implements UserInformation {
	
	private final ServiceInterface serviceInterface;
	
	private ResourceInformation resourceInfo = GrisuRegistry.getDefault().getResourceInformation();
	
	private String[] cachedFqans = null;
	private Set<String> cachedAllSubmissionLocations = null;
	private Set<String> cachedAllSites = null;

	
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
			cachedAllSites = new TreeSet<String>();
			for ( String fqan : getAllAvailableFqans() ) {
				cachedAllSubmissionLocations.addAll(Arrays.asList(resourceInfo.getAllAvailableSubmissionLocations(fqan)));
			}

		}
		return cachedAllSubmissionLocations;
	}

	public Set<String> getAllAvailableSites() {
		
		if ( cachedAllSites == null ) {
			Set<String> temp = new TreeSet<String>();
			for ( String subLoc : getAllAvailableSubmissionLocations() ) {
				temp.add(resourceInfo.getSite(subLoc));
			}
		}
		return cachedAllSites;
	}

}
