package org.vpac.grisu.frontend.view.swing.files;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.batch.BatchJobTabbedPane;
import org.vpac.grisu.frontend.view.swing.login.GrisuSwingClient;
import org.vpac.grisu.frontend.view.swing.login.LoginPanel;

public class GrisuTestSwingClientPanel extends JPanel implements
		GrisuSwingClient {

	/**
	 * Create the panel.
	 */
	public GrisuTestSwingClientPanel() {
		setLayout(new BorderLayout());
	}

	public JPanel getRootPanel() {
		return this;
	}

	public void setLoginPanel(LoginPanel lp) {
		// TODO Auto-generated method stub

	}

	public void setServiceInterface(ServiceInterface si) {

		JPanel panel = new BatchJobTabbedPane(si, null);

		add(panel, BorderLayout.CENTER);
	}

}
