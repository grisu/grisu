package grisu.backend.utils;

import grisu.backend.model.fs.CommonsVfsRemoteFileTransferObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.commons.vfs.FileContent;
import org.apache.log4j.Logger;

/**
 * Connector class to get convert {@link FileContent} to a {@link DataSource} so
 * we are able to get streams out of it.
 * 
 * @author Markus Binsteiner
 * 
 */
public class FileContentDataSourceConnector implements DataSource {
	
	private InputStream in;
	private OutputStream out;
	private IOException inIO  = null;
	private IOException outIO = null;
	private String name ;
	
	private FileContent content;
	
	static final Logger myLogger = Logger
	.getLogger(CommonsVfsRemoteFileTransferObject.class.getName());

	public FileContentDataSourceConnector(final FileContent content) {
		
		this.content = content;
		
		try {
			this.in = content.getInputStream();
		} catch (IOException ex){
			this.inIO = ex;
		}
		
		try {
			this.out = content.getOutputStream();
		} catch (IOException ex){
			this.outIO = ex;
		}
		
		this.name = content.getFile().getName().getBaseName();
	}

	public final String getContentType() {
		return "application/octet-stream";
	}

	public final InputStream getInputStream() throws IOException {
		if (this.inIO != null){
			throw this.inIO;
		}
		return this.in;
	}

	public final String getName() {
		return this.name;
	}

	public final OutputStream getOutputStream() throws IOException {
		if (this.outIO != null){
			throw this.outIO;
		}
		return this.out;
	}
	
	protected void finalize() throws Throwable{
		try {
			myLogger.warn("now closing all streams");
			content.close();
		} finally {
			try {
				getInputStream().close();
			}
			finally {
				getOutputStream().close();
			}
		}
	} 

}
