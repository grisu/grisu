package grisu.frontend.view.swing.login;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.jcommons.configuration.CommonGridProperties;
import grith.jgrith.control.LoginParams;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MyProxyLoginPanel extends JPanel implements LoginMethodPanel {
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField textField;
	private JPasswordField textField_1;

	private final boolean saveCredendentialsToLocalProxy = true;

	private LoginException possibleException = null;
	private ServiceInterface si = null;
	private boolean loginSuccessful = false;

	/**
	 * Create the panel.
	 */
	public MyProxyLoginPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("4dlu:grow"), }));
		add(getLblUsername(), "2, 2, right, default");
		add(getTextField(), "4, 2, fill, default");
		add(getLblPassword(), "2, 4, right, default");
		add(getPasswordField(), "4, 4, fill, default");

	}

	private JLabel getLblPassword() {
		if (lblPassword == null) {
			lblPassword = new JLabel("Password");
		}
		return lblPassword;
	}

	private JLabel getLblUsername() {
		if (lblUsername == null) {
			lblUsername = new JLabel("Username");
		}
		return lblUsername;
	}

	private JPasswordField getPasswordField() {
		if (textField_1 == null) {
			textField_1 = new JPasswordField();
			textField_1.setColumns(10);
		}
		return textField_1;
	}

	public LoginException getPossibleException() {
		return possibleException;
	}

	public ServiceInterface getServiceInterface() {
		return si;
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			final String lastUsername = CommonGridProperties.getDefault()
					.getLastMyProxyUsername();
			if (StringUtils.isNotBlank(lastUsername)) {
				textField.setText(lastUsername);
			}
			textField.setColumns(10);
		}
		return textField;
	}

	public void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {

				getTextField().setEnabled(!lock);
				getPasswordField().setEnabled(!lock);

			}
		});

	}

	public Thread login(final LoginParams params) {

		loginSuccessful = false;
		si = null;
		possibleException = null;

		final String username = getTextField().getText();
		final char[] password = getPasswordField().getPassword();

		params.setMyProxyUsername(username);
		params.setMyProxyPassphrase(password);

		final Thread loginThread = new Thread() {

			@Override
			public void run() {

				loginSuccessful = false;
				try {

					si = LoginManager.myProxyLogin(username, password,
							params.getLoginUrl());

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
}
