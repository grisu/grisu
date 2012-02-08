package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.frontend.control.fileTransfers.FileTransaction;
import grisu.frontend.control.fileTransfers.FileTransactionManager;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.files.GlazedFile;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlazedFilesTransferHandler extends TransferHandler {

	static final Logger myLogger = LoggerFactory
			.getLogger(GlazedFilesTransferHandler.class);

	public static final String LOCAL_SET_TYPE = DataFlavor.javaJVMLocalObjectMimeType
			+ ";class=java.util.Set";
	{
		try {
			SET_DATA_FLAVOR = new DataFlavor(LOCAL_SET_TYPE);
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

	}
	public static DataFlavor SET_DATA_FLAVOR;

	private final ServiceInterface si;
	private final FileTransactionManager ftm;
	private final FileManager fm;
	private final FileListPanel fileList;

	private final int[] rows = null;

	private final int addIndex = -1; // Location where items were added

	private final int addCount = 0; // Number of items added.

	public GlazedFilesTransferHandler(FileListPanel fileList,
			ServiceInterface si) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.ftm = FileTransactionManager.getDefault(si);
		this.fileList = fileList;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {

		final GlazedFile.Type type = fileList.getCurrentDirectory().getType();

		if (GlazedFile.Type.FILETYPE_FOLDER.equals(type)) {
			for (final DataFlavor flavor : flavors) {
				if (SET_DATA_FLAVOR.equals(flavor)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {

		if (c instanceof JTable) {

			final JTable table = (JTable) c;

			final Set<GlazedFile> selected = new TreeSet<GlazedFile>();

			for (final int r : table.getSelectedRows()) {

				if (r >= 0) {
					final GlazedFile sel = (GlazedFile) table.getValueAt(r, 0);
					if (GlazedFile.Type.FILETYPE_FILE.equals(sel.getType())
							|| GlazedFile.Type.FILETYPE_FOLDER.equals(sel
									.getType())) {
						selected.add(sel);
					} else {
						return null;
					}

				}

			}
			return new GlazedFilesTransferable(selected);
		}

		throw new RuntimeException("Container of class"
				+ c.getClass().toString() + " not supported.");
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		// TODO ?
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		if (canImport(c, t.getTransferDataFlavors())) {
			try {
				importGlazedFilesSet((Set<GlazedFile>) t
						.getTransferData(SET_DATA_FLAVOR));
				return true;
			} catch (final UnsupportedFlavorException ufe) {
				myLogger.error(ufe.getLocalizedMessage(), ufe);
			} catch (final IOException ioe) {
				myLogger.error(ioe.getLocalizedMessage(), ioe);
			}
		}

		return false;
	}

	protected void importGlazedFilesSet(Set<GlazedFile> glazedFiles) {

		final FileTransaction ft = new FileTransaction(fm, glazedFiles,
				fileList.getCurrentDirectory(), true);

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
