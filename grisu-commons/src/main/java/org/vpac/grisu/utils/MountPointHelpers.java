package org.vpac.grisu.utils;

import java.net.URI;

import com.google.common.collect.ImmutableBiMap;

public class MountPointHelpers {

	static final public ImmutableBiMap<String, String> MOUNTPOINT_ALIASS = new ImmutableBiMap.Builder<String, String>()
			.put("ng2.auckland.ac.nz (BeSTGRID)", "Auckland")
			.put("ng2.auckland.ac.nz (Workshop)", "Auckland")
			.put("ng2.aut.ac.nz (BeSTGRID)", "AUT")
			.put("ng2.canterbury.ac.nz (BeSTGRID)", "Canterbury").build();

	/**
	 * Calculates the name of the mountpoint for a given server and fqan. It
	 * does that so the mountpoint looks something like:
	 * "ng2.vpac.org (StartUp)". Not sure whether that is the way to go, but
	 * it's the best namingscheme I came up with. Asked in the developers
	 * mailing list but didn't get any answers that made sense...
	 * 
	 * @param server
	 *            the hostname
	 * @param fqan
	 *            the VO
	 * @return the name of the mountpoint
	 */
	public static String calculateMountPointName(final String server,
			final String fqan) {

		URI uri = null;
		String hostname = null;
		try {
			uri = new URI(server);
			hostname = uri.getHost();
		} catch (final Exception e) {
			hostname = server;
		}
		final String name = hostname + " ("
				+ (fqan.substring(fqan.lastIndexOf("/") + 1) + ")");

		return name;
	}

}
