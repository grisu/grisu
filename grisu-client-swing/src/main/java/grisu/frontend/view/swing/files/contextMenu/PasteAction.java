package grisu.frontend.view.swing.files.contextMenu;

import grisu.X;
import grisu.frontend.control.fileTransfers.FileTransaction;
import grisu.frontend.control.fileTransfers.FileTransactionManager;
import grisu.frontend.view.swing.files.FileTransactionStatusDialog;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.frontend.view.swing.files.virtual.DropVirtualGridFileDialog;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PasteAction extends AbstractAction {

	static final Logger myLogger = Logger.getLogger(CopyAction.class.getName());

	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	public PasteAction(GridFileListPanel fileList) {
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "Paste");
		putValue(SHORT_DESCRIPTION, "Paste copied file(s) from clipboard");
	}

	public void actionPerformed(ActionEvent e) {

		Clipboard clipboard = FileManager.FILE_TRANSFER_CLIPBOARD;

		Transferable t = clipboard.getContents(null);

		if (t == null) {
			X.p("Clipboard empty");
			return;
		}

		String filesString = null;

		try {
			// see if DataFlavor of
			// DataFlavor.stringFlavor is supported
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				// return text content
				filesString = (String) t
						.getTransferData(DataFlavor.stringFlavor);

			}
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
			return;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		}

		if (StringUtils.isBlank(filesString)) {
			X.p("String empty");
			return;
		}

		// get target
		final List<GridFile> temp = new LinkedList<GridFile>();
		for (final GridFile file : fileList.getSelectedFiles()) {
			if (!file.isFolder()) {
				X.p("One selected file is not folder.");
				return;
			}

			temp.add(file);
		}
		if (temp.size() != 1) {
			X.p("No or more than one file selected");
			return;
		}

		GridFile target = temp.get(0);

		String targetUrl = target.getUrl();
		if (target.getUrls().size() > 1) {
			DropVirtualGridFileDialog d = new DropVirtualGridFileDialog();
			d.setTargetGridFile(target);
			d.setVisible(true);

			targetUrl = d.getSelectedUrl();

			if (StringUtils.isBlank(targetUrl)) {
				X.p("User cancelled.");
				return;
			}

		}

		String[] files = filesString.split(" ");
		Set<String> sourceUrls = new TreeSet<String>(Arrays.asList(files));

		final FileTransaction ft = new FileTransaction(fm, sourceUrls,
				targetUrl);

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
