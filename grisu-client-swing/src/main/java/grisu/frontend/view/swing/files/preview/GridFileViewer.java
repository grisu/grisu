package grisu.frontend.view.swing.files.preview;

import grisu.model.dto.GridFile;

import java.io.File;

import javax.swing.JPanel;


public interface GridFileViewer {

	public JPanel getPanel();

	public String[] getSupportedMimeTypes();

	public void setFile(GridFile file, File localCacheFile);

}
