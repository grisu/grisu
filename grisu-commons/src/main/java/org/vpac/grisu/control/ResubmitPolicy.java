package org.vpac.grisu.control;

import org.vpac.grisu.model.dto.DtoProperties;

public interface ResubmitPolicy {

	/**
	 * The name of this policy.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the properties of this policy.
	 * 
	 * @return the properties
	 */
	public DtoProperties getProperties();

}
