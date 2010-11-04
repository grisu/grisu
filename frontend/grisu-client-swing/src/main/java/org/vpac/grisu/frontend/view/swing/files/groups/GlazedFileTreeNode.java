package org.vpac.grisu.frontend.view.swing.files.groups;

import java.util.ArrayList;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.files.GlazedFile;

public class GlazedFileTreeNode extends LazyLoadingTreeNode {

	private final FileManager fm;

	public GlazedFileTreeNode(FileManager fm, GlazedFile userObject,
			DefaultTreeModel model) {
		super(userObject, model);
		this.fm = fm;
		setAllowsChildren(getAllowsChildren());
	}

	@Override
	public boolean getAllowsChildren() {

		if (GlazedFile.Type.FILETYPE_FILE
				.equals(((GlazedFile) (getUserObject())).getType())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public MutableTreeNode[] loadChildren(DefaultTreeModel model) {

		ArrayList<MutableTreeNode> list = new ArrayList<MutableTreeNode>();

		try {
			for (GlazedFile f : fm.ls((GlazedFile) getUserObject())) {
				list.add(new GlazedFileTreeNode(fm, f, model));
			}
		} catch (RemoteFileSystemException e) {
			e.printStackTrace();
			return null;
		}
		return list.toArray(new MutableTreeNode[list.size()]);

	}
}
