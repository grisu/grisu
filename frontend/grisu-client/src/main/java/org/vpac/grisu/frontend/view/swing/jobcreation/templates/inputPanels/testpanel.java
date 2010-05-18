package org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels;

import javax.swing.JButton;
import javax.swing.JPanel;

public class testpanel extends JPanel {
	private JButton button;

	/**
	 * Create the panel.
	 */
	public testpanel() {
		add(getButton());

	}

	private JButton getButton() {
		if (button == null) {
			button = new JButton("New button");
			button.setBorder(null);
		}
		return button;
	}
}
