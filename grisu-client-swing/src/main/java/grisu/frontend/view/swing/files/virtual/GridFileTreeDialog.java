package grisu.frontend.view.swing.files.virtual;

import grisu.X;
import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.swing.files.GridFileListListener;
import grisu.model.dto.GridFile;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;


public class GridFileTreeDialog extends JDialog implements WindowListener,
GridFileListListener {

	// private class SwingAction extends AbstractAction {
	// public SwingAction() {
	// putValue(NAME, "SwingAction");
	// putValue(SHORT_DESCRIPTION, "Some short description");
	// }
	//
	// public void actionPerformed(ActionEvent e) {
	// }
	// }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ServiceInterface si = LoginManager.loginCommandline("Local");
			GridFileTreeDialog dialog = new GridFileTreeDialog(si, null, false,
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
	private boolean foldersSelectable = false;

	private final JButton okButton = new JButton("OK");

	/**
	 * @wbp.parser.constructor
	 */
	public GridFileTreeDialog(ServiceInterface si, List<GridFile> roots) {
		this(si, roots, false, null, true);
	}

	/**
	 * Create the dialog.
	 */
	public GridFileTreeDialog(ServiceInterface si, List<GridFile> roots,
			boolean displayHiddenFiles, String[] extensionsToDisplay,
			boolean displayLocalFilesystems) {
		this(null, si, roots, displayHiddenFiles, extensionsToDisplay,
				displayLocalFilesystems, true, null);
	}

	public GridFileTreeDialog(Window owner, ServiceInterface si,
			List<GridFile> roots, boolean displayHiddenFiles,
			String[] extensionsToDisplay, boolean foldersSelectable,
			boolean displayLocalFilesystems,
			String startupUrl) {
		setModal(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		addWindowListener(this);
		this.si = si;
		this.owner = owner;
		this.startupUrl = startupUrl;
		this.foldersSelectable = foldersSelectable;
		centerOnOwner();
		setSize(650, 450);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			virtualFileSystemTreeTablePanel = new GridFileTreePanel(si, roots,
					false, displayHiddenFiles, extensionsToDisplay);
			virtualFileSystemTreeTablePanel.addGridFileListListener(this);
			contentPanel.add(virtualFileSystemTreeTablePanel,
					BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton.setEnabled(false);
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
			List<GridFile> roots, String startUpUrl) {

		this(owner, si, roots, false, null, true, true, startUpUrl);
	}

	public void centerOnOwner() {
		setLocationRelativeTo(owner);
	}

	public void directoryChanged(GridFile newDirectory) {
		// nothing to do, right?
	}

	public void displayHiddenFiles(boolean display) {

		throw new RuntimeException(
		"Changing display of hidden files not implemented yet.");

	}

	public void fileDoubleClicked(GridFile file) {

		if (file.isFolder()) {
			return;
		}

		userCanceled = false;
		setVisible(false);


	}

	public void filesSelected(Set<GridFile> files) {

		X.p("Print: " + foldersSelectable);
		if ( foldersSelectable ) {
			okButton.setEnabled(true);
			return;
		}

		if (files == null) {
			return;
		}

		for ( GridFile f : files ) {
			if ( f.isFolder() ) {
				okButton.setEnabled(false);
				return;
			}
		}


		okButton.setEnabled(true);

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

	public void isLoading(boolean loading) {

		// do nothing

	}

	public void setExtensionsToDisplay(String[] extensions) {
		virtualFileSystemTreeTablePanel.setExtensionsToDisplay(extensions);
	}

	public void setFoldersSelectable(boolean foldersSelectable) {
		this.foldersSelectable = foldersSelectable;
	}

	public void setSelectionMode(int mode) {
		virtualFileSystemTreeTablePanel.setSelectionMode(mode);
	}

	public void windowActivated(WindowEvent e) {
		userCanceled = true;
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
