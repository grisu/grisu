package org.vpac.grisu.frontend.view.swing.jobmonitoring.single.appSpecific;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.JobDetailPanel;

import au.org.arcs.jcommons.constants.Constants;

public abstract class AppSpecificViewerPanel extends JPanel implements
		JobDetailPanel, PropertyChangeListener {

	public static AppSpecificViewerPanel create(JobObject job) {

		try {
			String appName = job.getJobProperty(Constants.APPLICATIONNAME_KEY);

			String className = "org.vpac.grisu.frontend.view.swing.jobmonitoring.single.appSpecific."
					+ appName;
			Class classO = Class.forName(className);

			AppSpecificViewerPanel asvp = (AppSpecificViewerPanel) classO
					.newInstance();

			return asvp;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private JobObject job = null;

	public AppSpecificViewerPanel() {
		super();
	}

	abstract public void initialize();

	abstract public void jobUpdated(PropertyChangeEvent evt);

	public void setJob(JobObject job) {
		if (this.job != null) {
			this.job.removePropertyChangeListener(this);
		}
		this.job = job;
		this.job.addPropertyChangeListener(this);

		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JPanel getPanel() {
		return this;
	}

	public JobObject getJob() {
		return this.job;
	}

	public void propertyChange(PropertyChangeEvent evt) {

		System.out.println("Job property change.");
		System.out.print(evt.getPropertyName() + ": ");
		System.out.println(evt.getNewValue());

		jobUpdated(evt);

	}

}
