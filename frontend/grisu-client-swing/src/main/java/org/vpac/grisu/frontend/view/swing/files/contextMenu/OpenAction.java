package org.vpac.grisu.frontend.view.swing.files.contextMenu;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.swing.AbstractAction;

import org.vpac.grisu.frontend.control.clientexceptions.FileTransactionException;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanel;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

public class OpenAction extends AbstractAction {

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	private final Desktop desktop;

	public OpenAction(GridFileListPanel fileList) {
		try {
			desktop = Desktop.getDesktop();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "Open");
		putValue(SHORT_DESCRIPTION,
				"Open selected file(s) in native application");
	}

	public void actionPerformed(ActionEvent e) {

		final Set<GridFile> files = fileList.getSelectedFiles();
		if ((files == null) || (files.size() <= 0)) {
			return;
		}

		for (final GridFile f : files) {

			if (f.isFolder()) {
				continue;
			}

			new Thread() {
				@Override
				public void run() {

					File file;
					FileDownloadDialogSmall d = null;

					try {
						d = new FileDownloadDialogSmall(f);
						file = fm.downloadFile(f.getUrl());
					} catch (FileTransactionException e1) {
						e1.printStackTrace();
						return;
					} finally {
						d.close();
					}

					try {
						desktop.open(file);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}.start();

		}
	}
}
