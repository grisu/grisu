package org.vpac.grisu.frontend.view.swing.files;

import java.util.Set;

import org.vpac.grisu.model.dto.GridFile;

public interface GrdFileListListener {

	public void directoryChanged(GridFile newDirectory);

	public void fileDoubleClicked(GridFile file);

	public void filesSelected(Set<GridFile> files);

	public void isLoading(boolean loading);

}
