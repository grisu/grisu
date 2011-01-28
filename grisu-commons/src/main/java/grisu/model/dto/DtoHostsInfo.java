package grisu.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A map object that contains all hostnames gridwide. These hostnames are all
 * mapped to the name of the site where they are located.
 * 
 * This can be useful for displaying structured information about the grid to
 * the user.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "hostsinfo")
public class DtoHostsInfo {

	public static DtoHostsInfo createHostsInfo(Map<String, String> allHosts) {

		final DtoHostsInfo result = new DtoHostsInfo();
		final List<DtoHost> list = new LinkedList<DtoHost>();

		for (final String key : allHosts.keySet()) {
			list.add(new DtoHost(key, allHosts.get(key)));
		}

		result.setAllHosts(list);

		return result;
	}

	/**
	 * The list of hosts with the sitenames mapped to them.
	 */
	private List<DtoHost> allHosts = new LinkedList<DtoHost>();

	public Map<String, String> asMap() {
		final Map<String, String> result = new TreeMap<String, String>();

		for (final DtoHost host : getAllHosts()) {
			result.put(host.getHostNameString(), host.getSiteNameString());
		}
		return result;
	}

	/**
	 * A convenience method to find a list of hostnames that are connected to
	 * the specified sitename.
	 * 
	 * @param site
	 *            the name of the site
	 * @return the list of hostnames
	 */
	public List<String> findHostsForSite(String site) {

		final List<String> result = new LinkedList<String>();

		for (final DtoHost serchHost : allHosts) {
			if (serchHost.getSiteNameString().equals(site)) {
				result.add(serchHost.getHostNameString());
			}
		}

		return result;
	}

	/**
	 * A convenience method to find a sitename that is connected to the
	 * specified host.
	 * 
	 * @param host
	 *            the hostname
	 * @return the sitename
	 */
	public String findSiteForHost(String host) {

		for (final DtoHost serchhost : allHosts) {
			if (serchhost.getHostNameString().equals(host)) {
				return serchhost.getSiteNameString();
			}
		}
		return null;
	}

	@XmlElement(name = "host")
	public List<DtoHost> getAllHosts() {
		return allHosts;
	}

	public void setAllHosts(List<DtoHost> allHosts) {
		this.allHosts = allHosts;
	}

}
