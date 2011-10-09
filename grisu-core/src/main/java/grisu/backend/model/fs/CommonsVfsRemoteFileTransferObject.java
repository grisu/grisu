package grisu.backend.model.fs;

import grisu.backend.model.FileSystemCache;
import grisu.backend.model.RemoteFileTransferObject;
import grisu.backend.utils.DummyMarkerImpl;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.settings.ServerPropertiesManager;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.globus.ftp.MarkerListener;

import uk.ac.dl.escience.vfs.util.VFSUtil;

public class CommonsVfsRemoteFileTransferObject implements
RemoteFileTransferObject {

	private final Thread fileTransferThread;

	private final FileObject source;
	private final FileObject target;
	private final boolean overwrite;

	private boolean failed = false;

	private boolean finished = false;

	private Exception possibleException;

	private final Map<Date, String> messages = new TreeMap<Date, String>();

	private final MarkerListener dummyMarker = new DummyMarkerImpl();

	private final FileSystemCache fsCache;

	private final String id = "TRANSFER_" + UUID.randomUUID().toString();

	static final Logger myLogger = Logger
			.getLogger(CommonsVfsRemoteFileTransferObject.class.getName());

	public CommonsVfsRemoteFileTransferObject(final FileSystemCache fsCache,
			final FileObject sourceF, final FileObject targetF,
			final boolean overwriteB) {
		this.fsCache = fsCache;
		this.source = sourceF;
		this.target = targetF;

		myLogger.debug("Creating file transfer object for "
				+ source.getName().getURI() + " -> "
				+ target.getName().getURI() + ". ID: " + id);

		this.overwrite = overwriteB;

		fileTransferThread = new Thread() {
			@Override
			public void run() {

				try {
					for (int tryNo = 0; tryNo <= ServerPropertiesManager
							.getFileTransferRetries(); tryNo++) {

						myLogger.debug(id + ": " + (tryNo + 1)
								+ ". try to transfer file: "
								+ source.getName().getURI() + " => "
								+ target.getName().getURI());
						try {
							myLogger.info(id + ": "
									+ "Copy thread started for target: "
									+ target.getName());
							transferFile(source, target, overwrite);
							myLogger.info(id + ": "
									+ "Copy thread finished for target: "
									+ target.getName());

							finished = true;
							break;
						} catch (final RemoteFileSystemException e) {
							myLogger.error(id + ": Failed: "
									+ e.getLocalizedMessage());
							if (tryNo >= (ServerPropertiesManager
									.getFileTransferRetries() - 1)) {
								finished = true;
								failed = true;
								possibleException = e;
								myLogger.debug(id + ": Failed for good...");
							} else {
								// sleep for a few seconds, maybe the gridftp
								// server needs some rest
								try {
									Thread.sleep(ServerPropertiesManager
											.getWaitTimeBetweenFailedFileTransferAndNextTryInSeconds() * 1000);
								} catch (final InterruptedException e1) {
								}
							}
						}
					}
				} finally {
					myLogger.debug(id + ": finalizing...");
					try {
						myLogger.debug(id + ": closing source...");
						source.close();
						myLogger.debug(id + ": closed source.");
					} catch (FileSystemException ex){
						myLogger.warn(id + ex.getLocalizedMessage());
					}
					try {
						myLogger.debug(id + ": closing target...");
						target.close();
						myLogger.debug(id + ": closed target.");
					} catch (FileSystemException ex){
						myLogger.warn(id + ex.getLocalizedMessage());
					}
					try {
						myLogger.debug(id + ": closing filesystem...");
						fsCache.close();
						myLogger.debug(id + ": filesytem closed.");
					} catch (Exception e) {
						myLogger.warn(id + e.getLocalizedMessage());
					}
					myLogger.debug(id+": finalized...");
				}

			}
		};

	}

	protected void addMessage(String message) {
		messages.put(new Date(), message);
		myLogger.debug(id + ": " + message);
	}

	public final Thread getFileTransferThread() {

		return this.fileTransferThread;

	}

	public String getId() {
		return id;
	}

	public Exception getPossibleException() {
		return possibleException;
	}

	public String getPossibleExceptionMessage() {

		if (getPossibleException() == null) {
			return "";
		} else {
			return getPossibleException().getLocalizedMessage();
		}

	}

	public boolean isFailed() {
		return failed;
	}

	public boolean isFinished() {
		return finished;
	}

	public final void joinFileTransfer() {

		try {
			addMessage("Waiting for filetransfer thread to finish: " + id);
			fileTransferThread.join();
			addMessage("Filetransfer thread finished: " + id);
		} catch (final InterruptedException e) {
			addMessage("File transfer thread interrupted: " + id);
			Thread.currentThread().interrupt();
		} catch (Throwable t) {
			addMessage("File transfer exception " + id + ": "
					+ t.getLocalizedMessage());
		}

	}

	public final void startTransfer(boolean waitForTransferToFinish) {

		// transferFile(source, target, overwrite);
		messages.put(new Date(), "Transfer startint...");
		myLogger.debug("Starting transfer " + id);
		fileTransferThread.start();

		if (waitForTransferToFinish) {
			myLogger.debug("Joining transfer " + id);
			joinFileTransfer();
			myLogger.debug("Finshed transfer " + id);
		} else {
			myLogger.debug("Transfer started in background." + id);
		}

	}

	protected void transferFile(final FileObject source_file,
			final FileObject target_file, final boolean overwrite)
					throws RemoteFileSystemException {

		try {

			if (source_file.getName().getURI()
					.equals(target_file.getName().getURI())) {
				addMessage("Input file and target file are the same. No need to copy...");
				return;

			}

			addMessage("Checking source file...");
			if (!source_file.exists()) {
				throw new RemoteFileSystemException("Could not copy file: "
						+ source_file.getURL().toString() + ": "
						+ "InputFile does not exist.");
			}

			addMessage("Checking target file...");
			// TODO check whether target is folder, if so, continue, also don't
			// worry about deleting the file because the VFSUtil method takes
			// care of that
			if (!overwrite && target_file.exists()) {
				addMessage("Target file exists and overwrite mode not enabled. Cancelling transfer...");
				throw new RemoteFileSystemException("Could not copy to file: "
						+ target_file.getURL().toString() + ": "
						+ "InputFile exists.");
			} else if (target_file.exists()) {
				if (!target_file.delete()) {
					addMessage("Target file exists and can not be deleted. Cancelling transfer");
					throw new RemoteFileSystemException(
							"Could not copy to file: "
									+ target_file.getURL().toString() + ": "
									+ "Could not delete target file.");
				}
			}
			myLogger.debug(id + ": Copying: "
					+ source_file.getName().toString()
					+ " to: " + target_file.getName().toString());
			// target_file.copyFrom(source_file, new AllFileSelector());
			//
			// if (!target_file.exists()) {
			// throw new RemoteFileSystemException("Could not copy file: "
			// + source + " to: " + target
			// + ": target file does not exist after copying.");
			// }

			try {
				addMessage("Starting file transfer...");
				VFSUtil.copy(source_file, target_file, dummyMarker,
						true);
				addMessage("Transfer finished...");
			} catch (final IOException e) {
				addMessage("File transfer failed (io problem): "
						+ e.getLocalizedMessage());
				throw new RemoteFileSystemException("Could not copy \""
						+ source_file.getURL().toString() + "\" to \""
						+ target_file.getURL().toString() + "\": "
						+ e.getMessage());
			}

		} catch (final FileSystemException e) {
			addMessage("File transfer failed: " + e.getLocalizedMessage());
			try {
				throw new RemoteFileSystemException("Could not copy \""
						+ source_file.getURL().toString() + "\" to \""
						+ target_file.getURL().toString() + "\": "
						+ e.getMessage());
			} catch (final FileSystemException e1) {
				throw new RemoteFileSystemException("Could not copy files: "
						+ e1.getLocalizedMessage());
			}
		}

	}

}
