package org.vpac.grisu.frontend.view.swing.files;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransaction;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.model.files.GlazedFile;

public class DefaultGrisuFileContextMenu extends JPopupMenu implements
		FileListPanelContextMenu, FileListListener {

	private class DeleteAction extends AbstractAction {
		public DeleteAction() {
			putValue(NAME, "Delete");
			putValue(SHORT_DESCRIPTION, "Delete selected files");
		}

		public void actionPerformed(ActionEvent e) {

			if (fileListPanel == null) {
				myLogger
						.error("Filelistpanel not set. Can't delete anything...");
				return;
			}

			StringBuffer msg = new StringBuffer(
					"Do you really want to delete below files?\n\n");
			Set<GlazedFile> files = fileListPanel.getSelectedFiles();
			for (GlazedFile file : files) {
				msg.append(file.getName() + "\n");
			}

			JFrame frame = (JFrame) SwingUtilities.getRoot(fileListPanel
					.getPanel());

			int n = JOptionPane.showConfirmDialog(frame, msg.toString(),
					"Confirm delete files", JOptionPane.YES_NO_OPTION);

			if (n == JOptionPane.NO_OPTION) {
				return;
			}

			FileTransaction transaction = ftm.deleteFiles(files);

			FileTransactionStatusDialog ftd = new FileTransactionStatusDialog(
					frame, transaction);

			ftd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ftd.setVisible(true);

		}
	}

	static final Logger myLogger = Logger
			.getLogger(DefaultGrisuFileContextMenu.class.getName());

	private JMenuItem deleteItem;
	private FileListPanel fileListPanel;
	private JMenuItem clipboardItem;

	private JMenuItem createFolder;
	private final ServiceInterface si;
	protected final FileTransactionManager ftm;

	private Action action;

	public DefaultGrisuFileContextMenu(ServiceInterface si) {
		super();
		this.si = si;
		this.ftm = FileTransactionManager.getDefault(si);
		add(getCreateFolder());
		add(getDeleteItem());
		add(getClipboardItem());
	}

	public void fileDoubleClicked(GlazedFile file) {
	}

	public void filesSelected(Set<GlazedFile> files) {

		if (files == null || files.size() == 0) {
			getDeleteItem().setEnabled(false);
			getClipboardItem().setEnabled(false);
		} else {
			getDeleteItem().setEnabled(true);
			getClipboardItem().setEnabled(true);
		}

	}

	private Action getAction() {
		if (action == null) {
			action = new DeleteAction();
		}
		return action;
	}

	private JMenuItem getClipboardItem() {
		if (clipboardItem == null) {
			clipboardItem = new JMenuItem("Copy url(s) to clipboard");
			clipboardItem.setEnabled(false);
		}
		return clipboardItem;
	}

	private JMenuItem getCreateFolder() {
		if (createFolder == null) {
			createFolder = new JMenuItem("Create folder");
		}
		return createFolder;
	}

	private JMenuItem getDeleteItem() {
		if (deleteItem == null) {
			deleteItem = new JMenuItem("Delete");
			deleteItem.setAction(getAction());
			deleteItem.setEnabled(false);
		}
		return deleteItem;
	}

	public JPopupMenu getJPopupMenu() {
		return this;
	}

	public void isLoading(boolean loading) {

	}

	public void setFileListPanel(FileListPanel panel) {
		this.fileListPanel = panel;
		this.fileListPanel.addFileListListener(this);
	}
}
