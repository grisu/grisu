package grisu.frontend.view.swing.utils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.ServiceInterfacePanel;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.GridEnvironment;
import grisu.settings.ClientPropertiesManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;

public class AdvancedSettingsPanel extends JPanel implements
ServiceInterfacePanel {

	public static final Logger myLogger = LoggerFactory.getLogger(AdvancedSettingsPanel.class);

	private JLabel lblClearFilesystemCache;
	private JButton btnClear;

	private ServiceInterface si = null;
	private JLabel lblNewLabel;
	private JTextField textField;
	private JButton btnApply;
	private JCheckBox chckbxAllowRemoteSupport;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblZipResultFiles;
	private JCheckBox inputFileCheckBox;
	private JCheckBox outputFilesCheckBox;

	/**
	 * Create the panel.
	 */
	public AdvancedSettingsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("119px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("153px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("46px"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("25px"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("25px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
//		setLayout(new FormLayout(new ColumnSpec[] {
//				FormSpecs.RELATED_GAP_COLSPEC,
//				ColumnSpec.decode("default:grow"),
//				FormSpecs.RELATED_GAP_COLSPEC,
//				ColumnSpec.decode("max(70dlu;default)"),
//				FormSpecs.RELATED_GAP_COLSPEC,
//				ColumnSpec.decode("default:grow"),
//				FormSpecs.RELATED_GAP_COLSPEC,
//				FormSpecs.DEFAULT_COLSPEC,
//				FormSpecs.RELATED_GAP_COLSPEC,},
//			new RowSpec[] {
//				FormSpecs.RELATED_GAP_ROWSPEC,
//				FormSpecs.DEFAULT_ROWSPEC,
//				FormSpecs.RELATED_GAP_ROWSPEC,
//				FormSpecs.DEFAULT_ROWSPEC,
//				FormSpecs.RELATED_GAP_ROWSPEC,
//				FormSpecs.DEFAULT_ROWSPEC,}));
		add(getLblNewLabel_1(), "2, 2, 5, 1, left, center");
		add(getChckbxAllowRemoteSupport(), "8, 2, right, center");
		add(getLblClearFilesystemCache(), "2, 4, 5, 1, left, center");
		add(getBtnClear(), "8, 4, right, top");
		add(getLblNewLabel_2(), "2, 6, 5, 1");
		add(getInputFileCheckBox(), "8, 6, right, default");
		add(getLblZipResultFiles(), "2, 8, 5, 1");
		add(getOutputFilesCheckBox(), "8, 8, right, default");
		add(getLblNewLabel(), "2, 10, left, center");
		add(getTextField(), "4, 10, 3, 1, fill, center");
		add(getBtnApply(), "8, 10, right, top");
	}

	private JButton getBtnApply() {
		if (btnApply == null) {
			btnApply = new JButton("Apply");
			btnApply.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String myProxy = getTextField().getText();
					GridEnvironment.setDefaultMyProxyHost(myProxy);

				}
			});
		}
		return btnApply;
	}

	private JButton getBtnClear() {
		if (btnClear == null) {
			btnClear = new JButton("Clear");
			btnClear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					if (si == null) {
						myLogger.debug("No serviceinterface set, not clearing cache.");
						return;
					}

					si.setUserProperty(Constants.CLEAR_MOUNTPOINT_CACHE, null);

					JOptionPane
					.showMessageDialog(AdvancedSettingsPanel.this,
							"A restart is required. The next startup might take a bit longer than usual.");

				}
			});
			btnClear.setEnabled(false);
			btnClear.setToolTipText("Press this button if you think that you can't see all the filesytems you are supposed to see. Restart required.");
		}
		return btnClear;
	}

	private JLabel getLblClearFilesystemCache() {
		if (lblClearFilesystemCache == null) {
			lblClearFilesystemCache = new JLabel("Clear filesystem cache");

		}
		return lblClearFilesystemCache;
	}

	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("MyProxy host");
		}
		return lblNewLabel;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getPanelTitle() {
		return "Advanced settings";
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (StringUtils.equals(
							GridEnvironment.getDefaultMyProxyServer(),
							textField.getText())) {
						getBtnApply().setEnabled(false);
					} else {
						getBtnApply().setEnabled(true);
					}
				}
			});
			textField.setColumns(10);
			String currentMyProxy = GridEnvironment.getDefaultMyProxyServer();
			if (StringUtils.isNotBlank(currentMyProxy)) {
				textField.setText(currentMyProxy);
			}
		}
		return textField;
	}

	public void setServiceInterface(ServiceInterface si) {

		if (si != null) {
			getBtnClear().setEnabled(true);
			getChckbxAllowRemoteSupport().setEnabled(true);

			String enableRemoteAccess = si.getUserProperty(Constants.ALLOW_REMOTE_SUPPORT);

			if ( Boolean.TRUE.toString().equals(enableRemoteAccess) ) {
				getChckbxAllowRemoteSupport().setSelected(true);
			}

		} else {
			getBtnClear().setEnabled(false);
			getChckbxAllowRemoteSupport().setEnabled(false);
		}
		this.si = si;

	}
	private JCheckBox getChckbxAllowRemoteSupport() {
		if (chckbxAllowRemoteSupport == null) {
			chckbxAllowRemoteSupport = new JCheckBox("");
			chckbxAllowRemoteSupport.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {

					if (si == null) {
						myLogger.debug("No serviceinterface set, not enabling/disabling remote access.");
						return;
					}

					if (ItemEvent.SELECTED == e.getStateChange()) {
						si.setUserProperty(Constants.ALLOW_REMOTE_SUPPORT, Boolean.TRUE.toString());
					} else {
						si.setUserProperty(Constants.ALLOW_REMOTE_SUPPORT, Boolean.FALSE.toString());
					}

				}
			});
			chckbxAllowRemoteSupport.setEnabled(false);
			chckbxAllowRemoteSupport.setToolTipText("Allows support staff to impersonate you using your login session");
			chckbxAllowRemoteSupport.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		return chckbxAllowRemoteSupport;
	}
	private JLabel getLblNewLabel_1() {
		if (lblNewLabel_1 == null) {
			lblNewLabel_1 = new JLabel("Allow remote support");
		}
		return lblNewLabel_1;
	}
	private JLabel getLblNewLabel_2() {
		if (lblNewLabel_2 == null) {
			lblNewLabel_2 = new JLabel("Zip input file before uploading");
		}
		return lblNewLabel_2;
	}
	private JLabel getLblZipResultFiles() {
		if (lblZipResultFiles == null) {
			lblZipResultFiles = new JLabel("Zip result files when job is finished");
		}
		return lblZipResultFiles;
	}

	private JCheckBox getInputFileCheckBox() {
		if (inputFileCheckBox == null) {
			inputFileCheckBox = new JCheckBox("");
			boolean set = ClientPropertiesManager.isCompressInputFiles();
			inputFileCheckBox.setSelected(set);

			inputFileCheckBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if ( ItemEvent.SELECTED == e.getStateChange() ) {
						ClientPropertiesManager.setCompressInputFiles(true);
					} else {
						ClientPropertiesManager.setCompressInputFiles(false);
					}
				}
			});
		}
		return inputFileCheckBox;
	}


	private JCheckBox getOutputFilesCheckBox() {
		if (outputFilesCheckBox == null) {
			outputFilesCheckBox = new JCheckBox("");
			boolean set = ClientPropertiesManager.isCompressOutputFiles();
			outputFilesCheckBox.setSelected(set);

			outputFilesCheckBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if ( ItemEvent.SELECTED == e.getStateChange() ) {
						ClientPropertiesManager.setCompressOutputFiles(true);
					} else {
						ClientPropertiesManager.setCompressOutputFiles(false);
					}
				}
			});
		}
		return outputFilesCheckBox;
	}
}
