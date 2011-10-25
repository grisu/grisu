package grisu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrisuClientInfo {

	static final Logger myLogger = LoggerFactory
			.getLogger(GrisuClientInfo.class.getName());

	public static String get(String module) {

		try {
			final List<String> result = readTextFromJar("/" + module
					+ ".version");

			if ((result == null) || (result.size() == 0)
					|| StringUtils.isEmpty(result.get(0))) {
				return "N/A";
			}

			if ("VERSION_TOKEN".equals(result.get(0))) {
				return "N/A";
			}

			return result.get(0);
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return "N/A";
		}

	}

	public static List<String> readTextFromJar(String s) {
		InputStream is = null;
		BufferedReader br = null;
		String line;
		final ArrayList<String> list = new ArrayList<String>();

		try {
			is = FileUtils.class.getResourceAsStream(s);
			br = new BufferedReader(new InputStreamReader(is));
			while (null != (line = br.readLine())) {
				list.add(line);
			}
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (final IOException e) {
				myLogger.error(e.getLocalizedMessage(), e);
			}
		}
		return list;
	}
}
