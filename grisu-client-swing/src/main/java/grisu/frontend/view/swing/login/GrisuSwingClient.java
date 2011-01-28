package grisu.frontend.view.swing.login;

import javax.swing.JPanel;

public interface GrisuSwingClient extends ServiceInterfaceHolder {

	public JPanel getRootPanel();

	public void setLoginPanel(LoginPanel lp);

}
