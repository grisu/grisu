package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractInputPanel extends JPanel implements PropertyChangeListener {


	protected final String DEFAULT_VALUE = "defaultValue";
	protected final String NAME = "name";
	protected final String TITLE = "title";
	protected final String PREFILLS = "prefills";
	protected final String USE_LAST_VALUE = "useLastValue";

	protected final JobSubmissionObjectImpl jobObject;

	public AbstractInputPanel(JobSubmissionObjectImpl jobObject, Map<String, String> panelProperties) {

		this.jobObject = jobObject;

		//$hide>>$
		//		this.jobObject.addPropertyChangeListener(this);
		//$hide<<$

		if ( panelProperties == null ) {
			panelProperties = getDefaultPanelProperties();
		}

		try {
			String title = panelProperties.get(TITLE);
			setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		preparePanel(panelProperties);
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



}
