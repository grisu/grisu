package grisu.frontend.view.swing.files.contextMenu;

import grisu.frontend.control.fileTransfers.FileTransaction;
import grisu.frontend.control.fileTransfers.FileTransactionManager;
import grisu.frontend.view.swing.files.FileTransactionStatusDialog;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

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

		final FileTransaction ft = ftm.deleteFiles(files);

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				final JFrame frame = (JFrame) SwingUtilities.getRoot(fileList
						.getPanel());
				final FileTransactionStatusDialog ftd = new FileTransactionStatusDialog(
						frame);
				ftd.setFileTransaction(ft);

				ftd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				ftd.setVisible(true);
				ftm.addFileTransfer(ft);
			}

		});

	}
}
