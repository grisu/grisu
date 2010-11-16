package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JList;
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
	private Outline outline;

	private GridFile oldDir;

	private boolean useAsDropTarget = true;

	private Vector<GridFileListListener> listeners;
	private GridFileListPanelContextMenu popupMenu;
	private boolean displayLocalFileSystems = true;

	private final boolean displayHiddenFiles;
	private final String[] extensionsToDisplay;

	private final GridFile root;

	public VirtualFileSystemTreeTablePanel(ServiceInterface si) {
		this(si, null, true);
	}

	public VirtualFileSystemTreeTablePanel(ServiceInterface si, GridFile root) {
		this(si, root, true);
	}

	public VirtualFileSystemTreeTablePanel(ServiceInterface si, GridFile root,
			boolean useAsDropTarget) {
		this(si, root, useAsDropTarget, false, null, true);
	}

	/**
	 * Create the panel.
	 */
	public VirtualFileSystemTreeTablePanel(ServiceInterface si, GridFile root,
			boolean useAsDropTarget, boolean displayHiddenFiles,
			String[] extensionsToDisplay, boolean displayLocalFileSystems) {
		this.si = si;
		this.displayHiddenFiles = displayHiddenFiles;
		this.extensionsToDisplay = extensionsToDisplay;
		this.displayLocalFileSystems = displayLocalFileSystems;
		if (root == null) {
			this.root = GrisuRegistryManager.getDefault(si).getFileManager()
					.getGridRoot();
		} else {
			this.root = root;
		}
		this.useAsDropTarget = useAsDropTarget;
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

		initialize(this.root);
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
		if (selFiles == null) {
			return null;
		}
		if (selFiles.size() == 1) {
			GridFile f = selFiles.iterator().next();
			if (f.isFolder()) {
				return f;
			}
		}
		return null;
	}

	private Outline getOutline() {
		if (outline == null) {
			outline = new Outline();
			outline.setRootVisible(false);
			outline.setRenderDataProvider(new VirtualFileTreeTableRenderer(si));
			outline.setDragEnabled(true);
			outline.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			outline.setTransferHandler(new GridFileTransferHandler(this, si,
					useAsDropTarget));

			outline.setGridColor(Color.white);
			outline.setFillsViewportHeight(true);

			// VirtualFileSystemDragSource ds = new VirtualFileSystemDragSource(
			// tree, DnDConstants.ACTION_COPY);
			if (useAsDropTarget) {
				VirtualFileSystemDropTarget dt = new VirtualFileSystemDropTarget(
						si, outline);
			}

			outline.addMouseListener(new MouseAdapter() {
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
		return outline;
	}

	public JPanel getPanel() {
		return this;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getOutline());

		}
		return scrollPane;
	}

	public Set<GridFile> getSelectedFiles() {
		int[] rows = getOutline().getSelectedRows();
		Set<GridFile> result = new HashSet<GridFile>();
		for (int row : rows) {
			try {
				GridFile file = (GridFile) ((VirtualFileTreeNode) (getOutline()
						.getValueAt(row, 0))).getUserObject();
				X.p("Selected: " + file.getName() + ": " + file.getUrl());
				result.add(file);
			} catch (ClassCastException cce) {
				X.p("Invalid file selected");
				return null;
			}
		}

		return result;
	}

	private void initialize(GridFile root) {

		VirtualFileTreeNode rootNode = new VirtualFileTreeNode(fm, root);

		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		rootNode.setModel(model);

		try {
			for (GridFile f : fm.ls(root)) {
				rootNode.add(new VirtualFileTreeNode(fm, f, model,
						displayHiddenFiles, extensionsToDisplay));
			}
		} catch (RemoteFileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		if (displayLocalFileSystems) {

			GridFile localRoot = fm.getLocalRoot();

			VirtualFileTreeNode localRootNode = new VirtualFileTreeNode(fm,
					localRoot, model, displayHiddenFiles, extensionsToDisplay);
			rootNode.add(localRootNode);

		}

		OutlineModel m = DefaultOutlineModel.createOutlineModel(model,
				new VirtualFileTreeTableRowModel(), false, "File");

		final LazyLoadingTreeController controller = new LazyLoadingTreeController(
				model);

		m.getTreePathSupport().addTreeWillExpandListener(controller);
		getOutline().setModel(m);

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

				getOutline().setEnabled(!loading);
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

	/**
	 * Sets the table's selection mode to allow only single selections, a single
	 * contiguous interval, or multiple intervals.
	 * <P>
	 * <bold>Note:</bold> <code>JTable</code> provides all the methods for
	 * handling column and row selection. When setting states, such as
	 * <code>setSelectionMode</code>, it not only updates the mode for the row
	 * selection model but also sets similar values in the selection model of
	 * the <code>columnModel</code>. If you want to have the row and column
	 * selection models operating in different modes, set them both directly.
	 * <p>
	 * Both the row and column selection models for <code>JTable</code> default
	 * to using a <code>DefaultListSelectionModel</code> so that
	 * <code>JTable</code> works the same way as the <code>JList</code>. See the
	 * <code>setSelectionMode</code> method in <code>JList</code> for details
	 * about the modes.
	 * 
	 * @see JList#setSelectionMode
	 * @beaninfo description: The selection mode used by the row and column
	 *           selection models. enum: SINGLE_SELECTION
	 *           ListSelectionModel.SINGLE_SELECTION SINGLE_INTERVAL_SELECTION
	 *           ListSelectionModel.SINGLE_INTERVAL_SELECTION
	 *           MULTIPLE_INTERVAL_SELECTION
	 *           ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
	 */
	public void setSelectionMode(int mode) {
		getOutline().setSelectionMode(mode);
	}
}
