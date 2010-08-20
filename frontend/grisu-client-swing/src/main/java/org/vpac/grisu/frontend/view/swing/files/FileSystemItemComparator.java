package org.vpac.grisu.frontend.view.swing.files;

import java.io.File;
import java.util.Comparator;

import org.vpac.grisu.model.files.FileSystemItem;

public class FileSystemItemComparator implements Comparator<FileSystemItem> {

	private static final String homeDirName = new File(System
			.getProperty("user.home")).getName();

	public int compare(FileSystemItem arg0, FileSystemItem arg1) {

		if (!arg0.getType().equals(arg1.getType())) {
			return arg0.getType().compareTo(arg1.getType());
		} else {

			if (arg0.isDummy()) {
				return -1;
			} else if (arg1.isDummy()) {
				return 1;
			}

			// make sure the user home dir is always first
			if (FileSystemItem.Type.LOCAL.equals(arg0.getType())) {
				if (homeDirName.equals(arg0.getAlias())) {
					return -1;
				}
			}
			if (FileSystemItem.Type.LOCAL.equals(arg1.getType())) {
				if (homeDirName.equals(arg1.getAlias())) {
					return 1;
				}
			}

			return arg0.getAlias().compareTo(arg1.getAlias());
		}

	}

}
