package grisu.frontend.view.swing.utils.ssh;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.jcommons.configuration.CommonGridProperties;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;
import grith.gridsession.view.SLCSCredPanel;
import grith.jgrith.cred.AbstractCred;
import grith.jgrith.cred.AbstractCred.PROPERTY;
import grith.jgrith.cred.Cred;
import grith.jgrith.utils.GridSshKey;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class PanSSHKeyCopyPanel extends JPanel {
	private JScrollPane scrollPane;
	private JEditorPane infoPane;
	private SLCSCredPanel credPanel;
	private JButton btnNewButton;
	private JScrollPane scrollPane_1;
	private JTextArea progressArea;

	private StringBuffer progressLog = new StringBuffer();
	private JPasswordField passwordField;
	private JLabel lblSshKeyPassword;
	private JLabel lblStatus;
	private JLabel lblConfirmPassword;
	private JPasswordField confirmPasswordField;
	private JLabel lblNewLabel;
	
	private String backend = "bestgrid";
	
	public void setBackend(String backend) {
		this.backend = backend;
	}

	private String templatePath, mobaxtermpath;

	/**
	 * Create the panel.
	 */
	public PanSSHKeyCopyPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(33dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(61dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(98dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(58dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(24dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(49dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getScrollPane(), "2, 2, 7, 1, fill, fill");
		add(getCredPanel(), "2, 4, 7, 1, fill, fill");
		add(getLblStatus(), "2, 8, default, bottom");
		if (!GridSshKey.defaultGridsshkeyExists()) {
			add(getLblSshKeyPassword(), "4, 6, right, default");
			add(getPasswordField(), "6, 6, fill, default");
			add(getLblConfirmPassword(), "4, 8, right, top");
			add(getConfirmPasswordField(), "6, 8, fill, top");
			add(getLblNewLabel(), "8, 8, default, top");
		}
		add(getBtnNewButton(), "8, 6, right, default");
		add(getScrollPane_1(), "2, 10, 7, 1, fill, fill");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getInfoPane().scrollRectToVisible(new Rectangle(1, 1, 1, 1));
			}
		});

		if (GridSshKey.defaultGridsshkeyExists()) {
			addLogMessage("SSH key already exists in "
					+ CommonGridProperties.getDefault().getGridSSHKey()
					+ ". (Re-)using that...");
		} else {
			addLogMessage("SSH key does not yet exist in "
					+ CommonGridProperties.getDefault().getGridSSHKey()
					+ ". Will be created later.");
			checkPasswords();
		}
	}

	private void addLogMessage(String msg) {
		progressLog.append(msg + "\n");
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getProgressArea().setText(progressLog.toString());
			}
		});
	}

	private void checkPasswords() {

		char[] pw1 = getPasswordField().getPassword();
		char[] pw2 = getConfirmPasswordField().getPassword();

		if (!Arrays.equals(pw1, pw2)) {
			setPasswordStatus("Passwords don't match", false);
			return;
		}

		if (pw1.length < 6) {
			setPasswordStatus("Password too short", false);
			return;
		}

		setPasswordStatus("Password ok", true);
		return;

	}


	private JButton getBtnNewButton() {
		if (btnNewButton == null) {
			btnNewButton = new JButton("Start");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if ("Close".equals(btnNewButton.getText())) {
						System.exit(0);
					} else {
						login();
					}

				}
			});

		}
		return btnNewButton;
	}

	private JPasswordField getConfirmPasswordField() {
		if (confirmPasswordField == null) {
			confirmPasswordField = new JPasswordField();
			confirmPasswordField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					checkPasswords();
				}
			});
		}
		return confirmPasswordField;
	}

	private SLCSCredPanel getCredPanel() {
		if (credPanel == null) {
			credPanel = new SLCSCredPanel();
		}
		return credPanel;
	}

	private JEditorPane getInfoPane() {
		if (infoPane == null) {
			infoPane = new JEditorPane();
			infoPane.setEditable(false);
			infoPane.setContentType("text/html");
			StringBuffer text = new StringBuffer(
					"<h3>Enable ssh access to the Pan cluster</h3>");
			text.append("<p>This application helps you to setup ssh access to the Pan login node. In order to start the process, provide your institution credentials and click the button below.</p>");
			if (GridSshKey.defaultGridsshkeyExists()) {
				text.append("<p>You already seem to have a ssh private key in "
						+ CommonGridProperties.getDefault().getGridSSHKey()
						+ ". The associated public key will be copied to the authorized_keys file on the Pan login node. If you want to create a new one, please close this application, delete the key and restart.");
			} else {
				text.append("<p>You also have to provide a password to create a private ssh key  ("
						+ CommonGridProperties.getDefault().getGridSSHKey()
						+ "). The associated public key will be copied to the authorized_keys file on the Pan login node.</p>");
			}
			infoPane.setText(text.toString());
		}
		return infoPane;
	}

	private JLabel getLblConfirmPassword() {
		if (lblConfirmPassword == null) {
			lblConfirmPassword = new JLabel("Confirm password");
		}
		return lblConfirmPassword;
	}

	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("");
		}
		return lblNewLabel;
	}

	private JLabel getLblSshKeyPassword() {
		if (lblSshKeyPassword == null) {
			lblSshKeyPassword = new JLabel("SSH key password");
		}
		return lblSshKeyPassword;
	}

	private JLabel getLblStatus() {
		if (lblStatus == null) {
			lblStatus = new JLabel("Status:");
		}
		return lblStatus;
	}

	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					checkPasswords();
				}
			});
		}
		return passwordField;
	}

	private JTextArea getProgressArea() {
		if (progressArea == null) {
			progressArea = new JTextArea();
			progressArea.setEditable(false);
		}
		return progressArea;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getInfoPane());
		}
		return scrollPane;
	}

	private JScrollPane getScrollPane_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getProgressArea());
		}
		return scrollPane_1;
	}

	private void login() {

		getBtnNewButton().setEnabled(false);


		Thread t = new Thread() {
			@Override
			public void run() {

				try {
					Map<PROPERTY, Object> config = getCredPanel().createCredConfig();
					addLogMessage("Logging in...");
					Cred cred = AbstractCred.loadFromConfig(config);
					addLogMessage("Logged in. Identity: " + cred.getDN());

					if (GridSshKey.defaultGridsshkeyExists()) {
						addLogMessage("Skipping creation of ssh key, re-using existing one...");
					} else {
						addLogMessage("Creating ssh key...");
						char[] pw = getPasswordField().getPassword();
						try {
							GridSshKey.getDefaultGridsshkey(pw, cred.getDN());
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
						if (GridSshKey.defaultGridsshkeyExists()) {
							// TODO something
							System.out.println("SSH KEY DOES STILL NOT EXIST");
						}
					}

					addLogMessage("Logging into grid...");
					ServiceInterface si = null;
					try {
						si = LoginManager.login(backend, cred, false);
					} catch (LoginException e) {
						e.printStackTrace();
					}

					addLogMessage("Logging successful.");
					String url = "gsiftp://pan.nesi.org.nz/~/";
					addLogMessage("Resolving home directory for: " + url);
					FileManager fm = GrisuRegistryManager.getDefault(si)
							.getFileManager();
					GridFile file = null;
					try {
						file = fm.ls(url);
					} catch (RemoteFileSystemException e) {
						e.printStackTrace();
					}
					addLogMessage("Access succesful, home directory: "
							+ file.getUrl());

					String tmp = FileManager.removeTrailingSlash(file.getUrl());
					String username = tmp.substring(tmp.lastIndexOf("/") + 1);
					addLogMessage("Username on Pan: " + username);

					String authorized_key_file = tmp+"/.ssh/authorized_keys";
					try {
						File authKeyFile = fm.downloadFile(authorized_key_file);
						addLogMessage("Downloading authorized_keys file...");

						try {
							String pub = FileUtils
									.readFileToString(new File(CommonGridProperties
											.getDefault().getGridSSHCert()));

							String current_auth_file_content = FileUtils
									.readFileToString(authKeyFile);
							if (current_auth_file_content.contains(pub)) {
								addLogMessage("Key already in authorized_keys file, doing nothing.");
							} else {
								addLogMessage("Appending grid ssh key and uploading authorized_keys file...");
								FileUtils.writeStringToFile(authKeyFile, pub, true);

								fm.uploadFile(authKeyFile, authorized_key_file, true);
								addLogMessage("File uploaded. All good.");
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}

					} catch (FileTransactionException e) {
						addLogMessage("authorized_keys file does not exist yet, uploading new one...");
						try {
							fm.uploadFile(new File(CommonGridProperties
									.getDefault().getGridSSHCert()),
									authorized_key_file, false);
						} catch (FileTransactionException e1) {
							e1.printStackTrace();
						}
					}

					addLogMessage("Creating ssh client configs...");

					if ( GridSshKey.createMobaXTermIniFile(templatePath, mobaxtermpath, username) ) {

						addLogMessage("");
						addLogMessage("Ini file for MobaXterm created in "
								+ mobaxtermpath);
					}

					if ( GridSshKey.createSSHConfigFile(username)) {
						addLogMessage("");
						addLogMessage("Config entry for pan ssh access created. When using the commandline, just issue the following command to log in:" );
						addLogMessage("");
						addLogMessage("\tssh pan");
					}

				} finally {
					getBtnNewButton().setText("Close");
					getBtnNewButton().setEnabled(true);
				}

			}
		};
		t.setName("COPY_SSH_KEY_THREAD");
		t.start();
	}

	public void setMobaxtermpath(String mobaxtermpath) {
		this.mobaxtermpath = mobaxtermpath;
	}


	private void setPasswordStatus(final String msg, final boolean enableButton) {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				if (enableButton) {
					getBtnNewButton().setEnabled(true);
					getLblNewLabel().setForeground(Color.BLACK);
				} else {
					getBtnNewButton().setEnabled(false);
					getLblNewLabel().setForeground(Color.RED);
				}
				getLblNewLabel().setText(msg);
			}
		});


	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
}
