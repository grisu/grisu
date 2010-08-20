package org.vpac.grisu.frontend.view.swing.files;

import java.util.Set;

import javax.swing.JPanel;

import org.vpac.grisu.model.files.GlazedFile;

public interface FileListPanel {

	public void addFileListListener(FileListListener l);

	public GlazedFile getCurrentDirectory();

	public JPanel getPanel();

	public Set<GlazedFile> getSelectedFiles();

	public void refresh();

	public void removeFileListListener(FileListListener l);

	public void setContextMenu(FileListPanelContextMenu menu);

	public void setCurrentUrl(String url);

	public void setRootUrl(String url);

	public void displayHiddenFiles(boolean display);

	/**
	 * Sets the extensions to display.
	 * 
	 * @param extensions
	 *            the extensions or null/empty for displaying everything
	 */
	public void setExtensionsToDisplay(String[] extensions);

}
