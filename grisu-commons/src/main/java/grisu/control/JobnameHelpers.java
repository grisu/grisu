package grisu.control;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class JobnameHelpers {
	
	public final static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy.MM.dd_HH.mm.ss.SSS");

	public final static SimpleDateFormat short_format = new SimpleDateFormat(
			"yyyy.MM.dd_HH.mm.ss");

	
	public static String calculateTimestampedJobname(String baseJobname) {
		return calculateTimestampedJobname(baseJobname, format);
	}

	public static String calculateTimestampedJobname(String baseJobname,
			SimpleDateFormat format) {
		String newJobname = baseJobname + "_" + format.format(new Date());
		newJobname = newJobname.replace(":", "_");
		newJobname = newJobname.replace("\\", "_");
		newJobname = newJobname.replace("/", "_");
		newJobname = newJobname.replaceAll("\\s", "_");
		return newJobname;
	}

	public static String calculateUUIDJobname(String basejobname) {
		return basejobname + "_" + UUID.randomUUID().toString();
	}


}
