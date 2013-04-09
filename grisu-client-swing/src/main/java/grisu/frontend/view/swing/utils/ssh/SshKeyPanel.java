package grisu.frontend.view.swing.utils.ssh;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grith.gridsession.GridClient;
import grith.gridsession.view.CredCreationPanel;
import grith.jgrith.cred.Cred;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;





public class SshKeyPanel extends JPanel implements PropertyChangeListener {

	private CredCreationPanel credCreationPanel;
	private JButton btnLogin;
	private SshKeyCopyPanel sshKeyCopyPanel;

	private final GridClient client;

	/**
	 * Create the panel.
	 */
	public SshKeyPanel() {
		try {
			client = new GridClient();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(104dlu;min)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC, }));
		add(getCredCreationPanel(), "2, 2, fill, fill");
		add(getBtnLogin(), "2, 4, right, default");
		add(getSshKeyCopyPanel(), "2, 6, fill, fill");

	}

	private JButton getBtnLogin() {
		if (btnLogin == null) {
			btnLogin = new JButton("Login");
			btnLogin.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					getCredCreationPanel().createCredential();
				}
			});
		}
		return btnLogin;
	}

	private CredCreationPanel getCredCreationPanel() {
		if (credCreationPanel == null) {
			credCreationPanel = new CredCreationPanel(client);
			credCreationPanel.addPropertyChangeListener(this);
		}
		return credCreationPanel;
	}

	private SshKeyCopyPanel getSshKeyCopyPanel() {
		if (sshKeyCopyPanel == null) {
			sshKeyCopyPanel = new SshKeyCopyPanel();
		}
		return sshKeyCopyPanel;
	}

	public void lockUI(final boolean lock) {
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getBtnLogin().setEnabled(!lock);
			}
		});

	}


	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getSource() != credCreationPanel) {
			return;
		}

		if ("creatingCredential".equals(evt.getPropertyName())) {
			boolean creating = (Boolean) evt.getNewValue();
			lockUI(creating);
			return;
		}

		if ("credential".equals(evt.getPropertyName())) {
			try {
				Cred c = (Cred) evt.getNewValue();
				ServiceInterface si = LoginManager.login("nesi", c, false);
				getSshKeyCopyPanel().setServiceInterface(si);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lockUI(false);
			}
		}
	}
}
