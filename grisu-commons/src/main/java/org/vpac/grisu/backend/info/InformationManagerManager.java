package org.vpac.grisu.backend.info;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.settings.Environment;

import au.org.arcs.jcommons.interfaces.InformationManager;

public class InformationManagerManager {

	static final Logger myLogger = Logger
			.getLogger(InformationManagerManager.class.getName());

	public static final String IM_CLASS = "type";

	private static Map<String, InformationManager> infoManagers = new HashMap<String, InformationManager>();

	public static String createKey(Map<String, String> map) {

		String type = map.get("type");
		if (StringUtils.isBlank(type)) {
			return "DEFAULT";
		}
		return type;
	}

	public static InformationManager getInformationManager(
			Map<String, String> parameters) {

		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		String key = createKey(parameters);
		InformationManager im = infoManagers.get(key);

		if (im == null) {
			System.out.println("Creating infoManager: " + key);
			im = createInfoManager(parameters);
			infoManagers.put(key, im);
		}
		return im;
	}

	public static InformationManager createInfoManager(
			Map<String, String> parameters) {

		try {
			String imClassName = parameters.get("type");

			if (StringUtils.isBlank(imClassName)) {
				parameters.put("type", "CachedMdsInformationManager");
				imClassName = "CachedMdsInformationManager";

				String dir = parameters.get("mdsFileDir");
				if (StringUtils.isBlank(dir)) {
					parameters.put("mdsFileDir", Environment
							.getVarGrisuDirectory().toString());
				}
			}

			if (!imClassName.contains(".")) {
				imClassName = "org.vpac.grisu.control.info." + imClassName;
			}

			Class imClass = Class.forName(imClassName);
			Constructor<InformationManager> constructor = imClass
					.getConstructor(Map.class);

			InformationManager im = constructor.newInstance(parameters);

			return im;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
