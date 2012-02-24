package grisu.model;

import grisu.jcommons.utils.FileSystemHelpers;
import grisu.model.dto.DtoProperty;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * The concept of MountPoints is pretty important within grisu. A MountPoint is
 * basically a mapping of a "logical name" to an url. Much like mountpoints in a
 * unix filesystem. A logical name should contain the site where the filesystem
 * sits and the VO that has got access to this filesystem so that the user can
 * recognise which one is meant when looking at the name in a file browser.
 * 
 * @author Markus Binsteiner
 * 
 */
@Entity
@XmlRootElement(name = "mountpoint")
@XmlAccessorType(XmlAccessType.NONE)
public class MountPoint implements Comparable<MountPoint> {

	static final Logger myLogger = LoggerFactory.getLogger(MountPoint.class
			.getName());

	public static final String ALIAS_KEY = "label";
	public static final String PATH_KEY = "path";
	public static final String USER_SUBDIR_KEY = "user_subdir";

	public static final String HIDDEN_KEY = "hidden";

	private Long mountPointId = null;

	/**
	 * The dn of the user.
	 */
	private String dn = null;
	/**
	 * The fqan that is used to create a voms credential to access this
	 * mountpoint.
	 */
	private String fqan = null;
	/**
	 * The alias of this mountpoint.
	 */
	private String alias = null;
	/**
	 * The url of the root of this mountpoint.
	 */
	private String rootUrl = null;

	/**
	 * The name of the site this mountpoint belongs to.
	 */
	private String site = null;

	private List<DtoProperty> properties = new LinkedList<DtoProperty>();

	private boolean automaticallyMounted = false;
	private boolean disabled = false;

	private boolean isHomeDir = false;

	private boolean isVolatileFileSystem = false;

	// for hibernate
	public MountPoint() {
	}

	/**
	 * This is used primarily to create a "dummy" mountpoint to be able to use
	 * the {@link User#unmountFileSystem(String)} method.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @param mountpoint
	 *            the name of the mountpoint
	 */
	public MountPoint(final String dn, final String mountpoint) {
		this.dn = dn;
		this.alias = mountpoint;
	}

	/**
	 * Creates a MountPoint. Sets automount property to false.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @param fqan
	 *            the fqan that is used to access this filesystem
	 * @param url
	 *            the root url
	 * @param mountpoint
	 *            the name of the mountpoint
	 */
	public MountPoint(final String dn, final String fqan, final String url,
			final String mountpoint, final String site) {
		this.dn = dn;
		this.fqan = fqan;
		this.rootUrl = FileManager.ensureTrailingSlash(url);
		this.alias = mountpoint;
		this.site = site;
	}


	/**
	 * Creates a Mountpoint.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @param fqan
	 *            the fqan that is used to access this filesystem
	 * @param url
	 *            the root url
	 * @param mountpoint
	 *            the name of the mountpoint
	 * @param automaticallyMounted
	 *            whether this mountpoint was mounted automatically using mds
	 *            information or manually by the user
	 */
	public MountPoint(final String dn, final String fqan, final String url,
			final String mountpoint, final String site,
			final boolean automaticallyMounted, boolean homedir) {
		this(dn, fqan, url, mountpoint, site);
		this.automaticallyMounted = automaticallyMounted;
		this.isHomeDir = homedir;
	}

	public void addProperty(String key, String value) {
		this.properties.add(new DtoProperty(key, value));
	}

	public int compareTo(final MountPoint mp) {
		return ComparisonChain.start().compare(getRootUrl(), mp.getRootUrl())
				.compare(getAlias(), mp.getAlias()).result();
	}

	@Override
	public boolean equals(final Object otherMountPoint) {

		if (otherMountPoint instanceof MountPoint) {
			final MountPoint other = (MountPoint) otherMountPoint;

			return Objects.equal(getRootUrl(), other.getRootUrl())
					&& Objects.equal(getAlias(), other.getAlias());
		} else {
			return false;
		}

	}

	@Column(nullable = false)
	@XmlAttribute(name = "alias")
	public String getAlias() {
		return alias;
	}

	@Column(nullable = false)
	@XmlElement(name = "dn")
	public String getDn() {
		return dn;
	}

	/**
	 * The fqan that is used to create a voms proxy to access this mountpoint.
	 * 
	 * @return the fqan
	 */
	@XmlElement(name = "fqan")
	public String getFqan() {
		return fqan;
	}

	@Id
	@GeneratedValue
	public Long getMountPointId() {
		return mountPointId;
	}

	@Transient
	public String getPath() {
		return FileSystemHelpers.getPath(getRootUrl());
	}

	@Transient
	@XmlElement(name = "property")
	public List<DtoProperty> getProperties() {
		return properties;
	}

	@Transient
	public String getProperty(String key) {
		for (final DtoProperty prop : this.properties) {
			if (prop.getKey().equals(key)) {
				return prop.getValue();
			}
		}
		return null;
	}

