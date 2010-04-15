package org.vpac.grisu.frontend.view.swing.files;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistryManager;
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

	private FileSystemItem lastFileSystem = null;

	private final EventList<FileSystemItem> allFileSystems;
	private final SortedList<FileSystemItem> sortedFileSystemsList;
	private final EventComboBoxModel<FileSystemItem> filesystemModel;

	private boolean displaySize = true;
	private boolean displayTimeStamp = false;

	private boolean fireEvent = true;

	/**
	 * Create the panel.
	 */
	public FileListPanelPlus(ServiceInterface si,  String startUrl, boolean displaySize, boolean displayTimestamp) {
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

		//		this.rootUrl = rootUrl;
		this.startUrl = startUrl;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(177dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getComboBox(), "2, 2, fill, default");

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
		add(getFileListPanel(), "2, 4, fill, fill");
	}

	public void addFileListListener(FileListListener l) {
		getFileListPanel().addFileListListener(l);
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
								FileSystemItem fsi = (FileSystemItem) (filesystemModel
										.getSelectedItem());
								if (fsi.isDummy() && (lastFileSystem != null)) {
									comboBox.setSelectedItem(lastFileSystem);
									return;
								}
								GlazedFile sel = fsi.getRootFile();
								if (sel.equals(lastFileSystem)) {
									return;
								}
								getFileListPanel().setRootAndCurrentUrl(sel);
								lastFileSystem = fsi;
							} catch (NullPointerException e) {
								// that's ok.
							}

						}
					}
				}
			});
			if ( (startUrl == null) || GlazedFile.ROOT.equals(startUrl) ) {
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

	public void setCurrentUrl(String url) {

		getFileListPanel().setCurrentUrl(url);
	}

	public void setRootUrl(String url) {

		getFileListPanel().setRootUrl(url);
	}
}
