package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.StringUtils;
import org.netbeans.swing.outline.RenderDataProvider;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.virtual.utils.VirtualFileSystemBrowserTreeRenderer;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.GridFile;

import furbelow.SpinningDial;

public class GridFileTreeTableRenderer implements RenderDataProvider {

	private static SpinningDial LOADING_ICON = new SpinningDial(16, 16);
	private static URL imgURL = GridFileTreeTableRenderer.class
			.getResource("/group.gif");

	private static Icon groupIcon = new ImageIcon(imgURL);
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

	public GridFileTreeTableRenderer(ServiceInterface si) {
		this.si = si;
		this.uem = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
	}

	public Color getBackground(Object arg0) {
		return null;
	}

	public String getDisplayName(Object arg0) {

		Object userObject = ((DefaultMutableTreeNode) arg0).getUserObject();

		if (userObject instanceof String) {
			return (String) userObject;
		} else if (userObject instanceof GridFile) {

			GridFile f = (GridFile) userObject;
			return f.getName();
		} else {
			return arg0.getClass().getName();
		}
	}

	public Color getForeground(Object arg0) {
		return null;
	}

	public Icon getIcon(Object arg0) {

		Object userObject = ((DefaultMutableTreeNode) arg0).getUserObject();

		if (userObject instanceof String) {
			String string = (String) userObject;
			if (VirtualFileSystemBrowserTreeRenderer.LOADING_STRING
					.equals(string)) {
				return LOADING_ICON;
			}
		} else if (userObject instanceof GridFile) {
			GridFile f = (GridFile) userObject;
			// if (f.isVirtual()) {
			if (f.isVirtual() && StringUtils.isNotBlank(f.getPath())
					&& f.getPath().contains("group")) {

				// if (f.isVirtual()) {
				return groupIcon;
				// }
				// return null;
			} else if (f.isFolder()) {
				return folderIcon;
			} else {
				return fileIcon;
			}
		}

		return null;
	}

	public String getTooltipText(Object arg0) {

		Object userObject = ((DefaultMutableTreeNode) arg0).getUserObject();

		if (userObject instanceof GridFile) {

			GridFile file = (GridFile) userObject;

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

			String virt = "";
			if (file.isVirtual()) {
				virt = " / virtual";
			}

			String ttt = siteString + " / " + fqanString + virt;
			return ttt;
		} else {
			return null;
		}
	}

	public boolean isHtmlDisplayName(Object arg0) {
		return false;
	}

}
