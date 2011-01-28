package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.GridFile;
import grisu.model.files.FileSystemItem;
import grisu.model.files.GlazedFile;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileListPanelPlusOld extends JPanel implements FileListPanel,
		FileListListener {

	private JComboBox comboBox;
	private FileListPanelSimple fileListPanel;

	private final ServiceInterface serviceInterface;
	private final String rootUrl;
	private final String startUrl;

	private final UserEnvironmentManager em;
	private final FileSystemsManager fsm;

	private FileSystemItem lastFileSystem = null;

	private final EventList<FileSystemItem> allFileSystems;
	private final SortedList<FileSystemItem> sortedFileSystemsList;
	private final EventComboBoxModel<FileSystemItem> filesystemModel;

	private boolean displaySize = true;
	private boolean displayTimeStamp = false;

	private boolean fireEvent = true;
	private JLabel label;

	/**
	 * Create the panel.
	 */
	public FileListPanelPlusOld(ServiceInterface si, String startUrl,
			boolean displaySize, boolean displayTimestamp) {
		this.displaySize = displaySize;
		this.displayTimeStamp = displayTimestamp;
		this.serviceInterface = si;
		this.em = GrisuRegistryManager.getDefault(serviceInterface)
				.getUserEnvironmentManager();
		this.fsm = FileSystemsManager.getDefault(si);

		allFileSystems = this.fsm.getAllFileSystems();

		sortedFileSystemsList = new SortedList<FileSystemItem>(allFileSystems,
				new FileSystemItemComparator());

		filesystemModel = new EventComboBoxModel<FileSystemItem>(
				sortedFileSystemsList);

		// this.rootUrl = rootUrl;
		this.startUrl = startUrl;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(41dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(54dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLabel(), "2, 2, right, default");
		add(getComboBox(), "4, 2, fill, default");

		final FileSystemItem item = em.getFileSystemForUrl(startUrl);
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
		add(getFileListPanel(), "2, 4, 3, 1, fill, fill");
	}

	public void addFileListListener(FileListListener l) {
		getFileListPanel().addFileListListener(l);
	}

	public void directoryChanged(GlazedFile newDirectory) {

		// TODO

	}

	public void displayHiddenFiles(boolean display) {
		fileListPanel.displayHiddenFiles(display);
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

					if (fireEvent) {
						if (ItemEvent.SELECTED == arg0.getStateChange()) {
							try {
								final FileSystemItem fsi = (FileSystemItem) (filesystemModel
										.getSelectedItem());
								if (fsi.isDummy() && (lastFileSystem != null)) {
									comboBox.setSelectedItem(lastFileSystem);
									return;
								}
								final GlazedFile sel = fsi.getRootFile();
								if (sel.equals(lastFileSystem)) {
									return;
								}
								getFileListPanel().setRootAndCurrentUrl(sel);
								lastFileSystem = fsi;
							} catch (final NullPointerException e) {
								// that's ok.
							}

						}
					}
				}
			});
			if ((startUrl == null) || GridFile.ROOT.equals(startUrl)) {
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

	public JPanel getPanel() {
		return this;
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

	public void setExtensionsToDisplay(String[] extensions) {
		fileListPanel.setExtensionsToDisplay(extensions);
	}

	public void setRootUrl(String url) {

		getFileListPanel().setRootUrl(url);
	}
}
