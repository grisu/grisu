package grisu.frontend.view.swing.login;

import grisu.control.ServiceInterface;
import grith.jgrith.control.LoginParams;

public interface LoginMethodPanel {

	public Exception getPossibleException();

	public ServiceInterface getServiceInterface();

	public void lockUI(boolean lock);

	public Thread login(LoginParams params);

	public boolean loginSuccessful();

}
