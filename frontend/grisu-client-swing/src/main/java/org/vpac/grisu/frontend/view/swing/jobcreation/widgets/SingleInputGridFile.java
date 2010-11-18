package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.frontend.view.swing.files.virtual.GridFileTreeDialog;
import org.vpac.grisu.frontend.view.swing.utils.FirstItemPromptItemRenderer;
import org.vpac.grisu.model.dto.GridFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SingleInputGridFile extends AbstractWidget {

	private JComboBox comboBox;
	private JButton btnBrowse;

	protected final DefaultComboBoxModel fileModel = new DefaultComboBoxModel();

	private GridFileTreeDialog fileDialog = null;

	public final String selString = "Please select a file";
	private boolean displayHiddenFiles = false;
	private String[] extensions = null;

	private String currentUrl = null;

	/**
	 * Create the panel.
	 */
	public SingleInputGridFile() {
		super();
		setBorder(new TitledBorder(null, "Input file", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getComboBox(), "2, 2, fill, default");
		add(getBtnBrowse(), "4, 2");

	}

	public void displayHiddenFiles(boolean display) {
		this.displayHiddenFiles = display;
		if (fileDialog != null) {
			fileDialog.displayHiddenFiles(display);
		}
	}

	protected JButton getBtnBrowse() {
		if (btnBrowse == null) {
			btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					final String oldValue = getValue();

					final GridFile file = popupFileDialogAndAskForFile();

					if (file == null) {
						return;
					}

					setInputFile(file.getUrl());
					getPropertyChangeSupport().firePropertyChange(
							"inputFileUrl", oldValue, getValue());
				}
			});
		}
		return btnBrowse;
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(fileModel);
			comboBox.setEditable(false);
			comboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			comboBox.addItem(selString);
			comboBox.setRenderer(new FirstItemPromptItemRenderer(selString));
			comboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					if (ItemEvent.SELECTED == e.getStateChange()) {

						if (StringUtils.isNotBlank((String) fileModel
								.getSelectedItem())) {

							setInputFile((String) fileModel.getSelectedItem());
							getPropertyChangeSupport().firePropertyChange(
									"inputFileUrl", currentUrl, getValue());
						}
						currentUrl = getValue();
					}
				}

			});
		}

		return comboBox;
	}

	protected GridFileTreeDialog getFileDialog() {

		if (fileDialog == null) {

			fileDialog = createGridFileDialog(getServiceInterface(),
					getHistoryKey() + "_last_dir", extensions,
					displayHiddenFiles, SwingUtilities.getWindowAncestor(this));
			// String startUrl = getHistoryManager().getLastEntry(
			// getHistoryKey() + "_last_dir");
			//
			// if (StringUtils.isBlank(startUrl)) {
			// startUrl = new File(System.getProperty("user.home")).toURI()
			// .toString();
			// } else if (!FileManager.isLocal(startUrl)) {
			// try {
			// if (!getServiceInterface().isFolder(startUrl)) {
			// startUrl = new File(System.getProperty("user.home"))
			// .toURI().toString();
			// }
			// } catch (RemoteFileSystemException e) {
			// myLogger.debug(e);
			// startUrl = new File(System.getProperty("user.home"))
			// .toURI().toString();
			// }
			// }
			// fileDialog = new GrisuFileDialog(getServiceInterface(),
			// startUrl);
			// fileDialog
			// .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			//
			// fileDialog.setExtensionsToDisplay(extensions);
			// fileDialog.displayHiddenFiles(displayHiddenFiles);
		}

		return fileDialog;

	}

	public String getInputFileUrl() {
		final String temp = (String) getComboBox().getSelectedItem();

		if (selString.equals(temp)) {
			return null;
		} else {
			return temp;
		}
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

	@Override
	public void lockIUI(final boolean lock) {
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getComboBox().setEnabled(!lock);
				getBtnBrowse().setEnabled(!lock);
			}
		});

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

	public void setExtensionsToDisplay(String[] extensions) {
		this.extensions = extensions;
		if (fileDialog != null) {
			fileDialog.setExtensionsToDisplay(extensions);
		}
	}

	public void setFileDialog(GridFileTreeDialog d) {
		this.fileDialog = d;
	}

	protected void setInputFile(String url) {

		if (StringUtils.isBlank(url)) {
			fileModel.setSelectedItem(selString);
			return;
		}

		final int index = fileModel.getIndexOf(url);
		if (index < 0) {
			fileModel.addElement(url);
		}
		fileModel.setSelectedItem(url);
	}

	@Override
	protected boolean setLastValue() {
		return false;
	}

	@Override
	public void setValue(String value) {
		setInputFile(value);
	}
}
