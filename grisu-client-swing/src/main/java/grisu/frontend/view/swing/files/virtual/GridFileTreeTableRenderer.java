package grisu.frontend.view.swing.files.virtual;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.StringUtils;
import org.netbeans.swing.outline.RenderDataProvider;

import furbelow.SpinningDial;
import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.files.virtual.utils.VirtualFileSystemBrowserTreeRenderer;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.GridFile;

public class GridFileTreeTableRenderer implements RenderDataProvider {

	private static SpinningDial LOADING_ICON = new SpinningDial(16, 16);
	private static URL remoteImgUrl = GridFileTreeTableRenderer.class
			.getResource("/icons/remote.png");
	private static URL groupImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/group.png");
	private static URL jobsImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/jobs.png");
	private static URL folderImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/folder.png");
	private static URL folderVirtualImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/folder_virtual.png");
	private static URL fileImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/file.png");
	private static URL userImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/user.png");
	private static URL serverImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/server.png");
	private static URL sitesImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/sites.png");
	private static URL siteImgURL = GridFileTreeTableRenderer.class
			.getResource("/icons/site.png");

	private static Icon remoteIcon = new ImageIcon(remoteImgUrl);
	private static Icon groupIcon = new ImageIcon(groupImgURL);
	private static Icon jobsIcon = new ImageIcon(jobsImgURL);
	private static Icon folderIcon = new ImageIcon(folderImgURL);
	private static Icon folderVirtualIcon = new ImageIcon(folderVirtualImgURL);
	private static Icon fileIcon = new ImageIcon(fileImgURL);
	private static Icon userIcon = new ImageIcon(userImgURL);
	private static Icon serverIcon = new ImageIcon(serverImgURL);
	private static Icon siteIcon = new ImageIcon(siteImgURL);
	private static Icon sitesIcon = new ImageIcon(sitesImgURL);

	private static Icon errorIcon = new ImageIcon(
			GridFileTreeTableRenderer.class.getResource("/error.gif"));

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

	/**
	 * @wbp.parser.entryPoint
	 */
	public GridFileTreeTableRenderer(ServiceInterface si) {
		this.si = si;
		this.uem = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
	}

	public Color getBackground(Object arg0) {
		return null;
	}

	public String getDisplayName(Object arg0) {

		final Object userObject = ((DefaultMutableTreeNode) arg0)
				.getUserObject();

		if (userObject instanceof String) {
			return (String) userObject;
		} else if (userObject instanceof GridFile) {

			final GridFile f = (GridFile) userObject;
			if (f.isInaccessable()) {
				if (StringUtils.containsIgnoreCase(f.getComment(), "timeout")) {
					return ("Timeout error : " + f.getUrl());
				} else {
					return ("Connection error : " + f.getUrl());
				}
			} else {
				return f.getName();
			}
		} else {
			return arg0.getClass().getName();
		}
	}

	public Color getForeground(Object arg0) {
		final Object userObject = ((DefaultMutableTreeNode) arg0)
				.getUserObject();
		if (userObject instanceof GridFile) {
			final GridFile f = (GridFile) userObject;
			if (f.isInaccessable()) {
				return Color.red;
			}
		}
		return null;

	}

	public Icon getIcon(Object arg0) {

		final Object userObject = ((DefaultMutableTreeNode) arg0)
				.getUserObject();

		if (userObject instanceof String) {
			final String string = (String) userObject;
			if (VirtualFileSystemBrowserTreeRenderer.LOADING_STRING
					.equals(string)) {
				return LOADING_ICON;
			}
		} else if (userObject instanceof GridFile) {

			final GridFile f = (GridFile) userObject;
			if (f.isInaccessable()) {
				return errorIcon;
			} else {
				if (f.isVirtual()) {
					return getVirtualIcon(f);
				} else if (f.isFolder()) {
					return folderIcon;
				} else {
					return fileIcon;
				}
			}
		}

		return null;
	}

	public String getTooltipText(Object arg0) {

		final Object userObject = ((DefaultMutableTreeNode) arg0)
				.getUserObject();

		if (userObject instanceof GridFile) {

			final GridFile file = (GridFile) userObject;

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
				final Set<String> tmp = new TreeSet<String>();
				for (final String group : file.getFqans()) {
					tmp.add(group);
				}
				fqanString = "Groups: " + StringUtils.join(tmp, ", ");
				break;
			}

			String virt = "";
			if (file.isVirtual()) {
				virt = " / virtual";
			}

			final String ttt = siteString + " / " + fqanString + virt;
			return ttt;
		} else {
			return null;
		}
	}

	private Icon getVirtualIcon(GridFile f) {

		if (!f.isFolder()) {
			return fileIcon;
		}

		final String path = FileManager.removeTrailingSlash(f.getPath());

		if (StringUtils.isBlank(path)) {
			return folderVirtualIcon;
		}

		// X.p("Path: " + path);

		if (path.equals(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://")) {
			return remoteIcon;
		} else if (path.startsWith(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME
				+ "://groups")) {
			return groupIcon;
		} else if (path.equals(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME
				+ "://jobs")) {
			return jobsIcon;
		} else if (path.equals(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME
				+ "://sites")) {
			return sitesIcon;
		} else if (path.startsWith(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME
				+ "://sites/")) {
			return siteIcon;
		} else {
			return folderVirtualIcon;
		}

	}

	public boolean isHtmlDisplayName(Object arg0) {
		return false;
	}

}
