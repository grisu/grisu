package org.vpac.grisu.fs.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.commons.vfs.FileContent;

/**
 * Connector class to get convert {@link FileContent} to a {@link DataSource} so we are able
 * to get streams out of it.
 * 
 * @author Markus Binsteiner
 *
 */
public class FileContentDataSourceConnector implements DataSource {
	
	private FileContent content = null;
	
	public FileContentDataSourceConnector(FileContent content) {
		this.content = content;
	}

	public String getContentType() {
		return "application/octet-stream";
	}

	public InputStream getInputStream() throws IOException {
		return content.getInputStream();
	}

	public String getName() {
		return content.getFile().getName().getBaseName();
	}

	public OutputStream getOutputStream() throws IOException {
		return content.getOutputStream();
	}





}
