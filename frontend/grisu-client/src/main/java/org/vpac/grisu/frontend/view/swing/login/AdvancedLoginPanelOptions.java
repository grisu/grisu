package org.vpac.grisu.frontend.view.swing.login;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class AdvancedLoginPanelOptions extends JPanel {
	private JCheckBox chckbxAdvancedConnectionSettings;
	private ServiceInterfaceUrlPanel serviceInterfaceUrlPanel;

	/**
	 * Create the panel.
	 */
	public AdvancedLoginPanelOptions() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("33px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("125px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("22px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getChckbxAdvancedConnectionSettings(), "2, 2, 3, 1, left, center");
		add(getServiceInterfaceUrlPanel(), "4, 4, fill, fill");
	}

	private JCheckBox getChckbxAdvancedConnectionSettings() {
		if (chckbxAdvancedConnectionSettings == null) {
			chckbxAdvancedConnectionSettings = new JCheckBox("Advanced connection settings");
			chckbxAdvancedConnectionSettings.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {

					showAdvancedOptions(chckbxAdvancedConnectionSettings.isSelected());

				}
			});
		}
		return chckbxAdvancedConnectionSettings;
	}

	public String getServiceInterfaceUrl() {
		return getServiceInterfaceUrlPanel().getServiceInterfaceUrl();
	}

	private ServiceInterfaceUrlPanel getServiceInterfaceUrlPanel() {
		if (serviceInterfaceUrlPanel == null) {
			serviceInterfaceUrlPanel = new ServiceInterfaceUrlPanel();
			serviceInterfaceUrlPanel.setVisible(false);
		}
		return serviceInterfaceUrlPanel;
	}

	public void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getChckbxAdvancedConnectionSettings().setEnabled(!lock);
				getServiceInterfaceUrlPanel().lockUI(lock);
			}
		});

	}

	private void showAdvancedOptions(boolean show) {

		getServiceInterfaceUrlPanel().setVisible(show);

	}
}
