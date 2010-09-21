package org.vpac.grisu.backend.model;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.globus.ftp.MarkerListener;
import org.vpac.grisu.backend.utils.DummyMarkerImpl;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.settings.ServerPropertiesManager;

import uk.ac.dl.escience.vfs.util.VFSUtil;

public class RemoteFileTransferObject {

	static final Logger myLogger = Logger
			.getLogger(RemoteFileTransferObject.class.getName());

	private final MarkerListener dummyMarker = new DummyMarkerImpl();

	private final Thread fileTransferThread;

	private final FileObject source;
	private final FileObject target;
	private final boolean overwrite;

	private boolean failed = false;

	private boolean finished = false;

	private Exception possibleException;

	private final Map<Date, String> messages = new TreeMap<Date, String>();

	public RemoteFileTransferObject(final FileObject sourceF,
			final FileObject targetF, final boolean overwriteB) {
		this.source = sourceF;
		this.target = targetF;

		this.overwrite = overwriteB;

		fileTransferThread = new Thread() {
			@Override
			public void run() {

				try {
					for (int tryNo = 0; tryNo <= ServerPropertiesManager
							.getFileTransferRetries(); tryNo++) {

						myLogger.debug(tryNo + 1 + ". try to transfer file: "
								+ source.getName().getURI()
								+ target.getName().getURI());
						try {
							myLogger.info("Copy thread started for target: "
									+ target.getName());
							transferFile(source, target, overwrite);
							finished = true;
							break;
						} catch (final RemoteFileSystemException e) {
							e.printStackTrace();
							if (tryNo >= ServerPropertiesManager
									.getFileTransferRetries() - 1) {
								finished = true;
								failed = true;
								possibleException = e;
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
					// try {
					// sourceF.getFileSystem().getFileSystemManager().closeFileSystem(sourceF.getFileSystem());
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// try {
					// targetF.getFileSystem().getFileSystemManager().closeFileSystem(sourceF.getFileSystem());
					// } catch (Exception e) {
					// e.printStackTrace();
					// }

				}

			}
		};

	}

	public final Thread getFileTransferThread() {

		return this.fileTransferThread;

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
			fileTransferThread.join();
		} catch (final InterruptedException e) {
			messages.put(new Date(), "File transfer thread interrupted.");
			Thread.currentThread().interrupt();
		}

	}

	public final void startTransfer(boolean waitForTransferToFinish) {

		// transferFile(source, target, overwrite);
		messages.put(new Date(), "Transfer started.");
		fileTransferThread.start();

		if (waitForTransferToFinish) {
			joinFileTransfer();
		}

	}

	private void transferFile(final FileObject source_file,
			final FileObject target_file, final boolean overwrite)
			throws RemoteFileSystemException {

		try {

			if (source_file.getName().getURI()
					.equals(target_file.getName().getURI())) {
				messages.put(new Date(),
						"Input file and target file are the same. No need to copy...");
				return;

			}

			messages.put(new Date(), "Checking source file...");
			if (!source_file.exists()) {
				throw new RemoteFileSystemException("Could not copy file: "
						+ source_file.getURL().toString() + ": "
						+ "InputFile does not exist.");
			}

			messages.put(new Date(), "Checking target file...");
			// TODO check whether target is folder, if so, continue, also don't
			// worry about deleting the file because the VFSUtil method takes
			// care of that
			if (!overwrite && target_file.exists()) {
				messages.put(new Date(),
						"Target file exists and overwrite mode not enabled. Cancelling transfer...");
				throw new RemoteFileSystemException("Could not copy to file: "
						+ target_file.getURL().toString() + ": "
						+ "InputFile exists.");
			} else if (target_file.exists()) {
				if (!target_file.delete()) {
					messages.put(new Date(),
							"Target file exists and can not be deleted. Cancelling transfer");
					throw new RemoteFileSystemException(
							"Could not copy to file: "
									+ target_file.getURL().toString() + ": "
									+ "Could not delete target file.");
				}
			}
			myLogger.debug("Copying: " + source_file.getName().toString()
					+ " to: " + target_file.getName().toString());
			// target_file.copyFrom(source_file, new AllFileSelector());
			//
			// if (!target_file.exists()) {
			// throw new RemoteFileSystemException("Could not copy file: "
			// + source + " to: " + target
			// + ": target file does not exist after copying.");
			// }

			try {
				messages.put(new Date(), "Starting file transfer...");
				VFSUtil.copy(source_file, target_file, dummyMarker, true);
				messages.put(new Date(), "Transfer finished...");
			} catch (final IOException e) {
				messages.put(new Date(), "File transfer failed (io problem): "
						+ e.getLocalizedMessage());
				throw new RemoteFileSystemException("Could not copy \""
						+ source_file.getURL().toString() + "\" to \""
						+ target_file.getURL().toString() + "\": "
						+ e.getMessage());
			}

		} catch (final FileSystemException e) {
			messages.put(new Date(),
					"File transfer failed: " + e.getLocalizedMessage());
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
