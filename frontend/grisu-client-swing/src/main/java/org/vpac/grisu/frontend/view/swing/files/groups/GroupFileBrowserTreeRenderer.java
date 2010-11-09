package org.vpac.grisu.frontend.view.swing.files.groups;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.files.GlazedFile;

public class GroupFileBrowserTreeRenderer extends DefaultTreeCellRenderer
		implements TreeCellRenderer {

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

	private final boolean displayFullFqan;
	private final ServiceInterface si;
	private final UserEnvironmentManager uem;
	private final FileManager fm;

	public GroupFileBrowserTreeRenderer(ServiceInterface si) {
		this(si, false);
	}

	public GroupFileBrowserTreeRenderer(ServiceInterface si,
			boolean displayFullFqan) {
		this.si = si;
		this.displayFullFqan = displayFullFqan;
		this.uem = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		if (isSelected) {
			setBackground((Color) UIManager.get("Table.selectionBackground"));
		} else {
			setBackground(tree.getBackground());
		}

		final Object value2 = (((DefaultMutableTreeNode) value).getUserObject());

		if (value2 instanceof String) {

			// this.setText((String) value2);
			this.setText((String) value2);
			this.setIcon(null);

		} else if (value2 instanceof GlazedFile) {
			final GlazedFile file = (GlazedFile) value2;
			String text = null;
			if (file.isFolder()) {
				if (GlazedFile.Type.FILETYPE_GROUP.equals(file.getType())) {
					if (displayFullFqan) {
						text = file.getName();
					} else {
						text = uem.getUniqueGroupname(file.getName());
					}
				} else {
					text = file.getName();
				}

				// if
				// (GlazedFile.Type.FILETYPE_MOUNTPOINT.equals(file.getType()))
				// {
				// text = uem.getMountPointForUrl(file.getUrl()).getSite();
				// }
				if (GlazedFile.Type.FILETYPE_MOUNTPOINT.equals(file.getType())) {
					text = uem.getMountPointForUrl(file.getUrl()).getAlias();
				}

				this.setIcon(folderIcon);
			} else {
				this.setIcon(fileIcon);
				try {
					text = URLDecoder.decode(file.getName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					text = file.getName();
				}

			}

			this.setText(text);

		}

		return this;

	}
}
