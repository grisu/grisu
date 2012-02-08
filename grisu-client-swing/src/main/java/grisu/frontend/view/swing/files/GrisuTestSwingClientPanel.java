package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.jobmonitoring.batch.BatchJobTabbedPane;
import grisu.frontend.view.swing.login.GrisuSwingClient;
import grisu.frontend.view.swing.login.LoginPanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

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

		final JPanel panel = new BatchJobTabbedPane(si, null);

		add(panel, BorderLayout.CENTER);
	}

}
