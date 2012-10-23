package grisu.frontend.view.swing.login;

import grisu.control.ServiceInterface;
import grisu.settings.ClientPropertiesManager;
import grith.gridsession.SessionClient;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPanel extends JPanel implements ServiceInterfaceHolder {

	static final Logger myLogger = LoggerFactory.getLogger(LoginPanel.class
			.getName());

	private GrisuLoginPanel loginPanel;

	private final String SWING_CLIENT_PANEL = "ROOT";
	private final String LOGIN_PANEL = "LOGIN";
	private final String PROGRESS_PANEL = "PROGRESS";

	private final boolean tryExistingGridProxy;


	private LoginProgressPanel progressPanel = null;

	private final List<ServiceInterfaceHolder> siHolders;



	/**
	 * Create the panel.
	 */
	public LoginPanel(GrisuSwingClient client,
			List<ServiceInterfaceHolder> siHolders) {

		if (siHolders == null) {
			this.siHolders = new LinkedList<ServiceInterfaceHolder>();
		} else {
			this.siHolders = siHolders;
		}
		this.tryExistingGridProxy = ClientPropertiesManager.getAutoLogin();
		setLayout(new CardLayout(0, 0));
		add(getLoginPanel(), LOGIN_PANEL);
		add(client.getRootPanel(), SWING_CLIENT_PANEL);
		add(getProgressPanel(), PROGRESS_PANEL);


	}

	public GrisuLoginPanel getLoginPanel() {
		if (loginPanel == null) {
			loginPanel = new GrisuLoginPanel(this);
		}
		return loginPanel;
	}

	private LoginProgressPanel getProgressPanel() {

		if (progressPanel == null) {
			progressPanel = new LoginProgressPanel();
		}
		return progressPanel;
	}

	public void setServiceInterface(final ServiceInterface si) {


		try {
			getProgressPanel().setLoginToBackend(si);
			switchToProgressPanel();
			for (final ServiceInterfaceHolder sih : siHolders) {
				sih.setServiceInterface(si);
			}

			switchToClientPanel();
		} catch (final InterruptedException ie) {
			myLogger.error(ie.getLocalizedMessage(), ie);
			switchToLoginPanel();
		}
	}

	public void setSessionClient(SessionClient sc) {
		getLoginPanel().setSessionClient(sc);
	}

	private void switchToClientPanel() {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				final CardLayout cl = (CardLayout) (getLayout());
				cl.show(LoginPanel.this, SWING_CLIENT_PANEL);
			}
		});
	}

	private void switchToLoginPanel() {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				final CardLayout cl = (CardLayout) (getLayout());
				cl.show(LoginPanel.this, LOGIN_PANEL);
			}
		});
	}

	private void switchToProgressPanel() {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				final CardLayout cl = (CardLayout) (getLayout());
				cl.show(LoginPanel.this, PROGRESS_PANEL);
			}
		});
	}
}
