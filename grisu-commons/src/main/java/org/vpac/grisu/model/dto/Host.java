package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class Host {
	
	public Host() {
		
	}
	
	public Host(String host, String site) {
		this.hostNameString = host;
		this.siteNameString = site;
	}
	
    @XmlElement(name="hostname")
    public String hostNameString; 

    @XmlElement(name="sitename")
    public String siteNameString;


}
