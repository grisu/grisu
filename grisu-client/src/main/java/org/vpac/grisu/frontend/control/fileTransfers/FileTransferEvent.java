package org.vpac.grisu.frontend.control.fileTransfers;

public class FileTransferEvent {

	private final FileTransaction fileTransfer;

	private final String propertyChanged;
	private final Object newValue;

	public FileTransferEvent(FileTransaction ft, String propertyChanged,
			Object newValue) {
		this.fileTransfer = ft;
		this.propertyChanged = propertyChanged;
		this.newValue = newValue;

		// Thread.dumpStack();
	}

	public String getChangedProperty() {
		return propertyChanged;
	}

	public FileTransaction getFileTransfer() {
		return this.fileTransfer;
	}

	public Object getNewValue() {
		return newValue;
	}

}
