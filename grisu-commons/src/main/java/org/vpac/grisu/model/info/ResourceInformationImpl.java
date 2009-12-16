package org.vpac.grisu.model.info;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;

/**
 * Implementation of {@link ResourceInformation}.
 * 
 * @author markus
 * 
 */
public class ResourceInformationImpl implements ResourceInformation {

	static final Logger myLogger = Logger.getLogger(ResourceInformation.class
			.getName());

	public static String getHost(final String urlOrSubmissionLocation) {
		String hostname = null;

		if (urlOrSubmissionLocation.contains("://")) {

			// int firstIndex = urlOrSubmissionLocation.indexOf("://")+3;
			// int lastIndex = urlOrSubmissionLocation.indexOf("/", firstIndex);

			// int firstIndex = 0;
			// int lastIndex = urlOrSubmissionLocation.length();

			URI address;
			try {
				// dodgy, I know
				address = new URI(urlOrSubmissionLocation);
				// address = new
				// URI(urlOrSubmissionLocation.substring(firstIndex,
				// lastIndex));
			} catch (Exception e) {
				myLogger.error("Couldn't create url from: "
						+ urlOrSubmissionLocation);
				throw new RuntimeException("Couldn't create url from: "
						+ urlOrSubmissionLocation);
			}
			if (address.getHost() == null) {
				hostname = urlOrSubmissionLocation;
			} else {
				hostname = address.getHost();
			}
		} else if (urlOrSubmissionLocation.contains(":")
				&& !urlOrSubmissionLocation.contains("/")) {

			int startIndex = urlOrSubmissionLocation.indexOf(":") + 1;
			if (startIndex == -1) {
				startIndex = 0;
			}
			int endIndex = urlOrSubmissionLocation.indexOf("#");
			if (endIndex == -1) {
				endIndex = urlOrSubmissionLocation.length();
			}
			hostname = urlOrSubmissionLocation.substring(startIndex, endIndex);
		} else {
			myLogger
					.error("Could not parse url or submissionLocation for String: "
							+ urlOrSubmissionLocation);
			// TODO throw exception maybe?
			return null;
		}
		return hostname;
	}

	private ServiceInterface serviceInterface;
	private String[] cachedAllSubmissionLocations = null;
	private Map<String, Set<String>> cachedSiteAllSubmissionLocationsMap = new TreeMap<String, Set<String>>();
	private String[] cachedAllSites = null;
	private Map<String, String> cachedHosts = new HashMap<String, String>();
	private Map<String, String[]> cachedAllSubmissionLocationsPerFqan = new HashMap<String, String[]>();
	private Map<String, Set<String>> cachedAllSitesPerFqan = new HashMap<String, Set<String>>();

	private Map<String, List<String>> cachedStagingFilesystemsPerSubLoc = new HashMap<String, List<String>>();

	public ResourceInformationImpl(final ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public final Set<String> distillSitesFromSubmissionLocations(
			final Set<String> submissionLocations) {

		Set<String> temp = new TreeSet<String>();
		for (String subLoc : submissionLocations) {
			String site = null;
			try {
				site = getSite(subLoc);
				temp.add(site);
			} catch (Exception e) {
				myLogger.error("Could not get site for submissionlocation: "
						+ subLoc + ", ignoring it. Error: "
						+ e.getLocalizedMessage());
			}
		}
		return temp;
	}

	public final Set<String> filterSubmissionLocationsForSite(
			final String site, final Set<String> submissionlocations) {

		Set<String> temp = new TreeSet<String>();
		for (String subLoc : submissionlocations) {
			if (site.equals(getSite(subLoc))) {
				temp.add(subLoc);
			}
		}
		return temp;
	}

	public final Set<String> getAllAvailableSites(final String fqan) {

		if (cachedAllSitesPerFqan.get(fqan) == null) {
			Set<String> temp = new TreeSet<String>();
			for (String subLoc : getAllAvailableSubmissionLocations(fqan)) {
				temp.add(getSite(subLoc));
			}
		}
		return cachedAllSitesPerFqan.get(fqan);
	}

	public final String[] getAllAvailableSubmissionLocations(final String fqan) {

		if (cachedAllSubmissionLocationsPerFqan.get(fqan) == null) {
			String[] temp = serviceInterface.getAllSubmissionLocationsForFqan(
					fqan).asSubmissionLocationStrings();
			cachedAllSubmissionLocationsPerFqan.put(fqan, temp);
		}
		return cachedAllSubmissionLocationsPerFqan.get(fqan);
	}

	public final String[] getAllSites() {

		if (cachedAllSites == null) {

			for (String subLoc : getAllSubmissionLocations()) {
				cachedAllSites = serviceInterface.getAllSites().asArray();
			}
		}
		return cachedAllSites;
	}

	public final String[] getAllSubmissionLocations() {

		if (cachedAllSubmissionLocations == null) {
			cachedAllSubmissionLocations = serviceInterface
					.getAllSubmissionLocations().asSubmissionLocationStrings();
		}
		return cachedAllSubmissionLocations;
	}

	public final Set<String> getAllSubmissionLocationsForSite(final String site) {

		if (cachedSiteAllSubmissionLocationsMap.get(site) == null) {
			// now we are building the complete map, not only for this one site
			for (String subLoc : getAllSubmissionLocations()) {
				String sitetemp = getSite(subLoc);
				if (cachedSiteAllSubmissionLocationsMap.get(sitetemp) == null) {
					cachedSiteAllSubmissionLocationsMap.put(sitetemp,
							new HashSet<String>());
				}
				cachedSiteAllSubmissionLocationsMap.get(sitetemp).add(subLoc);
			}
		}
		return cachedSiteAllSubmissionLocationsMap.get(site);

	}

	public final String getRecommendedStagingFileSystemForSubmissionLocation(
			final String subLoc) {

		List<String> temp = getStagingFilesystemsForSubmissionLocation(subLoc);
		if (temp != null && temp.size() > 0) {
			return temp.get(0);
		} else {
			return null;
		}
	}

	public final String getSite(final String urlOrSubmissionLocation) {

		String host = getHost(urlOrSubmissionLocation);

		if (cachedHosts.get(host) == null) {
			cachedHosts.put(host, serviceInterface.getSite(host));
		}
		return cachedHosts.get(host);
	}

	public final List<String> getStagingFilesystemsForSubmissionLocation(
			final String subLoc) {

		if (subLoc == null || "".equals(subLoc)) {
			return null;
		}
		if (cachedStagingFilesystemsPerSubLoc.get(subLoc) == null) {
			List<String> temp = serviceInterface
					.getStagingFileSystemForSubmissionLocation(subLoc)
					.getStringList();
			cachedStagingFilesystemsPerSubLoc.put(subLoc, temp);
		}
		return cachedStagingFilesystemsPerSubLoc.get(subLoc);
	}

}
