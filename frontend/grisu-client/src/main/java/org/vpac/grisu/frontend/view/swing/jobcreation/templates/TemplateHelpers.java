package org.vpac.grisu.frontend.view.swing.jobcreation.templates;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.filters.Filter;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.AbstractInputPanel;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

public class TemplateHelpers {

	static final Logger myLogger = Logger
	.getLogger(TemplateHelpers.class.getName());

	private static void addValueToConfig(PanelConfig config, String line) throws TemplateException {

		if ( config == null ) {
			throw new TemplateException("No config object when parsing line: "+line);
		}

		line = line.trim();

		int index = line.indexOf("=");
		if ( index < 0 ) {
			throw new TemplateException("Can't find = char in line: "+line);
		}

		String key = line.substring(0, index-1).trim();
		String value = line.substring(index+1).trim();

		if ( StringUtils.isBlank(key) ) {
			throw new TemplateException("Can't parse key for line: "+line);
		}

		if ( StringUtils.isBlank(value) ) {
			throw new TemplateException("Can't parse value for line: "+line);
		}

		if ( "type".equals(key) ) {
			config.setType(value);
		} if ( "filter".equals(key) ) {
			Filter filter = createFilter(value);
			config.addFilter(filter);
		} else {
			config.addConfig(key, value);
		}

	}

	public static Filter createFilter(String configString) throws TemplateException {


		try {

			String[] configParts = configString.split(":");

			Map<String, String> filterConfig = createFilterConfig(configString);

			Class filterClass = Class.forName("org.vpac.grisu.frontend.view.swing.jobcreation.templates.filters."+filterConfig.get("type"));
			filterConfig.remove("type");

			Filter filter = null;
			if ( filterConfig.size() == 0 ) {
				filter = (Filter)filterClass.newInstance();
			} else {
				Constructor<Filter> filterConstructor = filterClass.getConstructor(Map.class);
				filter = filterConstructor.newInstance(filterConfig);

			}

			return filter;

		} catch (Exception e) {
			throw new TemplateException("Can't create filter for config string: "+configString, e);
		}


	}

	private static Map<String, String> createFilterConfig(String configString) throws TemplateException {

		configString = configString.trim();

		Map<String, String> config = new HashMap<String, String>();

		int startIndex = configString.indexOf("[");
		if ( startIndex > 0 ) {
			// means configuration
			int endIndex = configString.indexOf("]");
			String[] initValues = configString.substring(startIndex+1, endIndex).split(":");
			for ( String value : initValues ) {
				value = value.trim();
				int index = value.indexOf("=");
				if ( index <= 0 ) {
					throw new TemplateException("Can't create filter config because. Unable to find = character in string "+value);
				}
				String key = value.substring(0, index).trim();
				String value2 = value.substring(index+1).trim();
				config.put(key, value2);
			}
		}

		String type = null;
		if ( startIndex == -1 ) {
			type = configString;
		} else {
			type = configString.substring(0, startIndex).trim();
		}
		config.put("type", type);

		return config;
	}

	public static AbstractInputPanel createInputPanel(PanelConfig config) throws TemplateException {

		if ( config == null ) {
			throw new TemplateException("No config object. Can't create panel.");
		}

		String type = config.getType();

		try {
			Class inputPanelClass = Class.forName("org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels."+type);
			Constructor<AbstractInputPanel> constructor = inputPanelClass.getConstructor(PanelConfig.class);

			AbstractInputPanel panel = constructor.newInstance(config);

			return panel;

		} catch (Exception e) {
			throw new TemplateException("Can't create input panel "+config.getConfig().get(AbstractInputPanel.NAME) + " of type " + config.getType(), e);
		}

	}

	public static JPanel createTab(LinkedList<JPanel> rows) {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		for ( JPanel row : rows ) {
			panel.add(row);
		}

		return panel;

	}

	public static String getCommandline(String line) throws TemplateException {

		String commandline = line;
		commandline = commandline.trim();

		if ( StringUtils.isBlank(commandline) ) {
			throw new TemplateException("First line of the config needs to be the specification of the commandline (for example \"commandline = echo hello world\"");
		}

		if ( ! commandline.startsWith("commandline") ) {
			throw new TemplateException("First line of the config needs to be the specification of the commandline (for example \"commandline = echo hello world\"");
		}

		int index = commandline.indexOf("=");
		if ( index <= 0 ) {
			throw new TemplateException("First line of the config needs to be the specification of the commandline (for example \"commandline = echo hello world\"");
		}

		commandline = commandline.substring(index+1).trim();
		if ( StringUtils.isBlank(commandline) ) {
			throw new TemplateException("First line of the config needs to be the specification of the commandline (for example \"commandline = echo hello world\"");
		}

		return commandline;

	}

	private static String getNewPageIndicator(String line) throws TemplateException {

		line = line.trim();

		if ( line.startsWith("=") ) {
			if ( ! line.endsWith("=") ) {
				throw new TemplateException("Line starts with = but doesn't end with another =: "+line);
			}

			line = line.replace("=", "");
			line = line.trim();
			return line;
		} else {
			return null;
		}
	}

	private static String getNewRowIndicator(String line) throws TemplateException {

		line = line.trim();

		if ( line.startsWith("-") ) {
			if ( ! line.endsWith("-") ) {
				throw new TemplateException("Line starts with - but doesn't end with another -: "+line);
			}

			line = line.replace("-", "");
			line = line.trim();
			return line;
		} else {
			return null;
		}

	}

