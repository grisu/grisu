package grisu.frontend.view.swing.files.preview;

import grisu.model.files.GlazedFile;

public interface FilePreviewPanel {

	public String[] getSupportedFileTypes();

	public void setFile(GlazedFile file);

}
