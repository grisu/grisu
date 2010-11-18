package org.vpac.grisu.frontend.view.swing.files.virtual.utils;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

public abstract class LazyLoadingTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	private DefaultTreeModel model;

	public LazyLoadingTreeNode(Object userObject) {
		super(userObject);
	}

	/**
	 * Default Constructor
	 * 
	 * @param userObject
	 *            an Object provided by the user that constitutes the node's
	 *            data
	 * @param tree
	 *            the JTree containing this Node
	 * @param cancelable
	 */
	public LazyLoadingTreeNode(Object userObject, DefaultTreeModel model) {
		super(userObject);
		this.model = model;
	}

	/**
	 * 
	 * @return <code>true</code> if there are some childrens
	 */
	protected boolean areChildrenLoaded() {
		return (getChildCount() > 0) && getAllowsChildren();
	}

	/**
	 * If the
	 * 
	 * @see #getAllowsChildren()
	 * @return false, this node can't be a leaf
	 */
	@Override
	public boolean isLeaf() {
		return !getAllowsChildren();
	}

	/**
	 * This method will be executed in a background thread. If you have to do
	 * some GUI stuff use {@link SwingUtilities#invokeLater(Runnable)}
	 * 
	 * @param tree
	 *            the tree
	 * @return the Created nodes
	 */
	public abstract MutableTreeNode[] loadChildren(DefaultTreeModel model);

	/**
	 * Need some improvement ... This method should restore the Node initial
	 * state if the worker if canceled
	 */
	protected void reset() {
		int childCount = getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				model.removeNodeFromParent((MutableTreeNode) getChildAt(0));
			}
		}
		setAllowsChildren(true);
	}

	/**
	 * Define nodes children
	 * 
	 * @param nodes
	 *            new nodes
	 */
	protected void setChildren(MutableTreeNode... nodes) {
		int childCount = getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				model.removeNodeFromParent((MutableTreeNode) getChildAt(0));
			}
		}
		for (int i = 0; (nodes != null) && (i < nodes.length); i++) {
			model.insertNodeInto(nodes[i], this, i);
		}
	}

	public void setModel(DefaultTreeModel model) {
		this.model = model;
	}

}
