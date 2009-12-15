package org.vpac.grisu.frontend.view.swing.files;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.BatchJobSelectionListener;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.model.dto.DtoFolder;
import org.vpac.grisu.model.files.GlazedFile;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;

public class FileListPanel extends JPanel {

	private final ServiceInterface si;
	private final FileManager fm;
	private final UserEnvironmentManager em;

	private EventTableModel<GlazedFile> fileModel;

	private EventList<GlazedFile> currentDirectoryContent = new BasicEventList<GlazedFile>();
	private SortedList<GlazedFile> sortedList = new SortedList<GlazedFile>(
			currentDirectoryContent, new GlazedFileComparator());

	private GlazedFile currentDirectory = null;
	private DtoFolder currentFolder = null;
	private JTable table;

	private Thread updateThread;
	private JScrollPane scrollPane;

	private boolean includeLocal = true;

	private String rootUrl = null;

	/**
	 * Create the panel.
	 */
	public FileListPanel(ServiceInterface si, String rootUrl, String startUrl) {

		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		setLayout(new BorderLayout(0, 0));

		fileModel = new EventTableModel<GlazedFile>(sortedList,
				new GlazedFileTableFormat());
		add(getScrollPane(), BorderLayout.CENTER);

		setRootAndCurrentUrl(rootUrl, startUrl);

	}
	
	public void setRootAndCurrentUrl(GlazedFile startFile) {
		
		setRootAndCurrentUrl(startFile.getUrl(), startFile);
	}
	
	public void setRootAndCurrentUrl(String rootUrl, GlazedFile startFile) {
		
		if ( rootUrl == null ) {
			this.rootUrl = GlazedFile.ROOT;
		} else {
			this.rootUrl = rootUrl;
		}
		
		if ( startFile == null ) {
			setCurrent(GlazedFile.ROOT);
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

		if (startUrl == null) {
			setCurrent(GlazedFile.ROOT);
		} else {
			setCurrent(startUrl);
		}
	}

	private synchronized void setCurrent(final String url, final GlazedFile file) {

		if (updateThread != null && updateThread.isAlive()) {
			updateThread.interrupt();
			try {
				updateThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 

		updateThread = new Thread("Populating fileList") {

				public void run() {

					setLoading(true);

					try {

						if (file != null) {

							if (file == null
									|| GlazedFile.Type.FILETYPE_ROOT
											.equals(file.getType())) {
								setCurrentDirToGridRoot();
							} else if (GlazedFile.Type.FILETYPE_SITE
									.equals(file.getType())) {
								setCurrentDirToSite(file.getName());
							} else if (GlazedFile.Type.FILETYPE_MOUNTPOINT
									.equals(file.getType())) {
								setUrl(file.getUrl());
							} else if (GlazedFile.Type.FILETYPE_FOLDER
									.equals(file.getType())) {
								setUrl(file.getUrl());
							} else {
								// do nothing
							}

						} else {

							if (url == null || GlazedFile.ROOT.equals(url)) {
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

					} catch (Exception e) {

					} finally {

						SwingUtilities.invokeLater(new Thread() {
							public void run() {
								table.repaint();
								table.clearSelection();
							}

						});

						setLoading(false);
					}
				}
			};

			updateThread.start();

	}

	public void setCurrent(String url) {

		setCurrent(url, null);

	}

	public boolean currentUrlIsStartUrl() {

		if (rootUrl.equals(getCurrentDirectory().getUrl())) {
			return true;
		} else {
			return false;
		}
	}

	public GlazedFile getCurrentDirectory() {
		return currentDirectory;
	}
	
	public String getCurrentDirectoryUrl() {
		if ( currentDirectory != null ) {
			return currentDirectory.getUrl();
		} else {
			return null;
		}
	}

	public void setCurrent(GlazedFile file) {

		setCurrent(null, file);

	}

	private void setUrl(String url) throws RemoteFileSystemException {

		DtoFolder folder = fm.ls(url);

		setCurrentDirToFolder(folder);

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
					currentDirectoryContent.add(new GlazedFile(mp));
				}
			} finally {
				currentDirectoryContent.getReadWriteLock().writeLock().unlock();
			}
		}
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
						GlazedFile temp = new GlazedFile(fm
								.calculateParentUrl(folder.getRootUrl()), si);
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

	private void fileDoubleClickOccured() {

		int selRow = table.getSelectedRow();
		if (selRow >= 0) {

			GlazedFile sel = (GlazedFile) fileModel.getValueAt(selRow, 0);
			
			if ( sel.isFolder() ) {
				setCurrent(sel);
			} else {
				fireFileSelected(sel);
			}

		}

	}

	private JTable getTable() {
		if (table == null) {
			table = new JTable(fileModel);
			table.setDefaultRenderer(GlazedFile.class, new GlazedFileRenderer(
					this));
			table.setDefaultRenderer(Long.class, new FileSizeRenderer());

			int vColIndex = 0;
			TableColumn col = table.getColumnModel().getColumn(vColIndex);
			int width = 120;
			col.setPreferredWidth(width);
			col.setMinWidth(80);
			vColIndex = 1;
			col = table.getColumnModel().getColumn(vColIndex);
			width = 60;
			col.setPreferredWidth(width);
			col.setMaxWidth(60);

			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {

					if (arg0.getClickCount() == 2) {
						fileDoubleClickOccured();
					}

				}
			});
		}
		return table;
	}

	private void setLoading(final boolean loading) {

		SwingUtilities.invokeLater(new Thread() {

			public void run() {

				getTable().setEnabled(!loading);

				if (loading) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				} else {
					setCursor(Cursor.getDefaultCursor());
				}

			}

		});

	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getTable());
		}
		return scrollPane;
	}
	
	
	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<FileSelectionListener> listeners;

	private void fireFileSelected(GlazedFile file) {
		// if we have no mountPointsListeners, do nothing...
		if (listeners != null && !listeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<FileSelectionListener> targets;
			synchronized (this) {
				targets = (Vector<FileSelectionListener>) listeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			for ( FileSelectionListener l : targets ) {
				try {
					l.fileSelected(file);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// register a listener
	synchronized public void addUserInputListener(FileSelectionListener l) {
		if (listeners == null)
			listeners = new Vector<FileSelectionListener>();
		listeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeUserInputListener(FileSelectionListener l) {
		if (listeners == null) {
			listeners = new Vector<FileSelectionListener>();
		}
		listeners.removeElement(l);
	}
}
