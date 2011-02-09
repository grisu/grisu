package grisu.frontend.view.swing.files;

import grisu.model.files.GlazedFile;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;

public class GlazedFileMatcherEditor extends AbstractMatcherEditor<GlazedFile> {

	private final boolean currentDisplayHiddenFile = false;

	public GlazedFileMatcherEditor() {
		super();
		currentMatcher = new GlazedFileMatcher(currentDisplayHiddenFile);
	}

	public void displayHiddenFiles(boolean display) {
		if (currentDisplayHiddenFile != display) {
			((GlazedFileMatcher) currentMatcher).displayHiddenFiles(display);
			if (!display) {
				fireConstrained(currentMatcher);
			} else {
				fireRelaxed(currentMatcher);
			}
		}
	}

	public void setExtensionsToDisplay(String[] extensions) {

		((GlazedFileMatcher) currentMatcher).setExtensionsToDisplay(extensions);
		fireChanged(currentMatcher);
	}

}
