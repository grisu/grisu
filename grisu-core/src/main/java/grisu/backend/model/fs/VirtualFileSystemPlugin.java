package grisu.backend.model.fs;

import grisu.control.exceptions.RemoteFileSystemException;
import grisu.model.dto.GridFile;

public interface VirtualFileSystemPlugin {

	/**
	 * Calculates the absolute url out of the provided virtual path.
	 * 
	 * @param tokens
	 *            the tokens of the path (which where seperated by "/"'s
	 * @return the absolute url
	 * @throws RemoteFileSystemException
	 */
	public GridFile createGridFile(String path, int recursiveLevels)
			throws InvalidPathException, RemoteFileSystemException;

}
