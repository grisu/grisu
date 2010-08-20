package org.vpac.grisu.frontend.view.swing.files;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.events.FolderCreatedEvent;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransaction;
import org.vpac.grisu.frontend.control.fileTransfers.FileTransferEvent;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.model.dto.DtoFolder;
import org.vpac.grisu.model.files.GlazedFile;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;

public class FileListPanelSimple extends JPanel implements FileListPanel,
		EventSubscriber {

	static final Logger myLogger = Logger.getLogger(FileListPanelSimple.class
			.getName());

	public static final String UNDETERMINED = "undetermined";

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
	private final FileManager fm;

	private final UserEnvironmentManager em;

	private final EventTableModel<GlazedFile> fileModel;
	private final EventList<GlazedFile> currentDirectoryContent = new BasicEventList<GlazedFile>();

	private final SortedList<GlazedFile> sortedList = new SortedList<GlazedFile>(
			currentDirectoryContent, new GlazedFileComparator());

	private final GlazedFileMatcherEditor textMatcherEditor = new GlazedFileMatcherEditor();

	private final FilterList<GlazedFile> filteredList = new FilterList<GlazedFile>(
			sortedList, textMatcherEditor);

	private GlazedFile currentDirectory = null;
	private DtoFolder currentFolder = null;

	private JXTable table;
	private Thread updateThread;

	private JScrollPane scrollPane;
	private final boolean includeLocal = true;
	private boolean displayTimestamp = false;

	private boolean displaySize = true;

	private String rootUrl = null;
	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<FileListListener> listeners;
	private FileListPanelContextMenu popupMenu;

	/**
	 * Create the panel.
	 * 
	 * @wbp.parser.constructor
	 */
	public FileListPanelSimple(ServiceInterface si, String rootUrl,
			String startUrl) {
		this(si, rootUrl, startUrl, true, false);
	}

	public FileListPanelSimple(ServiceInterface si, String rootUrl,
			String startUrl, boolean displaySize, boolean displayTimestamp) {

		this.displaySize = displaySize;
		this.displayTimestamp = displayTimestamp;
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		setLayout(new BorderLayout(0, 0));

		fileModel = new EventTableModel<GlazedFile>(filteredList,
				new GlazedFileTableFormat());
		add(getScrollPane(), BorderLayout.CENTER);

		setRootAndCurrentUrl(rootUrl, startUrl);

		DefaultGrisuFileContextMenu menu = new DefaultGrisuFileContextMenu(
				this.si);
		setContextMenu(menu);

		EventBus.subscribe(FileTransferEvent.class, this);
		EventBus.subscribe(FolderCreatedEvent.class, this);
	}

	// register a listener
	synchronized public void addFileListListener(FileListListener l) {
		if (listeners == null) {
			listeners = new Vector<FileListListener>();
		}
		listeners.addElement(l);
	}

	public boolean currentUrlIsStartUrl() {

		if (rootUrl.equals(getCurrentDirectory().getUrl())) {
			return true;
		} else {
			return false;
		}
	}

	private void fileClickOccured() {

		fireFilesSelected(getSelectedFiles());

	}

	private void fileDoubleClickOccured() {

		int selRow = table.getSelectedRow();
		if (selRow >= 0) {

			GlazedFile sel = (GlazedFile) fileModel.getValueAt(selRow, 0);

			if (sel.isFolder()) {
				fireFilesSelected(null);
				setCurrent(sel);
			} else {
				fireFileDoubleClicked(sel);
			}

		}

	}

	private void fireFileDoubleClicked(final GlazedFile file) {
		// if we have no mountPointsListeners, do nothing...
		if ((listeners != null) && !listeners.isEmpty()) {

			Thread thread = new Thread() {
				@Override
				public void run() {

					setLoading(true);
					try {
						// make a copy of the listener list in case
						// anyone adds/removes mountPointsListeners
						Vector<FileListListener> targets;
						synchronized (this) {
							targets = (Vector<FileListListener>) listeners
									.clone();
						}

						// walk through the listener list and
						// call the userInput method in each
						for (FileListListener l : targets) {
							try {
								l.fileDoubleClicked(file);
							} catch (Exception e1) {
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

	private void fireFilesSelected(Set<GlazedFile> files) {

		// if we have no mountPointsListeners, do nothing...
		if ((listeners != null) && !listeners.isEmpty()) {

			setLoading(true);
			try {
				// make a copy of the listener list in case
				// anyone adds/removes mountPointsListeners
				Vector<FileListListener> targets;
				synchronized (this) {
					targets = (Vector<FileListListener>) listeners.clone();
				}

				// walk through the listener list and
				// call the userInput method in each
				for (FileListListener l : targets) {
					try {
						l.filesSelected(files);
					} catch (Exception e1) {
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
			Vector<FileListListener> targets;
			synchronized (this) {
				targets = (Vector<FileListListener>) listeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			for (FileListListener l : targets) {
				try {
					l.isLoading(loading);
				} catch (Exception e1) {
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
			Vector<FileListListener> targets;
			synchronized (this) {
				targets = (Vector<FileListListener>) listeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			for (FileListListener l : targets) {
				try {
					l.directoryChanged(getCurrentDirectory());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public GlazedFile getCurrentDirectory() {
		return currentDirectory;
	}

	public String getCurrentDirectoryUrl() {
		if (currentDirectory != null) {
			return currentDirectory.getUrl();
		} else {
			return null;
		}
	}

	public JPanel getPanel() {
		return this;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {

			scrollPane = new JScrollPane(getTable());

		}
		return scrollPane;
	}

	public Set<GlazedFile> getSelectedFiles() {

		Set<GlazedFile> selected = new TreeSet<GlazedFile>();

		for (int r : table.getSelectedRows()) {

			if (r >= 0) {
				GlazedFile sel = (GlazedFile) fileModel.getValueAt(r, 0);
				selected.add(sel);
			}

		}

		return selected;

	}

	private JXTable getTable() {
		if (table == null) {
			table = new JXTable(fileModel);

			table.setDragEnabled(true);
			table.setDropMode(DropMode.ON);
			table.setTransferHandler(new GlazedFilesTransferHandler(this, si));

			// disable sorting for now
			table.setAutoCreateRowSorter(false);
			table.setRowSorter(null);
			table.setColumnControlVisible(true);
			// table.setDefaultRenderer(GlazedFile.class, new
			// GlazedFileRenderer(
			// this));
			// table.setDefaultRenderer(Long.class, new FileSizeRenderer());

			int vColIndex = 0;
			TableColumn col = table.getColumnModel().getColumn(vColIndex);
			col.setCellRenderer(new GlazedFileRenderer(this));
			int width = 120;
			col.setPreferredWidth(width);
			col.setMinWidth(80);

			vColIndex = 1;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setCellRenderer(new FileSizeRenderer());
			width = 60;
			col.setPreferredWidth(width);
			col.setMaxWidth(80);

			vColIndex = 2;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setCellRenderer(new TimestampRenderer());
			width = 90;
			col.setPreferredWidth(width);
			col.setMaxWidth(120);

			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {

					if (arg0.getClickCount() == 2) {
						fileDoubleClickOccured();
					} else if (arg0.getClickCount() == 1) {
						fileClickOccured();
					}

				}
			});

			if (!displaySize) {
				table.getColumnExt("Size").setVisible(false);
			}

			if (!displayTimestamp) {
				TableColumnExt colext = table.getColumnExt("Date modified");
				colext.setVisible(false);
			}

		}
		return table;
	}

	public void onEvent(Object event) {

		if (event instanceof FileTransferEvent) {
			FileTransferEvent fevent = (FileTransferEvent) event;
			if (fevent.getFileTransfer().isFinished()) {

				if (FileTransaction.DELETE_STRING.equals(fevent
						.getFileTransfer().getTargetDirUrl())) {
					// means files deleted
					if ((getCurrentDirectoryUrl() != null)) {
						for (String url : fevent.getFileTransfer()
								.getSourceUrl()) {
							if (url.startsWith(getCurrentDirectoryUrl())) {
								refresh();
								return;
							}
						}

					}

				} else if ((getCurrentDirectoryUrl() != null)
						&& getCurrentDirectoryUrl().equals(
								fevent.getFileTransfer().getTargetDirUrl())) {
					refresh();
				}
			}
		} else if (event instanceof FolderCreatedEvent) {
			FolderCreatedEvent fevent = (FolderCreatedEvent) event;

			if ((getCurrentDirectoryUrl() != null)
					&& fevent.getUrl().startsWith(getCurrentDirectoryUrl())) {
				refresh();
			}
		}

	}

	public void refresh() {

		setCurrent(null, getCurrentDirectory());
	}

	// remove a listener
	synchronized public void removeFileListListener(FileListListener l) {
		if (listeners == null) {
			listeners = new Vector<FileListListener>();
		}
		listeners.removeElement(l);
	}

	public void setContextMenu(FileListPanelContextMenu menu) {

		if (this.popupMenu != null) {
			removeFileListListener(this.popupMenu);
		}
		this.popupMenu = menu;
		menu.setFileListPanel(this);
		addFileListListener(this.popupMenu);
		addPopup(table, this.popupMenu.getJPopupMenu());

	}

	public void setCurrent(GlazedFile file) {

		setCurrent(null, file);

	}

	private synchronized void setCurrent(final String url, final GlazedFile file) {

		if ((updateThread != null) && updateThread.isAlive()) {
			updateThread.interrupt();
			try {
				updateThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		updateThread = new Thread("Populating fileList") {

			@Override
			public void run() {

				setLoading(true);

				GlazedFile oldDir = getCurrentDirectory();
				try {

					if (file != null) {

						if (GlazedFile.Type.FILETYPE_ROOT
								.equals(file.getType())) {
							setCurrentDirToGridRoot();
						} else if (GlazedFile.Type.FILETYPE_SITE.equals(file
								.getType())) {
							setCurrentDirToSite(file.getName());
						} else if (GlazedFile.Type.FILETYPE_MOUNTPOINT
								.equals(file.getType())) {
							setUrl(file.getUrl());
						} else if (GlazedFile.Type.FILETYPE_FOLDER.equals(file
								.getType())) {
							setUrl(file.getUrl());
						} else {
							// do nothing
						}

					} else {

						if (url == null) {
							setEmptyList();
						} else if (GlazedFile.ROOT.equals(url)) {
							setCurrentDirToGridRoot();
						} else if (em.getAllAvailableSites().contains(url)) {
							setCurrentDirToSite(url);
						} else if (em.isMountPointAlias(url)) {
							String rootUrl = em.getMountPointForAlias(url)
									.getRootUrl();
							setUrl(rootUrl);
						} else {
							setUrl(url);
						}

					}

					if (oldDir != null && !oldDir.equals(getCurrentDirectory())) {
						fireNewDirectory();
					}

				} catch (Exception e) {

				} finally {

					SwingUtilities.invokeLater(new Thread() {
						@Override
						public void run() {
							table.repaint();
							table.clearSelection();
							// scroll to top of list
							table.scrollRectToVisible(table.getCellRect(0, 0,
									true));
						}

					});

					setLoading(false);
				}
			}
		};

		updateThread.start();

	}

	private void setCurrentDirToFolder(DtoFolder folder) {

		currentDirectory = new GlazedFile(folder);
		currentFolder = folder;

		currentDirectoryContent.getReadWriteLock().writeLock().lock();
		try {
			currentDirectoryContent.clear();

			if (!FileManager.isLocal(folder.getRootUrl())) {
				if (!currentUrlIsStartUrl()) {

					if (em.isMountPointRoot(folder.getRootUrl())) {
						GlazedFile temp = new GlazedFile(em
								.getMountPointForUrl(folder.getRootUrl())
								.getSite());
						temp.setParent();
						currentDirectoryContent.add(temp);
					} else {
						GlazedFile temp = new GlazedFile(
								fm.calculateParentUrl(folder.getRootUrl()), si);
						temp.setParent();
						currentDirectoryContent.add(temp);
					}
				}
			} else {
				if (!currentUrlIsStartUrl()) {
					try {
						File parent = new File(new URI(folder.getRootUrl()))
								.getParentFile();

						GlazedFile tempFile;
						if (parent == null) {
							tempFile = new GlazedFile(
									GlazedFile.LOCAL_FILESYSTEM);
						} else {
							tempFile = new GlazedFile(parent);
						}
						tempFile.setParent();
						currentDirectoryContent.add(tempFile);
					} catch (URISyntaxException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}

			for (DtoFolder fol : folder.getChildrenFolders()) {
				currentDirectoryContent.add(new GlazedFile(fol));
			}

			for (DtoFile file : folder.getChildrenFiles()) {
				currentDirectoryContent.add(new GlazedFile(file));
			}

		} finally {
			currentDirectoryContent.getReadWriteLock().writeLock().unlock();
		}

	}

	private void setCurrentDirToGridRoot() {

		currentDirectory = new GlazedFile();
		currentFolder = null;

		currentDirectoryContent.getReadWriteLock().writeLock().lock();
		try {
			currentDirectoryContent.clear();

			// include local filesystems
			if (includeLocal) {
				currentDirectoryContent.add(new GlazedFile(
						GlazedFile.LOCAL_FILESYSTEM));
			}

			for (String siteName : em.getAllAvailableSites()) {
				currentDirectoryContent.add(new GlazedFile(siteName));
			}
		} finally {
			currentDirectoryContent.getReadWriteLock().writeLock().unlock();
		}
	}

	private void setCurrentDirToSite(String sitename) {

		currentDirectory = new GlazedFile(sitename);
		currentFolder = null;

		if (GlazedFile.LOCAL_FILESYSTEM.equals(sitename)) {

			currentDirectoryContent.getReadWriteLock().writeLock().lock();
			try {
				currentDirectoryContent.clear();

				if (!currentUrlIsStartUrl()) {
					GlazedFile temp = new GlazedFile();
					temp.setParent();
					currentDirectoryContent.add(temp);
				}

				for (File root : File.listRoots()) {
					currentDirectoryContent.add(new GlazedFile(root));
				}

			} finally {
				currentDirectoryContent.getReadWriteLock().writeLock().unlock();
			}

		} else {

			currentDirectoryContent.getReadWriteLock().writeLock().lock();
			try {
				currentDirectoryContent.clear();

				if (!currentUrlIsStartUrl()) {
					GlazedFile temp = new GlazedFile();
					temp.setParent();
					currentDirectoryContent.add(temp);
				}

				for (MountPoint mp : em.getMountPointsForSite(sitename)) {
					// if ("true".equalsIgnoreCase(mp
					// .getProperty(MountPoint.HIDDEN_KEY))) {
					// continue;
					// }
					currentDirectoryContent.add(new GlazedFile(mp));
				}
			} finally {
				currentDirectoryContent.getReadWriteLock().writeLock().unlock();
			}
		}
	}

	public void setCurrentUrl(String url) {

		setCurrent(url, null);

	}

	private void setEmptyList() {

		// currentDirectory = null;
		// currentFolder = null;

		try {
			SwingUtilities.invokeAndWait(new Thread() {
				// SwingUtilities.invokeLater(new Thread() {

				@Override
				public void run() {
					currentDirectoryContent.getReadWriteLock().writeLock()
							.lock();
					currentDirectoryContent.clear();
					currentDirectoryContent.getReadWriteLock().writeLock()
							.unlock();
					getTable().revalidate();
				}

			});
		} catch (Exception e) {
			myLogger.warn(e);
		}

	}

	private void setLoading(final boolean loading) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {

				if (!loading) {
					fireIsLoading(false);
				}

				getTable().setEnabled(!loading);
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

	public void setRootAndCurrentUrl(GlazedFile startFile) {

		setRootAndCurrentUrl(startFile.getUrl(), startFile);
	}

	public void setRootAndCurrentUrl(String rootUrl, GlazedFile startFile) {

		if (rootUrl == null) {
			this.rootUrl = GlazedFile.ROOT;
		} else {
			this.rootUrl = rootUrl;
		}

		if (startFile == null) {
			setCurrentUrl(GlazedFile.ROOT);
		} else {
			setCurrent(startFile);
		}

	}

	public void setRootAndCurrentUrl(String rootUrl, String startUrl) {
		if (rootUrl == null) {
			this.rootUrl = GlazedFile.ROOT;
		} else {
			this.rootUrl = rootUrl;
		}

		setCurrentUrl(startUrl);
	}

	public void setRootUrl(String url) {
		if (url == null) {
			this.rootUrl = GlazedFile.ROOT;
		} else {
			this.rootUrl = url;
		}
	}

	private void setUrl(String url) throws RemoteFileSystemException {

		setEmptyList();
		DtoFolder folder = fm.ls(url);

		setCurrentDirToFolder(folder);

	}

	public void displayHiddenFiles(boolean display) {
		textMatcherEditor.displayHiddenFiles(display);

	}

	public void setExtensionsToDisplay(String[] extensions) {
		textMatcherEditor.setExtensionsToDisplay(extensions);
	}
}
