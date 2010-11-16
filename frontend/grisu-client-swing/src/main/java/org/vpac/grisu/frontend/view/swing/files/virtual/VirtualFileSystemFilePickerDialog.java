package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

public class VirtualFileSystemFilePickerDialog extends JDialog implements
		WindowListener {

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
			VirtualFileSystemFilePickerDialog dialog = new VirtualFileSystemFilePickerDialog(
					si, false, new String[] { ".txt" }, true);
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

	private final VirtualFileSystemTreeTablePanel virtualFileSystemTreeTablePanel;

	/**
	 * @wbp.parser.constructor
	 */
	public VirtualFileSystemFilePickerDialog(ServiceInterface si) {
		this(si, false, null, true);
	}

	/**
	 * Create the dialog.
	 */
	public VirtualFileSystemFilePickerDialog(ServiceInterface si,
			boolean displayHiddenFiles, String[] extensionsToDisplay,
			boolean displayLocalFilesystems) {
		setModal(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		addWindowListener(this);
		this.si = si;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			virtualFileSystemTreeTablePanel = new VirtualFileSystemTreeTablePanel(
					si, null, false, displayHiddenFiles, extensionsToDisplay,
					displayLocalFilesystems);
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

	public Set<GridFile> getSelectedFiles() {
		if (userCanceled) {
			return null;
		} else {
			return virtualFileSystemTreeTablePanel.getSelectedFiles();
		}
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
