package grisu.model.utils;

import grisu.X;
import grisu.model.info.dto.Package;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.collect.Sets;

public class InformationUtils {

	public static String guessLatestVersion(Collection<String> versions) {
		return guessLatestVersion(versions, 0);
	}

	public static String guessLatestVersion(Collection<String> versions,
			int startToken) {

		final List<List<Integer>> tokenizedVersions = new LinkedList<List<Integer>>();

		boolean lastTokenReached = false;

		Integer currentMax = Integer.MIN_VALUE;
		Map<List<Integer>, String> equals = new LinkedHashMap<List<Integer>, String>();

		while (!lastTokenReached) {
			lastTokenReached = true;

			int i = startToken;
			for (final String version : versions) {
				Integer t = Integer.MIN_VALUE;
				final List<Integer> temp = tokenizeVersionIntegers(version);
				try {
					t = temp.get(i);
					lastTokenReached = false;

					if (t == currentMax) {
						equals.put(temp, version);
					} else if (t > currentMax) {
						currentMax = t;
						equals = new LinkedHashMap<List<Integer>, String>();
						equals.put(temp, version);
					}
				} catch (final IndexOutOfBoundsException e) {
				}

			}
			i = i + 1;

			if (equals.size() == 1) {
				return equals.values().iterator().next();
			} else if (equals.size() > 1) {
				return guessLatestVersion(equals.values(), i);
			} else {
				// should never happen
				throw new RuntimeException(
						"Could not determine latest version for versions: "
								+ StringUtils.join(versions, " / "));
			}
		}
		throw new RuntimeException(
				"Could not determine latest version for versions: "
						+ StringUtils.join(versions, " / "));

	}

	public static String guessLatestVersionOfPackages(Collection<Package> packages) {

		Set<String> versions = Sets.newTreeSet();
		for (Package p : packages) {
			versions.add(p.getVersion().toString());
		}

		return guessLatestVersion(versions, 0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final List<String> versions = new LinkedList<String>();
		versions.add("1.5.23");
		versions.add("1.6.2");
		versions.add("1.6.23");
		versions.add("1.6.22.2");

		final String v = guessLatestVersion(versions, 0);

		X.p(v);

	}

	private static List<Integer> tokenizeVersionIntegers(String version) {

		final String[] tokens = version.split("[._]");

		final List<Integer> tokenList = new LinkedList<Integer>();

		for (final String token : tokens) {
			// if there is a number in it, we assume we can compare it...
			if (token.matches(".*\\d.*")) {
				try {
					tokenList
					.add(Integer.parseInt(token.replaceAll("\\D", "")));
				} catch (final Exception e) {
					// don't worry for now
				}
			}
		}

		return tokenList;

	}

}
