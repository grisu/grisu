package org.vpac.grisu.frontend.view.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXBusyLabel;

public class LoadingPanel extends JPanel {
	private JXBusyLabel busyLabel;

	/**
	 * Create the panel.
	 */
	public LoadingPanel() {
		setLayout(new BorderLayout(0, 0));
		add(getBusyLabel(), BorderLayout.CENTER);

	}
	private JXBusyLabel getBusyLabel() {
		if (busyLabel == null) {
			busyLabel = new JXBusyLabel();
			busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return busyLabel;
	}

	public void setLoading(boolean loading) {

		busyLabel.setBusy(loading);

	}
}
