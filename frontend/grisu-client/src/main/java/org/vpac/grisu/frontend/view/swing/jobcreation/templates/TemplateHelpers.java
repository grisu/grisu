package org.vpac.grisu.frontend.view.swing.jobcreation.templates;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.filters.Filter;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.AbstractInputPanel;

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
			Filter filter = null;
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

		} catch (Exception e) {
			throw new TemplateException("Can't create filter for config string: "+configString, e);
		}


	}

	private static Map<String, String> createFilterConfig(String configString) {

		Map<String, String> config = new HashMap<String, String>();

		String

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

		LinkedHashMap<String, AbstractInputPanel> panels = readConfig(lines);

		for ( String panel : panels.keySet() ) {
			System.out.println("Panelname: "+panel);
			System.out.println(panels.get(panel).toString());
		}


	}

	public static LinkedHashMap<String, AbstractInputPanel> readConfig(List<String> lines) throws TemplateException {

		LinkedHashMap<String, AbstractInputPanel> panels = new LinkedHashMap<String, AbstractInputPanel>();
		String currentPanel = null;
		PanelConfig currentConfig = null;

		for ( String line : lines ) {

			line = line.trim();

			if ( StringUtils.isBlank(line) ) {
				continue;
			}

			String panelName = getPanelName(line);
			if ( StringUtils.isNotBlank(panelName) ) {
				// means new or first panel
				if ( (currentPanel != null) && (currentConfig != null) ) {

					AbstractInputPanel panel = createInputPanel(currentConfig);
					panels.put(currentPanel, panel);
					currentPanel = null;
					currentConfig = null;
				}

				currentPanel = panelName;
				currentConfig = new PanelConfig();
				currentConfig.addConfig(AbstractInputPanel.NAME, panelName);
			} else {

				if ( StringUtils.isBlank(currentPanel) ) {
					// means no current panel so nothing to configure
					throw new TemplateException("No panel specified for confg line: "+line);
				}

				addValueToConfig(currentConfig, line);

			}
		}

		if ( (currentPanel != null) && (currentConfig != null) ) {

			AbstractInputPanel panel = createInputPanel(currentConfig);
			panels.put(currentPanel, panel);

		}

		return panels;


	}


}
