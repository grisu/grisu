package org.vpac.grisu.frontend.view.swing;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.vpac.grisu.frontend.view.swing.jobcreation.JobCreationPanel;
import org.vpac.grisu.frontend.view.swing.jobcreation.TemplateJobCreationPanel;

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

		//		return new JobCreationPanel[]{new DummyJobCreationPanel()};
		List<String> lines = null;;
		try {
			lines = FileUtils.readLines(new File("/home/markus/Desktop/test.template"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JobCreationPanel[]{new TemplateJobCreationPanel(lines)};
	}

	@Override
	public String getName() {
		return "Default Grisu client";
	}

}
