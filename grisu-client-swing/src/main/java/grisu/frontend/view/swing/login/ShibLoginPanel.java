package grisu.frontend.view.swing.login;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.jcommons.configuration.CommonGridProperties;
import grisu.jcommons.exceptions.CredentialException;
import grisu.settings.ClientPropertiesManager;
import grith.jgrith.control.LoginParams;
import grith.jgrith.credential.Credential;
import grith.jgrith.credential.CredentialFactory;
import grith.sibboleth.CredentialManager;
import grith.sibboleth.DummyCredentialManager;
import grith.sibboleth.DummyIdpObject;
import grith.sibboleth.IdpObject;
import grith.sibboleth.Shibboleth;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
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
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("8dlu"), FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("4dlu:grow"), }));
		add(getLblInstitution(), "2, 2, right, default");
		add(getComboBox(), "4, 2, fill, default");
		add(getLblUsername(), "2, 4, right, default");
		add(getTextField_1(), "4, 4, fill, default");
		add(getLblPassword(), "2, 6, right, default");
		add(getPasswordField(), "4, 6, fill, default");

		loadIdpList();

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
			final String lastUsername = CommonGridProperties.getDefault()
					.getLastShibUsername();
			if (StringUtils.isNotBlank(lastUsername)) {
				textField_1.setText(lastUsername);
			}
			textField_1.setColumns(10);
		}
		return textField_1;
	}

	private void loadIdpList() {

		final Thread loadThread = new Thread() {

			@Override
			public void run() {

				SwingUtilities.invokeLater(new Thread() {

					@Override
					public void run() {
						getComboBox().setEnabled(false);
					}

				});

				final String url = ClientPropertiesManager.getShibbolethUrl();
				shib.openurl(url);

				SwingUtilities.invokeLater(new Thread() {

					@Override
					public void run() {

						String lastIdp = (String) idpModel.getSelectedItem();

						idpModel.removeAllElements();

						for (final String idp : idpO.getIdps()) {
							idpModel.addElement(idp);
						}

						if (StringUtils.isBlank(lastIdp)) {
							lastIdp = CommonGridProperties.getDefault()
									.getLastShibIdp();
						}

						if (StringUtils.isNotBlank(lastIdp)
								&& (idpModel.getIndexOf(lastIdp) >= 0)) {
							idpModel.setSelectedItem(lastIdp);
						}

						getComboBox().setEnabled(true);
					}

				});

			}

		};

		loadThread.start();

	}

	public void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getTextField_1().setEnabled(!lock);
				getPasswordField().setEnabled(!lock);
				getComboBox().setEnabled(!lock);
			}
		});

	}

	public Thread login(final LoginParams params) {

		loginSuccessful = false;
		si = null;
		possibleException = null;

		final String username = getTextField_1().getText();
		final char[] password = getPasswordField().getPassword();
		final String idp = (String) idpModel.getSelectedItem();

		final Thread loginThread = new Thread() {

			@Override
			public void run() {

				loginSuccessful = false;
				try {
					try {
						Credential c = CredentialFactory.createFromSlcs(null, idp,
								username, password);
						si = LoginManager.login(c, params, false);
						if (saveCredendentialsToLocalProxy) {
							c.saveCredential();
						}
					} catch (CredentialException e) {
						throw new LoginException("Can't create credential.", e);
					}

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
