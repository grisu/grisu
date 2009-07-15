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

import uk.ac.dl.escience.vfs.util.VFSUtil;

public class RemoteFileTransferObject {

	static final Logger myLogger = Logger
			.getLogger(RemoteFileTransferObject.class.getName());

	private MarkerListener dummyMarker = new DummyMarkerImpl();

	private final Thread fileTransferThread;

	private final FileObject source;
	private final FileObject target;
	private final boolean overwrite;

	private Map<Date, String> messages = new TreeMap<Date, String>();

	public RemoteFileTransferObject(final FileObject sourceF, final FileObject targetF,
			final boolean overwriteB) {
		this.source = sourceF;
		this.target = targetF;

		this.overwrite = overwriteB;

		fileTransferThread = new Thread() {
			public void run() {
				try {
					myLogger.info("Copy thread started for target: "
							+ target.getName());
					transferFile(source, target, overwrite);
				} catch (RemoteFileSystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

	}

	public final Thread getFileTransferThread() {

		return this.fileTransferThread;

	}

	public final void joinFileTransfer() {

		try {
			fileTransferThread.join();
		} catch (InterruptedException e) {
			messages.put(new Date(), "File transfer thread interrupted.");
		}

	}

	public final void startTransfer() throws RemoteFileSystemException {

		transferFile(source, target, overwrite);

	}

	private void transferFile(final FileObject source_file, final FileObject target_file,
			final boolean overwrite) throws RemoteFileSystemException {

		try {
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
				messages
						.put(new Date(),
								"Target file exists and overwrite mode not enabled. Cancelling transfer...");
				throw new RemoteFileSystemException("Could not copy to file: "
						+ target_file.getURL().toString() + ": "
						+ "InputFile exists.");
			} else if (target_file.exists()) {
				if (!target_file.delete()) {
					messages
							.put(new Date(),
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
			} catch (IOException e) {
				messages.put(new Date(), "File transfer failed (io problem): "
						+ e.getLocalizedMessage());
				throw new RemoteFileSystemException("Could not copy \""
						+ source_file.getURL().toString() + "\" to \""
						+ target_file.getURL().toString() + "\": "
						+ e.getMessage());
			}

		} catch (FileSystemException e) {
			messages.put(new Date(), "File transfer failed: "
					+ e.getLocalizedMessage());
			try {
				throw new RemoteFileSystemException("Could not copy \""
						+ source_file.getURL().toString() + "\" to \""
						+ target_file.getURL().toString() + "\": "
						+ e.getMessage());
			} catch (FileSystemException e1) {
				throw new RemoteFileSystemException("Could not copy files...");
			}
		}

	}

}
