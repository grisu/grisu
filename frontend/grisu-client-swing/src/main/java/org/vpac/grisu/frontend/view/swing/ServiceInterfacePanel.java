package org.vpac.grisu.frontend.view.swing;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;

public interface ServiceInterfacePanel {

	public JPanel getPanel();

	public String getPanelTitle();

	public void setServiceInterface(ServiceInterface si);

}
