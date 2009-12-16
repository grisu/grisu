package org.vpac.grisu.frontend.view.swing.login;

import java.awt.CardLayout;

import javax.swing.JPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LoginPanel extends JPanel {
	private JPanel loginPanel;

	/**
	 * Create the panel.
	 */
	public LoginPanel() {
		setLayout(new CardLayout(0, 0));
		add(getLoginPanel(), "7ab6fef4-cd00-4382-8dcc-d8bc03103ff8");

	}

	private JPanel getLoginPanel() {
		if (loginPanel == null) {
			loginPanel = new JPanel();
			loginPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"), }, new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("default:grow"), }));
		}
		return loginPanel;
	}
}
