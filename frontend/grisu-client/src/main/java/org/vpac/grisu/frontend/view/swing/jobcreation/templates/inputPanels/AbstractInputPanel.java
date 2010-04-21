package org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels;

import java.awt.Dimension;
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
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.PanelConfig;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateException;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateObject;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.filters.Filter;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

public abstract class AbstractInputPanel extends JPanel implements PropertyChangeListener {

	static final Logger myLogger = Logger
	.getLogger(AbstractInputPanel.class.getName());

	public static final String DEFAULT_VALUE = "defaultValue";
	public static final String NAME = "name";
	public static final String TITLE = "title";
	public static final String PREFILLS = "prefills";
	public static final String USE_LAST_VALUE = "useLastValue";
	public static final String DEPENDENCY = "dependency";
	public static final String SIZE = "size";
	public static final String IS_VISIBLE = "isVisible";
	public static final String BEAN = "property";

	private TemplateObject template;
	private final LinkedList<Filter> filters;

	private boolean isVisible = true;
	protected final String bean;

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


	public AbstractInputPanel(PanelConfig config) throws TemplateException {

		if ( (config == null) || (config.getFilters() == null) ) {
			this.filters = new LinkedList<Filter>();
		} else {
			this.filters = config.getFilters();
		}

		if ( (config == null) || (config.getConfig() == null) || (config.getConfig().size() == 0) ) {
			this.panelProperties = getDefaultPanelProperties();
		} else {
			this.panelProperties = getDefaultPanelProperties();
			this.panelProperties.putAll(config.getConfig());
		}

		if ( StringUtils.isBlank(this.panelProperties.get(NAME)) ) {
			this.panelProperties.put(NAME, UUID.randomUUID().toString());
		}


		if ( ! StringUtils.isBlank(this.panelProperties.get(BEAN)) ) {
			bean = this.panelProperties.get(BEAN);
		} else {
			bean = null;
		}
		if ( ! StringUtils.isBlank(this.panelProperties.get(IS_VISIBLE)) ) {
			try {
				isVisible = Boolean.parseBoolean(this.panelProperties.get(IS_VISIBLE));
			} catch (Exception e) {
				throw new TemplateException("Can't parse isVisible property: "+e.getLocalizedMessage(), e);
			}
		}
		try {
			String title = this.panelProperties.get(TITLE);
			setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String size = this.panelProperties.get(SIZE);
		if ( StringUtils.isNotBlank(size) ) {
			try {
				int width = Integer.parseInt(size.substring(0, size.indexOf("x")).trim());
				int height = Integer.parseInt(size.substring(size.indexOf("x")+1).trim());
				setPreferredSize(new Dimension(width, height));
				setMaximumSize(new Dimension(width, height));
			} catch (Exception e) {
				throw new TemplateException("Can't parse size property for panel "+this.panelProperties.get(NAME)+": "+size);
			}
		}
	}

	protected void addValue(String bean, Object value) {
		try {
			Method method = jobObject.getClass().getMethod("add"+StringUtils.capitalize(bean), value.getClass());
			method.invoke(jobObject, value);
			applyFilters();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void applyFilters() {

		String string = getValueAsString();
		for ( Filter filter : filters ) {
			string = filter.filter(string);
		}

		template.userInput(getPanelName(), string);
	}

	/**
	 * Returns a set of default values if no configuration is specified in the template.
	 * 
	 * Good to have as a reference which values are available for this panel.
	 * 
	 * @return the properties
	 */
	abstract protected Map<String, String> getDefaultPanelProperties();

	public String getPanelName() {
		return this.panelProperties.get(NAME);
	}

	abstract protected String getValueAsString();

	//	protected Object getValue(String bean) {
	//		try {
	//			Method method = jobObject.getClass().getMethod("get"+StringUtils.capitalize(bean));
	//			return method.invoke(jobObject);
	//		} catch (Exception e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//			return null;
	//		}
	//	}

	public void initPanel(TemplateObject template, ServiceInterface si, JobSubmissionObjectImpl jobObject) throws TemplateException {

		this.template = template;

		// needed for example for the file dialog
		if ( singletonServiceinterface == null ) {
			singletonServiceinterface = si;
		}
		this.si = si;

		if ( this.jobObject != null ) {
			this.jobObject.removePropertyChangeListener(this);
		}

		this.jobObject = jobObject;
		this.jobObject.addPropertyChangeListener(this);

		preparePanel(panelProperties);
	}

	public boolean isDisplayed() {
		return isVisible;
	}

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
	 * @throws TemplateException
	 */
	abstract protected void preparePanel(Map<String, String> panelProperties) throws TemplateException;

	public void propertyChange(PropertyChangeEvent arg0) {
		jobPropertyChanged(arg0);
	}

	protected void removeValue(String bean, Object value) {
		try {
			Method method = jobObject.getClass().getMethod("remove"+StringUtils.capitalize(bean), value.getClass());
			method.invoke(jobObject, value);
			applyFilters();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setValue(String bean, Object value) throws TemplateException {
		try {
			if ( bean != null ) {
				Method method = jobObject.getClass().getMethod("set"+StringUtils.capitalize(bean), value.getClass());
				method.invoke(jobObject, value);
			}
			applyFilters();
		} catch (Exception e) {
			throw new TemplateException("Can't set value for property "+bean+": "+e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String toString() {

		StringBuffer temp = new StringBuffer();

		temp.append("Name: "+getName()+"\n");
		temp.append("Class: "+this.getClass().toString()+"\n");
		temp.append("Properties: \n");
		for ( String key : panelProperties.keySet() ) {
			temp.append("\t"+key+": "+panelProperties.get(key)+"\n");
		}
		temp.append("Filters:\n");
		for ( Filter filter : filters ) {
			temp.append("\tClass: "+filter.getClass().toString()+"\n");
		}

		return temp.toString();

	}



}
