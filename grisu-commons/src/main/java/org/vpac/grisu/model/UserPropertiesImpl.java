package org.vpac.grisu.model;

import org.vpac.grisu.control.FqanListener;

public class UserPropertiesImpl implements UserProperties {
	
	private String currentFqan;

	public void addFqanListener(FqanListener listener) {
		// TODO Auto-generated method stub

	}

	public String getCurrentFqan() {
		return currentFqan;
	}

	public void removeFqanListener(FqanListener listener) {
		// TODO Auto-generated method stub

	}

	public void setCurrentFqan(String currentFqan) {
		this.currentFqan = currentFqan;
	}

}
