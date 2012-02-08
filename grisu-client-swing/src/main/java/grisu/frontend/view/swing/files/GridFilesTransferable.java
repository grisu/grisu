package grisu.frontend.view.swing.files;

import grisu.model.dto.GridFile;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class GridFilesTransferable implements Transferable {

	public final DataFlavor[] data_flavors;
	private final Set<GridFile> files;

	public GridFilesTransferable(Set<GridFile> files) {
		this.files = files;
		data_flavors = new DataFlavor[] { GridFileTransferHandler.SET_DATA_FLAVOR };

	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {

		if (flavor.equals(GridFileTransferHandler.SET_DATA_FLAVOR)) {
			return this.files;
		}

		throw new UnsupportedFlavorException(flavor);

	}

	public DataFlavor[] getTransferDataFlavors() {

		return data_flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {

		if (Arrays.binarySearch(getTransferDataFlavors(), flavor) >= 0) {
			return true;
		} else {
			return false;
		}

	}

}
