package org.vpac.grisu.frontend.view.swing.login;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class X509LoginPanel extends JPanel implements LoginMethodPanel {

	private JLabel lblCertificatePassphrase;
	private JPasswordField textField;

	private final boolean saveCredendentialsToLocalProxy = true;

	private LoginException possibleException = null;
	private ServiceInterface si = null;
	private boolean loginSuccessful = false;


	/**
	 * Create the panel.
	 */
	public X509LoginPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("4dlu:grow"),}));
		add(getLblCertificatePassphrase(), "2, 2");
		add(getTextField(), "2, 4, fill, default");

	}

	private JLabel getLblCertificatePassphrase() {
		if (lblCertificatePassphrase == null) {
			lblCertificatePassphrase = new JLabel("Certificate passphrase");
		}
		return lblCertificatePassphrase;
	}
	public LoginException getPossibleException() {
		return possibleException;
	}

	public ServiceInterface getServiceInterface() {
		return si;
	}

	private JPasswordField getTextField() {
		if (textField == null) {
			textField = new JPasswordField();
			textField.setColumns(10);
		}
		return textField;
	}

	public Thread login(final LoginParams params) {
		loginSuccessful = false;
		si = null;
		possibleException = null;

		final char[] passphrase = getTextField().getPassword();


		Thread loginThread = new Thread() {

			@Override
			public void run() {

				loginSuccessful = false;
				try {
					si = LoginManager.login(null, passphrase, null, null, params, saveCredendentialsToLocalProxy);
					loginSuccessful = true;
				} catch (LoginException e) {
					possibleException = e;
				}
			}

		};

		return loginThread;
	}

	public boolean loginSuccessful() {
		return loginSuccessful;
	}
}
