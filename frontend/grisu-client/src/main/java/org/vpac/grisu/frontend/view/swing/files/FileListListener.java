package org.vpac.grisu.frontend.view.swing.files;

import java.util.Set;

import org.vpac.grisu.model.files.GlazedFile;

public interface FileListListener {

	public void fileDoubleClicked(GlazedFile file);

	public void filesSelected(Set<GlazedFile> files);

	public void isLoading(boolean loading);

}
