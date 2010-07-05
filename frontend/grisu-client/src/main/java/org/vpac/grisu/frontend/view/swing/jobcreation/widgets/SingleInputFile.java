package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.border.TitledBorder;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.WindowConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.view.swing.files.GrisuFileDialog;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.AbstractInputPanel;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.files.GlazedFile;
import org.vpac.historyRepeater.HistoryManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class SingleInputFile extends JPanel {

	static final Logger myLogger = Logger.getLogger(SingleInputFile.class
			.getName());

	private JComboBox comboBox;
	private JButton btnBrowse;

	private DefaultComboBoxModel fileModel = new DefaultComboBoxModel();

	private ServiceInterface si;
	private String historyKey;
	private HistoryManager hm;

	private GrisuFileDialog fileDialog = null;

	/**
	 * Create the panel.
	 */
	public SingleInputFile() {


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
	
	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		this.hm = GrisuRegistryManager.getDefault(si).getHistoryManager();
	}
	
	public void setHistoryKey(String key) {
		this.historyKey = key;
	}

	protected GlazedFile popupFileDialogAndAskForFile() {
		
		if ( si == null ) {
			return null;
		}

		getFileDialog().setVisible(true);

		GlazedFile file = getFileDialog().getSelectedFile();
		getFileDialog().clearSelection();

		GlazedFile currentDir = getFileDialog().getCurrentDirectory();

		if (StringUtils.isNotBlank(historyKey)) {
			hm.addHistoryEntry(historyKey, currentDir.getUrl());
		}

		return file;
	}

	protected GrisuFileDialog getFileDialog() {

		if (fileDialog == null) {
			String startUrl = GrisuRegistryManager.getDefault(si)
					.getHistoryManager().getLastEntry(historyKey);

			if (StringUtils.isBlank(startUrl)) {
				startUrl = new File(System.getProperty("user.home")).toURI()
						.toString();
			} else if (!FileManager.isLocal(startUrl)) {
				try {
					if (!si.isFolder(startUrl)) {
						startUrl = new File(System.getProperty("user.home"))
								.toURI().toString();
					}
				} catch (RemoteFileSystemException e) {
					myLogger.debug(e);
					startUrl = new File(System.getProperty("user.home"))
							.toURI().toString();
				}
			}
			fileDialog = new GrisuFileDialog(si, startUrl);
			fileDialog
					.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		}
		return fileDialog;

	}

	public void setTitle(String title) {
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			
		}
		return comboBox;
	}
	
	private void setInputFile(String url) {
		getComboBox().setSelectedItem(url);
	}
	
	public String getInputFileUrl() {
		return (String)getComboBox().getSelectedItem();
	}
	
	private JButton getBtnBrowse() {
		if (btnBrowse == null) {
			btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GlazedFile file = popupFileDialogAndAskForFile();

					if (file == null) {
						return;
					}

					setInputFile(file.getUrl());
				}
			});
		}
		return btnBrowse;
	}
}
