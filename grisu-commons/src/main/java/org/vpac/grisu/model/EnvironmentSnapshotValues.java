package org.vpac.grisu.model;

import org.vpac.grisu.control.FqanListener;



public interface EnvironmentSnapshotValues {
	
	public String getCurrentFqan();

	public void setCurrentFqan(String currentFqan);
	
	public void addFqanListener(FqanListener listener);
	
	public void removeFqanListener(FqanListener listener);
	

}
