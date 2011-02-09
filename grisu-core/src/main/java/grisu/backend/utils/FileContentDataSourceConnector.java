package grisu.backend.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.commons.vfs.FileContent;

/**
 * Connector class to get convert {@link FileContent} to a {@link DataSource} so
 * we are able to get streams out of it.
 * 
 * @author Markus Binsteiner
 * 
 */
public class FileContentDataSourceConnector implements DataSource {

	private FileContent content = null;

	public FileContentDataSourceConnector(final FileContent content) {
		this.content = content;
	}

	public final String getContentType() {
		return "application/octet-stream";
	}

	public final InputStream getInputStream() throws IOException {
		return content.getInputStream();
	}

	public final String getName() {
		return content.getFile().getName().getBaseName();
	}

	public final OutputStream getOutputStream() throws IOException {
		return content.getOutputStream();
	}

}
