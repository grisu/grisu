package org.vpac.grisu.control.files;

public class FileTransferException extends Exception {
	
	private final String sourceFileUrl;
	public String getSourceFileUrl() {
		return sourceFileUrl;
	}

	public String getTargetFileUrl() {
		return targetFileUrl;
	}

	private final String targetFileUrl;

	public FileTransferException(String sourceFileUrl, String targetFileUrl) {
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

	public FileTransferException(String sourceFileUrl, String targetFileUrl, String arg0) {
		super(arg0);
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

	public FileTransferException(String sourceFileUrl, String targetFileUrl, Throwable arg0) {
		super(arg0);
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

	public FileTransferException(String sourceFileUrl, String targetFileUrl, String arg0, Throwable arg1) {
		super(arg0, arg1);
		this.sourceFileUrl = sourceFileUrl;
		this.targetFileUrl = targetFileUrl;
	}

}
