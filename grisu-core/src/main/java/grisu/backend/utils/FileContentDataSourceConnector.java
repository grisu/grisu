package grisu.backend.utils;

import grisu.backend.model.fs.CommonsVfsRemoteFileTransferObject;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import javax.activation.DataSource;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connector class to get convert {@link FileContent} to a {@link DataSource} so
 * we are able to get streams out of it.
 * 
 * @author Markus Binsteiner
 * 
 */
public class FileContentDataSourceConnector implements DataSource {

	private class ContentInputStream extends FilterInputStream {

		private final FileObject f;

		public ContentInputStream(FileObject f, InputStream in) {
			super(in);
			myLogger.debug("creating input stream: "
					+ inNumber.incrementAndGet());
			this.f = f;
		}

		@Override
		public void close() throws IOException {
			myLogger.debug("content input stream is closed " + inNumber.get());
			try {
				f.close();
			} catch (final Exception ex) {
				myLogger.warn(ex.getMessage());
			}
			in.close();
		}

		@Override
		protected void finalize() throws Throwable {
			this.close();
		}
	}

	private class ContentOutputStream extends FilterOutputStream {

		private final FileObject f;

		public ContentOutputStream(FileObject f, OutputStream out) {
			super(out);
			myLogger.debug("creating output stream: "
					+ outNumber.incrementAndGet());
			this.f = f;
		}

		@Override
		public void close() throws IOException {
			myLogger.debug("content output stream is closed " + outNumber.get());
			try {
				f.getContent().close();
			} catch (final Exception ex) {
				myLogger.warn(ex.getMessage());
			}
			try {
				f.close();
			} catch (final Exception ex) {
				myLogger.warn(ex.getMessage());
			}
			out.close();

		}

		@Override
		protected void finalize() throws Throwable {
			this.close();
		}
	}

	private final String name;

	private static AtomicInteger inNumber = new AtomicInteger(0);

	private static AtomicInteger outNumber = new AtomicInteger(0);

	private final FileObject f;

	static final Logger myLogger = LoggerFactory
			.getLogger(CommonsVfsRemoteFileTransferObject.class.getName());

	public FileContentDataSourceConnector(final FileObject f) {

		this.f = f;
		this.name = f.getName().getBaseName();

	}

	@Override
	protected void finalize() throws Throwable {
		myLogger.debug("now closing all streams");
		try {
			f.close();
		} catch (final Exception ex) {
			myLogger.warn(ex.getMessage());
		}
	}

	public final String getContentType() {
		return "application/octet-stream";
	}

	public final InputStream getInputStream() throws IOException {
		return new ContentInputStream(f, f.getContent().getInputStream());
	}

	public final String getName() {
		return this.name;
	}

	public final OutputStream getOutputStream() throws IOException {
		return new ContentOutputStream(f, f.getContent().getOutputStream());
	}

}
