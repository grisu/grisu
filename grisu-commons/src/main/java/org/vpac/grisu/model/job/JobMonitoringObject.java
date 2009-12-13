package org.vpac.grisu.model.job;

import java.beans.PropertyChangeListener;

public interface JobMonitoringObject {
	
	public String getJobname();
	
	public int getStatus(boolean refresh);
	
	public boolean isBatchJob();
	
	public String getProperty(String key);
	
	public void addPropertyChangeListener(PropertyChangeListener l);
	
	public void removePropertyChangeListener(PropertyChangeListener l);
	
	public void refresh();
	
	public String getApplication();

}
