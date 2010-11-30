package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.X;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.dto.GridFile;

public class GridFileTreeDialog extends JDialog implements WindowListener {

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ServiceInterface si = LoginManager.loginCommandline("Local");
			GridFileTreeDialog dialog = new GridFileTreeDialog(si, false,
					new String[] { ".txt" }, true);
			dialog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			dialog.setVisible(true);

			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final JPanel contentPanel = new JPanel();
	private final ServiceInterface si;

	private boolean userCanceled = true;
	private final GridFileTreePanel virtualFileSystemTreeTablePanel;
	private final Window owner;
	private final String startupUrl;

	/**
	 * @wbp.parser.constructor
	 */
	public GridFileTreeDialog(ServiceInterface si) {
		this(si, false, null, true);
	}

	/**
	 * Create the dialog.
	 */
	public GridFileTreeDialog(ServiceInterface si, boolean displayHiddenFiles,
			String[] extensionsToDisplay, boolean displayLocalFilesystems) {
		this(null, si, displayHiddenFiles, extensionsToDisplay,
				displayLocalFilesystems, null);
	}

	public GridFileTreeDialog(Window owner, ServiceInterface si,
			boolean displayHiddenFiles, String[] extensionsToDisplay,
			boolean displayLocalFilesystems, String startupUrl) {
		setModal(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		addWindowListener(this);
		this.si = si;
		this.owner = owner;
		this.startupUrl = startupUrl;
		centerOnOwner();
		setSize(650, 450);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			virtualFileSystemTreeTablePanel = new GridFileTreePanel(si, null,
					false, displayHiddenFiles, extensionsToDisplay);
			contentPanel.add(virtualFileSystemTreeTablePanel,
					BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						userCanceled = false;
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						userCanceled = true;
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public GridFileTreeDialog(Window owner, ServiceInterface si,
			String startUpUrl) {

		this(owner, si, false, null, true, startUpUrl);
	}

	public void centerOnOwner() {
		setLocationRelativeTo(owner);
	}

	public void displayHiddenFiles(boolean display) {

		throw new RuntimeException(
				"Changing display of hidden files not implemented yet.");

	}

	public GridFile getSelectedFile() {
		if (userCanceled || (getSelectedFiles() == null)) {
			return null;
		} else {
			if (getSelectedFiles().size() != 1) {
				return null;
			} else {
				return getSelectedFiles().iterator().next();
			}
		}
	}

	public Set<GridFile> getSelectedFiles() {
		if (userCanceled) {
			return null;
		} else {
			return virtualFileSystemTreeTablePanel.getSelectedFiles();
		}
	}

	public void setExtensionsToDisplay(String[] extensions) {
		throw new RuntimeException(
				"Changing extensions to display not implemented yet.");
	}

	public void setSelectionMode(int mode) {
		virtualFileSystemTreeTablePanel.setSelectionMode(mode);
	}

	public void windowActivated(WindowEvent e) {
		userCanceled = true;
		X.p("OPened.");
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

}
