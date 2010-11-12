package org.vpac.grisu.frontend.view.swing.files;

import java.util.Set;

import org.vpac.grisu.model.dto.DtoFileObject;

public interface GrdFileListListener {

	public void directoryChanged(DtoFileObject newDirectory);

	public void fileDoubleClicked(DtoFileObject file);

	public void filesSelected(Set<DtoFileObject> files);

	public void isLoading(boolean loading);

}
