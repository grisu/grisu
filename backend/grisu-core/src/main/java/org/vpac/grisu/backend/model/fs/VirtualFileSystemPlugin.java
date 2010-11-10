package org.vpac.grisu.backend.model.fs;

import org.vpac.grisu.model.dto.DtoFileObject;

public interface VirtualFileSystemPlugin {

	/**
	 * Calculates the absolute url out of the provided virtual path.
	 * 
	 * @param tokens
	 *            the tokens of the path (which where seperated by "/"'s
	 * @return the absolute url
	 */
	public DtoFileObject createDtoFileObject(String path, int recursiveLevels)
			throws InvalidPathException;

}
