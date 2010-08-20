package org.vpac.grisu.frontend.view.swing.jobmonitoring.single;

import javax.swing.JPanel;

import org.vpac.grisu.frontend.model.job.JobObject;

public interface JobDetailPanel {

	public JPanel getPanel();

	public void setJob(JobObject job);

}
