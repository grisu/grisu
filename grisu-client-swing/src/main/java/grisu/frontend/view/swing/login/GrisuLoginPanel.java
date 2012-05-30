package grisu.frontend.view.swing.login;


import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManagerNew;
import grith.gridsession.view.CredCreationPanel;
import grith.jgrith.cred.Cred;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GrisuLoginPanel extends JPanel {

	private class LoginAction extends AbstractAction {
		public LoginAction() {
			putValue(NAME, "Login");
			putValue(SHORT_DESCRIPTION, "Login to backend");
		}

		public void actionPerformed(ActionEvent e) {

			Thread t = new Thread() {
				@Override
				public void run() {

					SwingUtilities.invokeLater(new Thread() {

						@Override
						public void run() {
							setEnabled(false);
						}

					});

					if (siHolder == null) {
						myLogger.error("No serviceInterfaceHolder attached to this panel.");
						return;
					}

					String backend = getAdvancedLoginPanelOptions()
							.getServiceInterfaceUrl();
					Cred cred = getCredCreationPanel().createCredential();

					try {
						ServiceInterface si = LoginManagerNew.login(backend, cred,
								false);
						try {
							siHolder.setServiceInterface(si);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

					} catch (LoginException e1) {

						e1.printStackTrace();
					} finally {
						SwingUtilities.invokeLater(new Thread() {

							@Override
							public void run() {
								setEnabled(true);
							}

						});
					}
				}
			};

			t.setName("GUI login thread");
			t.start();

		}
	}

	static final Logger myLogger = LoggerFactory
			.getLogger(GrisuLoginPanel.class.getName());
	private CredCreationPanel credCreationPanel;
	private AdvancedLoginPanelOptions advancedLoginPanelOptions;
	private JButton loginButton;

	private Action action;

	private final ServiceInterfaceHolder siHolder;

	/**
	 * Create the panel.
	 */
	public GrisuLoginPanel(ServiceInterfaceHolder siHolder) {
		this.siHolder = siHolder;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("min:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(74dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getCredCreationPanel(), "2, 2, fill, fill");
		add(getAdvancedLoginPanelOptions(), "2, 4, fill, fill");
		add(getLoginButton(), "2, 6, right, default");


	}

	private Action getAction() {
		if (action == null) {
			action = new LoginAction();
		}
		return action;
	}

	private AdvancedLoginPanelOptions getAdvancedLoginPanelOptions() {
		if (advancedLoginPanelOptions == null) {
			advancedLoginPanelOptions = new AdvancedLoginPanelOptions();
			advancedLoginPanelOptions
			.setBorder(new LineBorder(Color.LIGHT_GRAY));
		}
		return advancedLoginPanelOptions;
	}
	private CredCreationPanel getCredCreationPanel() {
		if (credCreationPanel == null) {
			credCreationPanel = new CredCreationPanel();
		}
		return credCreationPanel;
	}
	private JButton getLoginButton() {
		if (loginButton == null) {
			loginButton = new JButton("Login");
			loginButton.setAction(getAction());
		}
		return loginButton;
	}
}
