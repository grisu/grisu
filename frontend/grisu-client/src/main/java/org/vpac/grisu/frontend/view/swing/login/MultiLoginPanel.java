package org.vpac.grisu.frontend.view.swing.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.vpac.grisu.control.events.ClientPropertiesEvent;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.security.light.CredentialHelpers;
import org.vpac.security.light.certificate.CertificateHelper;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MultiLoginPanel extends JPanel implements EventSubscriber {

	private JTabbedPane tabbedPane;
	private ShibLoginPanel shibLoginPanel;
	private X509LoginPanel x509LoginPanel;
	private MyProxyLoginPanel myProxyLoginPanel;
	private JButton button;

	private final LoginPanel loginPanel;
	private AdvancedLoginPanelOptions advancedLoginPanelOptions;
	private JCheckBox autoLoginCheckbox;

	/**
	 * Create the panel.
	 */
	public MultiLoginPanel(LoginPanel loginPanel) {
		EventBus.subscribe(ClientPropertiesEvent.class, this);
		this.loginPanel = loginPanel;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("321px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("105px"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("184px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(15dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getTabbedPane(), "2, 2, 3, 1, fill, fill");
		add(getAdvancedLoginPanelOptions(), "2, 6, 3, 1, fill, fill");
		add(getAutoLoginCheckbox(), "2, 8, default, bottom");
		add(getButton(), "4, 8, right, bottom");
	}

	private AdvancedLoginPanelOptions getAdvancedLoginPanelOptions() {
		if (advancedLoginPanelOptions == null) {
			advancedLoginPanelOptions = new AdvancedLoginPanelOptions();
			advancedLoginPanelOptions.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		}
		return advancedLoginPanelOptions;
	}
	private JCheckBox getAutoLoginCheckbox() {
		if (autoLoginCheckbox == null) {
			autoLoginCheckbox = new JCheckBox("Auto-login (whenever possible)");

			if ( ClientPropertiesManager.getAutoLogin() ) {
				autoLoginCheckbox.setSelected(true);
			}

			autoLoginCheckbox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {

					ClientPropertiesManager.setAutoLogin(autoLoginCheckbox.isSelected());

				}
			});
		}
		return autoLoginCheckbox;
	}

	private JButton getButton() {
		if (button == null) {
			button = new JButton("Login");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					try {
						login();
					} catch (InterruptedException e) {
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

	private ShibLoginPanel getShibLoginPanel() {
		if (shibLoginPanel == null) {
			shibLoginPanel = new ShibLoginPanel();
		}
		return shibLoginPanel;
	}
	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addTab("Institution login", null, getShibLoginPanel(), null);
			
			if ( CertificateHelper.globusCredentialsReady() ) {
				tabbedPane.addTab("Certificate login", null, getX509LoginPanel(), null);
			}
			tabbedPane.addTab("MyProxy login", null, getMyProxyLoginPanel(), null);
		}
		return tabbedPane;
	}
	private X509LoginPanel getX509LoginPanel() {
		if (x509LoginPanel == null) {
			x509LoginPanel = new X509LoginPanel();
		}
		return x509LoginPanel;
	}

	private void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {

				getButton().setEnabled(!lock);
				getTabbedPane().setEnabled(!lock);

				getAdvancedLoginPanelOptions().lockUI(lock);
				getShibLoginPanel().lockUI(lock);
				getMyProxyLoginPanel().lockUI(lock);
				getX509LoginPanel().lockUI(lock);

				getAutoLoginCheckbox().setEnabled(!lock);
			}

		});

	}

	public void login() throws InterruptedException {

		new Thread() {

			@Override
			public void run() {

				lockUI(true);

				try {
					LoginMethodPanel temp = (LoginMethodPanel)(getTabbedPane().getSelectedComponent());

					String url = getAdvancedLoginPanelOptions().getServiceInterfaceUrl();

					LoginParams params = new LoginParams(url, null, null);

					Thread loginThread = temp.login(params);

					loginThread.start();

					try {
						loginThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}

					if ( temp.loginSuccessful() ) {
						loginPanel.setServiceInterface(temp.getServiceInterface());
					} else {
						temp.getPossibleException().printStackTrace();
					}

				} finally {
					lockUI(false);
				}
			}
		}.start();

	}

	public void onEvent(Object event) {

		if ( event instanceof ClientPropertiesEvent ) {
			ClientPropertiesEvent ev = (ClientPropertiesEvent)event;
			if ( ClientPropertiesManager.AUTO_LOGIN_KEY.equals(((ClientPropertiesEvent) event).getKey()) ) {
				try {
					Boolean b = Boolean.parseBoolean(ev.getValue());
					getAutoLoginCheckbox().setSelected(b);
				} catch (Exception e) {
					// not that important
				}
			}
		}

	}
}
