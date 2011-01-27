package org.vpac.grisu.frontend.view.swing.files;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.files.GlazedFile;

public class GrisuFileDialog extends JDialog implements FileListListener {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {

			ServiceInterface si = null;

			si = LoginManager.login();

			System.out.println("Creating dialog.");
			final GrisuFileDialog dialog = new GrisuFileDialog(null, si, null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			System.out.println("Created dialog. Setting visible.");
			dialog.setVisible(true);

			for (final GlazedFile file : dialog.getSelectedFiles()) {
				System.out.println("File: " + file.getUrl());
			}

			dialog.dispose();

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	private final JPanel contentPanel = new JPanel();

	private final JPanel buttonPane = new JPanel();
	private JButton okButton = null;
	private JButton cancelButton = null;
	private FileListPanelPlus fileListPanel = null;

	private Set<GlazedFile> selectedFiles = null;

	private GlazedFile selectedFile = null;
	private final ServiceInterface si;
	private final String startUrl;
	private final Window owner;

	/**
	 * Create the dialog.
	 */
	public GrisuFileDialog(Window owner, ServiceInterface si, String startUrl) {
		super(owner, "Select file(s)/folder");
		this.startUrl = startUrl;
		setModal(true);
		setSize(450, 300);
		this.owner = owner;
		centerOnOwner();
		this.si = si;
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(getFileListPanel(), BorderLayout.CENTER);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.add(getOkButton());
		getRootPane().setDefaultButton(getOkButton());
		buttonPane.add(getCancelButton());
	}

	public void centerOnOwner() {
		setLocationRelativeTo(owner);
	}

	public void clearSelection() {
		selectedFiles = null;
		selectedFile = null;
	}

	public void directoryChanged(GlazedFile newDirectory) {
		// TODO Auto-generated method stub

	}

	public void displayHiddenFiles(boolean display) {
		fileListPanel.displayHiddenFiles(display);
	}

	public void fileDoubleClicked(GlazedFile file) {
		final Set<GlazedFile> temp = new HashSet<GlazedFile>();
		temp.add(file);
		setSelectedFiles(temp);
		setSelectedFile(file);
	}

	public void filesSelected(Set<GlazedFile> files) {
		try {
			System.out.println("File selected: "
					+ files.iterator().next().getUrl());
		} catch (final Exception e) {
		}
	}

	protected JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("Cancel");
			cancelButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					selectedFile = null;
					selectedFiles = null;
					GrisuFileDialog.this.setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	public GlazedFile getCurrentDirectory() {

		return getFileListPanel().getCurrentDirectory();
	}

	protected FileListPanelPlus getFileListPanel() {
		if (fileListPanel == null) {
			fileListPanel = new FileListPanelPlus(si, startUrl, true, true);
			fileListPanel.addFileListListener(this);
		}
		return fileListPanel;
	}

	protected JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.setActionCommand("OK");
			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					setSelectedFiles(getFileListPanel().getSelectedFiles());
					GrisuFileDialog.this.setVisible(false);
				}
			});
		}
		return okButton;
	}

	public GlazedFile getSelectedFile() {
		return selectedFile;
	}

	public Set<GlazedFile> getSelectedFiles() {
		return selectedFiles;
	}

	public void isLoading(boolean loading) {

		getOkButton().setEnabled(!loading);
	}

	public void setExtensionsToDisplay(String[] extensions) {
		fileListPanel.setExtensionsToDisplay(extensions);
	}

	private void setSelectedFile(GlazedFile file) {
		selectedFile = file;
		this.setVisible(false);
	}

	private void setSelectedFiles(Set<GlazedFile> files) {
		if (files.size() == 1) {
			selectedFile = files.iterator().next();
			selectedFiles = files;
		} else {
			selectedFiles = files;
			selectedFile = null;
		}
	}

}
