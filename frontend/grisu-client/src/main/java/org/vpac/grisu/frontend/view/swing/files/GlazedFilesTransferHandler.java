package org.vpac.grisu.frontend.view.swing.files;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransfer;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransferManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.files.GlazedFile;

public class GlazedFilesTransferHandler extends TransferHandler {

	public static final String LOCAL_SET_TYPE = DataFlavor.javaJVMLocalObjectMimeType	+ ";class=java.util.Set";
	{
		try {
			SET_DATA_FLAVOR = new DataFlavor(LOCAL_SET_TYPE);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

	}
	public static DataFlavor SET_DATA_FLAVOR;

	private final ServiceInterface si;
	private final FileTransferManager ftm;
	private final FileManager fm;
	private final FileListPanel fileList;

	private final int[] rows = null;

	private final int addIndex = -1; //Location where items were added

	private final int addCount = 0; //Number of items added.

	public GlazedFilesTransferHandler(FileListPanel fileList, ServiceInterface si) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.ftm = FileTransferManager.getDefault(si);
		this.fileList = fileList;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {

		GlazedFile.Type type = fileList.getCurrentDirectory().getType();

		if ( GlazedFile.Type.FILETYPE_FOLDER.equals(type) ) {
			for (DataFlavor flavor : flavors) {
				if (SET_DATA_FLAVOR.equals(flavor)) {
					return true;
				}
			}
		}

		return false;
	}



	@Override
	protected Transferable createTransferable(JComponent c) {

		if ( c instanceof JTable ) {

			JTable table = (JTable)c;

			Set<GlazedFile> selected = new TreeSet<GlazedFile>();

			for (int r : table.getSelectedRows()) {

				if (r >= 0) {
					GlazedFile sel = (GlazedFile) table.getValueAt(r, 0);
					if ( GlazedFile.Type.FILETYPE_FILE.equals(sel.getType()) || GlazedFile.Type.FILETYPE_FOLDER.equals(sel.getType()) ) {
						selected.add(sel);
					} else {
						return null;
					}

				}

			}
			return new GlazedFilesTransferable(selected);
		}

		throw new RuntimeException("Container of class"+c.getClass().toString()+ " not supported.");
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		//TODO ?
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		if (canImport(c, t.getTransferDataFlavors())) {
			try {
				importGlazedFilesSet((Set<GlazedFile>)t.getTransferData(SET_DATA_FLAVOR));
				return true;
			} catch (UnsupportedFlavorException ufe) {
				ufe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return false;
	}

	protected void importGlazedFilesSet(Set<GlazedFile> glazedFiles) {

		FileTransfer ft = new FileTransfer(fm, glazedFiles, fileList.getCurrentDirectory(), true);
		ftm.addFileTransfer(ft);

		FileTransferStatusDialog ftd = new FileTransferStatusDialog(ft);

		ftd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		ftd.setVisible(true);

	}

}
