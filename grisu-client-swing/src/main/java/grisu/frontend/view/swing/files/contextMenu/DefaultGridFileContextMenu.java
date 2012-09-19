package grisu.frontend.view.swing.files.contextMenu;

import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.model.FileManager;
import grisu.model.dto.GridFile;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGridFileContextMenu extends JPopupMenu implements
		GridFileListPanelContextMenu {

	static final Logger myLogger = LoggerFactory
			.getLogger(DefaultGridFileContextMenu.class.getName());

	private JMenuItem downloadMenuItem;
	private GridFileListPanel fileList;
	private JMenuItem createFolderMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem copyUrlsMenuItem;
	private JMenuItem refreshMenuItem;
	private JMenuItem viewMenuItem;
	private JMenuItem propertiesMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem openMenuItem;

	public DefaultGridFileContextMenu() {
	}

	public void directoryChanged(GridFile newDirectory) {
		// TODO Auto-generated method stub

	}

	public void fileDoubleClicked(GridFile file) {
		// TODO Auto-generated method stub

	}

	public void filesSelected(Set<GridFile> files) {

		if ((files == null) || (files.size() == 0)) {
			getViewMenuItem().setEnabled(false);
			getRefreshMenuItem().setEnabled(false);
			getDownloadMenuItem().setEnabled(false);
			getCreateFolderMenuItem().setEnabled(false);
			getDeleteMenuItem().setEnabled(false);
			getCopyMenuItem().setEnabled(false);
			return;
		}

		if (files.size() == 1) {
			if (files.iterator().next().isFolder()) {
				getRefreshMenuItem().setEnabled(true);

				final Clipboard cb = FileManager.FILE_TRANSFER_CLIPBOARD;
				final Transferable t = cb.getContents(null);
				if (t != null) {

					final GridFile target = files.iterator().next();
					if (target.isVirtual()) {
						if (target.getUrls().size() == 1) {
							if (target
									.getUrl()
									.startsWith(
											ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME)) {
								getPasteMenuItem().setEnabled(false);
							} else {
								getPasteMenuItem().setEnabled(true);
							}
						} else {
							getPasteMenuItem().setEnabled(true);
						}
					} else {
						getPasteMenuItem().setEnabled(true);
					}
				} else {
					getPasteMenuItem().setEnabled(false);
				}
			} else {
				getRefreshMenuItem().setEnabled(false);
				getPasteMenuItem().setEnabled(false);
			}
		} else {
			getRefreshMenuItem().setEnabled(false);
			getPasteMenuItem().setEnabled(false);
		}

		boolean folder = false;
		for (final GridFile file : files) {
			if (file.isFolder()) {
				folder = true;
			}
			if (file.isVirtual()) {

				if (folder) {
					getDownloadMenuItem().setEnabled(false);
					if (getOpenMenuItem() != null) {
						getOpenMenuItem().setEnabled(false);
					}
					getCreateFolderMenuItem().setEnabled(true);
				} else {
					if (file.getUrls().size() == 1) {
						getDownloadMenuItem().setEnabled(true);
						if (getOpenMenuItem() != null) {
							getOpenMenuItem().setEnabled(true);
						}
					} else {
						getDownloadMenuItem().setEnabled(false);
						if (getOpenMenuItem() != null) {
							getOpenMenuItem().setEnabled(false);
						}
					}
					getCreateFolderMenuItem().setEnabled(false);
				}

				getDeleteMenuItem().setEnabled(false);
				getCopyMenuItem().setEnabled(false);
				return;
			}
		}

		if (folder) {
			getViewMenuItem().setEnabled(false);
			if (getOpenMenuItem() != null) {
				getOpenMenuItem().setEnabled(false);
			}
		} else {
			getViewMenuItem().setEnabled(true);
			if (getOpenMenuItem() != null) {
				getOpenMenuItem().setEnabled(true);
			}
		}
		getDownloadMenuItem().setEnabled(true);
		getCreateFolderMenuItem().setEnabled(true);
		getDeleteMenuItem().setEnabled(true);
		getCopyMenuItem().setEnabled(true);
	}

	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem("Copy");
			copyMenuItem.setAction(new CopyAction(fileList));
		}
		return copyMenuItem;
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

	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem("Open");
			try {
				openMenuItem.setAction(new OpenAction(fileList));
			} catch (Exception e) {
				openMenuItem = null;
			}
		}
		return openMenuItem;
	}

	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem("Paste");
			pasteMenuItem.setAction(new PasteAction(fileList));
		}
		return pasteMenuItem;
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

		JMenuItem openItem = getOpenMenuItem();
		if (openItem != null) {
			add(getOpenMenuItem());
		}

		add(getRefreshMenuItem());
		add(getCopyMenuItem());
		add(getPasteMenuItem());

		add(getViewMenuItem());
		// add(getCopyUrlsMenuItem());
		add(getCreateFolderMenuItem());
		add(getDeleteMenuItem());
		add(getDownloadMenuItem());
		add(getPropetiesMenuItem());
	}
}
