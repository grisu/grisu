package org.vpac.grisu.frontend.view.swing.jobcreation.templates;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobcreation.JobCreationPanel;

public class TemplatesJobCreationPanel implements JobCreationPanel {

	public TemplatesJobCreationPanel(String[] configFileLines) {

	}



	public boolean createsBatchJob() {
		return false;
	}

	public boolean createsSingleJob() {
		return true;
	}

	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPanelName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSupportedApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setServiceInterface(ServiceInterface si) {
		// TODO Auto-generated method stub

	}

}
