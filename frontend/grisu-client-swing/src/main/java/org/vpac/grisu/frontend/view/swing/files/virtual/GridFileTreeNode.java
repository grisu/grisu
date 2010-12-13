package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.view.swing.files.virtual.utils.LazyLoadingTreeNode;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.dto.GridFile;

public class GridFileTreeNode extends LazyLoadingTreeNode {

	private static final long serialVersionUID = 1L;
	private final FileManager fm;

	private final boolean displayHiddenFiles;
	private final String[] extensionsToDisplay;

	public GridFileTreeNode(FileManager fm, GridFile userObject) {
		this(fm, userObject, null, true, null);
	}

	public GridFileTreeNode(FileManager fm, GridFile userObject,
			DefaultTreeModel model) {
		this(fm, userObject, model, true, null);
	}

	public GridFileTreeNode(FileManager fm, GridFile userObject,
			DefaultTreeModel model, boolean displayHiddenFiles,
			String[] extensionsToDisplay) {
		super(userObject, model);
		this.displayHiddenFiles = displayHiddenFiles;
		this.extensionsToDisplay = extensionsToDisplay;
		this.fm = fm;
		setAllowsChildren(getAllowsChildren());
	}

	public GridFileTreeNode(FileManager fm, String name) {
		this(fm, new GridFile(name, -1L), null, true, null);
	}

	@Override
	public boolean getAllowsChildren() {

		if (GridFile.FILETYPE_FILE.equals(((GridFile) (getUserObject()))
				.getType())) {
			return false;
		} else {
			return true;
		}
	}

	public GridFile getGridFile() {

		Object o = getUserObject();
		if (o instanceof GridFile) {
			return (GridFile) o;
		} else {
			return null;
		}

	}

	@Override
	public MutableTreeNode[] loadChildren(DefaultTreeModel model) {

		ArrayList<MutableTreeNode> list = new ArrayList<MutableTreeNode>();
		Set<String> names = new HashSet<String>();
		Set<String> duplicateNames = new HashSet<String>();

		GridFile temp = ((GridFile) getUserObject());
		temp.setChildren(null);
		try {
			Set<GridFile> dfo = fm.ls((GridFile) getUserObject());
			if (dfo == null) {
				return new MutableTreeNode[0];
			}
			for (GridFile f : dfo) {

				if (names.contains(f.getName())) {
					String oldName = f.getName();
					duplicateNames.add(oldName);
				}

				if (!displayHiddenFiles && f.getName().startsWith(".")) {
					continue;
				}

				if ((extensionsToDisplay != null)
						&& GridFile.FILETYPE_FILE.equals(f.getType())) {
					boolean display = false;
					for (String ext : extensionsToDisplay) {
						if (f.getName().endsWith(ext)) {
							display = true;
							break;
						}
					}
					if (!display) {
						continue;
					}
				}

				GridFileTreeNode gftn = new GridFileTreeNode(fm, f, model,
						displayHiddenFiles, extensionsToDisplay);

				list.add(gftn);
				names.add(f.getName());

				temp.addChild(f);
			}
		} catch (RemoteFileSystemException e) {
			e.printStackTrace();
			return null;
		}

		// replace duplicate names with sites appended to name
		for (MutableTreeNode node : list) {
			GridFile f = ((GridFileTreeNode) node).getGridFile();
			String oldName = f.getName();
			if (duplicateNames.contains(oldName)) {
				String sites = StringUtils.join(f.getSites(), ",");
				f.setName(oldName + " (" + sites + ")");
			}
		}
		return list.toArray(new MutableTreeNode[list.size()]);

	}
}
