package org.vpac.grisu.frontend.view.swing.jobcreation.templates;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

public class TemplateObject {

	static final Logger myLogger = Logger.getLogger(TemplateObject.class
			.getName());

	public static Map<String, String> parseCommandlineTemplate(String template)
			throws TemplateException {

		Map<String, String> map = new HashMap<String, String>();

		String[] parts = template.split("\\$");

		for (String part : parts) {

			if (!part.startsWith("{")) {
				continue;
				// throw new
				// TemplateException("Template format wrong: $ is not followed by {");
			}
			if (!part.contains("}")) {
				throw new TemplateException(
						"Template format wrong: opening { does not have a closing }");
			}

			String variableName = part.substring(1, part.indexOf("}"));
			map.put(variableName, "");

		}

		return map;

	}

	private JobSubmissionObjectImpl jobObject;
	private final String commandlineTemplate;
	private final ServiceInterface si;

	private JPanel templatePanel;
	// private final Map<String, AbstractInputPanel> panels = new
	// HashMap<String, AbstractInputPanel>();
	private final Map<String, String> changedValues;

	public TemplateObject(ServiceInterface si, String commandlineTemplate)
			throws TemplateException {
		this.si = si;
		this.commandlineTemplate = commandlineTemplate;
		changedValues = parseCommandlineTemplate(commandlineTemplate);
	}

	public JobSubmissionObjectImpl getJobSubmissionObject() {
		return this.jobObject;
	}

	// public void registerInputPanel(AbstractInputPanel panel) {
	// panels.put(panel.getName(), panel);
	// }

	public ServiceInterface getServiceInterface() {
		return this.si;
	}

	public JPanel getTemplatePanel() {
		return this.templatePanel;
	}

	public void setJobObject(JobSubmissionObjectImpl job) {
		this.jobObject = job;
	}

	public void setTemplatePanel(JPanel templatePanel) {
		this.templatePanel = templatePanel;
	}

	public void submitJob() throws JobPropertiesException,
			JobSubmissionException, InterruptedException {

		JobObject job = null;
		job = new JobObject(si, jobObject.getJobDescriptionDocument());

		job.createJob("/ACC");

		job.submitJob();

	}

	public void userInput(String panelName, String newValue) {

		if (newValue == null) {
			newValue = "";
		}

		if ((panelName != null) && (changedValues.get(panelName) == null)) {
			myLogger.debug("Commandline doesn't require value from panel "
					+ panelName);
			return;
		}

		if (panelName != null) {
			changedValues.put(panelName, newValue);
		}
		String newCommandline = commandlineTemplate;
		for (String key : changedValues.keySet()) {
			newCommandline = newCommandline.replace("${" + key + "}",
					changedValues.get(key));
		}

		jobObject.setCommandline(newCommandline);
	}

}
