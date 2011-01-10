package org.vpac.grisu.frontend.view.swing.files.contextMenu;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.vpac.grisu.X;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanel;
import org.vpac.grisu.frontend.view.swing.files.preview.GridFilePreviewDialog;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

public class ViewAction extends AbstractAction {

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	public ViewAction(GridFileListPanel fileList) {
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "View");
		putValue(SHORT_DESCRIPTION, "Preview selected file(s)");
	}

	public void actionPerformed(ActionEvent e) {

		final Set<GridFile> files = fileList.getSelectedFiles();
		if ((files == null) || (files.size() <= 0)) {
			return;
		}

		for (final GridFile f : files) {

			X.p("File: " + f.getName());

			SwingUtilities.invokeLater(new Thread() {

				@Override
				public void run() {
					final GridFilePreviewDialog dialog = new GridFilePreviewDialog(
							fileList.getServiceInterface());
					dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);

					new Thread() {
						@Override
						public void run() {
							dialog.setFile(f, null);
						}
					}.start();

				}
			});

		}
	}
}
