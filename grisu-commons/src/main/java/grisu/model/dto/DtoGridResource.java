package grisu.model.dto;

import grisu.jcommons.interfaces.GridResource;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper object that is created when the grid is queried for resources that
 * are able to run a certain type of job.
 * 
 * This contains a snapshot of values for a single grid resource that can be of
 * importance when deciding where to run a job.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "gridresource")
public class DtoGridResource implements GridResource {

	/**
	 * All the a executables that are available for the selected application on
	 * this grid resource.
	 */
	private Set<String> allExecutables;
	/**
	 * The application name of the job that was used to find this grid resource.
	 */
	private String applicationName;
	/**
	 * A list of versions of the application that are available on this grid
	 * resource.
	 */
	private List<String> availableApplicationVersion;
	/**
	 * The contact string for this grid resource.
	 */
	private String contactString;
	/**
	 * The number of free job slots for the VO that was used to find this grid
	 * resource.
	 */
	private int freeJobSlots;
	/**
	 * The job manager that is used on this grid resource.
	 */
	private String jobManager;
	/**
	 * The main memory ram size that is available on this grid resource.
	 */
	private int mainMemoryRAMSize;
	/**
	 * The main memory virtual size that is available on this grid resource.
	 */
	private int mainMemoryVirtualSize;
	/**
	 * The queue name for this grid resource.
	 */
	private String queueName;
	/**
	 * The rank for this grid resource. This is a value that was calculated by
	 * the ranking algorithm that is used in the matchmaker library.
	 */
	private int rank;
	/**
	 * The total of running jobs for the VO that was used to find this grid
	 * resource.
	 */
	private int runningJobs;
	/**
	 * The latitude for the site where this grid resource is located.
	 */
	private double siteLatitude;
	/**
	 * The longitude for the site where this grid resource is located.
	 */
	private double siteLongitude;
	/**
	 * The name of the site where this grid resource is located.
	 */
	private String siteName;
	/**
	 * The smp size on this grid resource.
	 */
	private int smpSize;
	/**
	 * The total number of jobs on this grid resource for the VO that was used
	 * to find this grid resource.
	 */
	private int totalJobs;
	/**
	 * The number of jobs that wait in the queue on this grid resource for the
	 * VO that was used to find this grid resource.
	 */
	private int waitingJobs;
	/**
	 * Whether the version that was specified in the initial query is available
	 * on this resource.
	 * 
	 * Don't use that, I think it doesn't work. Use the list of versions
	 * instead.
	 */
	private boolean isDesiredVersionInstalled;

