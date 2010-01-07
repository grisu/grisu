package org.vpac.grisu.frontend.view.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.login.GrisuSwingClient;
import org.vpac.grisu.frontend.view.swing.login.LoginPanel;

public class GrisuMainPanel extends JPanel implements GrisuSwingClient {


	private ServiceInterface si;
	private LoginPanel lp;
	private GrisuNavigationPanel grisuNavigationPanel;
	private GrisuCenterPanel grisuCenterPanel;

	/**
	 * Create the panel.
	 */
	public GrisuMainPanel() {
		setLayout(new BorderLayout(0, 0));

		//		add(getGrisuNavigationPanel(), BorderLayout.WEST);
		//		add(getGrisuCenterPanel(), BorderLayout.CENTER);
	}

	private GrisuCenterPanel getGrisuCenterPanel() {
		if (grisuCenterPanel == null) {
			grisuCenterPanel = new GrisuCenterPanel(si);
		}
		return grisuCenterPanel;
	}

	private GrisuNavigationPanel getGrisuNavigationPanel() {
		if (grisuNavigationPanel == null) {
			grisuNavigationPanel = new GrisuNavigationPanel(this.si, getGrisuCenterPanel());
		}
		return grisuNavigationPanel;
	}

	public JPanel getRootPanel() {
		return this;
	}

	public void setLoginPanel(LoginPanel lp) {
		this.lp = lp;
	}
	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		add(getGrisuNavigationPanel(), BorderLayout.WEST);
		add(getGrisuCenterPanel(), BorderLayout.CENTER);

	}
}
