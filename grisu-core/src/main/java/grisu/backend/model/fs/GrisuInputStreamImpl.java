package grisu.backend.model.fs;

import grisu.backend.model.FileSystemCache;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrisuInputStreamImpl implements GrisuInputStream {

	static final Logger myLogger = LoggerFactory
			.getLogger(GrisuInputStreamImpl.class.getName());

	private final FileSystemCache fsCache;
	private final InputStream inputStream;

	public GrisuInputStreamImpl(FileSystemCache fsCache, InputStream inputStream) {

		this.fsCache = fsCache;
		this.inputStream = inputStream;

	}

	public void close() {
		try {
			this.inputStream.close();
		} catch (final IOException e) {
			myLogger.error(e.getLocalizedMessage(), e);
		}
		this.fsCache.close();
	}

	public InputStream getStream() {
		return this.inputStream;
	}

}
