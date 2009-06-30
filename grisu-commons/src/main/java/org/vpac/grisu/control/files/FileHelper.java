package org.vpac.grisu.control.files;

import java.io.File;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;

public class FileHelper {
	
	private final ServiceInterface serviceInterface;
	static final Logger myLogger = Logger.getLogger(FileHelper.class.getName());
	
	public FileHelper(ServiceInterface si) {
		this.serviceInterface = si;
	}
	
	public void uploadFile(String sourcePath, String targetDirectory) throws FileTransferException {
		
		File file = new File(sourcePath);
		uploadFile(file, targetDirectory);
		
	}
	
	public static boolean isLocal(String file) {
		
		if ( file.startsWith("gsiftp") ) {
			return false;
		} else if ( file.startsWith("file:") ) {
			return true;
		} else if ( file.startsWith("http:") ) {
			return false;
		} else {
			return true;
		}
		
	}
	
	public void uploadFile(File file, String targetDirectory) throws FileTransferException {
		
		if ( ! file.exists() ) {
			throw new FileTransferException("File does not exist: "+file.toString(), null);
		}
		
		if ( ! file.canRead() ) {
			throw new FileTransferException("Can't read file: "+file.toString(), null);
		}

		// checking whether folder exists and is folder
		try {
			if ( ! serviceInterface.fileExists(targetDirectory) ) {
				try {
					boolean success = serviceInterface.mkdir(targetDirectory);

					if ( ! success ) {
						throw new FileTransferException("Could not create target directory: "+targetDirectory);
					}
				} catch (Exception e) {
					throw new FileTransferException("Could not create target directory: "+targetDirectory, e);
				}
			} else {
		 		try {
					if ( ! serviceInterface.isFolder(targetDirectory) ) {
						throw new FileTransferException("Can't upload file to: "+targetDirectory+": target is a file.");
					}
				} catch (Exception e2) {
					myLogger.debug( "Could not access target directory. Trying to create it...");
							
					try {
						boolean success = serviceInterface.mkdir(targetDirectory);

						if ( ! success ) {
							throw new FileTransferException("Could not create target directory: "+targetDirectory);
						}
					} catch (Exception e) {
						throw new FileTransferException("Could not create target directory: "+targetDirectory, e);
					}
				}
			}
			
			
		} catch (Exception e) {
			throw new FileTransferException("Could not determine whether target directory "+targetDirectory+" exists: "+e.getLocalizedMessage());
		}
		

		

		myLogger.debug("Uploading local file: "+file.toString()+" to: "+targetDirectory);
		
		if ( file.isDirectory() ) {
			throw new FileTransferException("Transfer of folders not supported yet.");
		} else {
			DataSource source = new FileDataSource(file);
			try {
				myLogger.info("Uploading file "+file.getName()+"...");
				String filetransferHandle = serviceInterface.upload(source, targetDirectory+"/"+file.getName(), true);
				myLogger.info("Upload of file "+file.getName()+" successful.");
			} catch (Exception e1) {
				myLogger.info("Upload of file "+file.getName()+" failed: "+e1.getLocalizedMessage());
				myLogger.error("File upload failed: "+e1.getLocalizedMessage());
				throw new FileTransferException("Could not upload file \""+file.getName() + "\": "
						+ e1.getLocalizedMessage(), e1);
			}
		}

		
		
	}
	
	
	

}
