package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

public abstract class AbstractInputPanel extends JPanel implements PropertyChangeListener {

	protected final JobSubmissionObjectImpl jobObject;

	public AbstractInputPanel(JobSubmissionObjectImpl jobObject) {
		this.jobObject = jobObject;
		this.jobObject.addPropertyChangeListener(this);
	}



}
