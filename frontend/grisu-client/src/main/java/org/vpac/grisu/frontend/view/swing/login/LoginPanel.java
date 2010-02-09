package org.vpac.grisu.frontend.view.swing.login;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.security.light.plainProxy.LocalProxy;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LoginPanel extends JPanel {
	private JPanel loginPanel;

	private final String SWING_CLIENT_PANEL = "ROOT";
	private final String LOGIN_PANEL = "LOGIN";
	private final String PROGRESS_PANEL = "PROGRESS";

	private final boolean tryExistingGridProxy;

	private final GrisuSwingClient client;
	private MultiLoginPanel multiLoginPanel;

	private LoginProgressPanel progressPanel = null;

	/**
	 * Create the panel.
	 */
	public LoginPanel(GrisuSwingClient client) {
		this.client = client;
		this.tryExistingGridProxy = ClientPropertiesManager.getAutoLogin();
		setLayout(new CardLayout(0, 0));
		add(getLoginPanel(), LOGIN_PANEL);
		add(client.getRootPanel(), SWING_CLIENT_PANEL);
		add(getProgressPanel(), PROGRESS_PANEL);

		if ( tryExistingGridProxy ) {
			if ( LocalProxy.validGridProxyExists() ) {

				new Thread() {
					@Override
					public void run() {

						try {
							getProgressPanel().setCreatingServiceInterface();
							switchToProgressPanel();
							ServiceInterface si = LoginManager.login();
							setServiceInterface(si);
						} catch (LoginException e) {
							switchToLoginPanel();
						}
					}
				}.start();

			}
		}
	}

	private JPanel getLoginPanel() {
		if (loginPanel == null) {
			loginPanel = new JPanel();
			loginPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"), }, new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("default:grow"), }));
			loginPanel.add(getMultiLoginPanel(), "2, 2, fill, fill");
		}
		return loginPanel;
	}

	private MultiLoginPanel getMultiLoginPanel() {
		if (multiLoginPanel == null) {
			multiLoginPanel = new MultiLoginPanel(this);
		}
		return multiLoginPanel;
	}

	private LoginProgressPanel getProgressPanel() {

		if ( progressPanel == null ) {
			progressPanel = new LoginProgressPanel();
		}
		return progressPanel;
	}


	public void setServiceInterface(ServiceInterface si) {

		getProgressPanel().setLoginToBackend(si);
		switchToProgressPanel();
		client.setServiceInterface(si);

		switchToClientPanel();
	}

	private void switchToClientPanel() {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				CardLayout cl = (CardLayout) (getLayout());
				cl.show(LoginPanel.this, SWING_CLIENT_PANEL);
			}
		});
	}

	private void switchToLoginPanel() {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				CardLayout cl = (CardLayout) (getLayout());
				cl.show(LoginPanel.this, LOGIN_PANEL);
			}
		});
	}

	private void switchToProgressPanel() {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				CardLayout cl = (CardLayout) (getLayout());
				cl.show(LoginPanel.this, PROGRESS_PANEL);
			}
		});
	}
}
