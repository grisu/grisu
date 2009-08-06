package org.vpac.grisu.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import au.org.arcs.jcommons.interfaces.GridResource;

/**
 * A wrapper that holds a list of {@link DtoGridResource} objects.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="gridresources")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoGridResources {

	public static DtoGridResources createGridResources(List<GridResource> resources) {
		
		DtoGridResources result = new DtoGridResources();
		
		List<DtoGridResource> list = new LinkedList<DtoGridResource>();
		
		for ( GridResource r : resources ) {
			DtoGridResource dtor= new DtoGridResource(r);
			list.add(dtor);
		}
		
		result.setGridResources(list);
		
		return result;
		
	}
	
	
	/**
	 * The list of grid resources.
	 */
	@XmlElement(name="gridresource")
	List<DtoGridResource> gridResources = new LinkedList<DtoGridResource>();

	public List<DtoGridResource> getGridResources() {
		return gridResources;
	}

	public void setGridResources(List<DtoGridResource> gridResources) {
		this.gridResources = gridResources;
	}
	
	public SortedSet<GridResource> wrapGridResourcesIntoInterfaceType() {
		
		SortedSet<GridResource> result = new TreeSet<GridResource>();
		
		for ( DtoGridResource gr : getGridResources() ) {
			result.add(gr);
		}
		
		return result;
	}
	
	
	
}
