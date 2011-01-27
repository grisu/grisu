package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.frontend.control.fileTransfers.FileTransaction;

public class FileTransactionFailedEvent {

	private final FileTransaction ft;

	public FileTransactionFailedEvent(FileTransaction ft) {
		this.ft = ft;
	}

	public FileTransaction getFileTransaction() {
		return ft;
	}

}
