package org.vpac.grisu.frontend.view.swing.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;

import org.vpac.grisu.frontend.control.login.LoginParams;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MultiLoginPanel extends JPanel {

	private JTabbedPane tabbedPane;
	private ShibLoginPanel shibLoginPanel;
	private X509LoginPanel x509LoginPanel;
	private MyProxyLoginPanel myProxyLoginPanel;
	private JButton button;

	private final LoginPanel loginPanel;
	private AdvancedLoginPanelOptions advancedLoginPanelOptions;

	/**
	 * Create the panel.
	 */
	public MultiLoginPanel(LoginPanel loginPanel) {
		this.loginPanel = loginPanel;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("321px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("144px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(15dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getTabbedPane(), "2, 2, fill, fill");
		add(getAdvancedLoginPanelOptions(), "2, 6, fill, fill");
		add(getButton(), "2, 8, right, bottom");
	}

	private AdvancedLoginPanelOptions getAdvancedLoginPanelOptions() {
		if (advancedLoginPanelOptions == null) {
			advancedLoginPanelOptions = new AdvancedLoginPanelOptions();
			advancedLoginPanelOptions.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		}
		return advancedLoginPanelOptions;
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
			tabbedPane.addTab("Certificate login", null, getX509LoginPanel(), null);
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
	private void login() throws InterruptedException {

		LoginMethodPanel temp = (LoginMethodPanel)(getTabbedPane().getSelectedComponent());

		String url = getAdvancedLoginPanelOptions().getServiceInterfaceUrl();

		LoginParams params = new LoginParams(url, null, null);

		Thread loginThread = temp.login(params);

		loginThread.start();

		loginThread.join();

		if ( temp.loginSuccessful() ) {
			loginPanel.setServiceInterface(temp.getServiceInterface());
		} else {
			temp.getPossibleException().printStackTrace();
		}

	}
}
