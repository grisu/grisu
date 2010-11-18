package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import org.netbeans.swing.outline.Outline;
import org.vpac.grisu.X;

public class GridFileTreeDragSource implements DragSourceListener,
		DragGestureListener {
	private final DragSource source;
	private final DragGestureRecognizer recognizer;

	Outline sourceTree;

	public GridFileTreeDragSource(Outline tree, int actions) {
		sourceTree = tree;
		source = new DragSource();
		recognizer = source.createDefaultDragGestureRecognizer(sourceTree,
				actions, this);
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {

		System.out.println("Drop Action: " + dsde.getDropAction());

	}

	/*
	 * Drag Event Handlers
	 */
	public void dragEnter(DragSourceDragEvent dsde) {

		X.p(dsde.getDragSourceContext().getDragSource().getClass().toString());
		X.p("DGE2: " + dsde.getSource().toString());
	}

	public void dragExit(DragSourceEvent dse) {
		X.p("DGE2: " + dse.getSource().toString());
	}

	/*
	 * Drag Gesture Handler
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {

		X.p("DGE: " + dge.toString());

		// TreePath path = sourceTree.getSelectionPath();
		// if ((path == null) || (path.getPathCount() <= 1)) {
		// We can't move the root node or an empty selection
		// return;
		// }
		// oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		// transferable = new TransferableTreeNode(path);
		// source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable,
		// this);

		// If you support dropping the node anywhere, you should probably
		// start with a valid move cursor:
		// source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
		// this);
	}

	public void dragOver(DragSourceDragEvent dsde) {
		X.p("DGE2: " + dsde.getSource().toString());
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
		System.out.println("Action: " + dsde.getDropAction());
		System.out.println("Target Action: " + dsde.getTargetActions());
		System.out.println("User Action: " + dsde.getUserAction());
	}

}
