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

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class AdvancedSettingsPanel extends JPanel implements
ServiceInterfacePanel {

	private JLabel lblClearFilesystemCache;
	private JButton btnClear;

	private ServiceInterface si = null;
	private JLabel lblNewLabel;
	private JTextField textField;
	private JButton btnApply;

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
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));
		add(getLblClearFilesystemCache(), "2, 2, 3, 1");
		add(getBtnClear(), "8, 2, fill, default");
		add(getLblNewLabel(), "2, 4, left, default");
		add(getTextField(), "4, 4, 3, 1, fill, default");
		add(getBtnApply(), "8, 4, fill, default");
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

		this.si = si;
		if (si != null) {
			getBtnClear().setEnabled(true);
		} else {
			getBtnClear().setEnabled(false);
		}

	}
}
