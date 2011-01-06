package org.vpac.grisu.frontend.view.swing.files.contextMenu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.vpac.grisu.X;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanel;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

public class RefreshAction extends AbstractAction {

	static final Logger myLogger = Logger.getLogger(RefreshAction.class
			.getName());

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	public RefreshAction(GridFileListPanel fileList) {
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "Refresh");
		putValue(SHORT_DESCRIPTION, "Refresh folder");
	}

	public void actionPerformed(ActionEvent e) {

		if (fileList.getSelectedFiles().size() != 1) {
			return;
		}

		GridFile f = fileList.getSelectedFiles().iterator().next();

		if (f.isFolder()) {
			X.p("REFRESH");
			fileList.refreshFolder(f.getUrl());
		}

	}

}
