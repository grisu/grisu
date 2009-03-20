package org.vpac.grisu.client.model;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.vpac.grisu.client.control.EnvironmentManager;

public class ApplicationInfoObject {
	
	public final static String DEFAULT_VERSION_STRING = "default";
	
	public final static int DEFAULT_VERSION_MODE = 0;
	public final static int ANY_VERSION_MODE = 1;
	public final static int EXACT_VERSION_MODE = 2;
	
	private final EnvironmentManager em;
	private final String application;
	
	private int mode = -1;
	private String currentVersion = null;
	private Set<String> currentlyAvailableVersions = null;
	private Set<SubmissionLocation> currentSubLocs = null;
	
	public ApplicationInfoObject(EnvironmentManager em, String app) {
		this(em, app, ANY_VERSION_MODE);
	}
	
	public ApplicationInfoObject(EnvironmentManager em, String app, int initial_mode) {
		
		this.em = em;
		this.application = app;
		
	}
	
	public Set<String> getCurrentlyAvailableVersions() {
		
	}
	
	/**
	 * Sets the mode for this info object. Set it to either: {@link #DEFAULT_VERSION_MODE}, {@link #ANY_VERSION_MODE}, {@link #EXACT_VERSION_MODE}.
	 * @param mode the mode
	 * @param version the version, if the mode is {@link #EXACT_VERSION_MODE}, if you use {@link #ANY_VERSION_MODE} you can set a preferred version or null, for {@link #DEFAULT_VERSION_MODE} this will be ignored.
	 * @throws ModeNotSupportedException if there are no submission locations for this combination of mode and version
	 */
	public void setMode(int mode, String version) throws ModeNotSupportedException {
		
		Set<SubmissionLocation> tempSubLocs = null;
		
		switch (mode) {
		case DEFAULT_VERSION_MODE: 
			tempSubLocs = getSubmissionLocationsForDefaultVersion(); 
			currentlyAvailableVersions = new HashSet<String>();
			currentlyAvailableVersions.add(DEFAULT_VERSION_STRING);
			break;
		case ANY_VERSION_MODE: 
			tempSubLocs = getSubmissionLocationsForAnyVersion();
			currentlyAvailableVersions = em.getServiceInterface().get
			break;
		case EXACT_VERSION_MODE: 
			tempSubLocs = getSubmissionLocationsForExactVersion(version);
			currentlyAvailableVersions = new HashSet<String>();
			currentlyAvailableVersions.add(version);
			break;
		default: throw new ModeNotSupportedException(mode);
		}
		
		if ( currentSubLocs.size() == 0 ) {
			throw new ModeNotSupportedException(mode);
		}
		
		this.currentVersion = version;
		this.currentSubLocs = tempSubLocs;
		this.mode = mode;
		
	}
	
	public Set<SubmissionLocation> getCurrentSubmissionLocations() {
		return currentSubLocs;
	}
	
	private Set<SubmissionLocation> getSubmissionLocationsForAnyVersion() {
		
		return em.getAllAvailableSubmissionLocationsForApplication(application);
	}
	
	private Set<SubmissionLocation> getSubmissionLocationsForDefaultVersion() {
		
		return em.getAllAvailableSubmissionLocationsForApplicationAndVersion(application, DEFAULT_VERSION_STRING);
	}
	
	private Set<SubmissionLocation> getSubmissionLocationsForExactVersion(String version) {
		return em.getAllAvailableSubmissionLocationsForApplicationAndVersion(application, DEFAULT_VERSION_STRING); 
	}
	

}
