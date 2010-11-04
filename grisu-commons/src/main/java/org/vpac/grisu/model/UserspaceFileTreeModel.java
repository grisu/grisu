package org.vpac.grisu.model;

import java.util.List;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.files.GlazedFile;
import org.vpac.grisu.model.utils.TreeModelSupport;

public class UserspaceFileTreeModel extends TreeModelSupport implements
		TreeModel {

	// protected Object dirIcon;
	// protected Object fileIcon;

	protected final GlazedFile root;

	private final ServiceInterface si;
	private final FileManager fm;

	public UserspaceFileTreeModel(ServiceInterface si, GlazedFile root) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();

		if (root == null) {
			this.root = new GlazedFile("/ARCS/BeSTGRID");
		} else {
			this.root = root;
		}
		// dirIcon = UIManager.get("DirectoryPane.directoryIcon");
		// fileIcon = UIManager.get("DirectoryPane.fileIcon");

	}

	public GlazedFile getChild(Object parent, int index) {

		try {
			List<GlazedFile> childs = fm.ls((GlazedFile) parent);
			return childs.get(index);
		} catch (RemoteFileSystemException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getChildCount(Object parent) {
		try {
			List<GlazedFile> childs = fm.ls((GlazedFile) parent);
			return childs.size();
		} catch (RemoteFileSystemException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int getIndexOfChild(Object parent, Object child) {
		try {
			List<GlazedFile> childs = fm.ls((GlazedFile) parent);
			return childs.indexOf(childs);
		} catch (RemoteFileSystemException e) {
			e.printStackTrace();
			return -1;
		}

	}

	public GlazedFile getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {

		System.out.println("ISLEAF: " + ((GlazedFile) node).getName());
		if (GlazedFile.Type.FILETYPE_FILE.equals(((GlazedFile) node).getType())) {
			System.out.println("FALSE");
			return false;
		} else {
			System.out.println("TRUE");
			return true;
		}
	}

	public void valueForPathChanged(TreePath path, Object newValue) {

		System.out.println("Message: " + path.toString() + " for object: "
				+ newValue.toString());
	}

}