package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.view.swing.files.virtual.GridFileTreeDialog;
import grisu.model.dto.GridFile;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

abstract public class AbstractInputGridFile extends AbstractWidget {

	public static final String FOLDER_SELECTABLE = "folder_selectable";
	public static final String EXTENSIONS_TO_DISPLAY = "extensions_to_display";
	public static final String DISPLAY_HIDDEN_FILES = "display_hidden_files";

	private GridFileTreeDialog fileDialog = null;

	// public final String selString = "Please select a file";
	private boolean displayHiddenFiles = false;
	private String[] extensions = null;

	private List<GridFile> roots = null;

	private String currentUrl = null;

	private boolean lockCombo = false;

	private JComboBox comboBox;
	protected final DefaultComboBoxModel fileModel = new DefaultComboBoxModel();
	private int selectionMode = ListSelectionModel.SINGLE_SELECTION;
	private boolean foldersSelectable = true;
	private final boolean displayLocalFilesystems = true;

	public AbstractInputGridFile() {
		super();
	}

	public void askForFile() {
		GridFile f = popupFileDialogAndAskForFile();

		if (f != null) {
			setInputFile(f);
		}
	}

	protected GridFileTreeDialog getFileDialog() {

		if (fileDialog == null) {

			fileDialog = createGridFileDialog(getServiceInterface(), roots,
					getHistoryKey() + "_last_dir", extensions,
					displayHiddenFiles, foldersSelectable,
					displayLocalFilesystems,
					SwingUtilities.getWindowAncestor(this));
		}

		return fileDialog;

	}

	public GridFile getInputFile() {
		final GridFile temp = (GridFile) getInputFileComboBox()
		.getSelectedItem();
		return temp;
	}

	protected JComboBox getInputFileComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(fileModel);
			comboBox.setEditable(false);
			comboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

			comboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					if (e.getStateChange() == ItemEvent.DESELECTED) {
						return;
					}

					Object sel = fileModel.getSelectedItem();
					if (!(sel instanceof GridFile)) {
						return;
					}

					GridFile f = (GridFile) fileModel.getSelectedItem();
					if ((f != null) && !f.getUrl().equals(currentUrl)) {

						setInputFile((GridFile) fileModel.getSelectedItem());
						// getPropertyChangeSupport().firePropertyChange(
						// "inputFileUrl", currentUrl, getValue());
						currentUrl = getValue();

					}


				}

			});
		}

		return comboBox;
	}

	public String getInputFileUrl() {
		final GridFile temp = (GridFile) getInputFileComboBox()
		.getSelectedItem();

		return temp.getUrl();
	}

	@Override
	public String getValue() {
		return getInputFileUrl();
	}

	@Override
	public void historyKeySet() {
		getHistoryManager().setMaxNumberOfEntries(getHistoryKey(), 8);
		for (final String entry : getHistoryManager().getEntries(
				getHistoryKey())) {
			if (fileModel.getIndexOf(entry) < 0) {
				fileModel.addElement(entry);
			}
		}
	}

	protected GridFile popupFileDialogAndAskForFile() {

		if (getServiceInterface() == null) {
			getMylogger().error(
			"ServiceInterface not set. Can't open dialog...");
			return null;
		}


		getFileDialog().setVisible(true);

		final GridFile file = getFileDialog().getSelectedFile();

		return file;
	}

	public void setConfiguration(Map<String, String> config) {

		if (config == null) {
			return;
		}

		for ( String key : config.keySet() ) {

			if ( FOLDER_SELECTABLE.equals(key) ) {
				System.out.println("TODO");
			} else if ( EXTENSIONS_TO_DISPLAY.equals(key) ) {
				setExtensionsToDisplay(config.get(key).split(","));
			} else if ( DISPLAY_HIDDEN_FILES.equals(key) ) {
				try {
					boolean display = Boolean.parseBoolean(config.get(key));
					setDisplayHiddenFiles(display);
				} catch (Exception e) {
					myLogger.warn("Can't parse value of " + key + ": "
							+ config.get(key));
					continue;
				}
			}

		}
	}

	public void setDisplayHiddenFiles(boolean display) {
		this.displayHiddenFiles = display;
		if (fileDialog != null) {
			fileDialog.displayHiddenFiles(display);
		}
	}

	public void setExtensionsToDisplay(String[] extensions) {
		this.extensions = extensions;
		if (fileDialog != null) {
			fileDialog.setExtensionsToDisplay(extensions);
		}
	}

	public void setFoldersSelectable(boolean foldersSelectable) {
		this.foldersSelectable = foldersSelectable;
		if (fileDialog != null) {
			fileDialog.setFoldersSelectable(foldersSelectable);
		}
	}

	public void setInputFile(GridFile file) {

		lockCombo = true;
		if (file == null) {
			// fileModel.setSelectedItem(selString);
			fileModel.setSelectedItem(null);
			return;
		}

		final int index = fileModel.getIndexOf(file);
		if (index < 0) {
			fileModel.addElement(file);
		}
		fileModel.setSelectedItem(file);
		getInputFileComboBox().setToolTipText(file.getUrl());
		lockCombo = false;

		getPropertyChangeSupport().firePropertyChange("inputFile", null, file);

	}


	public void setInputFileUrl(String fileUrl)
	throws RemoteFileSystemException {

		setInputFile(getFileManager().createGridFile(fileUrl));

	}

	@Override
	protected boolean setLastValue() {
		return false;
	}

	public void setRoots(List<GridFile> roots) {
		this.roots = roots;
	}

	public void setSelectionMode(int selectionMode) {
		this.selectionMode  = selectionMode;
		if (fileDialog != null) {
			fileDialog.setSelectionMode(selectionMode);
		}
	}

	@Override
	public void setValue(String value) {

		GridFile f;
		try {
			f = getFileManager().createGridFile(value);
		} catch (RemoteFileSystemException e) {
			throw new RuntimeException(e);
		}
		setInputFile(f);

	}

}
