package org.vpac.grisu.frontend.view.swing.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.events.ClientPropertiesEvent;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.security.light.Init;
import org.vpac.security.light.certificate.CertificateHelper;
import org.vpac.security.light.plainProxy.LocalProxy;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MultiLoginPanel extends JPanel implements EventSubscriber,
		LoginMethodPanel {

	private class QuickLoginAction extends AbstractAction {
		public QuickLoginAction() {
			putValue(NAME, "Quick-login");
			putValue(SHORT_DESCRIPTION,
					"Use existing local credentials to login.");
		}

		public void actionPerformed(ActionEvent e) {

			try {
				login(MultiLoginPanel.this);
			} catch (final InterruptedException e1) {
				e1.printStackTrace();
			}

		}
	}

	private JTabbedPane tabbedPane;
	private ShibLoginPanel shibLoginPanel;
	private X509LoginPanel x509LoginPanel;
	private MyProxyLoginPanel myProxyLoginPanel;

	private JButton button;
	private final LoginPanel loginPanel;
	private AdvancedLoginPanelOptions advancedLoginPanelOptions;

	private JCheckBox autoLoginCheckbox;
	private final Action action = new AbstractAction() {

		public void actionPerformed(ActionEvent arg0) {

			try {
				final LoginMethodPanel temp = (LoginMethodPanel) (getTabbedPane()
						.getSelectedComponent());
				login(temp);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	private JButton btnQuicklogin;

	private Thread loginThread = null;

	private LoginException possibleException = null;

	private ServiceInterface si = null;

	private boolean loginSuccessful = false;

	private Action quickLoginAction;

	private Action cancelAction;

	/**
	 * Create the panel.
	 */
	public MultiLoginPanel(LoginPanel loginPanel) {
		EventBus.subscribe(ClientPropertiesEvent.class, this);
		this.loginPanel = loginPanel;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("110px:grow"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("105px"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("184px"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(15dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getTabbedPane(), "2, 2, 5, 1, fill, fill");
		add(getAdvancedLoginPanelOptions(), "2, 6, 5, 1, fill, fill");
		add(getQuickLoginButton(), "2, 10, left, center");
		add(getAutoLoginCheckbox(), "4, 10, left, center");
		add(getLoginButton(), "6, 10, right, center");

		final String keyStrokeAndKey = "ENTER";

		final KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke,
				keyStrokeAndKey);
		getActionMap().put(keyStrokeAndKey, action);
		// component.getInputMap(...).put(keyStroke, keyStrokeAndKey);
		// component.getActionMap().put(keyStrokeAndKey, action);

		try {
			Init.initBouncyCastle();
			if (LocalProxy.validGridProxyExists(120)) {
				getQuickLoginAction().setEnabled(true);
			} else {
				getQuickLoginAction().setEnabled(false);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	private AdvancedLoginPanelOptions getAdvancedLoginPanelOptions() {
		if (advancedLoginPanelOptions == null) {
			advancedLoginPanelOptions = new AdvancedLoginPanelOptions();
			advancedLoginPanelOptions.setBorder(new EtchedBorder(
					EtchedBorder.LOWERED, null, null));
		}
		return advancedLoginPanelOptions;
	}

	private JCheckBox getAutoLoginCheckbox() {
		if (autoLoginCheckbox == null) {
			autoLoginCheckbox = new JCheckBox("Quick-login (whenever possible)");

			if (ClientPropertiesManager.getAutoLogin()) {
				autoLoginCheckbox.setSelected(true);
			}

			autoLoginCheckbox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {

					ClientPropertiesManager.setAutoLogin(autoLoginCheckbox
							.isSelected());

				}
			});
		}
		return autoLoginCheckbox;
	}

	private JButton getLoginButton() {
		if (button == null) {
			button = new JButton("Login");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					try {
						final LoginMethodPanel temp = (LoginMethodPanel) (getTabbedPane()
								.getSelectedComponent());
						login(temp);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}

				}
			});
		}
		return button;
	}

	private MyProxyLoginPanel getMyProxyLoginPanel() {
		if (myProxyLoginPanel == null) {
			myProxyLoginPanel = new MyProxyLoginPanel();
		}
		return myProxyLoginPanel;
	}

	public LoginException getPossibleException() {
		return possibleException;
	}

	private Action getQuickLoginAction() {
		if (quickLoginAction == null) {
			quickLoginAction = new QuickLoginAction();
		}
		return quickLoginAction;
	}

	private JButton getQuickLoginButton() {
		if (btnQuicklogin == null) {
			btnQuicklogin = new JButton();
			btnQuicklogin.setAction(getQuickLoginAction());
		}
		return btnQuicklogin;
	}

	public ServiceInterface getServiceInterface() {
		return si;
	}

	private ShibLoginPanel getShibLoginPanel() {
		if (shibLoginPanel == null) {
			shibLoginPanel = new ShibLoginPanel();
		}
		return shibLoginPanel;
	}

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(SwingConstants.TOP);
			tabbedPane.addTab("Institution login", null, getShibLoginPanel(),
					null);

			if (CertificateHelper.globusCredentialsReady()) {
				tabbedPane.addTab("Certificate login", null,
						getX509LoginPanel(), null);
			}
			tabbedPane.addTab("MyProxy login", null, getMyProxyLoginPanel(),
					null);
		}
		return tabbedPane;
	}

	private X509LoginPanel getX509LoginPanel() {
		if (x509LoginPanel == null) {
			x509LoginPanel = new X509LoginPanel();
		}
		return x509LoginPanel;
	}

	public void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {

				getLoginButton().setEnabled(!lock);
				getTabbedPane().setEnabled(!lock);

				getAdvancedLoginPanelOptions().lockUI(lock);
				getShibLoginPanel().lockUI(lock);
				getMyProxyLoginPanel().lockUI(lock);
				getX509LoginPanel().lockUI(lock);

				getAutoLoginCheckbox().setEnabled(!lock);
				getQuickLoginButton().setEnabled(!lock);
			}

		});

	}

	public void login(final LoginMethodPanel temp) throws InterruptedException {

		new Thread() {

			@Override
			public void run() {

				if (loginThread != null) {
					return;
				}

				lockUI(true);

				try {
					final String url = getAdvancedLoginPanelOptions()
							.getServiceInterfaceUrl();

					final LoginParams params = new LoginParams(url, null, null);

					loginThread = temp.login(params);

					loginThread.start();

					try {
						loginThread.join();
					} catch (final InterruptedException e) {
						e.printStackTrace();
						return;
					} finally {
						loginThread = null;
					}

					if (temp.loginSuccessful()) {
						loginPanel.setServiceInterface(temp
								.getServiceInterface());
					} else {
						temp.getPossibleException().printStackTrace();
						final ErrorInfo info = new ErrorInfo("Login error",
								"Error while trying to login.", temp
										.getPossibleException()
										.getLocalizedMessage(), (String) null,
								temp.getPossibleException(), Level.SEVERE,
								(Map) null);
						JXErrorPane.showDialog(MultiLoginPanel.this, info);
						// JXErrorPane.showDialog(temp.getPossibleException());
					}

				} finally {
					getQuickLoginButton().setAction(getQuickLoginAction());
					lockUI(false);
				}
			}
		}.start();

	}

	public Thread login(final LoginParams params) {

		loginSuccessful = false;
		si = null;
		possibleException = null;

		final Thread loginThread = new Thread() {

			@Override
			public void run() {

				loginSuccessful = false;
				try {
					si = LoginManager.login(params.getServiceInterfaceUrl());
					loginSuccessful = true;
				} catch (final LoginException e) {
					possibleException = e;
				}
			}

		};

		return loginThread;
	}

	public boolean loginSuccessful() {
		return loginSuccessful;
	}

	public void onEvent(Object event) {

		if (event instanceof ClientPropertiesEvent) {
			final ClientPropertiesEvent ev = (ClientPropertiesEvent) event;
			if (ClientPropertiesManager.AUTO_LOGIN_KEY
					.equals(((ClientPropertiesEvent) event).getKey())) {
				try {
					final Boolean b = Boolean.parseBoolean(ev.getValue());
					getAutoLoginCheckbox().setSelected(b);
				} catch (final Exception e) {
					// not that important
				}
			}
		}

	}

	// private class CancelLoginAction extends AbstractAction {
	// public CancelLoginAction() {
	// putValue(NAME, "Cancel");
	// putValue(SHORT_DESCRIPTION, "Cancel login.");
	// }
	//
	// public void actionPerformed(ActionEvent e) {
	//
	// if (loginThread != null) {
	// loginThread.interrupt();
	// }
	//
	// }
	// }
	//
	// private Action getCancelAction() {
	// if (cancelAction == null) {
	// cancelAction = new CancelLoginAction();
	// }
	// return cancelAction;
	// }
}
