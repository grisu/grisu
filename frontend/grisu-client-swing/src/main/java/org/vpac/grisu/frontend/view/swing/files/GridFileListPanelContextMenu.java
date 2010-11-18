package org.vpac.grisu.frontend.view.swing.files;

import javax.swing.JPopupMenu;

public interface GridFileListPanelContextMenu extends GridFileListListener {

	public JPopupMenu getJPopupMenu();

	public void setFileListPanel(GridFileListPanel panel);

}
