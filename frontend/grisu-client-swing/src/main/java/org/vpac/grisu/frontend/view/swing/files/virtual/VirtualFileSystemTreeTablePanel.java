package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeModel;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.view.swing.files.FileListListener;
import org.vpac.grisu.frontend.view.swing.files.FileListPanel;
import org.vpac.grisu.frontend.view.swing.files.FileListPanelContextMenu;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.DtoFileObject;
import org.vpac.grisu.model.files.GlazedFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class VirtualFileSystemTreeTablePanel extends JPanel implements
		FileListPanel {

	private final ServiceInterface si;
	private final UserEnvironmentManager uem;
	private final FileManager fm;
	private JScrollPane scrollPane;
	private Outline tree;

	/**
	 * Create the panel.
	 */
	public VirtualFileSystemTreeTablePanel(ServiceInterface si) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.uem = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getScrollPane(), "2, 2, fill, fill");

		initialize();
	}

	public void addFileListListener(FileListListener l) {
		// TODO Auto-generated method stub

	}

	public void displayHiddenFiles(boolean display) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalize() {
		ToolTipManager.sharedInstance().unregisterComponent(tree);
	}

	public GlazedFile getCurrentDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	public JPanel getPanel() {
		return this;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTree());

		}
		return scrollPane;
	}

	public Set<GlazedFile> getSelectedFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	private Outline getTree() {
		if (tree == null) {
			tree = new Outline();
			// ToolTipManager.sharedInstance().registerComponent(tree);
			// tree.setCellRenderer(new
			// VirtualFileSystemBrowserTreeRenderer(si));
			tree.setRootVisible(false);
			tree.setRenderDataProvider(new VirtualFileTreeTableRenderer(si));
			tree.setDragEnabled(true);
			tree.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			tree.setTransferHandler(newHandler)
		}
		return tree;
	}

	// private JXTreeTable getTreeTable() {
	// if (treeTable == null) {
	// // TreeTableModel m = new UserspaceFileTreeTableModel(si,
	// // "/ARCS/BeSTGRID");
	// treeTable = new JXTreeTable();
	// }
	// return treeTable;
	// }

	private void initialize() {

		DtoFileObject root = new DtoFileObject("grid://groups", -1L);
		VirtualFileTreeNode rootNode = new VirtualFileTreeNode(fm, root);

		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		rootNode.setModel(model);

		try {
			for (DtoFileObject f : fm.ls(root)) {
				rootNode.add(new VirtualFileTreeNode(fm, f, model));
			}
		} catch (RemoteFileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		OutlineModel m = DefaultOutlineModel.createOutlineModel(model,
				new VirtualFileTreeTableRowModel(), false, "File");
		final LazyLoadingTreeController controller = new LazyLoadingTreeController(
				model);

		m.getTreePathSupport().addTreeWillExpandListener(controller);
		getTree().setModel(m);

	}

	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void removeFileListListener(FileListListener l) {
		// TODO Auto-generated method stub

	}

	public void setContextMenu(FileListPanelContextMenu menu) {
		// TODO Auto-generated method stub

	}

	public void setCurrentUrl(String url) {
		// TODO Auto-generated method stub

	}

	public void setExtensionsToDisplay(String[] extensions) {
		// TODO Auto-generated method stub

	}

	public void setRootUrl(String url) {
		// TODO Auto-generated method stub

	}
}
