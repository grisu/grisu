package grisu.model.utils;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class TreeModelSupport {
	private final Vector vector = new Vector();

	public void addTreeModelListener(TreeModelListener listener) {
		if ((listener != null) && !vector.contains(listener)) {
			vector.addElement(listener);
		}
	}

	public void fireTreeNodesChanged(TreeModelEvent e) {
		final Enumeration listeners = vector.elements();
		while (listeners.hasMoreElements()) {
			final TreeModelListener listener = (TreeModelListener) listeners
					.nextElement();
			listener.treeNodesChanged(e);
		}
	}

	public void fireTreeNodesInserted(TreeModelEvent e) {
		final Enumeration listeners = vector.elements();
		while (listeners.hasMoreElements()) {
			final TreeModelListener listener = (TreeModelListener) listeners
					.nextElement();
			listener.treeNodesInserted(e);
		}
	}

	public void fireTreeNodesRemoved(TreeModelEvent e) {
		final Enumeration listeners = vector.elements();
		while (listeners.hasMoreElements()) {
			final TreeModelListener listener = (TreeModelListener) listeners
					.nextElement();
			listener.treeNodesRemoved(e);
		}
	}

	public void fireTreeStructureChanged(TreeModelEvent e) {
		final Enumeration listeners = vector.elements();
		while (listeners.hasMoreElements()) {
			final TreeModelListener listener = (TreeModelListener) listeners
					.nextElement();
			listener.treeStructureChanged(e);
		}
	}

	public void removeTreeModelListener(TreeModelListener listener) {
		if (listener != null) {
			vector.removeElement(listener);
		}
	}
}
