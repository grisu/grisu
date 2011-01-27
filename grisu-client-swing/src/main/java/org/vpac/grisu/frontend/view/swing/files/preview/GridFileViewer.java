package org.vpac.grisu.frontend.view.swing.files.preview;

import java.io.File;

import javax.swing.JPanel;

import org.vpac.grisu.model.dto.GridFile;

public interface GridFileViewer {

	public JPanel getPanel();

	public String[] getSupportedMimeTypes();

	public void setFile(GridFile file, File localCacheFile);

}
