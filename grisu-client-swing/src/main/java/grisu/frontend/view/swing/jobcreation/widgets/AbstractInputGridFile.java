package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.X;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.view.swing.files.virtual.GridFileTreeDialog;
import grisu.model.dto.GridFile;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

abstract public class AbstractInputGridFile extends AbstractWidget {

	private GridFileTreeDialog fileDialog = null;

	// public final String selString = "Please select a file";
	private boolean displayHiddenFiles = false;
	private String[] extensions = null;

	private List<GridFile> roots = null;

	private String currentUrl = null;

	private boolean lockCombo = false;

	private JComboBox comboBox;
	protected final DefaultComboBoxModel fileModel = new DefaultComboBoxModel();

	public AbstractInputGridFile() {
		super();
	}

	public void askForFile() {
		GridFile f = popupFileDialogAndAskForFile();

		setInputFile(f);
	}

	protected GridFileTreeDialog getFileDialog() {

		if (fileDialog == null) {

			fileDialog = createGridFileDialog(getServiceInterface(), roots,
					getHistoryKey() + "_last_dir", extensions,
					displayHiddenFiles, SwingUtilities.getWindowAncestor(this));
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
						X.p("Selection: " + sel.toString());
						return;
					}

					if (((GridFile) fileModel.getSelectedItem()) != null) {

						setInputFile((GridFile) fileModel.getSelectedItem());
						getPropertyChangeSupport().firePropertyChange(
								"inputFileUrl", currentUrl, getValue());
					}
					currentUrl = getValue();
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

	public void setDisplayHiddenFiles(boolean display) {
		this.displayHiddenFiles = display;
		//		if (fileDialog != null) {
		//			fileDialog.displayHiddenFiles(display);
		//		}
	}

	public void setExtensionsToDisplay(String[] extensions) {
		this.extensions = extensions;
		// if (fileDialog != null) {
		// fileDialog.setExtensionsToDisplay(extensions);
		// }
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

	@Override
	protected boolean setLastValue() {
		return false;
	}

	public void setRoots(List<GridFile> roots) {
		this.roots = roots;
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
