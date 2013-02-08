package grisu.frontend.view.swing.jobmonitoring.single;

import grisu.frontend.model.job.GrisuJob;

import javax.swing.JPanel;

public interface JobDetailPanel {

	public JPanel getPanel();

	public void setJob(GrisuJob job);

}
