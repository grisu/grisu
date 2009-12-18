package org.vpac.grisu.frontend.view.swing.files.preview;

import org.vpac.grisu.model.files.GlazedFile;

public interface FilePreviewPanel {

	public String[] getSupportedFileTypes();

	public void setFile(GlazedFile file);

}
