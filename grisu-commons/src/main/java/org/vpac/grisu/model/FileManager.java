package org.vpac.grisu.model;

import java.io.File;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.FileTransferException;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.utils.FileHelpers;

/**
 * A class to make file-related stuff like transfers from/to the backend easier.
 * 
 * It also manages an internal cache.
 * 
 * @author Markus Binsteiner
 * 
 */
public class FileManager {

	public static final String NON_MOUNTPOINT_CACHE_DIRECTORYNAME = "non-grisu-user-space";

	private final ServiceInterface serviceInterface;
	static final Logger myLogger = Logger
			.getLogger(FileManager.class.getName());

	/**
	 * Default constructor.
	 * 
	 * @param si
	 *            the serviceInterface
	 */
	public FileManager(final ServiceInterface si) {
		this.serviceInterface = si;
	}

	/**
	 * Uploads a file to the backend which forwards it to it's target
	 * destination.
	 * 
	 * @param sourcePath
	 *            the path to the local file
	 * @param targetDirectory
	 *            the target url
	 * @throws FileTransferException
	 *             if the transfer fails
	 */
	public final void uploadFile(final String sourcePath,
			final String targetDirectory) throws FileTransferException {

		File file = new File(sourcePath);
		uploadFile(file, targetDirectory);

	}

	/**
	 * Helper method to check whether the provided url is for a local file or
	 * not.
	 * 
	 * @param file
	 *            the url of the file
	 * @return whether the file is local or not.
	 */
	public static boolean isLocal(final String file) {

		if (file.startsWith("gsiftp")) {
			return false;
		} else if (file.startsWith("file:")) {
			return true;
		} else if (file.startsWith("http:")) {
			return false;
		} else {
			return true;
		}

	}

	private static String get_url_strin_path(final String url) {
		return url.replace("=", "_").replace(",", "_").replace(" ", "_")
				.replace(":", "").replace("//", File.separator).replace("/",
						File.separator);
	}

	private File getLocalCacheFile(final String url) {

		String rootPath = null;
		rootPath = Environment.getGrisuLocalCacheRoot() + File.separator
				+ get_url_strin_path(url);

		return new File(rootPath);

	}

	/**
	 * Downloads the file with the specified url into the local cache and
	 * returns a file object for it.
	 * 
	 * @param url
	 *            the source url
	 * @return the file object for the cached file
	 * @throws FileTransferException
	 *             if the transfer fails
	 */
	public final File downloadFile(final String url)
			throws FileTransferException {

		File cacheTargetFile = getLocalCacheFile(url);
		File cacheTargetParentFile = cacheTargetFile.getParentFile();

		if (!cacheTargetParentFile.exists()) {
			if (!cacheTargetParentFile.mkdirs()) {
				if (!cacheTargetParentFile.exists()) {
					throw new FileTransferException(url, cacheTargetFile
							.toString(),
							"Could not create parent folder for cache file.",
							null);
				}
			}
		}

		long lastModified = -1;
		try {
			lastModified = serviceInterface.lastModified(url);
		} catch (Exception e) {
			throw new FileTransferException(url, cacheTargetFile.toString(),
					"Could not get last modified time of source file.", null);
		}

		if (cacheTargetFile.exists()) {
			// check last modified date
			long local_last_modified = cacheTargetFile.lastModified();
			myLogger.debug("local file timestamp:\t" + local_last_modified);
			myLogger.debug("remote file timestamp:\t" + lastModified);
			if (local_last_modified >= lastModified) {
				myLogger
						.debug("Local cache file is not older than remote file. Doing nothing...");
				return cacheTargetFile;
			}
		}

		myLogger
				.debug("Remote file newer than local cache file or not cached yet, downloading new copy.");
		DataSource source = null;
		DataHandler handler = null;
		try {

			handler = serviceInterface.download(url);
		} catch (Exception e) {
			myLogger.error("Could not download file: " + url);
			throw new FileTransferException(url, cacheTargetFile.toString(),
					"Could not download file.", e);
		}

		try {
			FileHelpers.saveToDisk(handler.getDataSource(), cacheTargetFile);
			cacheTargetFile.setLastModified(lastModified);
		} catch (IOException e) {
			myLogger.error("Could not save file: " + url.lastIndexOf("/") + 1);
			throw new FileTransferException(url, cacheTargetFile.toString(),
					"Could not save file.", e);
		}

		return cacheTargetFile;
	}

