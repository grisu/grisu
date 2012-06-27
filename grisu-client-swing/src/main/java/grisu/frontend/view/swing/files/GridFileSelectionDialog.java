package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.swing.files.virtual.GridFileTreePanel;
import grisu.model.dto.GridFile;
import grith.gridsession.GridSessionCred;
import grith.jgrith.cred.Cred;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridFileSelectionDialog extends JDialog implements WindowListener,
GridFileListListener {

	static final Logger myLogger = LoggerFactory
			.getLogger(GridFileSelectionDialog.class);
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		try {

			Cred cred = GridSessionCred.create();

			ServiceInterface si = LoginManager.login("nesi", cred, true);
			GridFileSelectionDialog dialog = new GridFileSelectionDialog(null,
					si);
			dialog.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			dialog.setDisplayFiles(false);
			dialog.setDisplayHiddenFiles(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final JPanel contentPanel = new JPanel();
	private final GridFileTreePanel gridFileTreePanel;

	private boolean cancelled = true;
	private boolean foldersSelectable = true;
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");

	/**
	 * Create the dialog.
	 */
	public GridFileSelectionDialog(Window owner, ServiceInterface si) {
		super(owner, "Select file(s)/folder");
		setModal(true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		addWindowListener(this);
		centerOnOwner();
		setSize(800, 550);

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			gridFileTreePanel = new GridFileTreePanel(si);
			gridFileTreePanel
			.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			gridFileTreePanel.addGridFileListListener(this);
			contentPanel.add(gridFileTreePanel, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelled = false;
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelled = true;
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public void centerOnOwner() {
		setLocationRelativeTo(getParent());
	}

	public void directoryChanged(GridFile newDirectory) {

		myLogger.debug("new dir: " + newDirectory);

	}

	public void fileDoubleClicked(GridFile file) {

		cancelled = false;
		setVisible(false);

	}

	public void filesSelected(Set<GridFile> files) {

		myLogger.debug("files selected: " + files);

		if (foldersSelectable) {
			okButton.setEnabled(true);
			return;
		}

		if (files == null) {
			return;
		}

		for (final GridFile f : files) {
			if (f.isFolder()) {
				okButton.setEnabled(false);
				return;
			}
		}


	}

	public Set<GridFile> getCurrentDirectories() {
		// TODO for history purposes, re-open the tree as it was last time
		return null;
	}

	public GridFile getSelectedFile() {
		Set<GridFile> sel = getSelectedFiles();
		if (cancelled || (sel == null)) {
			return null;
		} else {
			if (sel.size() != 1) {
				return null;
			} else {
				return sel.iterator().next();
			}
		}
	}

	public Set<GridFile> getSelectedFiles() {
		if (cancelled) {
			return null;
		} else {
			return gridFileTreePanel.getSelectedFiles();
		}
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void isLoading(boolean loading) {

		myLogger.debug("is loading: " + loading);

	}

	public void setDisplayFiles(boolean displayFiles) {
		gridFileTreePanel.setDisplayFiles(displayFiles);
	}

	public void setDisplayHiddenFiles(boolean display) {

		gridFileTreePanel.setDisplayHiddenFiles(display);
	}
	public void setExtensionsToDisplay(String[] extensions) {
		gridFileTreePanel.setExtensionsToDisplay(extensions);

	}

	public void setFoldersSelectable(boolean selectable) {
		this.foldersSelectable = selectable;
	}

	/**
	 * Sets the table's selection mode to allow only single selections, a single
	 * contiguous interval, or multiple intervals.
	 * <P>
	 * <bold>Note:</bold> <code>JTable</code> provides all the methods for
	 * handling column and row selection. When setting states, such as
	 * <code>setSelectionMode</code>, it not only updates the mode for the row
	 * selection model but also sets similar values in the selection model of
	 * the <code>columnModel</code>. If you want to have the row and column
	 * selection models operating in different modes, set them both directly.
	 * <p>
	 * Both the row and column selection models for <code>JTable</code> default
	 * to using a <code>DefaultListSelectionModel</code> so that
	 * <code>JTable</code> works the same way as the <code>JList</code>. See the
	 * <code>setSelectionMode</code> method in <code>JList</code> for details
	 * about the modes.
	 *
	 * @see JList#setSelectionMode
	 * @beaninfo description: The selection mode used by the row and column
	 *           selection models. enum: SINGLE_SELECTION
	 *           ListSelectionModel.SINGLE_SELECTION SINGLE_INTERVAL_SELECTION
	 *           ListSelectionModel.SINGLE_INTERVAL_SELECTION
	 *           MULTIPLE_INTERVAL_SELECTION
	 *           ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
	 */
	public void setSelectionMode(int mode) {
		gridFileTreePanel.setSelectionMode(mode);
	}

	public void windowActivated(WindowEvent e) {
		cancelled = true;
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
