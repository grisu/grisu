package org.vpac.grisu.utils;

import java.io.File;
import java.io.FilenameFilter;

public class GrisuTemplateFilenameFilter implements FilenameFilter {

	public boolean accept(File arg0, String arg1) {

		if (arg1.endsWith(".template")) {
			return true;
		} else {
			return false;
		}
	}

}
