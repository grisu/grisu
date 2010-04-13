package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractInputPanel extends JPanel implements PropertyChangeListener {

	static final Logger myLogger = Logger
	.getLogger(AbstractInputPanel.class.getName());

	protected final String DEFAULT_VALUE = "defaultValue";
	//	protected final String NAME = "name";
	protected final String TITLE = "title";
	protected final String PREFILLS = "prefills";
	protected final String USE_LAST_VALUE = "useLastValue";

	protected JobSubmissionObjectImpl jobObject;

	protected final Map<String, String> panelProperties;

	public AbstractInputPanel(Map<String, String> panelProperties) {

		if ( panelProperties == null ) {
			this.panelProperties = getDefaultPanelProperties();
		} else {
			this.panelProperties = panelProperties;
		}

		try {
			String title = this.panelProperties.get(TITLE);
			setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns a set of default values if no configuration is specified in the template.
	 * 
	 * Good to have as a reference which values are available for this panel.
	 * 
	 * @return the properties
	 */
	abstract protected ImmutableMap<String, String> getDefaultPanelProperties();

	/**
	 * Must be implemented if a change in a job property would possibly change the
	 * value of one of the job properties this panel is responsible for.
	 * 
	 * @param e the property change event
	 */
	abstract protected void jobPropertyChanged(PropertyChangeEvent e);

	/**
	 * Implement this if the panel needs to be prepared with values from the template.
	 * 
	 * @param panelProperties the properties for the initial state of the panel
	 */
	abstract protected void preparePanel(Map<String, String> panelProperties);

	public void propertyChange(PropertyChangeEvent arg0) {
		jobPropertyChanged(arg0);
	}

	public void setJobObject(JobSubmissionObjectImpl jobObject) {

		if ( this.jobObject != null ) {
			this.jobObject.removePropertyChangeListener(this);
		}

		this.jobObject = jobObject;
		this.jobObject.addPropertyChangeListener(this);

		preparePanel(panelProperties);

	}



}
