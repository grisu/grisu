package org.vpac.grisu.frontend.view.swing.files.contextMenu;

import java.util.Set;

import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanel;
import org.vpac.grisu.model.dto.GridFile;
import javax.swing.JMenuItem;

public class DefaultGridFileContextMenu extends JPopupMenu implements
		GridFileListPanelContextMenu {

	static final Logger myLogger = Logger
			.getLogger(DefaultGridFileContextMenu.class.getName());

	private JMenuItem downloadMenuItem;
	private GridFileListPanel fileList;
	private JMenuItem createFolderMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem copyUrlsMenuItem;
	private JMenuItem refreshMenuItem;
	private JMenuItem viewMenuItem;
	private JMenuItem propertiesMenuItem;

	public DefaultGridFileContextMenu() {
	}

	public void directoryChanged(GridFile newDirectory) {
		// TODO Auto-generated method stub

	}

	public void fileDoubleClicked(GridFile file) {
		// TODO Auto-generated method stub

	}

	public void filesSelected(Set<GridFile> files) {

		if (files.size() == 1) {
			if (files.iterator().next().isFolder()) {
				getRefreshMenuItem().setEnabled(true);
			} else {
				getRefreshMenuItem().setEnabled(false);
			}
		} else {
			getRefreshMenuItem().setEnabled(false);
		}

		boolean folder = false;
		for (GridFile file : files) {
			if (file.isVirtual()) {
				getDownloadMenuItem().setEnabled(false);
				getCreateFolderMenuItem().setEnabled(false);
				getDeleteMenuItem().setEnabled(false);
				return;
			}
			if (file.isFolder()) {
				folder = true;
			}
		}

		if (folder) {
			getViewMenuItem().setEnabled(false);
		} else {
			getViewMenuItem().setEnabled(true);
		}
		getDownloadMenuItem().setEnabled(true);
		getCreateFolderMenuItem().setEnabled(true);
		getDeleteMenuItem().setEnabled(true);
	}

	private JMenuItem getCopyUrlsMenuItem() {
		if (copyUrlsMenuItem == null) {
			copyUrlsMenuItem = new JMenuItem("Copy url(s)");
			copyUrlsMenuItem.setAction(new CopyUrlsAction(fileList));
		}
		return copyUrlsMenuItem;
	}

	private JMenuItem getCreateFolderMenuItem() {
		if (createFolderMenuItem == null) {
			createFolderMenuItem = new JMenuItem("Create folder");
			createFolderMenuItem.setAction(new CreateFolderAction(fileList));
		}
		return createFolderMenuItem;
	}

	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem("Delete Selected files");
			deleteMenuItem.setAction(new DeleteAction(fileList));

		}
		return deleteMenuItem;
	}

	private JMenuItem getDownloadMenuItem() {
		if (downloadMenuItem == null) {
			downloadMenuItem = new JMenuItem("Download selected files");
			downloadMenuItem.setAction(new DownloadAction(fileList));
		}
		return downloadMenuItem;
	}

	public JPopupMenu getJPopupMenu() {
		return this;
	}

	private JMenuItem getPropetiesMenuItem() {
		if (propertiesMenuItem == null) {
			propertiesMenuItem = new JMenuItem("Properties");
			propertiesMenuItem.setAction(new PropertiesAction(fileList));
		}
		return propertiesMenuItem;
	}

	private JMenuItem getRefreshMenuItem() {
		if (refreshMenuItem == null) {
			refreshMenuItem = new JMenuItem("Refresh");
			refreshMenuItem.setAction(new RefreshAction(fileList));
		}
		return refreshMenuItem;
	}

	private JMenuItem getViewMenuItem() {
		if (viewMenuItem == null) {
			viewMenuItem = new JMenuItem("View");
			viewMenuItem.setAction(new ViewAction(fileList));
		}
		return viewMenuItem;
	}

	public void isLoading(boolean loading) {
	}

	public void setGridFileListPanel(GridFileListPanel panel) {
		this.fileList = panel;
		add(getRefreshMenuItem());
		add(getViewMenuItem());
		add(getCopyUrlsMenuItem());
		add(getCreateFolderMenuItem());
		add(getDeleteMenuItem());
		add(getDownloadMenuItem());
		add(getPropetiesMenuItem());
	}
}
