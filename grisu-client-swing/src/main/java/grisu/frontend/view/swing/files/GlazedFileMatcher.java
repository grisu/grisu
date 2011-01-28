package grisu.frontend.view.swing.files;

import grisu.model.files.GlazedFile;

import ca.odell.glazedlists.matchers.Matcher;

public class GlazedFileMatcher implements Matcher<GlazedFile> {

	private boolean displayHiddenFiles = false;
	// private String[] extensions = new String[] { ".txt" };

	private String[] extensions = null;

	public GlazedFileMatcher(boolean displayHiddenFiles) {
		this.displayHiddenFiles = displayHiddenFiles;
	}

	public void displayHiddenFiles(boolean display) {
		this.displayHiddenFiles = display;
	}

	public boolean matches(GlazedFile arg0) {

		if ("..".equals(arg0.getName())) {
			return true;
		}

		if (!displayHiddenFiles) {
			if (arg0.getName().startsWith(".")) {
				return false;
			}
		}

		if (arg0.isFolder()) {
			return true;
		}

		if (this.extensions == null || this.extensions.length == 0) {
			// display everything
			return true;
		} else {
			for (final String ext : extensions) {
				if (arg0.getName().endsWith(ext)) {
					return true;
				}
			}
			return false;
		}

	}

	public void setExtensionsToDisplay(String[] extensions) {

		this.extensions = extensions;

	}

}
