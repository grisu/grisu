package grisu.frontend.view.swing.files;

import grisu.model.files.GlazedFile;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;


public class GlazedFilesTransferable implements Transferable {

	public final DataFlavor[] data_flavors;
	private final Set<GlazedFile> files;

	public GlazedFilesTransferable(Set<GlazedFile> files) {
		this.files = files;
		data_flavors = new DataFlavor[] { GlazedFilesTransferHandler.SET_DATA_FLAVOR };

	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {

		if (flavor.equals(GlazedFilesTransferHandler.SET_DATA_FLAVOR)) {
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
