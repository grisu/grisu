package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class TestPanel extends JPanel {
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private JPopupMenu popupMenu;
	private JMenuItem mntmRestartSelectedJobs;

	/**
	 * Create the panel.
	 */
	public TestPanel() {
		addPopup(this, getPopupMenu());

	}
	private JMenuItem getMntmRestartSelectedJobs() {
		if (mntmRestartSelectedJobs == null) {
			mntmRestartSelectedJobs = new JMenuItem("Restart selected job(s)");
			mntmRestartSelectedJobs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
		}
		return mntmRestartSelectedJobs;
	}
	private JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getMntmRestartSelectedJobs());
		}
		return popupMenu;
	}
}
