package org.vpac.grisu.client.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.control.JobConstants;

public class VersionObject implements Comparable<VersionObject>, SubmissionObject {

	private String version = null;
	private ApplicationObject application = null;

	private String[] submissionLocationStrings = null;
	Set<SubmissionLocation> submissionLocations = null;
	
	private SubmissionLocation currentSubmissionLocation = null;

	private Map<String, Map<String, String>> applicationDetails = new TreeMap<String, Map<String, String>>();
	private String[] modules = null;

	public VersionObject(ApplicationObject application, String version) {
		this.application = application;
		this.version = version;
	}

	public String toString() {
		return version;
	}

	public String getVersion() {
		return version;
	}
	
	public String getApplicationName() {
		return application.getApplicationName();
	}

	private ApplicationObject getApplication() {
		return application;
	}

	public boolean equals(Object otherObject) {
		if (otherObject instanceof VersionObject) {
			VersionObject other = (VersionObject) otherObject;
			if (this.getApplication().equals(other.getApplication())
					&& this.getVersion().equals(other.getVersion())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		return version.hashCode() + application.getApplicationName().hashCode();
	}

	public int compareTo(VersionObject o) {

		return this.getVersion().compareTo(o.getVersion());
	}

	// public Set<SubmissionLocation> getActualAvailableSubmissionLocations() {
	// // return application.getSitesForVersion(this);
	//		
	// String[] locations =
	// application.getServiceInterface().getSubmissionLocationsForApplication(application.getApplicationName(),
	// version);
	//		
	// Set<SubmissionLocation> result = new TreeSet<SubmissionLocation>();
	//		
	// for (String location : locations) {
	// result.add(new SubmissionLocation());
	// }
	//		
	// }

	public String[] getSubmissionLocationStrings() {
		if (submissionLocationStrings == null) {
			submissionLocationStrings = application.getServiceInterface()
					.getSubmissionLocationsForApplication(
							application.getApplicationName(), version);
		}
		return submissionLocationStrings;
	}

	public Set<SubmissionLocation> getAllSubmissionLocations() {

		if (submissionLocations == null) {

			submissionLocations = new TreeSet<SubmissionLocation>();

			for (String sublocation : getSubmissionLocationStrings()) {
				SubmissionLocation temp = application.getEnvironmentManager().getSubmissionLocation(sublocation);
				if ( temp != null ) {
					submissionLocations.add(temp);
				}
			}
		}
		return submissionLocations;
	}

	public Set<String> getSitesWhereThisVersionIsAvailable() {

		Set<String> result = new TreeSet<String>();

		for (SubmissionLocation loc : getAllSubmissionLocations()) {
			result.add(loc.getSite());

		}
		return result;

	}
	
	public Set<String> getSitesWhereThisVersionIsAvailable(String fqan) {
		
		Set<String> temp = getSitesWhereThisVersionIsAvailable();
		String[] availSites = application.getEnvironmentManager().getAllOfTheUsersSites(fqan);
		
		Set<String> result = new TreeSet<String>();
		for ( String site : temp ) {
			if ( Arrays.binarySearch(availSites, site) >= 0 ) {
				result.add(site);
			}
		}
		return result;
	}
	
	public Set<SubmissionLocation> getSubmissionLocationsForSite(String site) {
		
		Set<SubmissionLocation> locations = new TreeSet<SubmissionLocation>();
		
		for ( SubmissionLocation sublocation : getAllSubmissionLocations() ) {
			if ( site.equals(sublocation.getSite()) ) {
				locations.add(sublocation);
			}
		}
		return locations;
	}

	public String[] getExecutables(String site) {

		Map<String, String> details = applicationDetails.get(site);
		if (details == null) {
			details = application.getServiceInterface().getApplicationDetails(
					application.getApplicationName(), version, site);
			applicationDetails.put(site, details);
		}

		return details.get("Executables").split(",");
	}

	public String[] getModules(String site) {

		Map<String, String> details = applicationDetails.get(site);
		if (details == null) {
			details = application.getServiceInterface().getApplicationDetails(
					application.getApplicationName(), version, site);
			applicationDetails.put(site, details);
		}

		if ( details.get("Module") == null || details.get("Module").length() == 0 ) {
			return new String[]{};
		}
			
		return details.get("Module").split(",");
	}

	public String[] getExecutables() {
		
		return application.getDetails(this.currentSubmissionLocation, version).get(JobConstants.MDS_EXECUTABLES_KEY).split(",");
		
	}

	public void setCurrentSubmissionLocation (SubmissionLocation currentSubmissionLocation) throws SubmissionLocationException {
		
		if ( getAllSubmissionLocations().contains(currentSubmissionLocation) ) {
			this.currentSubmissionLocation = currentSubmissionLocation;
		} else {
			throw new SubmissionLocationException("Version object \""+version+" for application \""+application.getApplicationName()+" is not supported at submission location: "+currentSubmissionLocation);
		}
	}
	
	public SubmissionLocation getLocation() {
		return this.currentSubmissionLocation;
	}
	
	public String[] getModules() {
		return application.getDetails(this.currentSubmissionLocation, version).get(JobConstants.MDS_MODULES_KEY).split(",");
	}

	public int getPreferredExecutableType() {
		return getApplication().getPreferredExecutableType();
	}

	public void setPreferredExecutableType(int type) {
		getApplication().setPreferredExecutableType(type);
	}

	public Map<String, String> getCurrentApplicationDetails() {
		return getApplication().getCurrentApplicationDetails();
	}

}
