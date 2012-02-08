package grisu.backend.model.fs;

import grisu.backend.model.FileSystemCache;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrisuOutputStreamImpl implements GrisuOutputStream {

	static final Logger myLogger = LoggerFactory
			.getLogger(GrisuOutputStreamImpl.class.getName());

	private final FileSystemCache fsCache;
	private final OutputStream outputStream;

	public GrisuOutputStreamImpl(FileSystemCache fsCache,
			OutputStream outputStream) {

		this.fsCache = fsCache;
		this.outputStream = outputStream;
	}

	public void close() {
		try {
			this.outputStream.close();
		} catch (final IOException e) {
			myLogger.error(e.getLocalizedMessage(), e);
		}
		this.fsCache.close();
	}

	public OutputStream getStream() {
		return this.outputStream;
	}

}
