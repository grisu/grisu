package org.vpac.grisu.frontend.control.utils;

import javax.swing.Icon;

import org.apache.commons.lang.StringUtils;

public class ApplicationsManager {

	public static Icon getIcon(String application) {
		return null;
	}

	public static String getPrettyName(String application) {

		return StringUtils.capitalize(application);
	}

	public static String getShortDescription(String application) {

		return application;

	}

}
