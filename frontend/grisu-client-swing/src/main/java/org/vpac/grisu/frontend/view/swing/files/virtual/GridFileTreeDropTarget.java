package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.netbeans.swing.outline.Outline;
import org.vpac.grisu.X;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

public class GridFileTreeDropTarget implements DropTargetListener {

	class OpenFolderTask extends TimerTask {

		private final TreePath path;
		private final Outline outline;

		public OpenFolderTask(Outline outline, TreePath path) {
			this.outline = outline;
			this.path = path;
		}

		@Override
		public void run() {
			outline.expandPath(path);
		}
	}

	public static int WAIT_DRAG_UNTIL_OPEN_FOLDER = 2000;

	private final ServiceInterface si;
	private final FileManager fm;

	private final DropTarget target;
	private final Outline targetOutline;

	private int[] lastSelectedRows = null;

	private TreePath currentlySelectedPath = null;
	private TimerTask openFolderTask;

	private final Timer timer = new Timer();

	public GridFileTreeDropTarget(ServiceInterface si, Outline tree) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		targetOutline = tree;
		target = new DropTarget(targetOutline, this);

	}

	public void dragEnter(DropTargetDragEvent dtde) {

		Outline outline = (Outline) ((dtde.getDropTargetContext()
				.getComponent()));

		lastSelectedRows = outline.getSelectedRows();

		// TreeNode node = getNodeForEvent(dtde);
		// if (node.isLeaf()) {
		// dtde.rejectDrag();
		// } else {
		// // start by supporting move operations
		// // dtde.acceptDrag(DnDConstants.ACTION_MOVE);
		// dtde.acceptDrag(dtde.getDropAction());
		// }
	}

	public void dragExit(DropTargetEvent dte) {

		if (openFolderTask != null) {
			openFolderTask.cancel();
		}

		Outline outline = (Outline) ((dte.getDropTargetContext().getComponent()));

		selectLastSelectedItems(outline);
	}

	public void dragOver(DropTargetDragEvent dtde) {

		X.p("Over");

		Outline outline = (Outline) ((dtde.getDropTargetContext()
				.getComponent()));

		int x = new Double(dtde.getLocation().getX()).intValue();
		int y = new Double(dtde.getLocation().getY()).intValue();
		TreePath p = outline.getClosestPathForLocation(x, y);

		if (p.getLastPathComponent() instanceof GridFileTreeNode) {
			GridFile dropTargetFile = ((GridFileTreeNode) p
					.getLastPathComponent()).getGridFile();

			Point point = new Point(x, y);
			selectFolderAndChildren(outline, p, point);

			if (dropTargetFile.isFolder()) {

				if (!outline.getOutlineModel().getTreePathSupport()
						.hasBeenExpanded(p)) {
					if ((currentlySelectedPath == null)
							|| !currentlySelectedPath.equals(p)) {
						currentlySelectedPath = p;
						if (openFolderTask != null) {
							openFolderTask.cancel();
						}
						openFolderTask = new OpenFolderTask(outline, p);
						timer.schedule(openFolderTask,
								WAIT_DRAG_UNTIL_OPEN_FOLDER);
					}
				}

				if (dropTargetFile.isVirtual()) {
					dtde.rejectDrag();
				} else {
					dtde.acceptDrag(DnDConstants.ACTION_COPY);
				}

			} else {
				openFolderTask.cancel();
				GridFile parent = ((GridFileTreeNode) p.getParentPath()
						.getLastPathComponent()).getGridFile();

				if (parent.isVirtual()) {
					dtde.rejectDrag();
				} else {
					dtde.acceptDrag(DnDConstants.ACTION_COPY);
				}

			}

		} else {
			X.p("Not filetreenode");
		}

	}

	public void drop(DropTargetDropEvent dtde) {
		X.p("drop");
		if (openFolderTask != null) {
			openFolderTask.cancel();
		}
		Point pt = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath
				.getLastPathComponent();
		if (parent.isLeaf()) {
			dtde.rejectDrop();
			return;
		}

		try {
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			for (DataFlavor flavor : flavors) {
				if (tr.isDataFlavorSupported(flavor)) {
					dtde.acceptDrop(dtde.getDropAction());
					TreePath p = (TreePath) tr.getTransferData(flavor);
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) p
							.getLastPathComponent();
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					model.insertNodeInto(node, parent, 0);
					dtde.dropComplete(true);
					return;
				}
			}
			dtde.rejectDrop();
		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		X.p("Drop action changed");
	}

	private void selectFolderAndChildren(Outline outline, TreePath path,
			Point point) {

		GridFile dropTargetFile = ((GridFileTreeNode) path
				.getLastPathComponent()).getGridFile();

		if (dropTargetFile.isFolder()) {

			int row = outline.rowAtPoint(point);

			if (outline.getOutlineModel().getTreePathSupport()
					.hasBeenExpanded(path)) {
				int childs = dropTargetFile.getChildren().size();

				outline.setRowSelectionInterval(row, row + childs);
			} else {
				outline.setRowSelectionInterval(row, row);
			}

		} else {
			TreePath parentPath = path.getParentPath();
			GridFileTreeNode parentNode = (GridFileTreeNode) parentPath
					.getLastPathComponent();

			int row = -1;
			for (int i = 0; i < outline.getRowCount(); i++) {

				GridFileTreeNode node = (GridFileTreeNode) outline.getValueAt(
						i, 0);
				if (parentNode.equals(node)) {
					row = i;
					break;
				}
			}
			Object o = outline.getOutlineModel().getChild(
					parentPath.getLastPathComponent(), 0);

			GridFile dropTargetFileParent = ((GridFileTreeNode) parentPath
					.getLastPathComponent()).getGridFile();

			if (outline.getOutlineModel().getTreePathSupport()
					.hasBeenExpanded(parentPath)) {
				int childs = dropTargetFileParent.getChildren().size();

				outline.setRowSelectionInterval(row, row + childs);
			} else {
				outline.setRowSelectionInterval(row, row);
			}

		}

	}

	private void selectLastSelectedItems(Outline outline) {
		if (lastSelectedRows != null) {
			outline.setRowSelectionInterval(lastSelectedRows[0],
					lastSelectedRows[0]);
		}

		for (int i = 1; i < lastSelectedRows.length; i++) {
			outline.addRowSelectionInterval(lastSelectedRows[i],
					lastSelectedRows[i]);
		}
	}
}
