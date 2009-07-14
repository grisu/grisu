

package org.vpac.grisu.fs.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.log4j.Logger;

/**
 * The concept of MountPoints is pretty important within grisu. A MountPoint is basically a mapping of a "logical name" to an url.
 * Much like mountpoints in a unix filesystem. A logical name should contain the site where the filesystem sits and the VO that has got
 * access to this filesystem so that the user can recognise which one is meant when looking at the name in a file browser.
 * 
 * @author Markus Binsteiner
 *
 */
@Entity
public class MountPoint implements Comparable<MountPoint> {
	
	static final Logger myLogger = Logger.getLogger(MountPoint.class
			.getName());
	
	private Long mountPointId = null;

	private String dn = null;
	private String fqan = null;
	private String mountpointName = null;
	private String rootUrl = null;
	
	private boolean automaticallyMounted = false;
	private boolean disabled = false;
		
	// for hibernate
	public MountPoint() {
	}
	
	/**
	 * Creates a MountPoint. Sets automount property to false.
	 * 
	 * @param dn the dn of the user
	 * @param fqan the fqan that is used to access this filesystem
	 * @param rootUrl the root url 
	 * @param mountpoint the name of the mountpoint
	 */
	public MountPoint(String dn, String fqan, String url, String mountpoint) {
		this.dn = dn;
		this.fqan = fqan;
		this.rootUrl = url;
		this.mountpointName = mountpoint;
	}
	
	/**
	 * Creates a Mountpoint.
	 * @param dn the dn of the user
	 * @param fqan the fqan that is used to access this filesystem
	 * @param url the root url
	 * @param mountpoint the name of the mountpoint
	 * @param automaticallyMounted whether this mountpoint was mounted automatically using mds information or manually by the user
	 */
	public MountPoint(String dn, String fqan, String url, String mountpoint, boolean automaticallyMounted) {
		this(dn,fqan,url,mountpoint);
		this.automaticallyMounted = automaticallyMounted;
	}
	
	/**
	 * This is used primarily to create a "dummy" mountpoint to be able to use the {@link User#unmountFileSystem(String)} method.
	 * @param dn the dn of the user
	 * @param mountpoint the name of the mountpoint
	 */
	public MountPoint(String dn, String mountpoint) {
		this.dn = dn;
		this.mountpointName = mountpoint;
	}

	@Column(nullable=false)
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getFqan() {
		return fqan;
	}

	public void setFqan(String fqan) {
		this.fqan = fqan;
	}
	
	@Column(nullable=false)
	public String getMountpointName() {
		return mountpointName;
	}
	
	public void setMountpointName(String mountpoint) {
		this.mountpointName = mountpoint;
	}

	@Column(nullable=false)
	public String getRootUrl() {
		return rootUrl;
	}
	
	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public void setUrl(String url) {
		this.rootUrl = url;
	}

	@Id
	@GeneratedValue
	public Long getMountPointId() {
		return mountPointId;
	}

	public void setMountPointId(Long id) {
		this.mountPointId = id;
	}
	
//	public boolean equals(Object otherMountPoint) {
//		if ( ! (otherMountPoint instanceof MountPoint) )
//			return false;
//		MountPoint other = (MountPoint)otherMountPoint;
//		if ( other.dn.equals(this.dn) && other.mountpoint.equals(this.mountpoint) )
//			return true;
//		else return false;
//	}
	
	public boolean equals(Object otherMountPoint) {
		
		if (otherMountPoint instanceof MountPoint) {
			MountPoint other = (MountPoint) otherMountPoint;
				
			return other.getMountpointName().equals(this.getMountpointName());
			
//			if ( other.getDn().equals(this.getDn()) &&
//							other.getRootUrl().equals(this.getRootUrl()) ) {
//
//				if ( other.getFqan() == null ) {
//					if ( this.getFqan() == null ) {
//						return true;
//					} else {
//						return false;
//					}
//				} else {
//					if ( this.getFqan() == null ) {
//						return false;
//					} else {
//						return other.getFqan().equals(this.getFqan());
//					}
//				}
//			} else {
//				return false;
//			}
		} else {
			return false;
		}
		
	}
	
