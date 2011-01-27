package org.vpac.grisu.frontend.view.swing.files.contextMenu;

import javax.swing.JPopupMenu;

import org.vpac.grisu.frontend.view.swing.files.GridFileListListener;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanel;

public interface GridFileListPanelContextMenu extends GridFileListListener {

	public JPopupMenu getJPopupMenu();

	public void setGridFileListPanel(GridFileListPanel panel);

}
