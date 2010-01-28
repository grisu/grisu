package org.vpac.grisu.frontend.view.swing.login;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.settings.ClientPropertiesManager;

import au.org.arcs.auth.shibboleth.CredentialManager;
import au.org.arcs.auth.shibboleth.DummyCredentialManager;
import au.org.arcs.auth.shibboleth.DummyIdpObject;
import au.org.arcs.auth.shibboleth.IdpObject;
import au.org.arcs.auth.shibboleth.Shibboleth;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ShibLoginPanel extends JPanel implements LoginMethodPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblInstitution;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField textField_1;
	private JPasswordField passwordField;
	private JComboBox comboBox;

	private final boolean saveCredendentialsToLocalProxy = true;

	final private IdpObject idpO = new DummyIdpObject();
	final private CredentialManager cm = new DummyCredentialManager();
	final private Shibboleth shib = new Shibboleth(idpO, cm);

	private static final String LOADING_STRING = "Loading list of institutions...";

	private final DefaultComboBoxModel idpModel = new DefaultComboBoxModel();

	private LoginException possibleException = null;
	private ServiceInterface si = null;
	private boolean loginSuccessful = false;

	/**
	 * Create the panel.
	 */
	public ShibLoginPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("8dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("4dlu:grow"),}));
		add(getLblInstitution(), "2, 2, right, default");
		add(getComboBox(), "4, 2, fill, default");
		add(getLblUsername(), "2, 4, right, default");
		add(getTextField_1(), "4, 4, fill, default");
		add(getLblPassword(), "2, 6, right, default");
		add(getPasswordField(), "4, 6, fill, default");

	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(idpModel);
		}
		return comboBox;
	}
	private JLabel getLblInstitution() {
		if (lblInstitution == null) {
			lblInstitution = new JLabel("Institution");
		}
		return lblInstitution;
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
		if (passwordField == null) {
			passwordField = new JPasswordField();
		}
		return passwordField;
	}

	public LoginException getPossibleException() {
		return possibleException;
	}

	public ServiceInterface getServiceInterface() {
		return si;
	}

	private JTextField getTextField_1() {
		if (textField_1 == null) {
			textField_1 = new JTextField();
			textField_1.setColumns(10);
		}
		return textField_1;
	}

	private void loadIdpList() {

		Thread loadThread = new Thread() {

			final String lastIdp = (String)idpModel.getSelectedItem();

			@Override
			public void run() {

				SwingUtilities.invokeLater(new Thread() {

					@Override
					public void run() {
						getComboBox().setEnabled(false);
					}

				});

				String url = ClientPropertiesManager.getShibbolethUrl();
				shib.openurl(url);

				SwingUtilities.invokeLater(new Thread() {

					@Override
					public void run() {
						idpModel.removeAllElements();

						for (String idp : idpO.getIdps() ) {
							idpModel.addElement(idp);
						}

						if ( StringUtils.isNotBlank(lastIdp) && (idpModel.getIndexOf(lastIdp) >= 0) ) {
							idpModel.setSelectedItem(lastIdp);
						}

						getComboBox().setEnabled(true);
					}

				});

			}

		};


	}

	public Thread login(final LoginParams params) {

		loginSuccessful = false;
		si = null;
		possibleException = null;

		final String username = getTextField_1().getText();
		final char[] password = getPasswordField().getPassword();
		final String idp = (String)idpModel.getSelectedItem();

		Thread loginThread = new Thread() {

			@Override
			public void run() {

				loginSuccessful = false;
				try {
					si = LoginManager.shiblogin(username, password, idp, saveCredendentialsToLocalProxy);
					loginSuccessful = true;
				} catch (LoginException e) {
					possibleException = e;
				}
			}

		};

		return loginThread;

	}

	public boolean loginSuccessful() {
		return false;
	}

}
