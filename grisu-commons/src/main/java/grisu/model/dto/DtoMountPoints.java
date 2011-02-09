package grisu.model.dto;

import grisu.model.MountPoint;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * A wrapper that holds a list of mountpoints.
 * 
 * These are all the mountpoints a user has access to.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "mountpoints")
public class DtoMountPoints {

	public static DtoMountPoints createMountpoints(MountPoint[] mps) {

		final DtoMountPoints result = new DtoMountPoints();
		result.setMountpoints(Arrays.asList(mps));
		return result;
	}

	public static DtoMountPoints createMountpoints(Set<MountPoint> mps) {

		final DtoMountPoints result = new DtoMountPoints();
		result.setMountpoints(new LinkedList<MountPoint>(mps));
		return result;
	}

	/**
	 * All mountpoints that are available for the user.
	 */
	private List<MountPoint> mountpoints = new LinkedList<MountPoint>();

	@XmlElement(name = "mountpoint")
	public List<MountPoint> getMountpoints() {
		return mountpoints;
	}

	public void setMountpoints(List<MountPoint> mountpoints) {
		this.mountpoints = mountpoints;
	}

}
