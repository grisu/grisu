package org.vpac.grisu.frontend.view.swing.utils;

public class WalltimeUtils {
	public static int convertHumanReadableStringIntoSeconds(
			String[] humanReadable) {

		int amount = -1;
		try {
			amount = Integer.parseInt(humanReadable[0]);
		} catch (Exception e) {
			throw new RuntimeException("Could not parse string.", e);
		}
		String unit = humanReadable[1];

		if ("minutes".equals(unit)) {
			return amount * 60;
		} else if ("hours".equals(unit)) {
			return amount * 3600;
		} else if ("days".equals(unit)) {
			return amount * 3600 * 24;
		} else {
			// throw new RuntimeException(unit+" not a supported unit name.");
			return amount * 60; // default
		}

	}

	public static String[] convertSecondsInHumanReadableString(
			int walltimeInSeconds) {

		int days = walltimeInSeconds / (3600 * 24);
		int hours = (walltimeInSeconds - (days * 3600 * 24)) / 3600;
		int minutes = (walltimeInSeconds - ((days * 3600 * 24) + (hours * 3600))) / 60;

		if ((days > 0) && (hours == 0) && (minutes == 0)) {
			return new String[] { new Integer(days).toString(), "days" };
		} else if ((days > 0) && (hours == 0)) {
			// fuck the minutes
			return new String[] { new Integer(days).toString(), "days" };
		} else if ((days > 0) && (hours > 0)) {
			return new String[] { new Integer(days * 24 + hours).toString(),
					"hours" };
		} else if ((days == 0) && (hours > 0) && (minutes == 0)) {
			return new String[] { new Integer(hours).toString(), "hours" };
		} else if ((days == 0) && (hours > 0) && (minutes > 0)) {
			if (hours > 6) {
				// fuck the minutes
				return new String[] { new Integer(hours).toString(), "hours" };
			} else {
				return new String[] {
						new Integer(hours * 60 + minutes).toString(), "minutes" };
			}
		} else {
			return new String[] { new Integer(minutes).toString(), "minutes" };
		}

	}

}
