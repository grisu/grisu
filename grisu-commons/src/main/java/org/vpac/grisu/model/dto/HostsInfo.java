package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
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
	
	@XmlElement(name="host")
	private List<Host> allHosts = new LinkedList<Host>();

	public List<Host> getAllHosts() {
		return allHosts;
	}

	public void setAllHosts(List<Host> allHosts) {
		this.allHosts = allHosts;
	}
	
	public String getSiteForHost(String host) {
		
		for ( Host serchhost : allHosts ) {
			if ( serchhost.hostNameString.equals(host) ) {
				return serchhost.siteNameString;
			}
		}
		return null;
	}
	
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
