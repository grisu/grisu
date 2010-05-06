package org.vpac.grisu.frontend.view.swing.files;

import javax.swing.JPopupMenu;

public interface FileListPanelContextMenu extends FileListListener {

	public JPopupMenu getJPopupMenu();

	public void setFileListPanel(FileListPanel panel);

}
