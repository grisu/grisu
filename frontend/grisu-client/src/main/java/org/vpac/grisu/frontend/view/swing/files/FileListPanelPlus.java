package org.vpac.grisu.frontend.view.swing.files;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.model.files.GlazedFile;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileListPanelPlus extends JPanel implements FileListPanel,
		FileListListener {

	private JComboBox comboBox;
	private FileListPanelSimple fileListPanel;

	private final ServiceInterface serviceInterface;
	private final String rootUrl;
	private final String startUrl;

	private final UserEnvironmentManager em;
	private final FileSystemsManager fsm;
	private final FileManager fm;

	private FileSystemItem lastFileSystem = null;

	private final EventList<FileSystemItem> allFileSystems;
	private final SortedList<FileSystemItem> sortedFileSystemsList;
	private final EventComboBoxModel<FileSystemItem> filesystemModel;

	private boolean displaySize = true;
	private boolean displayTimeStamp = false;

	private boolean fireEvent = true;
	private JLabel label;
	private JLabel lblPath;
	private JTextField pathTextField;

	private boolean comboLocked = false;

	/**
	 * Create the panel.
	 */
	public FileListPanelPlus(ServiceInterface si, String startUrl,
			boolean displaySize, boolean displayTimestamp) {
		this.displaySize = displaySize;
		this.displayTimeStamp = displayTimestamp;
		this.serviceInterface = si;
		this.em = GrisuRegistryManager.getDefault(serviceInterface)
				.getUserEnvironmentManager();
		this.fsm = FileSystemsManager.getDefault(si);
		this.fm = GrisuRegistryManager.getDefault(serviceInterface)
				.getFileManager();

		allFileSystems = this.fsm.getAllFileSystems();

		sortedFileSystemsList = new SortedList<FileSystemItem>(allFileSystems,
				new FileSystemItemComparator());

		filesystemModel = new EventComboBoxModel<FileSystemItem>(
				sortedFileSystemsList);

		// this.rootUrl = rootUrl;
		this.startUrl = startUrl;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(17dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(12dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(54dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(16dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLabel(), "2, 2, 3, 1, left, default");
		add(getComboBox(), "6, 2, fill, default");

		FileSystemItem item = em.getFileSystemForUrl(startUrl);
		if (item != null) {
			rootUrl = item.getRootFile().getUrl();
			fireEvent = false;
			getComboBox().setSelectedItem(item);
			lastFileSystem = item;
			fireEvent = true;
		} else {
			startUrl = null;
			rootUrl = null;
		}
		add(getLblPath(), "2, 4, left, default");
		add(getPathTextField(), "4, 4, 3, 1, fill, default");
		add(getFileListPanel(), "2, 6, 5, 1, fill, fill");

		directoryChanged(getCurrentDirectory());
	}

	public void addFileListListener(FileListListener l) {
		getFileListPanel().addFileListListener(l);
	}

	public void directoryChanged(GlazedFile newDirectory) {

		if (!GlazedFile.Type.FILETYPE_FOLDER.equals(newDirectory.getType())) {
			return;
		}

		if (FileManager.isLocal(newDirectory.getUrl())) {

			File curDir = FileManager.getFileFromUriOrPath(newDirectory
					.getUrl());

			getPathTextField().setText(curDir.toString());

		} else {

			MountPoint mp = this.em.getMountPointForUrl(newDirectory.getUrl());
			if (mp == null) {
				getPathTextField().setText("");
				return;
			} else {
				String path = newDirectory.getUrl().substring(
						mp.getRootUrl().length());
				if (path.startsWith("/")) {
					path = path.substring(1);
				}
				getPathTextField().setText(path);
			}
		}

	}

	public void fileDoubleClicked(GlazedFile file) {
	}

	public void filesSelected(Set<GlazedFile> files) {
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(filesystemModel);
			comboBox.setRenderer(new FileSystemItemRenderer());
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {

					if (comboLocked) {
						return;
					}
					if (fireEvent) {
						if (ItemEvent.SELECTED == arg0.getStateChange()) {
							try {
								FileSystemItem fsi = (FileSystemItem) (filesystemModel
										.getSelectedItem());
								if (fsi.isDummy() && (lastFileSystem != null)) {
									comboBox.setSelectedItem(lastFileSystem);
									return;
								}

								GlazedFile sel = fsi.getRootFile();

								if (FileSystemItem.Type.BOOKMARK.equals(fsi
										.getType())) {
									FileSystemItem fsi2 = fsm
											.getFileSystemForUrl(fsi
													.getRootFile().getUrl());
									comboLocked = true;
									comboBox.setSelectedItem(fsi2);
									comboLocked = false;
									lastFileSystem = fsi2;
								} else {

									if (sel.equals(lastFileSystem)) {
										return;
									}
									lastFileSystem = fsi;
								}
								getFileListPanel().setRootAndCurrentUrl(sel);
							} catch (NullPointerException e) {
								// that's ok.
							}

						}
					}
				}
			});
			if ((startUrl == null) || GlazedFile.ROOT.equals(startUrl)) {
				getComboBox().setSelectedIndex(0);
			}
		}
		return comboBox;
	}

	public GlazedFile getCurrentDirectory() {
		return getFileListPanel().getCurrentDirectory();
	}

	private FileListPanelSimple getFileListPanel() {
		if (fileListPanel == null) {
			fileListPanel = new FileListPanelSimple(serviceInterface, rootUrl,
					startUrl, displaySize, displayTimeStamp);
			fileListPanel.addFileListListener(this);
		}
		return fileListPanel;
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("Filesystem:");
		}
		return label;
	}

	private JLabel getLblPath() {
		if (lblPath == null) {
			lblPath = new JLabel("Path:");
		}
		return lblPath;
	}

	public JPanel getPanel() {
		return this;
	}

	private JTextField getPathTextField() {
		if (pathTextField == null) {
			pathTextField = new JTextField();
			pathTextField.setEditable(false);
			pathTextField.setColumns(10);
		}
		return pathTextField;
	}

	public Set<GlazedFile> getSelectedFiles() {
		return getFileListPanel().getSelectedFiles();
	}

	public void isLoading(boolean loading) {

		getComboBox().setEnabled(!loading);

	}

	public void refresh() {

		getFileListPanel().refresh();

	}

	public void removeFileListListener(FileListListener l) {
		getFileListPanel().removeFileListListener(l);
	}

	public void setContextMenu(FileListPanelContextMenu menu) {

		getFileListPanel().setContextMenu(menu);
	}

	public void setCurrentUrl(String url) {

		getFileListPanel().setCurrentUrl(url);
	}

	public void setRootUrl(String url) {

		getFileListPanel().setRootUrl(url);
	}
}
