package org.vpac.grisu.model;

import java.util.List;
import java.util.Set;

/**
 * A GridResource contains information about an application on a certain grid-resource
 * with up-to-date information about the grid-resource.
 * 
 * @author Markus Binsteiner
 */
public interface GridResource extends Comparable {

	/**
	 * The latitude of the site.
	 * 
	 * @return the latitude
	 */
	public abstract double getSiteLatitude();

	/**
	 * The longitude of the site.
	 * 
	 * @return the longitude
	 */
	public abstract double getSiteLongitude();

	/**
	 * The name of the site.
	 * 
	 * @return the sitename
	 */
	public abstract String getSiteName();

	/**
	 * The name of the application.
	 * 
	 * @return the applicationname
	 */
	public abstract String getApplicationName();

	/**
	 * All the versions of the applications on this resource.
	 * 
	 * @return the versions
	 */
	public abstract List<String> getAvailableApplicationVersion();

	/**
	 * The number of free job slots on this resource for the fqan that was specified earlier.
	 * 
	 * @return the free job slots
	 */
	public abstract int getFreeJobSlots();

	/**
	 * The number of running jobs on this resource for the fqan that was specified earlier.
	 *  
	 * @return the number of running jobs
	 */
	public abstract int getRunningJobs();

	/**
	 * The number of waiting jobs on this resource for the fqan that was spefified earlier.
	 * 
	 * @return the number of waiting jobs
	 */
	public abstract int getWaitingJobs();

	/**
	 * The number of total jobs for this resource for the fqan that was spefified earlier.
	 * 
	 * @return the total jobs
	 */
	public abstract int getTotalJobs();

	/**
	 * The main memory ram size on this resource. 
	 * 
	 * @return the memory
	 */
	public abstract int getMainMemoryRAMSize();

	/**
	 * The main memory virtual size on this resource.
	 * 
	 * @return the memory
	 */
	public abstract int getMainMemoryVirtualSize();

	/**
	 * Ths smp size on this resource.
	 * 
	 * @return the smp size
	 */
	public abstract int getSmpSize();

	/**
	 * The contact string for this resource.
	 * 
	 * @return the contact string
	 */
	public abstract String getContactString();

	/**
	 * The job manager for this resource.
	 * 
	 * @return the job manager
	 */
	public abstract String getJobManager();

	/**
	 * Whether the version of the application that was requested earlier is available on this resource.
	 * 
	 * Don't use that. I think it doesn't work.
	 * 
	 * @return whether the desired version is installed on this resource
	 */
	public abstract boolean isDesiredSoftwareVersionInstalled();

	/**
	 * The rank that was calculated by the specified ranking algorithm of the matchmaker.
	 * 
	 * @return the rank
	 */
	public abstract int getRank();

	/**
	 * The name of the queue for this resource.
	 * 
	 * @return the queue name
	 */
	public abstract String getQueueName();
	
	/**
	 * All executables that are available for the application on this resource.
	 * 
	 * @return all executables
	 */
	public abstract Set<String> getAllExecutables();
	
}