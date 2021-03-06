package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.files.contextMenu.GridFileListPanelContextMenu;
import grisu.model.dto.GridFile;

import java.util.Set;

import javax.swing.JPanel;

public interface GridFileListPanel {

	public void addGridFileListListener(GridFileListListener l);

	public GridFile getCurrentDirectory();

	public JPanel getPanel();

	public Set<GridFile> getSelectedFiles();

	public ServiceInterface getServiceInterface();


	public void refresh();

	public void refreshFolder(String url);

	public void removeGridFileListListener(GridFileListListener l);

	public void setContextMenu(GridFileListPanelContextMenu menu);

	public void setCurrentUrl(String url);

	public void setDisplayHiddenFiles(boolean display);

	/**
	 * Sets the extensions to display.
	 * 
	 * @param extensions
	 *            the extensions or null/empty for displaying everything
	 */
	public void setExtensionsToDisplay(String[] extensions);

	/**
	 * Removes all other roots and only lists the specified directory
	 * 
	 * @param url
	 */
	public void setRootUrl(GridFile url);
}
