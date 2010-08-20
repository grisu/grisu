package org.vpac.grisu.frontend.view.swing.jobcreation;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;

public interface JobCreationPanel {

	public boolean createsBatchJob();
	public boolean createsSingleJob();

	public JPanel getPanel();

	public String getPanelName();

	public String getSupportedApplication();

	public void setServiceInterface(ServiceInterface si);

}