	/**
	 * Uploads a file to the backend which forwards it to it's target
	 * destination.
	 * 
	 * @param file
	 *            the source file
	 * @param sourcePath
	 *            the local file
	 * @param targetDirectory
	 *            the target url
	 * @throws FileTransferException
	 *             if the transfer fails
	 */
	public final void uploadFile(final File file, final String targetDirectory)
			throws FileTransferException {

		if (!file.exists()) {
			throw new FileTransferException(file.toString(), targetDirectory,
					"File does not exist: " + file.toString(), null);
		}

		if (!file.canRead()) {
			throw new FileTransferException(file.toString(), targetDirectory,
					"Can't read file: " + file.toString(), null);
		}

		// checking whether folder exists and is folder
		try {
			if (!serviceInterface.fileExists(targetDirectory)) {
				try {
					boolean success = serviceInterface.mkdir(targetDirectory);

					if (!success) {
						throw new FileTransferException(
								file.toURL().toString(), targetDirectory,
								"Could not create target directory.", null);
					}
				} catch (Exception e) {
					throw new FileTransferException(file.toURL().toString(),
							targetDirectory,
							"Could not create target directory.", e);
				}
			} else {
				try {
					if (!serviceInterface.isFolder(targetDirectory)) {
						throw new FileTransferException(
								file.toURL().toString(), targetDirectory,
								"Can't upload file. Target is a file.", null);
					}
				} catch (Exception e2) {
					myLogger
							.debug("Could not access target directory. Trying to create it...");

					try {
						boolean success = serviceInterface
								.mkdir(targetDirectory);

						if (!success) {
							throw new FileTransferException(file.toURL()
									.toString(), targetDirectory,
									"Could not create target directory.", null);
						}
					} catch (Exception e) {
						throw new FileTransferException(
								file.toURL().toString(), targetDirectory,
								"Could not create target directory.", e);
					}
				}
			}

		} catch (Exception e) {
			throw new FileTransferException(file.toString(), targetDirectory,
					"Could not determine whether target directory exists: ", e);
		}

		myLogger.debug("Uploading local file: " + file.toString() + " to: "
				+ targetDirectory);

		if (file.isDirectory()) {
			throw new FileTransferException(file.toString(), targetDirectory,
					"Transfer of folders not supported yet.", null);
		} else {
			DataHandler handler = createDataHandler(file);
			String filetransferHandle = null;
			try {
				myLogger.info("Uploading file " + file.getName() + "...");
				filetransferHandle = serviceInterface.upload(handler,
						targetDirectory + "/" + file.getName(), true);
				myLogger.info("Upload of file " + file.getName()
						+ " successful.");
			} catch (Exception e1) {
				try {
					// try again
					myLogger.info("Uploading file " + file.getName() + "...");
					System.out.println("FAILED. SLEEPING 2 SECONDS");
					Thread.sleep(2000);
					filetransferHandle = serviceInterface.upload(handler,
							targetDirectory + "/" + file.getName(), true);
					myLogger.info("Upload of file " + file.getName()
							+ " successful.");
				} catch (Exception e) {
					myLogger.info("Upload of file " + file.getName()
							+ " failed: " + e1.getLocalizedMessage());
					myLogger.error("File upload failed: "
							+ e1.getLocalizedMessage());
					throw new FileTransferException(file.toString(),
							targetDirectory, "Could not upload file.", e1);
				}
			}
		}

	}
	
	public static final DataHandler createDataHandler(File file) {
		DataSource source = new FileDataSource(file);
		DataHandler handler = new DataHandler(source);
		return handler;
	}
	
	public static final DataHandler createDataHandler(String file) {
		return createDataHandler(new File(file));
	}

}
