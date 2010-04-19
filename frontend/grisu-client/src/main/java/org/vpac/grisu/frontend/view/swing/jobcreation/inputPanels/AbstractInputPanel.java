package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.GrisuFileDialog;
import org.vpac.grisu.frontend.view.swing.jobcreation.filters.Filter;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

public abstract class AbstractInputPanel extends JPanel implements PropertyChangeListener {

	static final Logger myLogger = Logger
	.getLogger(AbstractInputPanel.class.getName());

	protected final String DEFAULT_VALUE = "defaultValue";
	protected final String NAME = "name";
	protected final String TITLE = "title";
	protected final String PREFILLS = "prefills";
	protected final String USE_LAST_VALUE = "useLastValue";
	protected final String DEPENDENCY = "dependency";
	
	protected final LinkedList<Filter> filters;

	private JobSubmissionObjectImpl jobObject;

	protected final Map<String, String> panelProperties;
	protected ServiceInterface si;

	protected static ServiceInterface singletonServiceinterface;
	private static GrisuFileDialog dialog;

	public static GrisuFileDialog getFileDialog() {

		if ( singletonServiceinterface == null ) {
			return null;
		}

		if ( dialog == null ) {
			dialog = new GrisuFileDialog(singletonServiceinterface);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		}
		return dialog;
	}

	public AbstractInputPanel(Map<String, String> panelProperties, LinkedList<Filter> filters) {
		
		this.filters = filters;

		if ( panelProperties == null ) {
			this.panelProperties = getDefaultPanelProperties();
		} else {
			this.panelProperties = panelProperties;
		}

		if ( StringUtils.isBlank(this.panelProperties.get(NAME)) ) {
			this.panelProperties.put(NAME, UUID.randomUUID().toString());
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
	abstract protected Map<String, String> getDefaultPanelProperties();

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
	
	protected void setValue(String bean, Object value) {
		try {
			Method method = jobObject.getClass().getMethod("set"+StringUtils.capitalize(bean), value.getClass());
			method.invoke(jobObject, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void addValue(String bean, Object value) {
		try {
			Method method = jobObject.getClass().getMethod("add"+StringUtils.capitalize(bean), value.getClass());
			method.invoke(jobObject, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void removeValue(String bean, Object value) {
		try {
			Method method = jobObject.getClass().getMethod("remove"+StringUtils.capitalize(bean), value.getClass());
			method.invoke(jobObject, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void applyFilters() {
		
		String string = get
		for ( Filter filter : filters ) {
			
		}
		
	}

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

	public void setServiceInterface(ServiceInterface si) {
		if ( singletonServiceinterface == null ) {
			singletonServiceinterface = si;
		}
		this.si = si;
	}



}
