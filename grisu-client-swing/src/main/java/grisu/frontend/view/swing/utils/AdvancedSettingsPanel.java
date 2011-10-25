package grisu.frontend.view.swing.utils;

import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.ServiceInterfacePanel;
import grisu.jcommons.constants.Constants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class AdvancedSettingsPanel extends JPanel implements
		ServiceInterfacePanel {

	private JLabel lblClearFilesystemCache;
	private JButton btnClear;

	private ServiceInterface si = null;
	private JLabel lblUseoldSitebased;

	/**
	 * Create the panel.
	 */
	public AdvancedSettingsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));
		add(getLblClearFilesystemCache(), "2, 2");
		add(getBtnClear(), "4, 2");
		add(getLblUseoldSitebased(), "2, 4");
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

	private JLabel getLblUseoldSitebased() {
		if (lblUseoldSitebased == null) {
			lblUseoldSitebased = new JLabel(
					"Use (old) site-based file management panel");
		}
		return lblUseoldSitebased;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getPanelTitle() {
		return "Advanced settings";
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
