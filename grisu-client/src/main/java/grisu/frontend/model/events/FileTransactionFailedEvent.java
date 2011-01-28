package grisu.frontend.model.events;

import grisu.frontend.control.fileTransfers.FileTransaction;

public class FileTransactionFailedEvent {

	private final FileTransaction ft;

	public FileTransactionFailedEvent(FileTransaction ft) {
		this.ft = ft;
	}

	public FileTransaction getFileTransaction() {
		return ft;
	}

}
