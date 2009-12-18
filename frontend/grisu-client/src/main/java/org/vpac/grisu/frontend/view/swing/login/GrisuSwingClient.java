package org.vpac.grisu.frontend.view.swing.login;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;

public interface GrisuSwingClient {

	public JPanel getRootPanel();

	public void setLoginPanel(LoginPanel lp);

	public void setServiceInterface(ServiceInterface si);

}
