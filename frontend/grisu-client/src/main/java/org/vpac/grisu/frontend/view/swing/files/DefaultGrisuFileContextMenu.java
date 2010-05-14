package org.vpac.grisu.frontend.view.swing.files;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransaction;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.files.GlazedFile;

public class DefaultGrisuFileContextMenu extends JPopupMenu implements
		FileListPanelContextMenu, FileListListener {

	private class CopyClipboardAction extends AbstractAction {
		public CopyClipboardAction() {
			putValue(NAME, "Copy url(s) to clipboard");
			putValue(SHORT_DESCRIPTION,
					"Copy url(s) of selected file(s) to clipboard.");
		}

		public void actionPerformed(ActionEvent e) {

			List<String> temp = new LinkedList<String>();
			for (GlazedFile file : fileListPanel.getSelectedFiles()) {
				temp.add(file.getUrl());
			}

			String selection = StringUtils.join(temp, " ");

			StringSelection data = new StringSelection(selection);
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			clipboard.setContents(data, data);

		}
	}

	private class CreateFolderAction extends AbstractAction {
		public CreateFolderAction() {
			putValue(NAME, "Create folder");
			putValue(SHORT_DESCRIPTION,
					"Creates a folder in the current directory.");
		}

		public void actionPerformed(ActionEvent e) {

			String s = (String) JOptionPane.showInputDialog(SwingUtilities
					.getRoot(fileListPanel.getPanel()),
					"Name of the new folder:", "Create folder",
					JOptionPane.PLAIN_MESSAGE, null, null, "");

			if (StringUtils.isBlank(s)) {
				return;
			}

			DefaultGrisuFileContextMenu.this.fm.createFolder(fileListPanel
					.getCurrentDirectory(), s);

			fileListPanel.refresh();

		}
	}

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

			// FileTransactionStatusDialog ftd = new
			// FileTransactionStatusDialog(
			// frame, transaction);
			//
			// ftd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			// ftd.setVisible(true);

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
	protected final FileManager fm;

	private Action action;
	private Action action_1;

	private Action action_2;

	public DefaultGrisuFileContextMenu(ServiceInterface si) {
		super();
		this.si = si;
		this.ftm = FileTransactionManager.getDefault(si);
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		add(getCreateFolder());
		add(getDeleteItem());
		add(getClipboardItem());
	}

	public void directoryChanged(GlazedFile newDirectory) {

		if (newDirectory != null
				&& GlazedFile.Type.FILETYPE_FOLDER.equals(newDirectory
						.getType())) {
			getCreateFolder().setEnabled(true);
			getDeleteItem().setEnabled(true);
			getClipboardItem().setEnabled(true);
		} else if (GlazedFile.Type.FILETYPE_ROOT.equals(newDirectory.getType())) {
			getClipboardItem().setEnabled(false);
		} else {
			getCreateFolder().setEnabled(false);
			getDeleteItem().setEnabled(false);
			getClipboardItem().setEnabled(true);
		}

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

	private Action getAction_1() {
		if (action_1 == null) {
			action_1 = new CreateFolderAction();
		}
		return action_1;
	}

	private Action getAction_2() {
		if (action_2 == null) {
			action_2 = new CopyClipboardAction();
		}
		return action_2;
	}

	private JMenuItem getClipboardItem() {
		if (clipboardItem == null) {
			clipboardItem = new JMenuItem("Copy url(s) to clipboard");
			clipboardItem.setAction(getAction_2());
			clipboardItem.setEnabled(false);
		}
		return clipboardItem;
	}

	private JMenuItem getCreateFolder() {
		if (createFolder == null) {
			createFolder = new JMenuItem("Create folder");
			createFolder.setAction(getAction_1());
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
