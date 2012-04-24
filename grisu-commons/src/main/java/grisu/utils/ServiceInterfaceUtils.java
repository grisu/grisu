package grisu.utils;

import grisu.control.ServiceInterface;
import grisu.model.GrisuRegistryManager;
import grisu.model.info.dto.Application;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

public class ServiceInterfaceUtils {

	public static List<Application> filterApplicationNames(ServiceInterface si,
			String filter) {
		final LinkedList<Application> result = new LinkedList<Application>();
		for (final Application app : GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager().getAllAvailableApplications()) {
			if (FilenameUtils.wildcardMatch(app.getName(), filter,
					IOCase.INSENSITIVE)) {
				result.add(app);
			}
		}
		return result;
	}

	public static List<String> filterJobNames(Collection<String> names,
			String filter) {
		final LinkedList<String> result = new LinkedList<String>();
		for (final String jobname : names) {
			if (FilenameUtils.wildcardMatch(jobname, filter)) {
				result.add(jobname);
			}
		}
		Collections.sort(result);
		return result;
	}

	public static List<String> filterJobNames(ServiceInterface si, String filter) {
		return filterJobNames(si, filter, true);
	}

	public static List<String> filterJobNames(ServiceInterface si, String filter, boolean refresh) {
		SortedSet<String> jobnames = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager().getCurrentJobnames(refresh);
		return filterJobNames(jobnames, filter);
	}
}
