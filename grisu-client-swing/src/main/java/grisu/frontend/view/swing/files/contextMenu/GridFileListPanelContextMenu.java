package grisu.frontend.view.swing.files.contextMenu;

import grisu.frontend.view.swing.files.GridFileListListener;
import grisu.frontend.view.swing.files.GridFileListPanel;

import javax.swing.JPopupMenu;


public interface GridFileListPanelContextMenu extends GridFileListListener {

	public JPopupMenu getJPopupMenu();

	public void setGridFileListPanel(GridFileListPanel panel);

}