	/**
	 * Returns the path of the specified file relative to the root of this
	 * mountpoint.
	 * 
	 * @param url
	 *            the file
	 * @return the relative path or null if the file is not within the
	 *         filesystem of the mountpoint
	 */
	public String getRelativePathToRoot(final String url) {

		if (url.startsWith("/")) {
			if (!url.startsWith(getAlias())) {
				return null;
			} else {
				final String path = url.substring(getAlias().length());
				if (path.startsWith("/")) {
					return path.substring(1);
				} else {
					return path;
				}
			}
		} else {
			if (!url.startsWith(getRootUrl())) {
				return null;
			} else {
				final String path = url.substring(getRootUrl().length());
				if (path.startsWith("/")) {
					return path.substring(1);
				} else {
					return path;
				}
			}
		}

	}

	@Column(nullable = false)
	@XmlAttribute(name = "url")
	public String getRootUrl() {
		return rootUrl;
	}

	@Column(nullable = false)
	@XmlElement(name = "site")
	public String getSite() {
		return site;
	}

	@Override
	public int hashCode() {
		// return dn.hashCode() + mountpoint.hashCode();
		return Objects.hashCode(getRootUrl(), alias.hashCode());
	}

	@Column(nullable = false)
	@XmlElement(name = "automounted")
	public boolean isAutomaticallyMounted() {
		return automaticallyMounted;
	}

	@Column(nullable = false)
	@XmlElement(name = "disabled")
	public boolean isDisabled() {
		return disabled;
	}

	@Column(nullable = false)
	@XmlElement(name = "isHomeDir")
	public boolean isHomeDir() {
		return isHomeDir;
	}

	/**
	 * Checks whether the "userspace" url (/ngdata.vpac/file.txt) contains the
	 * file.
	 * 
	 * @param file
	 *            the file
	 * @return true - if it contains it; false - if not.
	 */
	public boolean isResponsibleForAbsoluteFile(final String file) {

		if (FileManager.removeTrailingSlash(file).startsWith(getRootUrl())) {
			return true;
		} else {
			if (file.startsWith(getRootUrl().replace(":2811", ""))) {
				// warning
				myLogger.warn("Found mountpoint. Didn't compare port numbers though...");
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the "userspace" url (/ngdata.vpac/file.txt) contains the
	 * file.
	 * 
	 * @param file
	 *            the file
	 * @return true - if it contains it; false - if not.
	 */
	public boolean isResponsibleForUserSpaceFile(final String file) {

		if (file.startsWith("gsiftp")) {
			if (FileManager.removeTrailingSlash(file).startsWith(getRootUrl())) {
				return true;
			} else {
				return false;
			}
		}

		if (file.startsWith(getAlias())) {
			return true;
		} else {
			return false;
		}
	}

	@Column(nullable = true)
	@XmlElement(name = "volatileFileSystem")
	public boolean isVolatileFileSystem() {
		return isVolatileFileSystem;
	}

	// public boolean equals(Object otherMountPoint) {
	// if ( ! (otherMountPoint instanceof MountPoint) )
	// return false;
	// MountPoint other = (MountPoint)otherMountPoint;
	// if ( other.dn.equals(this.dn) && other.mountpoint.equals(this.mountpoint)
	// )
	// return true;
	// else return false;
	// }

	/**
	 * Translates an absolute file url to a "mounted" file url.
	 * 
	 * @param file
	 *            the absolute file
	 *            (gsiftp://ngdata.vpac.org/home/sano4/markus/test.txt)
	 * @return /ngdata.vpac.org/test.txt
	 */
	public String replaceAbsoluteRootUrlWithMountPoint(final String file) {

		if (file.startsWith(getRootUrl())) {
			return file.replaceFirst(getRootUrl(), getAlias());

		} else {
			return null;
		}
	}

	public void setAlias(final String mountpoint) {
		this.alias = mountpoint;
	}

	public void setAutomaticallyMounted(final boolean am) {
		this.automaticallyMounted = am;
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	public void setDn(final String dn) {
		this.dn = dn;
	}

	public void setFqan(final String fqan) {
		this.fqan = fqan;
	}

	private void setHomeDir(final boolean homedir) {
		this.isHomeDir = homedir;
	}

	public void setMountPointId(final Long id) {
		this.mountPointId = id;
	}

	public void setProperties(List<DtoProperty> properties) {
		this.properties = properties;
	}

	// public int compareTo(Object o) {
	// // return ((MountPoint)o).getMountpoint().compareTo(getMountpoint());
	// return getRootUrl().compareTo(((MountPoint)o).getRootUrl());
	// }

	public void setRootUrl(final String rootUrl) {
		this.rootUrl = FileManager.ensureTrailingSlash(rootUrl);
	}

	public void setSite(final String site) {
		this.site = site;
	}

	public void setUrl(final String url) {
		this.rootUrl = FileManager.ensureTrailingSlash(url);
	}

	public void setVolatileFileSystem(final boolean isVolatile) {
		this.isVolatileFileSystem = isVolatile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getAlias();
	}

}
