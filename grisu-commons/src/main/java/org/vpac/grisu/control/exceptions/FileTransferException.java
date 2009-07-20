package org.vpac.grisu.control.exceptions;

public class FileTransferException extends Exception {

	private final String sourceFileUrl;

	public final String getSourceFileUrl() {
		return sourceFileUrl;
	}

	public final String getTargetFileUrl() {
		return targetFileUrl;
	}

	private final String targetFileUrl;

	public FileTransferException(final String sourceFileUrl, final String targetFileUrl) {
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

	public FileTransferException(final String sourceFileUrl, final String targetFileUrl,
			final String arg0) {
		super(arg0);
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

	public FileTransferException(final String sourceFileUrl, final String targetFileUrl,
			final Throwable arg0) {
		super(arg0);
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

	public FileTransferException(final String sourceFileUrl, final String targetFileUrl,
			final String arg0, final Throwable arg1) {
		super(arg0, arg1);
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

}