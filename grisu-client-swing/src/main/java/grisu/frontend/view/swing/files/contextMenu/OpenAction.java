package grisu.frontend.view.swing.files.contextMenu;

import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.frontend.control.fileTransfers.FileTransactionManager;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.frontend.view.swing.utils.BackgroundActionProgressDialogSmall;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;


public class OpenAction extends AbstractAction {

	static final Logger myLogger = Logger.getLogger(OpenAction.class.getName());

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	private final Desktop desktop;

	public OpenAction(GridFileListPanel fileList) {
		try {
			desktop = Desktop.getDesktop();
		} catch (Exception e) {
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
					BackgroundActionProgressDialogSmall d = null;

					try {
						d = new BackgroundActionProgressDialogSmall(
								"Downloading:", f.getName());
						file = fm.downloadFile(f.getUrl());
					} catch (FileTransactionException e1) {
						myLogger.error(e1);
						return;
					} finally {
						d.close();
					}

					try {
						desktop.open(file);
					} catch (IOException e) {
						myLogger.error(e);
					}

				}
			}.start();

		}
	}
}
