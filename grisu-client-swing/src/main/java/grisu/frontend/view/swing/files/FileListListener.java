package grisu.frontend.view.swing.files;

import grisu.model.files.GlazedFile;

import java.util.Set;


public interface FileListListener {

	public void directoryChanged(GlazedFile newDirectory);

	public void fileDoubleClicked(GlazedFile file);

	public void filesSelected(Set<GlazedFile> files);

	public void isLoading(boolean loading);

}
