package grisu.backend.model.fs;

import grisu.backend.model.FileSystemCache;

import java.io.IOException;
import java.io.InputStream;


public class GrisuInputStreamImpl implements GrisuInputStream {

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
			e.printStackTrace();
		}
		this.fsCache.close();
	}

	public InputStream getStream() {
		return this.inputStream;
	}

}
