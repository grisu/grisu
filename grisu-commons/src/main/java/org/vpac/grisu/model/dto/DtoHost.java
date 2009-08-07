package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
    private String hostNameString; 

	/**
     * The name of the site where this host is located.
     */
    private String siteNameString;

    
    @XmlElement(name="hostname")
    public String getHostNameString() {
		return hostNameString;
	}

	public void setHostNameString(String hostNameString) {
		this.hostNameString = hostNameString;
	}

    @XmlElement(name="sitename")
	public String getSiteNameString() {
		return siteNameString;
	}

	public void setSiteNameString(String siteNameString) {
		this.siteNameString = siteNameString;
	}



}
