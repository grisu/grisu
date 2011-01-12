package org.vpac.grisu.frontend.view.swing.files.contextMenu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransaction;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanel;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

public class DeleteAction extends AbstractAction {

	static final Logger myLogger = Logger.getLogger(DeleteAction.class
			.getName());

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	public DeleteAction(GridFileListPanel fileList) {
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "Delete");
		putValue(SHORT_DESCRIPTION, "Delete selected file(s)");
	}

	public void actionPerformed(final ActionEvent e) {

		if (fileList == null) {
			myLogger.error("Filelistpanel not set. Can't delete anything...");
			return;
		}

		final StringBuffer msg = new StringBuffer(
				"Do you really want to delete below files?\n\n");
		final Set<GridFile> files = fileList.getSelectedFiles();
		for (final GridFile file : files) {
			msg.append(file.getName() + "\n");
		}

		final JFrame frame = (JFrame) SwingUtilities.getRoot(fileList
				.getPanel());

		final int n = JOptionPane.showConfirmDialog(frame, msg.toString(),
				"Confirm delete files", JOptionPane.YES_NO_OPTION);

		if (n == JOptionPane.NO_OPTION) {
			return;
		}

		final FileTransaction transaction = ftm.deleteFiles(files);

		new Thread() {
			@Override
			public void run() {
				try {
					transaction.join();

					if (transaction.getException() != null) {
						transaction.getException().printStackTrace();
					}
				} catch (Exception e1) {
					Component c = null;
					try {
						c = (Component) e.getSource();
					} catch (Exception eee) {
					}
					JOptionPane.showMessageDialog(c, e1.getLocalizedMessage(),
							"Delete error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		}.start();

	}
}
