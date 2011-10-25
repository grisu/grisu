package grisu.frontend.view.swing.files.preview;

import grisu.model.files.GlazedFile;

import java.io.File;

import javax.swing.JPanel;

public interface FileViewer {

	public JPanel getPanel();

	public String[] getSupportedMimeTypes();

	public void setFile(GlazedFile file, File localCacheFile);

}
