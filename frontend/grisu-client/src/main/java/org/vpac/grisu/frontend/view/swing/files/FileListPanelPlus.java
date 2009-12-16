package org.vpac.grisu.frontend.view.swing.files;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.model.files.GlazedFile;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileListPanelPlus extends JPanel {
	private JComboBox comboBox;
	private FileListPanel fileListPanel;

	private final ServiceInterface serviceInterface;
	private String rootUrl;
	private String startUrl;

	private final UserEnvironmentManager em;

	private FileSystemItem lastFileSystem = null;

	private EventList<FileSystemItem> allFileSystems;
	private SortedList<FileSystemItem> sortedFileSystemsList;
	private EventComboBoxModel<FileSystemItem> filesystemModel;

	private boolean fireEvent = true;

	/**
	 * Create the panel.
	 */
	public FileListPanelPlus(ServiceInterface si, String rootUrl,
			String startUrl) {
		this.serviceInterface = si;
		this.em = GrisuRegistryManager.getDefault(serviceInterface)
				.getUserEnvironmentManager();

		allFileSystems = GlazedLists.eventList(em.getFileSystems());

		// Add seperators
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.LOCAL,
				" -- Local -- "));
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.BOOKMARK,
				" -- Bookmarks -- "));
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.REMOTE,
				" -- Grid -- "));

		sortedFileSystemsList = new SortedList<FileSystemItem>(allFileSystems,
				new FileSystemItemComparator());

		filesystemModel = new EventComboBoxModel<FileSystemItem>(
				sortedFileSystemsList);

		this.rootUrl = rootUrl;
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
		add(getFileListPanel(), "2, 4, fill, fill");

		FileSystemItem item = em.getFileSystemForUrl(startUrl);
		if (item != null) {
			fireEvent = false;
			getComboBox().setSelectedItem(item);
			lastFileSystem = item;
			fireEvent = true;
		}
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
								if (fsi.isDummy() && lastFileSystem != null) {
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
		}
		return comboBox;
	}

	private FileListPanel getFileListPanel() {
		if (fileListPanel == null) {
			fileListPanel = new FileListPanel(serviceInterface, rootUrl,
					startUrl);
		}
		return fileListPanel;
	}
}
