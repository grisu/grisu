package org.vpac.grisu.frontend.control.fileTransfers;


public class FileTransferEvent {

	private final FileTransaction fileTransfer;

	public FileTransferEvent(FileTransaction ft) {
		this.fileTransfer = ft;
	}

	public FileTransaction getFileTransfer() {
		return this.fileTransfer;
	}

}
