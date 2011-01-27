package org.vpac.grisu.frontend.view.swing.files.contextMenu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanel;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;

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

		CreateFolderAction.this.fm.createFolder(fileList.getCurrentDirectory(),
				s);

	}

}
