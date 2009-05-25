package org.vpac.grisu.control.utils;

import java.io.File;
import java.io.FileFilter;

public class GrisuPluginFilenameFilter implements FileFilter {

	public boolean accept(File arg0, String arg1) {

		if ( arg1.trim().endsWith(".jar") ) {
			return true;
		} else {
			return false;
		}
		
	}

	public boolean accept(File pathname) {

		if ( ! pathname.toString().endsWith(".jar") ) {
			return false;
		}
		
		if ( pathname.exists() && pathname.isFile() ) {
			return true;
		} else {
			return false;
		}
		
	}

}
