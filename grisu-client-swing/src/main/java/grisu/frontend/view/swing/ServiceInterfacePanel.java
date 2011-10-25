package grisu.frontend.view.swing;

import grisu.control.ServiceInterface;

import javax.swing.JPanel;

public interface ServiceInterfacePanel {

	public JPanel getPanel();

	public String getPanelTitle();

	public void setServiceInterface(ServiceInterface si);

}
