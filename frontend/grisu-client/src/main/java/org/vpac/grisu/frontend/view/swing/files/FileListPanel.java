package org.vpac.grisu.frontend.view.swing.files;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.model.dto.DtoFolder;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventTableModel;

public class FileListPanel extends JPanel {

	private final ServiceInterface si;
	private final FileManager fm;
	private final UserEnvironmentManager em;

	private EventTableModel<GlazedFile> fileModel;

	private EventList<GlazedFile> currentDirectoryContent = new BasicEventList<GlazedFile>();

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
	public FileListPanel(ServiceInterface si, String startUrl) {

		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		setLayout(new BorderLayout(0, 0));

		fileModel = new EventTableModel<GlazedFile>(currentDirectoryContent,
				new GlazedFileTableFormat());
		add(getScrollPane(), BorderLayout.CENTER);

		if (startUrl == null) {
			rootUrl = GlazedFile.ROOT;
			setCurrent(GlazedFile.ROOT);
		} else {
			rootUrl = startUrl;
			setCurrent(startUrl);
		}
	}

	private synchronized void setCurrent(final String url, final GlazedFile file) {

		if (updateThread == null || !updateThread.isAlive()) {

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
							}

						});

						setLoading(false);
					}
				}
			};

			updateThread.start();
		}

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
					currentDirectoryContent.add(new GlazedFile());
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
					currentDirectoryContent.add(new GlazedFile());
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
						currentDirectoryContent.add(new GlazedFile(em
								.getMountPointForUrl(folder.getRootUrl())
								.getSite()));
					} else {
						currentDirectoryContent.add(new GlazedFile(fm
								.calculateParentUrl(folder.getRootUrl()), si));
					}
				} else {
					try {
						File parent = new File(new URI(folder.getRootUrl()))
								.getParentFile();
						if (parent == null) {
							currentDirectoryContent.add(new GlazedFile(
									GlazedFile.LOCAL_FILESYSTEM));
						} else {
							currentDirectoryContent.add(new GlazedFile(parent));
						}
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

	private void fileSelectionOccured() {

		int selRow = table.getSelectedRow();
		if (selRow >= 0) {

			GlazedFile sel = (GlazedFile) fileModel.getValueAt(selRow, 0);
			setCurrent(sel);

		}

	}

	private JTable getTable() {
		if (table == null) {
			table = new JTable(fileModel);
			table.setDefaultRenderer(GlazedFile.class, new GlazedFileRenderer(
					this));
			table.setDefaultRenderer(Long.class, new FileSizeRenderer());
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {

					if (arg0.getClickCount() == 2) {
						fileSelectionOccured();
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
}
