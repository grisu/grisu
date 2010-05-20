package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransaction;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.frontend.view.swing.files.FileListListener;
import org.vpac.grisu.frontend.view.swing.files.FileListPanel;
import org.vpac.grisu.frontend.view.swing.files.FileListPanelContextMenu;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.files.GlazedFile;

import com.jidesoft.swing.FolderChooser;

public class BatchResultContextMenu extends JPopupMenu implements
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

	private class DownloadAction extends AbstractAction {
		public DownloadAction() {
			putValue(NAME, "Download");
			putValue(SHORT_DESCRIPTION, "Download selected files");
		}

		public void actionPerformed(ActionEvent e) {

			Set<GlazedFile> files = fileListPanel.getSelectedFiles();
			if (files == null || files.size() <= 0) {
				return;
			}

			FolderChooser _folderChooser = new FolderChooser(_currentFolder);
			// _folderChooser.setCurrentDirectory(_currentFolder);
			_folderChooser.setRecentList(_recentList);
			_folderChooser.setFileHidingEnabled(true);
			int result = _folderChooser.showOpenDialog(SwingUtilities
					.getRootPane(fileListPanel.getPanel()));

			if (result == FolderChooser.APPROVE_OPTION) {
				_currentFolder = _folderChooser.getSelectedFile();
				if (_recentList.contains(_currentFolder.toString())) {
					_recentList.remove(_currentFolder.toString());
				}
				_recentList.add(0, _currentFolder.toString());
				File selectedFile = _folderChooser.getSelectedFile();
				if (selectedFile != null) {
					Set<String> urls = new HashSet<String>();
					for (GlazedFile file : files) {
						urls.add(file.getUrl());
					}
					FileTransaction ft = new FileTransaction(fm, urls,
							selectedFile.toURI().toString(), true);
					ftm.addFileTransfer(ft);
				} else {
					return;
				}
			}

		}
	}

	private File _currentFolder = null;
	private final List<String> _recentList = new ArrayList<String>();

	static final Logger myLogger = Logger
			.getLogger(BatchResultContextMenu.class.getName());

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
	private JMenuItem menuItem;

	private Action downloadAction;

	public BatchResultContextMenu(ServiceInterface si) {
		super();
		this.si = si;
		this.ftm = FileTransactionManager.getDefault(si);
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		add(getMenuItem());
		add(getClipboardItem());
	}

	public void directoryChanged(GlazedFile newDirectory) {

	}

	public void fileDoubleClicked(GlazedFile file) {
	}

	public void filesSelected(Set<GlazedFile> files) {

		if (files == null || files.size() == 0) {
			getClipboardItem().setEnabled(false);
		} else {
			getClipboardItem().setEnabled(true);
		}

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

	private Action getDownloadAction() {
		if (downloadAction == null) {
			downloadAction = new DownloadAction();
		}
		return downloadAction;
	}

	public JPopupMenu getJPopupMenu() {
		return this;
	}

	private JMenuItem getMenuItem() {
		if (menuItem == null) {
			menuItem = new JMenuItem("Download selected files");
			menuItem.setAction(getDownloadAction());
		}
		return menuItem;
	}

	public void isLoading(boolean loading) {

	}

	public void setFileListPanel(FileListPanel panel) {
		this.fileListPanel = panel;
		this.fileListPanel.addFileListListener(this);
	}
}
