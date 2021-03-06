package grisu.frontend.control.clientexceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "grisu.control.jaxws.exceptions.FileTransactionException")
public class FileTransactionException extends Exception {

	private final String sourceFileUrl;

	private final String targetFileUrl;

	public FileTransactionException(final String sourceFileUrl,
			final String targetFileUrl, final String message,
			final Throwable cause) {
		super(message, cause);
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

	public final String getSourceFileUrl() {
		return sourceFileUrl;
	}

	// public FileTransferException(final String sourceFileUrl, final String
	// targetFileUrl) {
	// this.sourceFileUrl = sourceFileUrl;
	// this.targetFileUrl = targetFileUrl;
	// }
	//
	// public FileTransferException(final String sourceFileUrl, final String
	// targetFileUrl,
	// final String arg0) {
	// super(arg0);
	// this.sourceFileUrl = sourceFileUrl;
	// this.targetFileUrl = targetFileUrl;
	// }
	//
	// public FileTransferException(final String sourceFileUrl, final String
	// targetFileUrl,
	// final Throwable arg0) {
	// super(arg0);
	// this.sourceFileUrl = sourceFileUrl;
	// this.targetFileUrl = targetFileUrl;
	// }

	public final String getTargetFileUrl() {
		return targetFileUrl;
	}

}
