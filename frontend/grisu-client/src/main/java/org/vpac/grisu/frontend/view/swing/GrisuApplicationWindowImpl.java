package org.vpac.grisu.frontend.view.swing;

import java.awt.EventQueue;

import org.vpac.grisu.frontend.view.swing.jobcreation.DummyJobCreationPanel;
import org.vpac.grisu.frontend.view.swing.jobcreation.JobCreationPanel;

public class GrisuApplicationWindowImpl extends GrisuApplicationWindow {

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					GrisuApplicationWindow appWindow = new GrisuApplicationWindowImpl();
					appWindow.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public GrisuApplicationWindowImpl() {
		super();
	}

	@Override
	public JobCreationPanel[] getJobCreationPanels() {

		return new JobCreationPanel[]{new DummyJobCreationPanel()};
	}

	@Override
	public String getName() {
		return "Default Grisu client";
	}

}
