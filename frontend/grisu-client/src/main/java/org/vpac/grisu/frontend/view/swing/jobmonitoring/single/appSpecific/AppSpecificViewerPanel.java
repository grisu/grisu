package org.vpac.grisu.frontend.view.swing.jobmonitoring.single.appSpecific;

import it.infn.cnaf.forge.glueschema.spec.v12.r2.JobStatusEnum;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.JobDetailPanel;

import au.org.arcs.jcommons.constants.Constants;

public abstract class AppSpecificViewerPanel extends JPanel implements JobDetailPanel, PropertyChangeListener {

	public static AppSpecificViewerPanel create(JobObject job) {
		
		String appName = job.getJobProperty(Constants.APPLICATIONNAME_KEY);
		
		String className = "org.vpac.grisu.frontend.view.swing.jobmonitoring.single.appSpecific."+appName;
		try {
			Class classO = Class.forName(className);

			AppSpecificViewerPanel asvp = (AppSpecificViewerPanel)classO.newInstance();
			
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
	
	public void setJob(JobObject job) {
		if ( this.job != null ) {
			this.job.removePropertyChangeListener(this);
		}
		this.job = job;
		this.job.addPropertyChangeListener(this);
		initialize();
	}

	public JPanel getPanel() {
		return this;
	}
	
	public JobObject getJob() {
		return this.job;
	}

	public String getTitle() {
		if ( this.job != null ) {
			return this.job.getApplication()+ " details";
		} else {
			return "n/a";
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		
		System.out.println("Job property change.");
		
	}
	
}
