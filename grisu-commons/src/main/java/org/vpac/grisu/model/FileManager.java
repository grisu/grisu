package org.vpac.grisu.model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.clientexceptions.FileTransferException;
import org.vpac.grisu.model.dto.DtoFolder;
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

	public File getLocalCacheFile(final String url) {

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

	
	public final void uploadFile(final File file, final String targetFile, boolean overwrite) throws FileTransferException {
		
		if (!file.exists()) {
			throw new FileTransferException(file.toString(), targetFile,
					"File does not exist: " + file.toString(), null);
		}

		if (!file.canRead()) {
			throw new FileTransferException(file.toString(), targetFile,
					"Can't read file: " + file.toString(), null);
		}

		if (file.isDirectory()) {
			throw new FileTransferException(file.toString(), targetFile,
					"Transfer of folders not supported yet.", null);
		}

		// checking whether folder exists and is folder
		try {
			if (serviceInterface.fileExists(targetFile)) {

				if ( ! overwrite ) {
					throw new FileTransferException(file.toString(), targetFile, "Target file exists.", null);
				}
			}

		} catch (Exception e) {
			throw new FileTransferException(file.toString(), targetFile,
					"Could not determine whether target directory exists: ", e);
		}
		
		myLogger.debug("Uploading local file: " + file.toString() + " to: "
				+ targetFile);

		DataHandler handler = createDataHandler(file);
		String filetransferHandle = null;
		try {
			myLogger.info("Uploading file " + file.getName() + "...");
			filetransferHandle = serviceInterface.upload(handler,
					targetFile, true);
			myLogger.info("Upload of file " + file.getName() + " successful.");
		} catch (Exception e1) {
			try {
				// try again
				myLogger.info("Uploading file " + file.getName() + "...");
				System.out.println("FAILED. SLEEPING 1 SECONDS");
				Thread.sleep(1000);
				filetransferHandle = serviceInterface.upload(handler,
						targetFile + "/" + file.getName(), true);
				myLogger.info("Upload of file " + file.getName()
						+ " successful.");
			} catch (Exception e) {
				myLogger.info("Upload of file " + file.getName() + " failed: "
						+ e1.getLocalizedMessage());
				myLogger.error("File upload failed: "
						+ e1.getLocalizedMessage());
				throw new FileTransferException(file.toString(),
						targetFile, "Could not upload file.", e1);
			}
		}
		
		
		
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
	 *            the target directory url
	 * @param overwrite whether to overwrite a possibly existing target file
	 * @throws FileTransferException
	 *             if the transfer fails
	 */
	public final void uploadFileToDirectory(final File file, final String targetDirectory, final boolean overwrite)
			throws FileTransferException {

		if (!file.exists()) {
			throw new FileTransferException(file.toString(), targetDirectory,
					"File does not exist: " + file.toString(), null);
		}

		if (!file.canRead()) {
			throw new FileTransferException(file.toString(), targetDirectory,
					"Can't read file: " + file.toString(), null);
		}

		if (file.isDirectory()) {
			throw new FileTransferException(file.toString(), targetDirectory,
					"Transfer of folders not supported yet.", null);
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
					myLogger.debug("Could not access target directory.");

					throw new FileTransferException(file.toURL().toString(),
							targetDirectory,
							"Could not access target directory.", e2);
				}
			}

		} catch (Exception e) {
			throw new FileTransferException(file.toString(), targetDirectory,
					"Could not determine whether target directory exists: ", e);
		}

		myLogger.debug("Uploading local file: " + file.toString() + " to: "
				+ targetDirectory);

		uploadFile(file, targetDirectory + "/" + file.getName(), overwrite);

	}
	
	/**
	 * Uploads a file to the backend which forwards it to it's target
	 * destination.
	 * 
	 * @param sourcePath
	 *            the path to the local file
	 * @param targetDirectory
	 *            the target url
	 * @param overwrite whether to overwrite a possibly existing target file
	 * @throws FileTransferException
	 *             if the transfer fails
	 */
	public final void uploadFileToDirectory(final String sourcePath,
			final String targetDirectory, boolean overwrite) throws FileTransferException {

		File file = new File(sourcePath);
		if (file.isDirectory()) {
			throw new RuntimeException("Upload of folders not supported (yet).");
		}
		uploadFileToDirectory(file, targetDirectory, overwrite);

	}

	/**
	 * Helper method to extract the filename out of an url.
	 * 
	 * @param url the url
	 * @return the filename
	 */
	public static String getFilename(String url) {

		if ( isLocal(url) ) {
			return new File(url).getName();
		} else { 
			String filename = url.substring(url.lastIndexOf("/")+1);
			return filename;
		}

	}

	/**
	 * Downloads a remote file to the specified target.
	 * 
	 * If the target is an existing directory, the file will be put in there, if
	 * not, a file with that name will be created (along with all intermediate
	 * directories). If the target file already exists then you need to specify
	 * overwrite=true.
	 * 
	 * @param url
	 *            the url of the source file
	 * @param target
	 *            the target directory of file
	 * @param overwrite
	 *            whether to overwrite a possibly existing file
	 * @return the handle to the downloaded file
	 * @throws IOException
	 *             if the target isn't writable
	 * @throws FileTransferException
	 *             if the file can't be downloaded for some reason
	 */
	public File downloadFile(String url, String target, boolean overwrite)
			throws IOException, FileTransferException {

		File targetFile = new File(target);
		if (targetFile.exists() && targetFile.isDirectory()) {
			if (!targetFile.canWrite()) {
				throw new IOException("Can't write to target: "
						+ targetFile.toString());
			}
			targetFile = new File(target, getFilename(url));
			if (targetFile.exists()) {
				if (!overwrite) {
					throw new IOException("Can't download file to "
							+ targetFile + ". File already exists.");
				}
			}
		}

		File cacheFile = downloadFile(url);

		FileUtils.copyFile(cacheFile, targetFile);

		return targetFile;
	}

	/**
	 * Deletes the remote file and a possible local cache file.
	 * 
	 * @param url
	 *            the url of the remote file
	 * @throws RemoteFileSystemException
	 *             if the remote file can't be deleted for some reason
	 */
	public void deleteFile(String url) throws RemoteFileSystemException {

		File localCacheFile = getLocalCacheFile(url);

		if (localCacheFile.exists()) {
			FileUtils.deleteQuietly(localCacheFile);
		}

		serviceInterface.deleteFile(url);

	}
	
	public List<String> listAllChildrenFilesOfRemoteFolder(String folderUrl) throws RemoteFileSystemException {
		
		if ( ! serviceInterface.isFolder(folderUrl) ) {
			throw new IllegalArgumentException("Specified url is not a folder.");
		}
		
		DtoFolder folder = serviceInterface.ls(folderUrl, 0);
		
		return folder.listOfAllFilesUnderThisFolder();
	}

	/**
	 * Convenience method to create a datahandler out of a file.
	 * 
	 * @param file
	 *            the file
	 * @return the datahandler
	 */
	public static final DataHandler createDataHandler(File file) {
		DataSource source = new FileDataSource(file);
		DataHandler handler = new DataHandler(source);
		return handler;
	}

	/**
	 * Convenience method to create a datahandler out of a file.
	 * 
	 * @param file
	 *            the file
	 * @return the datahandler
	 */
	public static final DataHandler createDataHandler(String file) {
		return createDataHandler(new File(file));
	}

	public boolean fileExists(String file) throws RemoteFileSystemException {

		if ( isLocal(file) ) {
			return new File(file).exists();
		} else {
			return serviceInterface.fileExists(file);
		}
		
	}

	public boolean needsDownloading(String url) {
		
		File cacheTargetFile = getLocalCacheFile(url);
		File cacheTargetParentFile = cacheTargetFile.getParentFile();

		if ( ! cacheTargetFile.exists() ) {
			return true;
		}
		
		long lastModified = -1;
		try {
			lastModified = serviceInterface.lastModified(url);
		} catch (Exception e) {
			throw new RuntimeException("Could not get last modified time of file: "+url, e);
		}

		if (cacheTargetFile.exists()) {
			// check last modified date
			long local_last_modified = cacheTargetFile.lastModified();
			myLogger.debug("local file timestamp:\t" + local_last_modified);
			myLogger.debug("remote file timestamp:\t" + lastModified);
			if (local_last_modified >= lastModified) {
				myLogger
						.debug("Local cache file is not older than remote file. No download necessary...");
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
		
	}
	
}
