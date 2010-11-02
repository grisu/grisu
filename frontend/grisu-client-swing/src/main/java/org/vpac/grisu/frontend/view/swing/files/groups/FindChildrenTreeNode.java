package org.vpac.grisu.frontend.view.swing.files.groups;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

public class FindChildrenTreeNode extends LazyLoadingTreeNode {

	public FindChildrenTreeNode(Object userObject, DefaultTreeModel tree) {
		super(userObject, tree);
	}

	@Override
	public MutableTreeNode[] loadChildren(DefaultTreeModel model) {
		ArrayList<MutableTreeNode> list = new ArrayList<MutableTreeNode>(5);
		for (int i = 0; i < 5; i++) {
			list.add(new FindChildrenTreeNode(
					"Node " + i,
					model));
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				break;
			}
		}
		list.add(new DefaultMutableTreeNode("Leaf"));
		return list.toArray(new MutableTreeNode[list.size()]);
	}

}
