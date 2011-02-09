package grisu.model.job;

import java.beans.PropertyChangeListener;

public interface JobMonitoringObject {

	public void addPropertyChangeListener(PropertyChangeListener l);

	public String getApplication();

	public String getJobname();

	public String getProperty(String key);

	public int getStatus(boolean refresh);

	public boolean isBatchJob();

	public void refresh();

	public void removePropertyChangeListener(PropertyChangeListener l);

}
