package org.vpac.grisu.control.files;

import java.io.File;
import java.io.IOException;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.Environment;
import org.vpac.grisu.control.FileHelpers;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.ServiceInterface;

public class FileHelper {

	public static final String NON_MOUNTPOINT_CACHE_DIRECTORYNAME = "non-grisu-user-space";

	private final ServiceInterface serviceInterface;
	static final Logger myLogger = Logger.getLogger(FileHelper.class.getName());
//	private final GrisuRegistry registry;

	public FileHelper(ServiceInterface si) {
		this.serviceInterface = si;
//		this.registry = GrisuRegistry.getDefault(serviceInterface);
	}

	public void uploadFile(String sourcePath, String targetDirectory)
			throws FileTransferException {

		File file = new File(sourcePath);
		uploadFile(file, targetDirectory);

	}

	public static boolean isLocal(String file) {

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

	public static String GET_URL_STRING_PATH(String url) {
		return url.replace("=", "_").replace(",", "_").replace(" ", "_")
				.replace(":", "").replace("//", File.separator).replace("/",
						File.separator);
	}

	private File getLocalCacheFile(String url) {

		String rootPath = null;
		rootPath = Environment.GRISU_DIRECTORY+File.separator+Environment.CACHE_DIR_NAME + File.separator
				+ GET_URL_STRING_PATH(url);

		return new File(rootPath);

	}

	public File downloadFile(String url) throws FileTransferException {

		File cacheTargetFile = getLocalCacheFile(url);
		File cacheTargetParentFile = cacheTargetFile.getParentFile();
		
		if ( ! cacheTargetParentFile.exists() ) {
			if ( !cacheTargetParentFile.mkdirs() ) {
				if ( ! cacheTargetParentFile.exists() ) {
					throw new FileTransferException(url, cacheTargetFile.toString(), "Could not create parent folder for cache file.");
				}
			}
		}
		
		long lastModified = -1;
		try {
			lastModified = serviceInterface.lastModified(url);
		} catch (Exception e) {
			throw new FileTransferException(url, cacheTargetFile.toString(),
					"Could not get last modified time of source file.");
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
		try {

			source = serviceInterface.download(url);
		} catch (Exception e) {
			myLogger.error("Could not download file: " + url);
			throw new FileTransferException(url, cacheTargetFile.toString(), "Could not download file.", e);
		}

		try {
			FileHelpers.saveToDisk(source, cacheTargetFile);
			cacheTargetFile.setLastModified(lastModified);
		} catch (IOException e) {
			myLogger.error("Could not save file: " + url.lastIndexOf("/") + 1);
			throw new FileTransferException(url, cacheTargetFile.toString(), "Could not save file.", e);
		}
		
		return cacheTargetFile;
	}

	public void uploadFile(File file, String targetDirectory)
			throws FileTransferException {

		if (!file.exists()) {
			throw new FileTransferException("File does not exist: "
					+ file.toString(), null);
		}

		if (!file.canRead()) {
			throw new FileTransferException("Can't read file: "
					+ file.toString(), null);
		}

		// checking whether folder exists and is folder
		try {
			if (!serviceInterface.fileExists(targetDirectory)) {
				try {
					boolean success = serviceInterface.mkdir(targetDirectory);

					if (!success) {
						throw new FileTransferException(file.toString(),
								targetDirectory,
								"Could not create target directory.");
					}
				} catch (Exception e) {
					throw new FileTransferException(file.toString(),
							targetDirectory,
							"Could not create target directory.", e);
				}
			} else {
				try {
					if (!serviceInterface.isFolder(targetDirectory)) {
						throw new FileTransferException(file.toString(),
								targetDirectory,
								"Can't upload file. Target is a file.");
					}
				} catch (Exception e2) {
					myLogger
							.debug("Could not access target directory. Trying to create it...");

					try {
						boolean success = serviceInterface
								.mkdir(targetDirectory);

						if (!success) {
							throw new FileTransferException(file.toString(),
									targetDirectory,
									"Could not create target directory.");
						}
					} catch (Exception e) {
						throw new FileTransferException(file.toString(),
								targetDirectory,
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
					"Transfer of folders not supported yet.");
		} else {
			DataSource source = new FileDataSource(file);
			try {
				myLogger.info("Uploading file " + file.getName() + "...");
				String filetransferHandle = serviceInterface.upload(source,
						targetDirectory + "/" + file.getName(), true);
				myLogger.info("Upload of file " + file.getName()
						+ " successful.");
			} catch (Exception e1) {
				myLogger.info("Upload of file " + file.getName() + " failed: "
						+ e1.getLocalizedMessage());
				myLogger.error("File upload failed: "
						+ e1.getLocalizedMessage());
				throw new FileTransferException(file.toString(),
						targetDirectory, "Could not upload file.", e1);
			}
		}

	}

}