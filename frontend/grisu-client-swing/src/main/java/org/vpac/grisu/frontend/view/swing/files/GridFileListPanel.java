package org.vpac.grisu.frontend.view.swing.files;

import java.util.Set;

import javax.swing.JPanel;

import org.vpac.grisu.model.dto.DtoFileObject;

public interface GridFileListPanel {

	public void addFileListListener(FileListListener l);

	public void displayHiddenFiles(boolean display);

	public DtoFileObject getCurrentDirectory();

	public JPanel getPanel();

	public Set<DtoFileObject> getSelectedFiles();

	public void refresh();

	public void removeFileListListener(FileListListener l);

	public void setContextMenu(FileListPanelContextMenu menu);

	public void setCurrentUrl(String url);

	/**
	 * Sets the extensions to display.
	 * 
	 * @param extensions
	 *            the extensions or null/empty for displaying everything
	 */
	public void setExtensionsToDisplay(String[] extensions);

	public void setRootUrl(String url);

}
