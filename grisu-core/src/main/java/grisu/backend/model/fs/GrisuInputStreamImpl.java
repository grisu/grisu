package grisu.backend.model.fs;

import grisu.backend.model.FileSystemCache;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class GrisuInputStreamImpl implements GrisuInputStream {

	static final Logger myLogger = Logger.getLogger(GrisuInputStreamImpl.class
			.getName());

	private final FileSystemCache fsCache;
	private final InputStream inputStream;

	public GrisuInputStreamImpl(FileSystemCache fsCache, InputStream inputStream) {

		this.fsCache = fsCache;
		this.inputStream = inputStream;

	}

	public void close() {
		try {
			this.inputStream.close();
		} catch (IOException e) {
			myLogger.error(e);
		}
		this.fsCache.close();
	}

	public InputStream getStream() {
		return this.inputStream;
	}

}
