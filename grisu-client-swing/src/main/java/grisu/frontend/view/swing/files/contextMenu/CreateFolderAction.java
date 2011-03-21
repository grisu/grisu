package grisu.frontend.view.swing.files.contextMenu;

import grisu.frontend.control.fileTransfers.FileTransactionManager;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.frontend.view.swing.files.virtual.DropVirtualGridFileDialog;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

public class CreateFolderAction extends AbstractAction {

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	public CreateFolderAction(GridFileListPanel fileList) {
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "Create folder");
		putValue(SHORT_DESCRIPTION,
		"Creates a folder in the selected directory.");
	}

	public void actionPerformed(ActionEvent e) {
		final String s = (String) JOptionPane.showInputDialog(
				SwingUtilities.getRoot(fileList.getPanel()),
				"Name of the new folder:", "Create folder",
				JOptionPane.PLAIN_MESSAGE, null, null, "");

		if (StringUtils.isBlank(s)) {
			return;
		}

		GridFile parent = fileList.getCurrentDirectory();

		String parentUrl = parent.getUrl();
		try {

			if ( parent.isVirtual() ) {
				if ( parent.getUrls().size() == 1 ) {
					parentUrl = parent.getUrl();
				} else {

					DropVirtualGridFileDialog d = new DropVirtualGridFileDialog(
							"Create");
					d.setTargetGridFile(parent);
					d.setVisible(true);

					parentUrl = d.getSelectedUrl();

					if (StringUtils.isBlank(parentUrl)) {
						return;
					}
				}

				CreateFolderAction.this.fm.createFolder(parentUrl, s);
			} else {
				CreateFolderAction.this.fm.createFolder(parent, s);
			}


		} catch (Exception ex) {

			String msg = ex.getLocalizedMessage();
			ErrorInfo info = new ErrorInfo("Job archiving error", msg, null,
					"Error", null, Level.SEVERE, null);

			JXErrorPane pane = new JXErrorPane();
			pane.setErrorInfo(info);

			JXErrorPane.showDialog(fileList.getPanel(), pane);

		}

	}

}
