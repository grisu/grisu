package org.vpac.grisu.backend.model.fs;

import java.io.InputStream;

public interface GrisuInputStream {

	public void close();

	public InputStream getStream();

}
