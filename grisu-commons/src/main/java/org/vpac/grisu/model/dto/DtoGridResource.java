package org.vpac.grisu.model.dto;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import au.org.arcs.mds.GridResource;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoGridResource implements GridResource {

	@XmlElement(name="executable")
	private Set<String> allExecutables;
	@XmlAttribute(name="applicationName")
	private String applicationName;
	@XmlElement(name="version")
	private List<String> availableApplicationVersion;
	@XmlElement
	private String contactString;
	@XmlElement
	private int freeJobSlots;
	@XmlElement
	private String jobManager;
	@XmlElement
	private int mainMemoryRAMSize;
	@XmlElement
	private int mainMemoryVirtualSize;
	@XmlAttribute
	private String queueName;
	@XmlElement
	private int rank;
	@XmlElement
	private int runningJobs;
	@XmlAttribute
	private double siteLatitude;
	@XmlAttribute
	private double siteLongitude;
	@XmlAttribute
	private String siteName;
	@XmlElement
	private int smpSize;
	@XmlElement
	private int totalJobs;
	@XmlElement
	private int waitingJobs;
	@XmlAttribute
	private boolean isDesiredVersionInstalled;
	
	public DtoGridResource() {
		
	}
	
	public DtoGridResource(GridResource gr) {
		this.allExecutables = gr.getAllExecutables();
		this.applicationName = gr.getApplicationName();
		this.availableApplicationVersion = gr.getAvailableApplicationVersion();
		this.contactString = gr.getContactString();
		this.freeJobSlots = gr.getFreeJobSlots();
		this.jobManager = gr.getJobManager();
		this.mainMemoryRAMSize = gr.getMainMemoryRAMSize();
		this.mainMemoryVirtualSize = gr.getMainMemoryVirtualSize();
		this.queueName = gr.getQueueName();
		this.rank = gr.getRank();
		this.runningJobs = gr.getRunningJobs();
		this.siteLatitude = gr.getSiteLatitude();
		this.siteLongitude = gr.getSiteLongitude();
		this.siteName = gr.getSiteName();
		this.smpSize = gr.getSmpSize();
		this.totalJobs = gr.getTotalJobs();
		this.waitingJobs = gr.getWaitingJobs();
		this.isDesiredVersionInstalled = gr.isDesiredSoftwareVersionInstalled();
	}
	
	public Set<String> getAllExecutables() {
		return this.getAllExecutables();
	}

	public String getApplicationName() {
		return this.getApplicationName();
	}

	public List<String> getAvailableApplicationVersion() {
		return this.getAvailableApplicationVersion();
	}

	public String getContactString() {
		return this.getContactString();
	}

	public int getFreeJobSlots() {
		return this.getFreeJobSlots();
	}

	public String getJobManager() {
		return this.jobManager;
	}

	public int getMainMemoryRAMSize() {
		return this.mainMemoryRAMSize;
	}

	public int getMainMemoryVirtualSize() {
		return this.mainMemoryVirtualSize;
	}

	public String getQueueName() {
		return this.queueName;
	}

	public int getRank() {
		return this.rank;
	}

	public int getRunningJobs() {
		return this.runningJobs;
	}

	public double getSiteLatitude() {
		return this.siteLatitude;
	}

	public double getSiteLongitude() {
		return this.siteLongitude;
	}

	public String getSiteName() {
		return this.siteName;
	}

	public int getSmpSize() {
		return this.smpSize;
	}

	public int getTotalJobs() {
		return this.totalJobs;
	}

	public int getWaitingJobs() {
		return this.waitingJobs;
	}

	public boolean isDesiredSoftwareVersionInstalled() {
		return isDesiredVersionInstalled;
	}

	public int compareTo(Object arg0) {
		GridResource anotherResource = (GridResource)arg0;
		return this.getRank() < anotherResource.getRank() ? 1 :
			(this.getRank() == anotherResource.getRank() ? 0 : -1);
	}

}
