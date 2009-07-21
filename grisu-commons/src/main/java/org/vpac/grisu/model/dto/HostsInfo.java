package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A map object that contains all hostnames gridwide. These hostnames are all mapped to the name of the 
 * site where they are located.
 * 
 * This can be useful for displaying structured information about the grid to the user.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="hostsinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostsInfo {
	
	public static HostsInfo createHostsInfo(Map<String, String> allHosts) {
		
		HostsInfo result = new HostsInfo();
		List<Host> list = new LinkedList<Host>();
		
		for ( String key : allHosts.keySet() ) {
			list.add(new Host(key, allHosts.get(key)));
		}
		
		result.setAllHosts(list);
		
		return result;
	}
	
	/**
	 * The list of hosts with the sitenames mapped to them.
	 */
	@XmlElement(name="host")
	private List<Host> allHosts = new LinkedList<Host>();

	public List<Host> getAllHosts() {
		return allHosts;
	}

	public void setAllHosts(List<Host> allHosts) {
		this.allHosts = allHosts;
	}
	
	/**
	 * A convenience method to find a sitename that is connected to the specified host.
	 * 
	 * @param host the hostname
	 * @return the sitename
	 */
	public String getSiteForHost(String host) {
		
		for ( Host serchhost : allHosts ) {
			if ( serchhost.hostNameString.equals(host) ) {
				return serchhost.siteNameString;
			}
		}
		return null;
	}
	
	/**
	 * A convenience method to find a list of hostnames that are connected to the specified sitename.
	 * 
	 * @param site the name of the site
	 * @return the list of hostnames
	 */
	public List<String> getHostsForSite(String site) {
		
		List<String> result = new LinkedList<String>();
		
		for ( Host serchHost : allHosts ) {
			if ( serchHost.siteNameString.equals(site) ) {
				result.add(serchHost.hostNameString);
			}
		}
		
		return result;
	}
	
	

}
