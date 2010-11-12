package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.util.ArrayList;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.vpac.grisu.X;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.dto.DtoFileObject;

public class VirtualFileTreeNode extends LazyLoadingTreeNode {

	private static final long serialVersionUID = 1L;
	private final FileManager fm;

	public VirtualFileTreeNode(FileManager fm, DtoFileObject userObject) {
		super(userObject);
		this.fm = fm;
		setAllowsChildren(getAllowsChildren());
	}

	public VirtualFileTreeNode(FileManager fm, DtoFileObject userObject,
			DefaultTreeModel model) {
		super(userObject, model);
		this.fm = fm;
		setAllowsChildren(getAllowsChildren());
	}

	@Override
	public boolean getAllowsChildren() {

		if (DtoFileObject.FILETYPE_FILE
				.equals(((DtoFileObject) (getUserObject())).getType())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public MutableTreeNode[] loadChildren(DefaultTreeModel model) {

		ArrayList<MutableTreeNode> list = new ArrayList<MutableTreeNode>();

		DtoFileObject temp = ((DtoFileObject) getUserObject());
		X.p(getUserObject().toString());

		try {
			Set<DtoFileObject> dfo = fm.ls((DtoFileObject) getUserObject());
			if (dfo == null) {
				return new MutableTreeNode[0];
			}
			for (DtoFileObject f : dfo) {
				list.add(new VirtualFileTreeNode(fm, f, model));
			}
		} catch (RemoteFileSystemException e) {
			e.printStackTrace();
			return null;
		}
		return list.toArray(new MutableTreeNode[list.size()]);

	}

}
