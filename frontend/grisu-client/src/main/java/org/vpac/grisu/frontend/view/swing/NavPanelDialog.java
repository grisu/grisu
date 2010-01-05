package org.vpac.grisu.frontend.view.swing;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.control.ServiceInterface;

public class NavPanelDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			NavPanelDialog dialog = new NavPanelDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final JPanel contentPanel = new JPanel();
	private GrisuNavigationPanel grisuNavigationPanel;

	/**
	 * Create the dialog.
	 */
	public NavPanelDialog() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(getGrisuNavigationPanel(), BorderLayout.CENTER);

		getGrisuNavigationPanel().addTaskPane("Test1", null);
		getGrisuNavigationPanel().addTaskPaneItem("Test1", "testitem1", "itemdesc1", null);
		getGrisuNavigationPanel().addTaskPaneItem("Test1", "testitem2", "itemdesc2", null);
		getGrisuNavigationPanel().addTaskPaneItem("Test1", "testitem3", "itemdesc3", null);

		getGrisuNavigationPanel().addTaskPane("Test2", null);
		getGrisuNavigationPanel().addTaskPaneItem("Test2", "testitem1", "itemdesc1", null);
		getGrisuNavigationPanel().addTaskPaneItem("Test2", "testitem2", "itemdesc2", null);
		getGrisuNavigationPanel().addTaskPaneItem("Test2", "testitem3", "itemdesc3", null);
	}

	private GrisuNavigationPanel getGrisuNavigationPanel() {
		if (grisuNavigationPanel == null) {
			grisuNavigationPanel = new GrisuNavigationPanel((ServiceInterface) null, null);
		}
		return grisuNavigationPanel;
	}
}
