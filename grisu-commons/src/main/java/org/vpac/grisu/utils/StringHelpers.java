package org.vpac.grisu.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class StringHelpers {

	public static void main(String[] args) {

		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("file://whatever1", "path/whatever1");
		map.put("file://whatever2", "path/whatever2");
		map.put("file://whatever3", "path/whatever2");
		map.put("file://whatever4", "path/whatever2");

		String string = mapToString(map);

		System.out.println("String: " + string);

		Map<String, String> map2 = StringToMap(string);

		System.out.println("Map:");
		for (String key : map2.keySet()) {
			System.out.println(key + " / " + map2.get(key));
		}

	}

	public static String mapToString(Map<String, String> map) {

		List<String> values = new LinkedList<String>();
		for (String key : map.keySet()) {
			values.add("[" + key + "=" + map.get(key) + "]");
		}
		return StringUtils.join(values, "");
	}

	public static Map<String, String> StringToMap(String string) {

		Map<String, String> result = new LinkedHashMap<String, String>();

		if (StringUtils.isBlank(string)) {
			return result;
		}
		String[] strings = string.split("\\]\\[");
		if (strings.length == 0) {
			return result;
		}

		strings[0] = strings[0].substring(1);

		strings[strings.length - 1] = strings[strings.length - 1].substring(0,
				strings[strings.length - 1].length() - 1);

		for (String s : strings) {
			String key = s.substring(0, s.indexOf("="));
			String value = s.substring(s.indexOf("=") + 1);
			result.put(key, value);
		}
		return result;
	}
}
