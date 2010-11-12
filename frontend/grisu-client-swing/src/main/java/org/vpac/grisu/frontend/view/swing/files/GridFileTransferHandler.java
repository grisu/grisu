package org.vpac.grisu.frontend.view.swing.files;

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

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransaction;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.frontend.view.swing.files.virtual.VirtualFileTreeNode;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

public class GridFileTransferHandler extends TransferHandler {

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
	private final GridFileListPanel fileList;

	public GridFileTransferHandler(GridFileListPanel fileList,
			ServiceInterface si) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.ftm = FileTransactionManager.getDefault(si);
		this.fileList = fileList;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {

		// final String type = fileList.getCurrentDirectory().getType();
		//
		// if (GridFile.FILETYPE_FOLDER.equals(type)) {
		// for (final DataFlavor flavor : flavors) {
		// if (SET_DATA_FLAVOR.equals(flavor)) {
		// return true;
		// }
		// }
		// }

		return false;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {

		if (c instanceof JTable) {

			final JTable table = (JTable) c;

			final Set<GridFile> selected = new TreeSet<GridFile>();

			for (final int r : table.getSelectedRows()) {

				if (r >= 0) {
					final GridFile sel = (GridFile) ((VirtualFileTreeNode) (table
							.getValueAt(r, 0))).getUserObject();
					if (!sel.isVirtual()) {
						selected.add(sel);
					} else {
						return null;
					}

				}

			}
			return new GridFilesTransferable(selected);
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
				importGlazedFilesSet((Set<GridFile>) t
						.getTransferData(SET_DATA_FLAVOR));
				return true;
			} catch (final UnsupportedFlavorException ufe) {
				ufe.printStackTrace();
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return false;
	}

	protected void importGlazedFilesSet(Set<GridFile> gridFiles) {

		final FileTransaction ft = new FileTransaction(fm, gridFiles,
				fileList.getCurrentDirectory(), true);
		ftm.addFileTransfer(ft);

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				final JFrame frame = (JFrame) SwingUtilities.getRoot(fileList
						.getPanel());
				final FileTransactionStatusDialog ftd = new FileTransactionStatusDialog(
						frame, ft);

				ftd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				ftd.setVisible(true);
			}

		});

	}

}
