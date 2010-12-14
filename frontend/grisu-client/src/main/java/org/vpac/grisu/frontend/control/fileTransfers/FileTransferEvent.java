package org.vpac.grisu.frontend.control.fileTransfers;

public class FileTransferEvent {

	private final FileTransaction fileTransfer;

	private final String propertyChanged;

	public FileTransferEvent(FileTransaction ft, String propertyChanged) {
		this.fileTransfer = ft;
		this.propertyChanged = propertyChanged;

//		Thread.dumpStack();
	}

	public String getChangedProperty() {
		return propertyChanged;
	}

	public FileTransaction getFileTransfer() {
		return this.fileTransfer;
	}

}
