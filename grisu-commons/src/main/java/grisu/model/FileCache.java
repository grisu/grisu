package grisu.model;

import grisu.model.dto.GridFile;
import grisu.model.info.dto.DtoProperty;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCache {

	static final Logger myLogger = LoggerFactory.getLogger(FileCache.class);

	public static final CacheManager cacheManager = CacheManager.create();
	public static final Cache fileSystemCache = CacheManager.getInstance()
			.getCache("filesystem");

	public static GridFile getFileList(String url) {
		Element file = fileSystemCache.get(url);
		if (file == null) {
			return null;
		}
		myLogger.debug("filecache hit: " + url);
		return (GridFile) file.getObjectValue();
	}

	public static void putFileList(String url, GridFile file) {

		Element element = new Element(url, file);
		fileSystemCache.put(element);
		for (DtoProperty tmp : file.getUrls()) {
			Element e2 = new Element(tmp.getKey(), file);
			fileSystemCache.put(e2);
		}
	}

	public static void remove(GridFile file) {

		myLogger.debug("filecache remove: " + file.toString());
		fileSystemCache.remove(file.getUrl());
		for (DtoProperty tmp : file.getUrls()) {
			fileSystemCache.remove(tmp.getKey());
		}
	}

}
