package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.vpac.grisu.frontend.view.swing.files.GrisuFileDialog;
import org.vpac.grisu.model.files.GlazedFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SingleInputFile extends AbstractInputPanel {
	private JComboBox comboBox;
	private JButton button;
	private GrisuFileDialog dialog;

	private String selectedFile = null;

	public SingleInputFile(Map<String, String> panelProperties) {
		super(panelProperties);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getComboBox(), "2, 2, fill, default");
		add(getButton(), "4, 2");
	}

	private JButton getButton() {
		if (button == null) {
			button = new JButton("Browse");
			button.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					if ( si == null ) {
						myLogger.error("ServiceInterface not set yet.");
						return;
					}
					getFileDialog().setVisible(true);

					GlazedFile file = getFileDialog().getSelectedFile();
					getFileDialog().clearSelection();
					if ( file == null ) {
						return;
					}

					if ( selectedFile != null ) {
						jobObject.removeInputFileUrl(selectedFile);
					}
					selectedFile = file.getUrl();
					jobObject.addInputFileUrl(selectedFile);

					getComboBox().setSelectedItem(selectedFile);
				}
			});
		}
		return button;
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.setEditable(true);
		}
		return comboBox;
	}

	@Override
	protected Map<String, String> getDefaultPanelProperties() {

		Map<String, String> defaultProperties = new HashMap<String, String>();
		defaultProperties.put(TITLE, "Input file");

		return defaultProperties;
	}

	//	private GrisuFileDialog getFileDialog() {
	//
	//		if ( si == null ) {
	//			return null;
	//		}
	//
	//		if ( dialog == null ) {
	//			dialog = new GrisuFileDialog(si);
	//			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	//
	//		}
	//		return dialog;
	//	}

	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	protected void preparePanel(Map<String, String> panelProperties) {
		// TODO Auto-generated method stub

	}
}
