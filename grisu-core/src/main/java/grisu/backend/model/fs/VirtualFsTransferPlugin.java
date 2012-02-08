package grisu.backend.model.fs;

import grisu.backend.model.RemoteFileTransferObject;
import grisu.backend.model.User;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.model.FileManager;
import grisu.model.dto.DtoProperty;
import grisu.model.dto.GridFile;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class VirtualFsTransferPlugin implements FileTransferPlugin {

	private final User user;
	private final VirtualFileSystemInfoPlugin infoPlugin;

	public VirtualFsTransferPlugin(User user,
			VirtualFileSystemInfoPlugin infoPlugin) {
		this.user = user;
		this.infoPlugin = infoPlugin;
	}

	public RemoteFileTransferObject copySingleFile(String source,
			String target, boolean overwrite) throws RemoteFileSystemException {

		String realSource = null;
		if (source.startsWith(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME)) {

			final GridFile sourceFile = infoPlugin
					.createGsiftpGridFile(FileManager
							.calculateParentUrl(source));
			final Map<String, String> urls = DtoProperty
					.mapFromDtoPropertiesList(sourceFile.getUrls());

			for (final String key : urls.keySet()) {
				if (key.startsWith("gsiftp")) {
					realSource = key + "/" + FileManager.getFilename(source);
					break;
				}
			}
			if (StringUtils.isBlank(realSource)) {
				throw new RemoteFileSystemException(
						"No real file found for source: " + source);
			}
		} else {
			realSource = source;
		}

		String realTarget = null;
		if (target.startsWith(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME)) {
			final GridFile targetFile = infoPlugin
					.createGsiftpGridFile(FileManager
							.calculateParentUrl(target));

			final Map<String, String> urls = DtoProperty
					.mapFromDtoPropertiesList(targetFile.getUrls());

			for (final String key : urls.keySet()) {
				if (key.startsWith("gsiftp")) {
					realTarget = key + FileManager.getFilename(target);
					break;
				}
			}
			if (StringUtils.isBlank(realTarget)) {
				throw new RemoteFileSystemException(
						"No real file found for target: " + target);
			}
		} else {
			realTarget = target;
		}

		return user.getFileManager().copy(realSource, realTarget,
				overwrite);

	}

}
