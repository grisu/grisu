package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * A wrapper object that contains a hostname and the site to which this hostname belongs.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="host")
public class DtoHost {
	
	public DtoHost() {
		
	}
	
	public DtoHost(String host, String site) {
		this.hostNameString = host;
		this.siteNameString = site;
	}
	
    /**
     * The hostname.
     */
    @XmlElement(name="hostname")
    public String hostNameString; 

	/**
     * The name of the site where this host is located.
     */
    @XmlElement(name="sitename")
    public String siteNameString;

    
    public String getHostNameString() {
		return hostNameString;
	}

	public void setHostNameString(String hostNameString) {
		this.hostNameString = hostNameString;
	}

	public String getSiteNameString() {
		return siteNameString;
	}

	public void setSiteNameString(String siteNameString) {
		this.siteNameString = siteNameString;
	}



}
