package org.vpac.grisu.frontend.view.swing.login;

import java.awt.CardLayout;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.security.light.plainProxy.LocalProxy;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LoginPanel extends JPanel {
	private JPanel loginPanel;

	private final String SWING_CLIENT_PANEL = "ROOT";
	private final String LOGIN_PANEL = "LOGIN";

	private final boolean tryExistingGridProxy;

	private final GrisuSwingClient client;
	private MultiLoginPanel multiLoginPanel;

	/**
	 * @wbp.parser.constructor
	 */
	public LoginPanel(GrisuSwingClient client) {
		this(client, false);
	}

	/**
	 * Create the panel.
	 */
	public LoginPanel(GrisuSwingClient client, boolean tryExistingGridProxy) {
		this.client = client;
		this.tryExistingGridProxy = tryExistingGridProxy;
		setLayout(new CardLayout(0, 0));
		add(getLoginPanel(), LOGIN_PANEL);
		add(client.getRootPanel(), SWING_CLIENT_PANEL);

		if ( tryExistingGridProxy ) {
			if ( LocalProxy.validGridProxyExists() ) {
				try {
					ServiceInterface si = LoginManager.login();
					setServiceInterface(si);
				} catch (LoginException e) {
					e.printStackTrace();
				}
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


	public void setServiceInterface(ServiceInterface si) {

		client.setServiceInterface(si);

		switchToClientPanel();
	}

	private void switchToClientPanel() {

		CardLayout cl = (CardLayout) (getLayout());
		cl.show(this, SWING_CLIENT_PANEL);

	}
}
