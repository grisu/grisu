package grisu.frontend.view.swing.files.virtual;

import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.view.swing.files.virtual.utils.LazyLoadingTreeController;
import grisu.frontend.view.swing.files.virtual.utils.LazyLoadingTreeNode;
import grisu.model.FileManager;
import grisu.model.dto.GridFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridFileTreeNode extends LazyLoadingTreeNode {

	static final Logger myLogger = LoggerFactory
			.getLogger(GridFileTreeNode.class.getName());

	private static final long serialVersionUID = 1L;
	private final FileManager fm;

	private final boolean displayHiddenFiles;
	private final boolean displayFiles;
	private final String[] extensionsToDisplay;
	private final LazyLoadingTreeController controller;

	private boolean forceRefresh = false;

	public GridFileTreeNode(FileManager fm, GridFile userObject) {
		this(fm, userObject, null, true, true, null);
	}

	public GridFileTreeNode(FileManager fm, GridFile userObject,
			LazyLoadingTreeController controller) {
		this(fm, userObject, controller, true, true, null);
	}

	public GridFileTreeNode(FileManager fm, GridFile userObject,
			LazyLoadingTreeController controller, boolean displayFiles,
			boolean displayHiddenFiles,
			String[] extensionsToDisplay) {
		super(userObject, (controller == null) ? null : controller.getModel());
		this.displayFiles = displayFiles;
		this.displayHiddenFiles = displayHiddenFiles;
		this.extensionsToDisplay = extensionsToDisplay;
		this.fm = fm;
		this.controller = controller;
		setAllowsChildren(getAllowsChildren());
	}

	public GridFileTreeNode(FileManager fm, String name) {
		this(fm, new GridFile(name, -1L), null, true, true, null);
	}

	@Override
	public boolean getAllowsChildren() {

		if (getUserObject() instanceof GridFile) {
			final GridFile f = (GridFile) getUserObject();
			if (f.isInaccessable()) {
				return false;
			}
			if (f.isFolder()) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	public GridFile getGridFile() {

		final Object o = getUserObject();
		if (o instanceof GridFile) {
			return (GridFile) o;
		} else {
			return null;
		}

	}

	public boolean isExpanded() {
		return areChildrenLoaded();
	}

	@Override
	public MutableTreeNode[] loadChildren(DefaultTreeModel model) {

		final ArrayList<MutableTreeNode> list = new ArrayList<MutableTreeNode>();
		final Set<String> names = new HashSet<String>();
		final Set<String> duplicateNames = new HashSet<String>();

		final GridFile temp = ((GridFile) getUserObject());
		temp.setChildren(null);
		try {
			final Set<GridFile> dfo = fm.ls((GridFile) getUserObject(),
					forceRefresh)
					.getChildren();
			forceRefresh = false;
			if (dfo == null) {
				return new MutableTreeNode[0];
			}
			for (final GridFile f : dfo) {

				if (names.contains(f.getName())) {
					final String oldName = f.getName();
					duplicateNames.add(oldName);
				}

				if (!displayFiles && GridFile.FILETYPE_FILE.equals(f.getType())) {
					continue;
				}

				if (!displayHiddenFiles && f.getName().startsWith(".")) {
					continue;
				}

				if ((extensionsToDisplay != null)
						&& GridFile.FILETYPE_FILE.equals(f.getType())) {
					boolean display = false;
					for (final String ext : extensionsToDisplay) {
						if (f.getName().endsWith(ext)) {
							display = true;
							break;
						}
					}
					if (!display) {
						continue;
					}
				}

				final GridFileTreeNode gftn = new GridFileTreeNode(fm, f,
						controller, displayFiles, displayHiddenFiles,
						extensionsToDisplay);

				list.add(gftn);
				names.add(f.getName());

				temp.addChild(f);
			}
		} catch (final RemoteFileSystemException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return null;
		}

		// replace duplicate names with sites appended to name
		for (final MutableTreeNode node : list) {
			final GridFile f = ((GridFileTreeNode) node).getGridFile();
			final String oldName = f.getName();
			if (duplicateNames.contains(oldName)) {
				final String sites = StringUtils.join(f.getSites(), ",");
				f.setName(oldName + " (" + sites + ")");
			}
		}
		return list.toArray(new MutableTreeNode[list.size()]);

	}

	public void refresh() {

		if (isLeaf()) {
			return;
		}

		forceRefresh = true;

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {

				// X.p("Updating: " + f.getUrl());

				final Set<MutableTreeNode> children = new HashSet<MutableTreeNode>();
				for (int j = 0; j < getChildCount(); j++) {
					final MutableTreeNode childnode = (MutableTreeNode) getChildAt(j);
					children.add(childnode);
					// model.removeNodeFromParent(childnode);
					// GridFile f = (GridFile) childnode
					// .getUserObject();
					// X.p("Removing: " + f.getName());
				}

				for (final MutableTreeNode n : children) {
					getModel().removeNodeFromParent(n);

				}

				getModel().nodeChanged(GridFileTreeNode.this);

				controller.expandNode(GridFileTreeNode.this, getModel());
			}
		});

	}
}