	private String gramVersion;

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
		this.gramVersion = gr.getGRAMVersion();
	}

	public int compareTo(GridResource o) {

		if (this.getRank() < o.getRank()) {
			return Integer.MAX_VALUE;
		} else if (this.getRank() > o.getRank()) {
			return Integer.MIN_VALUE;
		} else {
			return this.getQueueName().compareTo(o.getQueueName());
		}

	}

	@Override
	public boolean equals(Object o) {

		if (o == null) {
			return false;
		}

		GridResource anotherResource = null;

		try {
			anotherResource = (GridResource) o;
		} catch (final Exception e) {
			return false;
		}

		if (queueName.equals(anotherResource.getQueueName())
				&& jobManager.equals(anotherResource.getJobManager())
				&& contactString.equals(anotherResource.getContactString())
				&& siteName.equals(anotherResource.getSiteName())) {
			return true;
		}
		return false;
	}

	@XmlElement(name = "executable")
	public Set<String> getAllExecutables() {
		return this.allExecutables;
	}

	@XmlAttribute(name = "applicationName")
	public String getApplicationName() {
		return this.applicationName;
	}

	@XmlElement(name = "version")
	public List<String> getAvailableApplicationVersion() {
		return this.availableApplicationVersion;
	}

	@XmlElement(name = "contactstring")
	public String getContactString() {
		return this.contactString;
	}

	@XmlElement(name = "freejobslots")
	public int getFreeJobSlots() {
		return this.freeJobSlots;
	}

	@XmlElement(name = "GRAMversion")
	public String getGRAMVersion() {
		return this.gramVersion;
	}

	@XmlElement(name = "jobmanager")
	public String getJobManager() {
		return this.jobManager;
	}

	@XmlElement(name = "mainmemoryramsize")
	public int getMainMemoryRAMSize() {
		return this.mainMemoryRAMSize;
	}

	@XmlElement(name = "mainmemoryvirtualsize")
	public int getMainMemoryVirtualSize() {
		return this.mainMemoryVirtualSize;
	}

	@XmlAttribute(name = "queuename")
	public String getQueueName() {
		return this.queueName;
	}

	@XmlElement(name = "rank")
	public int getRank() {
		return this.rank;
	}

	@XmlElement(name = "runningjobs")
	public int getRunningJobs() {
		return this.runningJobs;
	}

	@XmlAttribute(name = "latitude")
	public double getSiteLatitude() {
		return this.siteLatitude;
	}

	@XmlAttribute(name = "longitude")
	public double getSiteLongitude() {
		return this.siteLongitude;
	}

	@XmlAttribute(name = "sitename")
	public String getSiteName() {
		return this.siteName;
	}

	@XmlElement(name = "smpsize")
	public int getSmpSize() {
		return this.smpSize;
	}

	@XmlElement(name = "totaljobs")
	public int getTotalJobs() {
		return this.totalJobs;
	}

	// public int compareTo(Object arg0) {
	// GridResource anotherResource = (GridResource)arg0;
	// return this.getRank() < anotherResource.getRank() ? 1 :
	// (this.getRank() == anotherResource.getRank() ? 0 : -1);
	// }

	@XmlElement(name = "waitingjobs")
	public int getWaitingJobs() {
		return this.waitingJobs;
	}

	@Override
	public int hashCode() {
		return queueName.hashCode() + jobManager.hashCode()
				+ contactString.hashCode() + 23 * rank;
	}

	@XmlAttribute(name = "isdesiredversioninstalled")
	public boolean isDesiredSoftwareVersionInstalled() {
		return isDesiredVersionInstalled;
	}

	public void setAllExecutables(Set<String> allExecutables) {
		this.allExecutables = allExecutables;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setAvailableApplicationVersion(
			List<String> availableApplicationVersion) {
		this.availableApplicationVersion = availableApplicationVersion;
	}

	public void setContactString(String contactString) {
		this.contactString = contactString;
	}

	public void setDesiredSoftwareVersionInstalled(
			boolean isDesiredVersionInstalled) {
		this.isDesiredVersionInstalled = isDesiredVersionInstalled;
	}

	public void setFreeJobSlots(int freeJobSlots) {
		this.freeJobSlots = freeJobSlots;
	}

	public void setGRAMVersion(String gramVersion) {
		this.gramVersion = gramVersion;
	}

	public void setJobManager(String jobManager) {
		this.jobManager = jobManager;
	}

	public void setMainMemoryRAMSize(int mainMemoryRAMSize) {
		this.mainMemoryRAMSize = mainMemoryRAMSize;
	}

	public void setMainMemoryVirtualSize(int mainMemoryVirtualSize) {
		this.mainMemoryVirtualSize = mainMemoryVirtualSize;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setRunningJobs(int runningJobs) {
		this.runningJobs = runningJobs;
	}

	public void setSiteLatitude(double siteLatitude) {
		this.siteLatitude = siteLatitude;
	}

	public void setSiteLongitude(double siteLongitude) {
		this.siteLongitude = siteLongitude;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public void setSmpSize(int smpSize) {
		this.smpSize = smpSize;
	}

	public void setTotalJobs(int totalJobs) {
		this.totalJobs = totalJobs;
	}

	public void setWaitingJobs(int waitingJobs) {
		this.waitingJobs = waitingJobs;
	}

	@Override
	public String toString() {

		return getSiteName() + " : " + getQueueName() + " (Ranking: "
				+ getRank() + ")";
	}

}
