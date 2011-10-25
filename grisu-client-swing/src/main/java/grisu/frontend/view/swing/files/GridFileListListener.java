package grisu.frontend.view.swing.files;

import grisu.model.dto.GridFile;

import java.util.Set;

public interface GridFileListListener {

	public void directoryChanged(GridFile newDirectory);

	public void fileDoubleClicked(GridFile file);

	public void filesSelected(Set<GridFile> files);

	public void isLoading(boolean loading);

}
