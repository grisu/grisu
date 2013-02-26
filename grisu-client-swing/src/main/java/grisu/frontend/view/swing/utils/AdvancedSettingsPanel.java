package grisu.frontend.view.swing.utils;

import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.ServiceInterfacePanel;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.GridEnvironment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.globus.myproxy.MyProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

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

	/**
	 * Create the panel.
	 */
	public AdvancedSettingsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(70dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		add(getLblNewLabel_1(), "2, 2, 5, 1");
		add(getChckbxAllowRemoteSupport(), "8, 2, center, default");
		add(getLblClearFilesystemCache(), "2, 4, 3, 1");
		add(getBtnClear(), "8, 4, fill, default");
		add(getLblNewLabel(), "2, 6, left, default");
		add(getTextField(), "4, 6, 3, 1, fill, default");
		add(getBtnApply(), "8, 6, fill, default");
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
}
