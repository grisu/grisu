package org.vpac.grisu.frontend.view.swing.files.virtual.utils;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.GridFile;

import furbelow.SpinningDial;

public class VirtualFileSystemBrowserTreeRenderer extends
		DefaultTreeCellRenderer implements TreeCellRenderer {

	public static final String LOADING_STRING = "Loading...";

	private static SpinningDial LOADING_ICON = new SpinningDial(16, 16);

	private static FileSystemView fsView = FileSystemView.getFileSystemView();
	private static Icon folderIcon = fsView.getSystemIcon(new File(System
			.getProperty("user.home")));
	// TODO think of something better?
	private static Icon fileIcon = fsView.getSystemIcon(findFile());

	private static File findFile() {
		for (final File file : new File(System.getProperty("user.home"))
				.listFiles()) {
			if (file.isFile()) {
				return file;
			}
		}
		return null;
	}

	private final ServiceInterface si;
	private final UserEnvironmentManager uem;
	private final FileManager fm;

	public VirtualFileSystemBrowserTreeRenderer(ServiceInterface si) {
		this(si, false);
	}

	public VirtualFileSystemBrowserTreeRenderer(ServiceInterface si,
			boolean displayFullFqan) {
		this.si = si;
		this.uem = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, isSelected, expanded,
				leaf, row, hasFocus);

		if (isSelected) {
			setBackground((Color) UIManager.get("Table.selectionBackground"));
		} else {
			setBackground(tree.getBackground());
		}

		final Object value2 = (((DefaultMutableTreeNode) value).getUserObject());

		if (value2 instanceof String) {

			String txt = (String) value2;
			// this.setText((String) value2);
			this.setText(txt);
			if (LOADING_STRING.equals(txt)) {
				this.setIcon(LOADING_ICON);
			} else {
				this.setIcon(null);
			}

		} else if (value2 instanceof GridFile) {
			final GridFile file = (GridFile) value2;
			String text = null;
			if (file.isFolder()) {

				this.setIcon(folderIcon);
			} else {
				this.setIcon(fileIcon);
			}
			text = file.getName();
			this.setText(text);

			String siteString = null;

			switch (file.getSites().size()) {
			case 0:
				siteString = "Sites: n/a";
				break;
			case 1:
				siteString = "Site: " + file.getSites().iterator().next();
				break;
			default:
				siteString = "Sites: "
						+ StringUtils.join(file.getSites(), ", ");
				break;

			}

			String fqanString = null;

			switch (file.getFqans().size()) {
			case 0:
				fqanString = "Groups: n/a";
				break;
			case 1:
				fqanString = "Group: "
						+ uem.getUniqueGroupname(file.getFqans().iterator()
								.next());
				break;
			default:
				Set<String> tmp = new TreeSet<String>();
				for (String group : file.getFqans()) {
					tmp.add(group);
				}
				fqanString = "Groups: " + StringUtils.join(tmp, ", ");
				break;
			}

			String ttt = siteString + ", " + fqanString;

			this.setToolTipText(ttt);
			// tree.setToolTipText(ttt);
		}

		return this;

	}
}
