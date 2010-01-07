package org.vpac.grisu.frontend.control.fileTransfers;


public class FileTransferEvent {

	private final FileTransfer fileTransfer;

	public FileTransferEvent(FileTransfer ft) {
		this.fileTransfer = ft;
	}

	public FileTransfer getFileTransfer() {
		return this.fileTransfer;
	}

}
