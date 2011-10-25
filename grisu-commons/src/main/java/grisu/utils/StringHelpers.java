package grisu.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class StringHelpers {

	public static void main(String[] args) {

		final Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("file://whatever1", "path/whatever1");
		map.put("file://whatever2", "path/whatever2");
		map.put("file://whatever3", "path/whatever2");
		map.put("file://whatever4", "path/whatever2");

		final String string = mapToString(map);

		System.out.println("String: " + string);

		final Map<String, String> map2 = stringToMap(string);

		System.out.println("Map:");
		for (final String key : map2.keySet()) {
			System.out.println(key + " / " + map2.get(key));
		}

	}

	public static String mapToString(Map<String, String> map) {

		final List<String> values = new LinkedList<String>();
		for (final String key : map.keySet()) {
			values.add("[" + key + "=" + map.get(key) + "]");
		}
		return StringUtils.join(values, "");
	}

	public static Map<String, String> stringToMap(String string) {

		final Map<String, String> result = new LinkedHashMap<String, String>();

		if (StringUtils.isBlank(string)) {
			return result;
		}
		final String[] strings = string.split("\\]\\[");
		if (strings.length == 0) {
			return result;
		}

		strings[0] = strings[0].substring(1);

		strings[strings.length - 1] = strings[strings.length - 1].substring(0,
				strings[strings.length - 1].length() - 1);

		for (final String s : strings) {
			final String key = s.substring(0, s.indexOf("="));
			final String value = s.substring(s.indexOf("=") + 1);
			result.put(key, value);
		}
		return result;
	}
}
