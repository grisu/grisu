package grisu.frontend.view.swing.files.contextMenu;

import grisu.frontend.control.fileTransfers.FileTransactionManager;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyUrlsAction extends AbstractAction {

	static final Logger myLogger = LoggerFactory.getLogger(CopyUrlsAction.class
			.getName());

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	public CopyUrlsAction(GridFileListPanel fileList) {
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "Copy url(s) to clipboard");
		putValue(SHORT_DESCRIPTION,
				"Copy url(s) of selected file(s) to clipboard");
	}

	public void actionPerformed(ActionEvent e) {

		final List<String> temp = new LinkedList<String>();
		for (final GridFile file : fileList.getSelectedFiles()) {
			temp.add(file.getUrl());
		}

		final String selection = StringUtils.join(temp, " ");

		final StringSelection data = new StringSelection(selection);
		final Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
		clipboard.setContents(data, data);
	}

}
