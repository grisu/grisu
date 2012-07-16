package grisu.model;

import grisu.model.dto.GridFile;
import grisu.model.info.dto.DtoProperty;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCache {

	static final Logger myLogger = LoggerFactory.getLogger(FileCache.class);
	public static CacheManager cache = null;
	public static Cache fileSystemCache = null;
	public static Cache shortCache = null;

	static {
		try {

			for (CacheManager cm : CacheManager.ALL_CACHE_MANAGERS) {
				if (cm.getName().equals("grisu-client")) {
					cache = cm;
					break;
				}
			}
			if (cache == null) {
				URL url = ClassLoader
						.getSystemResource("/grisu-client.ehcache.xml");
				if (url == null) {
					url = myLogger.getClass().getResource(
							"/grisu-client.ehcache.xml");
				}
				cache = new CacheManager(url);
				cache.setName("grisu-client");
			}


			fileSystemCache = cache.getCache("filesystem");
			if (fileSystemCache == null) {
				myLogger.debug("filesystem cache is null");
			}

			shortCache = cache.getCache("short");
			if (shortCache == null) {
				myLogger.debug("short cache is null");
			}
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
		}
	}

	public static GridFile getFileList(String url) {
		// if (StringUtils.isBlank(url)) {
		// return null;
		// }
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
