package grisu.backend.model.fs;

import grisu.backend.model.FileSystemCache;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class GrisuOutputStreamImpl implements GrisuOutputStream {

	static final Logger myLogger = Logger.getLogger(GrisuOutputStreamImpl.class
			.getName());

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
		} catch (IOException e) {
			myLogger.error(e);
		}
		this.fsCache.close();
	}

	public OutputStream getStream() {
		return this.outputStream;
	}

}
