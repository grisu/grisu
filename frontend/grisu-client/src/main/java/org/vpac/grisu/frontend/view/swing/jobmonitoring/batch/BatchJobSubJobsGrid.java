package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.SimpleSingleJobsGrid;

public class BatchJobSubJobsGrid extends SimpleSingleJobsGrid {

	private static final long serialVersionUID = -1811967498034047862L;

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

	private final BatchJobObject bj;

	private JPopupMenu popupMenu;
	private JMenuItem mntmRestartSelectedJobs;


	public BatchJobSubJobsGrid(ServiceInterface si, BatchJobObject bj) {
		super(si, bj.getJobs());
		this.bj = bj;
		addPopup(getTable(), getPopupMenu());
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
