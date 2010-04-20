package org.vpac.grisu.frontend.view.swing.jobcreation.templates;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

public class TemplateObject {

	static final Logger myLogger = Logger
	.getLogger(TemplateObject.class.getName());

	public static Map<String, String> parseCommandlineTemplate(String template) throws TemplateException {

		Map<String, String> map = new HashMap<String, String>();

		String[] parts = template.split("\\$");

		for ( String part : parts ) {

			if ( !part.startsWith("{") ) {
				continue;
				//				throw new TemplateException("Template format wrong: $ is not followed by {");
			}
			if ( !part.contains("}") ) {
				throw new TemplateException("Template format wrong: { does not have a }");
			}

			String variableName = part.substring(1, part.indexOf("}"));
			map.put(variableName, "");

		}

		return map;

	}

	private JobSubmissionObjectImpl jobObject;
	private final String commandlineTemplate;
	//	private final Map<String, AbstractInputPanel> panels = new HashMap<String, AbstractInputPanel>();
	private final Map<String, String> changedValues;

	public TemplateObject(String commandlineTemplate) throws TemplateException {
		this.commandlineTemplate= commandlineTemplate;
		changedValues = parseCommandlineTemplate(commandlineTemplate);
	}

	//	public void registerInputPanel(AbstractInputPanel panel) {
	//		panels.put(panel.getName(), panel);
	//	}

	public void setJobObject(JobSubmissionObjectImpl job) {
		this.jobObject = job;
	}

	public void userInput(String panelName, String newValue) {

		if ( newValue == null ) {
			newValue = "";
		}

		if ( changedValues.get(panelName) == null ) {
			myLogger.debug("Commandline doesn't require value from panel "+panelName);
			return;
		}

		changedValues.put(panelName, newValue);
		String newCommandline = commandlineTemplate;
		for ( String key : changedValues.keySet() ) {
			newCommandline = newCommandline.replace("${"+key+"}", changedValues.get(key));
		}

		jobObject.setCommandline(newCommandline);
	}


}
