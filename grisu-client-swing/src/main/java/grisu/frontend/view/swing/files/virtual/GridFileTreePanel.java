package grisu.frontend.view.swing.files.virtual;

import grisu.control.ServiceInterface;
import grisu.control.events.FileDeletedEvent;
import grisu.control.events.FolderCreatedEvent;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.fileTransfers.FileTransferEvent;
import grisu.frontend.view.swing.files.GridFileListListener;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.frontend.view.swing.files.GridFileTransferHandler;
import grisu.frontend.view.swing.files.contextMenu.DefaultGridFileContextMenu;
import grisu.frontend.view.swing.files.contextMenu.GridFileListPanelContextMenu;
import grisu.frontend.view.swing.files.virtual.utils.LazyLoadingTreeController;
import grisu.model.FileCache;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.GridFile;
import grisu.model.info.dto.DtoProperty;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GridFileTreePanel extends JPanel implements GridFileListPanel,
		EventSubscriber {

	static final Logger myLogger = LoggerFactory
			.getLogger(GridFileTreePanel.class.getName());

	public final static String LOCAL_ALIAS = "Local files";
	public final static String REMOTE_ALIAS = "Remote files";

	private static List<GridFile> defaultFileRoots = null;

	public static final LinkedHashMap<String, String> defaultRoots = Maps
			.newLinkedHashMap();

	public static synchronized final List<GridFile> getDefaultRoots(
			ServiceInterface si) {

		if (defaultFileRoots == null) {
			defaultFileRoots = new LinkedList<GridFile>();

			for (String alias : defaultRoots.keySet()) {
				if (LOCAL_ALIAS.equals(alias)
						&& (defaultRoots.get(alias) == null)) {
					final GridFile localRoot = GrisuRegistryManager
							.getDefault(si).getFileManager().getLocalRoot();
					defaultFileRoots.add(localRoot);
				} else if (REMOTE_ALIAS.equals(alias)
						&& (defaultRoots.get(alias) == null)) {
					final GridFile gridRoot = GrisuRegistryManager
							.getDefault(si).getFileManager().getGridRoot();
					defaultFileRoots.add(gridRoot);
				} else {
					GridFile file = null;
					try {
						file = GrisuRegistryManager.getDefault(si)
								.getFileManager()
								.createGridFile(defaultRoots.get(alias));
						file.setName(alias);
						defaultFileRoots.add(file);

					} catch (final RemoteFileSystemException e) {
						myLogger.error(e.getLocalizedMessage(), e);
					}
				}
			}
		}

		return defaultFileRoots;

	}

	{
		defaultRoots.put(REMOTE_ALIAS, null);
		defaultRoots.put(LOCAL_ALIAS, null);
	}

	public static final String EXTENSIONS_KEY = "extensions";
	public static final String FOLDERS_SELECTABLE_KEY = "folders_selectable";

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

	public static TreePath getPath(TreeNode treeNode) {
		List<Object> nodes = new ArrayList<Object>();
		if (treeNode != null) {
			nodes.add(treeNode);
			treeNode = treeNode.getParent();
			while (treeNode != null) {
				nodes.add(0, treeNode);
				treeNode = treeNode.getParent();
			}
		}

		return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
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
	private boolean displayHiddenFiles = false;
	private boolean displayFiles = true;

	private String[] extensionsToDisplay;

	private DefaultTreeModel model;

	private List<GridFile> roots;

	private GridFile root;

	private LazyLoadingTreeController controller;

	private volatile boolean isInitializing = false;

	/**
	 * @wbp.parser.constructor
	 */
	public GridFileTreePanel(ServiceInterface si) {
		this(si, null, true);
	}

	public GridFileTreePanel(ServiceInterface si, List<GridFile> root) {
		this(si, root, true);
	}

	public GridFileTreePanel(ServiceInterface si, List<GridFile> root,
			boolean useAsDropTarget) {
		this(si, root, useAsDropTarget, true, false, null);
	}

	/**
	 * Create the panel.
	 */
	public GridFileTreePanel(ServiceInterface si, List<GridFile> roots,
			boolean useAsDropTarget, boolean displayFiles,
			boolean displayHiddenFiles, String[] extensionsToDisplay) {
		this.si = si;
		this.displayFiles = displayFiles;
		this.displayHiddenFiles = displayHiddenFiles;
		this.extensionsToDisplay = extensionsToDisplay;

		this.useAsDropTarget = useAsDropTarget;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.uem = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		setRoots(roots);

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getScrollPane(), "2, 2, fill, fill");

		final GridFileListPanelContextMenu contextMenu = new DefaultGridFileContextMenu();
		setContextMenu(contextMenu);

		EventBus.subscribe(FileTransferEvent.class, this);
		EventBus.subscribe(FolderCreatedEvent.class, this);
		EventBus.subscribe(FileDeletedEvent.class, this);

		initialize();
	}

	// private void fileClickOccured() {
	//
	// fireFilesSelected(getSelectedFiles());
	//
	// }

	synchronized public void addGridFileListListener(GridFileListListener l) {
		if (listeners == null) {
			listeners = new Vector<GridFileListListener>();
		}
		listeners.addElement(l);
	}

	private void expandNodes(List<GridFile> files) {

		if (controller == null) {
			myLogger.debug("Controller not initialized, not checking nodes");
			return;
		}

		try {
			final OutlineModel m = (OutlineModel) getOutline().getModel();
			for (int i = 0; i < m.getRowCount(); i++) {

				final Object n = m.getValueAt(i, 0);

				if (n instanceof GridFileTreeNode) {

					final GridFileTreeNode node = (GridFileTreeNode) n;
					final GridFile f = (GridFile) node.getUserObject();

					if (files.contains(f)) {
						myLogger.debug("Expanding: " + f.getUrl());
						TreePath p = getPath(node);
						m.getTreePathSupport().expandPath(p);
					}

				}
			}
		} catch (ClassCastException e) {
			myLogger.debug("No outline model yet.");
			return;
		}

		return;
	}

	private void fileDoubleClickOccured() {

		final Set<GridFile> selFiles = getSelectedFiles();
		if (selFiles.size() == 1) {

			final GridFile f = selFiles.iterator().next();
			if (f.isInaccessable()) {
				fireFileDoubleClicked(f);
			}
			if (f.isFolder()) {
				fireFilesSelected(null);
			} else {

				fireFileDoubleClicked(f);
			}

		} else {
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
								myLogger.error(e1.getLocalizedMessage(), e1);
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
						myLogger.error(e1.getLocalizedMessage(), e1);
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
					myLogger.error(e1.getLocalizedMessage(), e1);
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
					myLogger.error(e1.getLocalizedMessage(), e1);
				}
			}
		}
	}

	public GridFile getCurrentDirectory() {

		final Set<GridFile> selFiles = getSelectedFiles();
		if (selFiles == null) {
			return null;
		}
		if (selFiles.size() == 1) {
			final GridFile f = selFiles.iterator().next();
			if (f.isFolder()) {
				return f;
			}
		}
		return null;
	}

	private List<GridFile> getCurrentlyExpandedNodes() {

		if (controller == null) {
			myLogger.debug("Controller not initialized, not checking nodes");
			return Lists.newArrayList();
		}

		List<GridFile> expanded = Lists.newArrayList();
		try {
			final OutlineModel m = (OutlineModel) getOutline().getModel();

			for (int i = 0; i < m.getRowCount(); i++) {

				final Object n = m.getValueAt(i, 0);

				if (n instanceof GridFileTreeNode) {

					final GridFileTreeNode node = (GridFileTreeNode) n;
					if (node.isExpanded()) {
						final GridFile f = (GridFile) node.getUserObject();
						expanded.add(f);
						myLogger.debug("node expanded: " + f.getUrl());
					}

				}
			}
		} catch (ClassCastException e) {
			myLogger.debug("No outline model yet.");
			return Lists.newArrayList();
		}

		return expanded;

	}

	private synchronized Outline getOutline() {
		if (outline == null) {
			outline = new Outline();
			outline.setRootVisible(false);
			outline.setRenderDataProvider(new GridFileTreeTableRenderer(si));
			outline.setDragEnabled(true);
			outline.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			outline.setTransferHandler(new GridFileTransferHandler(this, si,
					useAsDropTarget));
			// outline.setDropMode(DropMode.ON);

			outline.setGridColor(Color.white);
			outline.setFillsViewportHeight(true);

			// VirtualFileSystemDragSource ds = new VirtualFileSystemDragSource(
			// tree, DnDConstants.ACTION_COPY);
			if (useAsDropTarget) {
				final GridFileTreeDropTarget dt = new GridFileTreeDropTarget(
						si, outline);
			}

			outline.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getClickCount() == 2) {

						fileDoubleClickOccured();

					} else if (arg0.getClickCount() == 1) {

						final GridFile sel = getCurrentDirectory();
						if ((sel != null) && !sel.equals(oldDir)) {
							fireNewDirectory();
							fireFilesSelected(getSelectedFiles());
							oldDir = sel;
						} else {
							fireFilesSelected(getSelectedFiles());
						}
					}
				}

			});
			myLogger.debug("Outline created");
		}
		return outline;
	}

	public JPanel getPanel() {
		return this;
	}

	private synchronized List<GridFile> getRoots() {
		if (this.root != null) {
			if (this.roots == null) {
				Set<GridFile> childs = root.getChildren();
				if ((childs == null) || (childs.size() == 0)) {
					try {
						childs = fm.ls(root).getChildren();
					} catch (RemoteFileSystemException e) {
						childs = Sets.newHashSet((new GridFile(root.getUrl(),
								false, e)));
					}
					this.roots = Lists.newArrayList(childs);
				}
			}
		} else {
			if (this.roots == null) {
				final GridFile gridRoot = GrisuRegistryManager.getDefault(si)
						.getFileManager().getGridRoot();
				final GridFile localRoot = GrisuRegistryManager.getDefault(si)
						.getFileManager().getLocalRoot();
				this.roots = new LinkedList<GridFile>();
				this.roots.add(gridRoot);
				this.roots.add(localRoot);
			}

		}
		return this.roots;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setViewportView(getOutline());

		}
		return scrollPane;
	}

	public Set<GridFile> getSelectedFiles() {
		final int[] rows = getOutline().getSelectedRows();
		final Set<GridFile> result = new HashSet<GridFile>();
		for (final int row : rows) {
			try {
				final GridFile file = (GridFile) ((GridFileTreeNode) (getOutline()
						.getValueAt(row, 0))).getUserObject();
				result.add(file);
			} catch (final ClassCastException cce) {
				return null;
			}
		}

		return result;
	}

	public ServiceInterface getServiceInterface() {
		return si;
	}

	private void initialize() {

		if (isInitializing) {
			return;
		}

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// setEnabled(false);
				getOutline().setEnabled(false);
			}
		});

		try {

			isInitializing = true;
			final GridFileTreeNode rootNode = new GridFileTreeNode(fm,
					"virtual");

			model = new DefaultTreeModel(rootNode);

			rootNode.setModel(model);
			controller = new LazyLoadingTreeController(model);

			for (final GridFile f : getRoots()) {
				rootNode.add(new GridFileTreeNode(fm, f, controller,
						displayFiles, displayHiddenFiles, extensionsToDisplay));
			}

			final OutlineModel m = DefaultOutlineModel.createOutlineModel(
					model, new GridFileTreeTableRowModel(), false, "File");

			m.getTreePathSupport().addTreeWillExpandListener(controller);
			getOutline().setModel(m);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			isInitializing = false;
			SwingUtilities.invokeLater(new Thread() {
				@Override
				public void run() {
					getOutline().setEnabled(true);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}

	}

	public synchronized void onEvent(Object event) {

		if (event instanceof FileTransferEvent) {
			final FileTransferEvent ev = (FileTransferEvent) event;

			// X.p("---------------------------------");
			// X.p("Status: " + ev.getFileTransfer().getStatus());
			// X.p("Property changed: " + ev.getChangedProperty());
			// X.p("Transfer sources: "
			// + StringUtils
			// .join(ev.getFileTransfer().getSourceUrl(), ","));
			// X.p("Transfer target: " +
			// ev.getFileTransfer().getTargetDirUrl());
			// X.p("---------------------------------");

			if (ev.getFileTransfer().isFinished()) {

				refreshFolder(ev.getFileTransfer().getTargetDirUrl());

			}

		} else if (event instanceof FolderCreatedEvent) {
			final FolderCreatedEvent ev = (FolderCreatedEvent) event;

			refreshFolder(FileManager.calculateParentUrl(ev.getUrl()));
		} else if (event instanceof FileDeletedEvent) {
			final FileDeletedEvent ev = (FileDeletedEvent) event;
			// X.p("CCC: " + FileManager.calculateParentUrl(ev.getUrl()));

			refreshFolder(FileManager.calculateParentUrl(ev.getUrl()));
		}
	}

	public void refresh() {
		refresh(false);
	}

	public synchronized void refresh(boolean forceRefresh) {

		if (this.root != null) {
			FileCache.remove(this.root);
			this.roots = null;
		}

		final List<GridFile> exp = getCurrentlyExpandedNodes();

		Thread t = new Thread() {
			@Override
			public void run() {
				initialize();
				expandNodes(exp);
			}
		};
		t.setName("filepanelupdate");
		t.start();

	}

	public void refreshFolder(String url) {

		final String tempUrl = FileManager.removeTrailingSlash(url);
		final TableModel m = getOutline().getModel();

		for (int i = 0; i < m.getRowCount(); i++) {

			final Object n = m.getValueAt(i, 0);

			if (n instanceof GridFileTreeNode) {

				final GridFileTreeNode node = (GridFileTreeNode) n;

				final GridFile f = (GridFile) node.getUserObject();
				for (final String urlTemp : DtoProperty
						.mapFromDtoPropertiesList(f.getUrls()).keySet()) {

					// X.p(f.getUrl());
					// X.p(url);
					final String temp = FileManager
							.removeTrailingSlash(urlTemp);
					if (temp.equals(tempUrl)) {

						node.refresh();
					}
				}
			}
		}
	}

	synchronized public void removeGridFileListListener(GridFileListListener l) {
		if (listeners == null) {
			listeners = new Vector<GridFileListListener>();
		}
		listeners.removeElement(l);
	}

	public void setConfig(Map<String, String> config) {

	}

	public void setContextMenu(GridFileListPanelContextMenu menu) {

		if (this.popupMenu != null) {
			removeGridFileListListener(this.popupMenu);
		}
		this.popupMenu = menu;
		this.popupMenu.setGridFileListPanel(this);
		addGridFileListListener(this.popupMenu);
		addPopup(getOutline(), this.popupMenu.getJPopupMenu());

	}

	public void setCurrentUrl(String url) {
		// TODO Auto-generated method stub

	}

	public void setDisplayFiles(boolean displayFiles) {
		this.displayFiles = displayFiles;
		refresh();
	}

	public void setDisplayHiddenFiles(boolean display) {

		this.displayHiddenFiles = display;
		refresh();
	}

	public void setExtensionsToDisplay(String[] extensions) {

		this.extensionsToDisplay = extensions;
		refresh();
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

	public void setRoots(List<GridFile> rootsNew) {
		this.root = null;
		this.roots = null;
		if ((rootsNew != null) && (rootsNew.size() > 0)) {

			if (rootsNew.size() == 1) {
				setRootUrl(rootsNew.get(0));
			} else {
				this.roots = rootsNew;
				refresh();
			}
		} else {
			refresh();
		}
	}

	public void setRootUrl(GridFile root) {

		this.root = root;
		this.roots = null;

		refresh();

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