	public int hashCode() {
//		return dn.hashCode() + mountpoint.hashCode();
		return mountpointName.hashCode();
	}
	
	/**
	 * Translates a "mounted" file (on that filesystem to an absolute url like
	 * gsiftp://ngdata.vpac.org/home/san04/markus/test.txt)
	 * @param file the "mounted" file (e.g. /ngdata.vpac/test.txt
	 * @return the absoulte path of the file or null if the file is not in the mounted filesystem or is not a "mounted" file (starts with something like /home.sapac.ngadmin)
	 */
	public String replaceMountpointWithAbsoluteUrl(String file) {

		if ( file.startsWith(getMountpointName()) ) {
			return file.replaceFirst(getMountpointName(), getRootUrl());
		} else return null;
	}
	
	/**
	 * Translates an absolute file url to a "mounted" file url
	 * @param file the absolute file (gsiftp://ngdata.vpac.org/home/sano4/markus/test.txt)
	 * @return /ngdata.vpac.org/test.txt
	 */
	public String replaceAbsoluteRootUrlWithMountPoint(String file) {

		if ( file.startsWith(getRootUrl()) ) {
			return file.replaceFirst(getRootUrl(), getMountpointName());
			
		} else return null;
	}

	/**
	 * Checks whether the "userspace" url (/ngdata.vpac/file.txt) contains the file.
	 * @param file the file
	 * @return true - if it contains it; false - if not.
	 */
	public boolean isResponsibleForUserSpaceFile(String file) {
		
		if ( file.startsWith("gsiftp") ) {
			if ( file.startsWith(getRootUrl()) ) 
				return true;
			else 
				return false;
		}
		
		if ( file.startsWith(getMountpointName()) ) 
			return true;
		else 
			return false;
	}

	 /**
	  * Checks whether the "userspace" url (/ngdata.vpac/file.txt) contains the file.
	  * 
	  * @param file the file
	  * @return true - if it contains it; false - if not.
	  */
	public boolean isResponsibleForAbsoluteFile(String file) {
		
		if ( file.startsWith(getRootUrl()) ) { 
			return true;
		} else {
			if ( file.startsWith(getRootUrl().replace(":2811", "")) ) {
				// warning
				myLogger.warn("Found mountpoint. Didn't compare port numbers though...");
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getMountpointName();
	}
	
	/**
	 * Returns the path of the specified file relative to the root of this mountpoint.
	 * @param url the file
	 * @return the relative path or null if the file is not within the filesystem of the mountpoint
	 */
	public String getRelativePathToRoot(String url) {
		
		if ( url.startsWith("/") ) {
			if ( ! url.startsWith(getMountpointName()) ) 
				return null;
			else {
				String path = url.substring(getMountpointName().length());
				if ( path.startsWith("/") ) 
					return path.substring(1);
				else 
					return path;
			}
		} else {
			if ( ! url.startsWith(getRootUrl()) ) 
				return null;
			else {
				String path = url.substring(getRootUrl().length());
				if ( path.startsWith("/") ) 
					return path.substring(1);
				else
					return path;
			}
		}
		
	}

//	public int compareTo(Object o) {
////		return ((MountPoint)o).getMountpoint().compareTo(getMountpoint());
//		return getRootUrl().compareTo(((MountPoint)o).getRootUrl());
//	}
	
	public int compareTo(MountPoint mp) {
		return getRootUrl().compareTo(mp.getRootUrl());
	}
	
	@Column(nullable=false)
	public boolean isAutomaticallyMounted() {
		return automaticallyMounted;
	}

	public void setAutomaticallyMounted(boolean automaticallyMounted) {
		this.automaticallyMounted = automaticallyMounted;
	}
	
	@Column(nullable=false)
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	
}
