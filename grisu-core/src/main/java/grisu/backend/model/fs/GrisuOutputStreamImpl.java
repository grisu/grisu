package grisu.backend.model.fs;

import grisu.backend.model.FileSystemCache;

import java.io.IOException;
import java.io.OutputStream;


public class GrisuOutputStreamImpl implements GrisuOutputStream {

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
			e.printStackTrace();
		}
		this.fsCache.close();
	}

	public OutputStream getStream() {
		return this.outputStream;
	}

}
