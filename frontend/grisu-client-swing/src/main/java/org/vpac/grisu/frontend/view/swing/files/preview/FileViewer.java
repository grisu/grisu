package org.vpac.grisu.frontend.view.swing.files.preview;

import java.io.File;

import javax.swing.JPanel;

import org.vpac.grisu.model.files.GlazedFile;

public interface FileViewer {

	public JPanel getPanel();

	public String[] getSupportedMimeTypes();

	public void setFile(GlazedFile file, File localCacheFile);

}
