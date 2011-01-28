package grisu.frontend.view.swing.files.contextMenu;

import grisu.frontend.control.fileTransfers.FileTransaction;
import grisu.frontend.control.fileTransfers.FileTransactionManager;
import grisu.frontend.view.swing.files.FileTransactionStatusDialog;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


import com.jidesoft.swing.FolderChooser;

public class DownloadAction extends AbstractAction {

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	private static final List<String> _recentList = new ArrayList<String>();
	private static File _currentFolder = null;

	public DownloadAction(GridFileListPanel fileList) {
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "Download");
		putValue(SHORT_DESCRIPTION, "Download selected files");
	}

	public void actionPerformed(ActionEvent e) {
		final Set<GridFile> files = fileList.getSelectedFiles();
		if ((files == null) || (files.size() <= 0)) {
			return;
		}

		final FolderChooser _folderChooser = new FolderChooser();
		// _folderChooser.setCurrentDirectory(_currentFolder);
		// _folderChooser.setRecentList(_recentList);
		_folderChooser.setFileHidingEnabled(true);
		final int result = _folderChooser.showOpenDialog(SwingUtilities
				.getRootPane(fileList.getPanel()));

		if (result == JFileChooser.APPROVE_OPTION) {
			_currentFolder = _folderChooser.getSelectedFile();
			if (_recentList.contains(_currentFolder.toString())) {
				_recentList.remove(_currentFolder.toString());
			}
			_recentList.add(0, _currentFolder.toString());
			final File selectedFile = _folderChooser.getSelectedFile();
			if (selectedFile != null) {
				final Set<String> urls = new HashSet<String>();
				for (final GridFile file : files) {
					urls.add(file.getUrl());
				}
				final FileTransaction ft = new FileTransaction(fm, urls,
						selectedFile.toURI().toString(), true);

				final FileTransactionStatusDialog d = new FileTransactionStatusDialog(
						null);
				d.setFileTransaction(ft);
				d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				d.setVisible(true);
				ftm.addFileTransfer(ft);
			} else {
				return;
			}
		}

	}

}
