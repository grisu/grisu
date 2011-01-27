package org.vpac.grisu.backend.model.fs;

import java.io.OutputStream;

public interface GrisuOutputStream {

	public void close();

	public OutputStream getStream();

}