	public static String getPanelName(String line) throws TemplateException {

		line = line.trim();

		int start = line.indexOf("[");
		if ( start < 0 ) {
			myLogger.debug("No panel name config.");
			return null;
		}

		if ( start != 0 ) {
			myLogger.debug("No panel name because [ is not the first character.");
			return null;
		}

		int end = line.indexOf("]");
		if ( end < 0 ) {
			throw new TemplateException("No closing ] bracket in line "+line);
		}

		String name = line.substring(start+1, end);

		return name;

	}

	public static void main(String[] args) throws IOException, TemplateException {

		List<String> lines = FileUtils.readLines(new File("/home/markus/Desktop/test.template"));

		//		LinkedHashMap<String, AbstractInputPanel> panels = parseConfig(lines);
		//
		//		for ( String panel : panels.keySet() ) {
		//			System.out.println("Panelname: "+panel);
		//			System.out.println(panels.get(panel).toString());
		//		}





	}


	public static TemplateObject parseAndCreateTemplatePanel(ServiceInterface si, List<String> linesOrig) throws TemplateException {


		String commandline = getCommandline(linesOrig.get(0));

		List<String> lines = new LinkedList(linesOrig);
		lines.remove(0);

		TemplateObject template = new TemplateObject(si, commandline);

		LinkedHashMap<String, PanelConfig> inputConfigs = parseConfig(lines);
		LinkedHashMap<String, AbstractInputPanel> inputPanels = new LinkedHashMap<String, AbstractInputPanel>();

		LinkedHashMap<String, LinkedList<JPanel>> tabs = new LinkedHashMap<String, LinkedList<JPanel>>();

		LinkedList<JPanel> currentTab = null;
		JPanel currentRow = null;

		for ( String line : lines ) {

			line = line.trim();
			if ( StringUtils.isBlank(line) ) {
				continue;
			}


			String lineType = getNewPageIndicator(line);

			if ( StringUtils.isNotBlank(lineType) ) {
				// means a new page
				currentRow = null;
				currentTab = new LinkedList<JPanel>();
				tabs.put(lineType, currentTab);
				continue;
			}

			lineType = getNewRowIndicator(line);

			if ( lineType != null ) {
				currentRow = new JPanel();
				currentRow.setAlignmentX(Component.LEFT_ALIGNMENT);
				currentRow.setAlignmentY(Component.TOP_ALIGNMENT);
				BoxLayout layout = new BoxLayout(currentRow, BoxLayout.X_AXIS);
				currentRow.setLayout(layout);
				if ( lineType.length() > 0 ) {
					currentRow.setBorder(new TitledBorder(null, lineType, TitledBorder.LEADING, TitledBorder.TOP, null, null));
				}
				if ( currentTab == null ) {
					throw new TemplateException("Creating row but no tab created yet to add the row to...");
				}
				currentTab.add(currentRow);
				continue;
			}

			lineType = getPanelName(line);
			if ( StringUtils.isNotBlank(lineType) ) {
				PanelConfig config = inputConfigs.get(lineType);

				AbstractInputPanel panel = createInputPanel(config);
				inputPanels.put(lineType, panel);

				if ( panel == null ) {
					throw new TemplateException("Can't find panel for panelName: "+lineType);
				}

				if ( currentRow == null ) {
					throw new TemplateException("No row created when trying to add panel with name: "+lineType);
				}

				if ( panel.isDisplayed() ) {
					currentRow.add(panel);
				}
				continue;
			}

		}

		JobSubmissionObjectImpl newJob = new JobSubmissionObjectImpl();
		template.setJobObject(newJob);


		for ( AbstractInputPanel panel : inputPanels.values() ) {
			panel.initPanel(template, si, newJob);
		}

		JPanel mainPanel = null;
		// now create the tabs
		if ( tabs.size() > 1 ) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			JTabbedPane tabbedPanel = new JTabbedPane();

			for ( String tabname : tabs.keySet() ) {
				tabbedPanel.addTab(tabname, createTab(tabs.get(tabname)));
			}

			mainPanel.add(tabbedPanel, BorderLayout.CENTER);

		} else {
			mainPanel = createTab(tabs.values().iterator().next());
		}

		template.setTemplatePanel(mainPanel);

		template.userInput(null, null);

		return template;
	}

	public static LinkedHashMap<String, PanelConfig> parseConfig(List<String> linesOrig) throws TemplateException {

		LinkedHashMap<String, PanelConfig> panels = new LinkedHashMap<String, PanelConfig>();
		String currentPanel = null;
		PanelConfig currentConfig = null;

		List<String> lines = new LinkedList(linesOrig);
		lines.remove(0);

		for ( String line : lines ) {

			line = line.trim();

			if ( StringUtils.isBlank(line) ) {
				continue;
			}

			String panelName = getPanelName(line);
			if ( StringUtils.isNotBlank(panelName) ) {
				// means new or first panel
				if ( (currentPanel != null) && (currentConfig != null) ) {

					panels.put(currentPanel, currentConfig);
					currentPanel = null;
					currentConfig = null;
				}

				currentPanel = panelName;
				currentConfig = new PanelConfig();
				currentConfig.addConfig(AbstractInputPanel.NAME, panelName);
			} else {

				if ( (getNewPageIndicator(line) != null) || (getNewRowIndicator(line) != null) ) {
					// that's ok
					continue;
				}

				if ( StringUtils.isBlank(currentPanel) ) {
					// means no current panel so nothing to configure
					throw new TemplateException("No panel specified for confg line: "+line);
				}

				addValueToConfig(currentConfig, line);

			}
		}

		if ( (currentPanel != null) && (currentConfig != null) ) {
			panels.put(currentPanel, currentConfig);
		}

		return panels;
	}


}
