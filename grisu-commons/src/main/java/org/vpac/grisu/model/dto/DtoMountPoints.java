package org.vpac.grisu.model.dto;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.vpac.grisu.model.MountPoint;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoMountPoints {
	
	public static DtoMountPoints createMountpoints(MountPoint[] mps) {
		
		DtoMountPoints result = new DtoMountPoints();
		
		result.setMountpoints(Arrays.asList(mps));
		
		return result;
	}
	
	@XmlElement(name="mountpoint")
	private List<MountPoint> mountpoints = new LinkedList<MountPoint>();

	public List<MountPoint> getMountpoints() {
		return mountpoints;
	}

	public void setMountpoints(List<MountPoint> mountpoints) {
		this.mountpoints = mountpoints;
	}

}
