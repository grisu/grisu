package org.vpac.grisu.frontend.view.swing.jobcreation.templates;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.filters.Filter;

public class PanelConfig {

	private final Map<String, String> config = new HashMap<String, String>();
	private final LinkedList<Filter> filters = new LinkedList<Filter>();

	private String type;

	public PanelConfig() {
	}

	public void addConfig(String key, String value) {
		config.put(key, value);
	}

	public void addFilter(Filter filter) {
		filters.add(filter);
	}

	public Map<String, String> getPanelConfig() {
		return config;
	}

	public LinkedList<Filter> getFilters() {
		return filters;
	}

	public String getPanelType() {
		return type;
	}

	public String getType() {
		if (StringUtils.isBlank(type) ) {
			return "StringInput";
		} else {
			return type;
		}
	}

	public void setType(String type) {
		this.type = type;
	}

}
