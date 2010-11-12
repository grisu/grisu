package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.vpac.grisu.X;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.view.swing.files.GridFileListListener;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanel;
import org.vpac.grisu.frontend.view.swing.files.GridFileListPanelContextMenu;
import org.vpac.grisu.frontend.view.swing.files.GridFileTransferHandler;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.GridFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class VirtualFileSystemTreeTablePanel extends JPanel implements
		GridFileListPanel {

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private final ServiceInterface si;
	private final UserEnvironmentManager uem;
	private final FileManager fm;
	private JScrollPane scrollPane;
	private Outline tree;

	private GridFile oldDir;

	private Vector<GridFileListListener> listeners;
	private GridFileListPanelContextMenu popupMenu;

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

	synchronized public void addFileListListener(GridFileListListener l) {
		if (listeners == null) {
			listeners = new Vector<GridFileListListener>();
		}
		listeners.addElement(l);
	}

	public void displayHiddenFiles(boolean display) {
		// TODO Auto-generated method stub

	}

	private void fileClickOccured() {

		fireFilesSelected(getSelectedFiles());

	}

	private void fileDoubleClickOccured() {

		Set<GridFile> selFiles = getSelectedFiles();
		X.p("Size: " + selFiles.size());
		if (selFiles.size() == 1) {

			GridFile f = selFiles.iterator().next();
			if (f.isFolder()) {
				fireFilesSelected(null);
			} else {
				fireFileDoubleClicked(f);
			}

		} else {
			// TODO
			System.out.println("Shouldn't happen");
		}

	}

	private void fireFileDoubleClicked(final GridFile file) {
		// if we have no mountPointsListeners, do nothing...
		if ((listeners != null) && !listeners.isEmpty()) {

			final Thread thread = new Thread() {
				@Override
				public void run() {

					setLoading(true);
					try {
						// make a copy of the listener list in case
						// anyone adds/removes mountPointsListeners
						Vector<GridFileListListener> targets;
						synchronized (this) {
							targets = (Vector<GridFileListListener>) listeners
									.clone();
						}

						// walk through the listener list and
						// call the userInput method in each
						for (final GridFileListListener l : targets) {
							try {
								l.fileDoubleClicked(file);
							} catch (final Exception e1) {
								e1.printStackTrace();
							}
						}
					} finally {
						setLoading(false);
					}

				}
			};

			thread.start();

		}
	}

	private void fireFilesSelected(Set<GridFile> files) {

		// if we have no mountPointsListeners, do nothing...
		if ((listeners != null) && !listeners.isEmpty()) {

			setLoading(true);
			try {
				// make a copy of the listener list in case
				// anyone adds/removes mountPointsListeners
				Vector<GridFileListListener> targets;
				synchronized (this) {
					targets = (Vector<GridFileListListener>) listeners.clone();
				}

				// walk through the listener list and
				// call the userInput method in each
				for (final GridFileListListener l : targets) {
					try {
						l.filesSelected(files);
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
				}
			} finally {
				setLoading(false);
			}
		}

	}

	private void fireIsLoading(boolean loading) {
		// if we have no mountPointsListeners, do nothing...
		if ((listeners != null) && !listeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<GridFileListListener> targets;
			synchronized (this) {
				targets = (Vector<GridFileListListener>) listeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			for (final GridFileListListener l : targets) {
				try {
					l.isLoading(loading);
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void fireNewDirectory() {
		// if we have no mountPointsListeners, do nothing...
		if ((listeners != null) && !listeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<GridFileListListener> targets;
			synchronized (this) {
				targets = (Vector<GridFileListListener>) listeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			for (final GridFileListListener l : targets) {
				try {
					l.directoryChanged(getCurrentDirectory());
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public GridFile getCurrentDirectory() {

		Set<GridFile> selFiles = getSelectedFiles();
		if (selFiles.size() == 1) {
			GridFile f = selFiles.iterator().next();
			if (f.isFolder()) {
				return f;
			}
		}
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

	public Set<GridFile> getSelectedFiles() {
		int[] rows = getTree().getSelectedRows();
		Set<GridFile> result = new HashSet<GridFile>();
		for (int row : rows) {
			GridFile file = (GridFile) ((VirtualFileTreeNode) (getTree()
					.getValueAt(row, 0))).getUserObject();
			X.p("Selected: " + file.getName() + ": " + file.getUrl());
			result.add(file);
		}

		return result;
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
			tree.setDropMode(DropMode.INSERT);
			tree.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			tree.setTransferHandler(new GridFileTransferHandler(this, si));

			tree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getClickCount() == 2) {

						fileDoubleClickOccured();

					} else if (arg0.getClickCount() == 1) {

						GridFile sel = getCurrentDirectory();
						if ((sel != null) && !sel.equals(oldDir)) {
							fireNewDirectory();
							oldDir = sel;
						}
					}
				}
			});
		}
		return tree;
	}

	private void initialize() {

		GridFile root = new GridFile("grid://groups", -1L);
		VirtualFileTreeNode rootNode = new VirtualFileTreeNode(fm, root);

		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		rootNode.setModel(model);

		try {
			for (GridFile f : fm.ls(root)) {
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

	synchronized public void removeFileListListener(GridFileListListener l) {
		if (listeners == null) {
			listeners = new Vector<GridFileListListener>();
		}
		listeners.removeElement(l);
	}

	public void setContextMenu(GridFileListPanelContextMenu menu) {
		// TODO Auto-generated method stub

	}

	public void setCurrentUrl(String url) {
		// TODO Auto-generated method stub

	}

	public void setExtensionsToDisplay(String[] extensions) {
		// TODO Auto-generated method stub

	}

	private void setLoading(final boolean loading) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {

				if (!loading) {
					fireIsLoading(false);
				}

				getTree().setEnabled(!loading);
				getScrollPane().setEnabled(!loading);

				if (loading) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				} else {
					setCursor(Cursor.getDefaultCursor());
				}
				if (loading) {
					fireIsLoading(true);
				}

			}
		});

	}

	public void setRootUrl(String url) {
		// TODO Auto-generated method stub

	}
}
