package org.vpac.grisu.model.dto;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.vpac.grisu.model.MountPoint;

/**
 * A wrapper that holds a list of mountpoints.
 * 
 * These are all the mountpoints a user has access to.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="mountpoints")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoMountPoints {
	
	public static DtoMountPoints createMountpoints(MountPoint[] mps) {
		
		DtoMountPoints result = new DtoMountPoints();
		
		result.setMountpoints(Arrays.asList(mps));
		
		return result;
	}
	
	/**
	 * All mountpoints that are available for the user.
	 */
	@XmlElement(name="mountpoint")
	private List<MountPoint> mountpoints = new LinkedList<MountPoint>();

	public List<MountPoint> getMountpoints() {
		return mountpoints;
	}

	public void setMountpoints(List<MountPoint> mountpoints) {
		this.mountpoints = mountpoints;
	}

}
