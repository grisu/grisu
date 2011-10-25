package grisu.backend.info;

import grisu.jcommons.interfaces.InformationManager;
import grisu.jcommons.interfaces.MatchMaker;
import grisu.settings.Environment;

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
	private static Map<String, MatchMaker> matchMakers = new HashMap<String, MatchMaker>();

	public static InformationManager createInfoManager(
			Map<String, String> parameters) {

		// try {
		String imClassName = parameters.get("type");

		if (StringUtils.isBlank(imClassName)) {
			parameters.put("type", "CachedMdsInformationManager");
			imClassName = "CachedMdsInformationManager";

			final String dir = parameters.get("mdsFileDir");
			if (StringUtils.isBlank(dir)) {
				parameters.put("mdsFileDir", Environment.getVarGrisuDirectory()
						.toString());
			}
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

	public static MatchMaker createMatchMaker(Map<String, String> parameters) {

		try {
			String imClassName = parameters.get("type");

			if (StringUtils.isBlank(imClassName)) {
				parameters.put("type", "MatchMakerImpl");
				imClassName = "MatchMakerImpl";

				final String dir = parameters.get("mdsFileDir");
				if (StringUtils.isBlank(dir)) {
					parameters.put("mdsFileDir", Environment
							.getVarGrisuDirectory().toString());
				}
			}

			if (!imClassName.contains(".")) {
				imClassName = "grisu.control.info." + imClassName;
			}

			final Class imClass = Class.forName(imClassName);
			final Constructor<MatchMaker> constructor = imClass
					.getConstructor(Map.class);

			final MatchMaker im = constructor.newInstance(parameters);

			return im;

		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return null;
		}

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

	public static MatchMaker getMatchMaker(Map<String, String> parameters) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		final String key = createKey(parameters);
		MatchMaker mm = matchMakers.get(key);

		if (mm == null) {
			mm = createMatchMaker(parameters);
			matchMakers.put(key, mm);
		}
		return mm;
	}

}
