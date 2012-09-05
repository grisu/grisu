package grisu.backend.info;

import grisu.jcommons.interfaces.InformationManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InformationManagerManager {

	static final Logger myLogger = LoggerFactory
			.getLogger(InformationManagerManager.class.getName());

	public static final String IM_CLASS = "type";

	private static Map<String, InformationManager> infoManagers = new HashMap<String, InformationManager>();

	public static InformationManager createInfoManager(
			Map<String, String> parameters) {

		// try {
		String imClassName = parameters.get("type");

		if (StringUtils.isBlank(imClassName)) {
			parameters.put("type", "GrinformationManager");
			imClassName = "grisu.jcommons.interfaces.GrinformationManager";

			// final String dir = parameters.get("mdsFileDir");
			// if (StringUtils.isBlank(dir)) {
			// parameters.put("mdsFileDir", Environment.getVarGrisuDirectory()
			// .toString());
			// }
		}

		if (!imClassName.contains(".")) {
			imClassName = "grisu.control.info." + imClassName;
		}

		try {
			final Class imClass = Class.forName(imClassName);
			final Constructor<InformationManager> constructor = imClass
					.getConstructor(Map.class);

			InformationManager im;

			im = constructor.newInstance(parameters);
			return im;
		} catch (final InvocationTargetException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			throw new RuntimeException(e);
		} catch (final RuntimeException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
			throw new RuntimeException(e);
		}

	}

	public static String createKey(Map<String, String> map) {

		final String type = map.get("type");
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
		final String key = createKey(parameters);
		InformationManager im = infoManagers.get(key);

		if (im == null) {
			im = createInfoManager(parameters);
			infoManagers.put(key, im);
		}
		return im;
	}



}
