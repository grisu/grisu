package grisu.frontend.view.swing.jobcreation;

import grisu.control.ServiceInterface;

import javax.swing.JPanel;


public interface JobCreationPanel {

	public boolean createsBatchJob();

	public boolean createsSingleJob();

	public JPanel getPanel();

	public String getPanelName();

	public String getSupportedApplication();

	public void setServiceInterface(ServiceInterface si);

}
