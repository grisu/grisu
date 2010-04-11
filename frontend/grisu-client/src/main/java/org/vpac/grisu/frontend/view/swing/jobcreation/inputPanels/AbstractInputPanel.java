package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

public abstract class AbstractInputPanel extends JPanel implements PropertyChangeListener {

	protected final JobSubmissionObjectImpl jobObject;

	public AbstractInputPanel(JobSubmissionObjectImpl jobObject) {
		this.jobObject = jobObject;
		//$hide>>$
		//		this.jobObject.addPropertyChangeListener(this);
		//$hide<<$
	}

	/**
	 * Must be implemented if a change in a job property would possibly change the
	 * value of one of the job properties this panel is responsible for.
	 * 
	 * @param e the property change event
	 */
	abstract void jobPropertyChanged(PropertyChangeEvent e);

	public void propertyChange(PropertyChangeEvent arg0) {
		jobPropertyChanged(arg0);
	}


}
