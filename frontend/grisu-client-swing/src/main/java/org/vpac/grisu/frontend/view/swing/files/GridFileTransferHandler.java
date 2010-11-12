package org.vpac.grisu.frontend.view.swing.files;

import java.awt.datatransfer.DataFlavor;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransactionManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoFileObject;

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

		final String type = fileList.getCurrentDirectory().getType();

		if (DtoFileObject.FILETYPE_FOLDER.equals(type)) {
			for (final DataFlavor flavor : flavors) {
				if (SET_DATA_FLAVOR.equals(flavor)) {
					return true;
				}
			}
		}

		return false;
	}

}
